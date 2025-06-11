package com.dbs.movieservice.service.member;

import com.dbs.movieservice.controller.dto.SignupRequest;
import com.dbs.movieservice.controller.dto.CustomerResponse;
import com.dbs.movieservice.controller.dto.CustomerUpdateRequest;
import com.dbs.movieservice.domain.member.Role;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.member.ClientLevel;
import com.dbs.movieservice.repository.member.ClientLevelRepository;
import com.dbs.movieservice.repository.member.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientLevelRepository clientLevelRepository;
    private final LevelUpService levelUpService;

    @Transactional
    public Customer registerCustomer(SignupRequest signupRequest) {
        // 아이디 중복 체크
        if (customerRepository.existsByCustomerInputId(signupRequest.getCustomerInputId())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        // 컨트롤러에서 엔티티 생성 로직을 서비스 계층으로 이동
        Customer customer = new Customer();
        customer.setCustomerInputId(signupRequest.getCustomerInputId());
        customer.setCustomerPw(passwordEncoder.encode(signupRequest.getCustomerPw())); // 암호화는 서비스 계층에서 직접 처리
        customer.setCustomerName(signupRequest.getCustomerName());
        customer.setBirthDate(signupRequest.getBirthDate());
        customer.setPhone(signupRequest.getPhone());
        
        // 기본값 설정
        customer.setAuthority(Role.ROLE_MEMBER.getCode());
        customer.setJoinDate(LocalDate.now());
        customer.setPoints(0);
        customer.setLevel(clientLevelRepository.findDefaultLevel());
        
        return customerRepository.save(customer);
    }
    
    public boolean checkDuplicateCustomerId(String customerInputId) {
        return customerRepository.existsByCustomerInputId(customerInputId);
    }
    
    @Transactional
    public Customer upgradeGuestToMember(String customerInputId) {
        Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerInputId));
        
        // GUEST인 경우에만 MEMBER로 업그레이드
        if (Role.ROLE_GUEST.getCode().equals(customer.getAuthority())) {
            customer.setAuthority(Role.ROLE_MEMBER.getCode());
            return customerRepository.save(customer);
        }
        
        // 이미 MEMBER나 ADMIN인 경우 변경하지 않음
        return customer;
    }
    
    /**
     * 고객 입력 아이디로 고객 조회
     * @param customerInputId
     * @return Customer
     */
    @Transactional(readOnly = true)
    public Customer getCustomerByInputId(String customerInputId) {
        return customerRepository.findByCustomerInputId(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerInputId));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAllCustomers() {
        log.info("Fetching all customers for admin");
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(this::convertToCustomerResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerResponse updateCustomer(String customerInputId, CustomerUpdateRequest request) {
        log.info("Updating customer: {}", customerInputId);
        
        Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));
        
        // 정보 업데이트
        customer.setCustomerName(request.getCustomerName());
        customer.setPhone(request.getPhone());
        if (request.getBirthDate() != null) {
            customer.setBirthDate(request.getBirthDate());
        }
        
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer updated successfully: {}", customerInputId);
        
        return convertToCustomerResponse(savedCustomer);
    }

    @Transactional
    public CustomerResponse updateCustomerLevel(String customerInputId, Integer levelId) {
        log.info("Updating customer level: {} to level {}", customerInputId, levelId);
        
        Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));
        
        ClientLevel newLevel = clientLevelRepository.findById(levelId)
                .orElseThrow(() -> new RuntimeException("Level not found: " + levelId));
        
        // 현재 등급 저장 (등급업 확인용)
        ClientLevel currentLevel = customer.getLevel();
        Integer currentLevelId = currentLevel != null ? currentLevel.getLevelId() : 1;
        
        customer.setLevel(newLevel);
        Customer savedCustomer = customerRepository.save(customer);
        
        // 등급이 상승했을 때만 등급업 쿠폰 발급
        if (levelId > currentLevelId) {
            log.info("Level up detected: {} from level {} to level {}", 
                    customerInputId, currentLevelId, levelId);
            levelUpService.issueLevelUpCoupon(savedCustomer);
        }
        
        log.info("Customer level updated successfully: {} to {}", customerInputId, newLevel.getLevelName());
        
        return convertToCustomerResponse(savedCustomer);
    }

    private CustomerResponse convertToCustomerResponse(Customer customer) {
        CustomerResponse.LevelInfo levelInfo = null;
        if (customer.getLevel() != null) {
            levelInfo = CustomerResponse.LevelInfo.builder()
                    .levelId(customer.getLevel().getLevelId())
                    .levelName(customer.getLevel().getLevelName())
                    .build();
        }
        
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .customerInputId(customer.getCustomerInputId())
                .customerName(customer.getCustomerName())
                .phone(customer.getPhone())
                .birthDate(customer.getBirthDate())
                .joinDate(customer.getJoinDate())
                .authority(customer.getAuthority())
                .points(customer.getPoints())
                .level(levelInfo)
                .build();
    }

} 
