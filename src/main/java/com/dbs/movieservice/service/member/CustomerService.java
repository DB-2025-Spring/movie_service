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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientLevelRepository clientLevelRepository;
    private final LevelUpService levelUpService;
    private final SignupCouponService signupCouponService;

    @Transactional
    public Customer registerCustomer(SignupRequest signupRequest) {
        // 아이디 중복 체크
        if (customerRepository.existsByCustomerInputId(signupRequest.getCustomerInputId())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        // 전화번호로 기존 비회원 계정 확인
        Optional<Customer> existingGuestOpt = customerRepository.findByPhone(signupRequest.getPhone());
        
        if (existingGuestOpt.isPresent()) {
            Customer existingGuest = existingGuestOpt.get();
            // 비회원인 경우 정회원으로 업그레이드
            if (Role.ROLE_GUEST.getCode().equals(existingGuest.getAuthority())) {
                log.info("비회원 계정을 정회원으로 업그레이드합니다: {}", existingGuest.getCustomerInputId());
                return upgradeGuestToFullMember(existingGuest, signupRequest);
            } else {
                // 이미 정회원이거나 관리자인 경우
                throw new RuntimeException("이미 가입된 전화번호입니다: " + signupRequest.getPhone());
            }
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
        
        // 저장
        Customer savedCustomer = customerRepository.save(customer);
        
        // 신규가입 쿠폰 발급
        try {
            boolean signupCouponIssued = signupCouponService.issueSignupCoupon(signupRequest.getCustomerInputId());
            if (signupCouponIssued) {
                log.info("신규 회원에게 신규가입 쿠폰 발급 완료: {}", signupRequest.getCustomerInputId());
            }
        } catch (Exception e) {
            log.warn("신규 회원에게 신규가입 쿠폰 발급 실패: {}", signupRequest.getCustomerInputId(), e);
            // 쿠폰 발급 실패는 회원가입 실패로 처리하지 않음
        }
        
        return savedCustomer;
    }
    
    /**
     * 비회원 계정을 정회원으로 업그레이드
     * 기존 비회원 계정의 데이터를 유지하면서 정회원 정보로 업데이트
     */
    @Transactional
    public Customer upgradeGuestToFullMember(Customer guestCustomer, SignupRequest signupRequest) {
        // 비회원 계정 정보 업데이트
        guestCustomer.setCustomerInputId(signupRequest.getCustomerInputId());
        guestCustomer.setCustomerPw(passwordEncoder.encode(signupRequest.getCustomerPw()));
        // 이름, 생일, 전화번호는 이미 있으므로 변경하지 않거나 필요시 업데이트
        if (!guestCustomer.getCustomerName().equals(signupRequest.getCustomerName())) {
            guestCustomer.setCustomerName(signupRequest.getCustomerName());
        }
        if (!guestCustomer.getBirthDate().equals(signupRequest.getBirthDate())) {
            guestCustomer.setBirthDate(signupRequest.getBirthDate());
        }
        
        // 권한을 MEMBER로 업그레이드
        guestCustomer.setAuthority(Role.ROLE_MEMBER.getCode());
        
        // 가입일은 변경하지 않음 (비회원으로 가입한 날짜 유지)

        guestCustomer.setPoints(0);
        
        // 저장
        Customer savedCustomer = customerRepository.save(guestCustomer);
        
        log.info("비회원({})이 정회원({})으로 업그레이드되었습니다.", 
                guestCustomer.getCustomerId(), signupRequest.getCustomerInputId());
        
        // 신규가입 쿠폰 발급
        try {
            boolean signupCouponIssued = signupCouponService.issueSignupCoupon(signupRequest.getCustomerInputId());
            if (signupCouponIssued) {
                log.info("업그레이드된 회원에게 신규가입 쿠폰 발급 완료: {}", signupRequest.getCustomerInputId());
            }
        } catch (Exception e) {
            log.warn("업그레이드된 회원에게 신규가입 쿠폰 발급 실패: {}", signupRequest.getCustomerInputId(), e);
            // 쿠폰 발급 실패는 회원 업그레이드 실패로 처리하지 않음
        }
        
        return savedCustomer;
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

    /**
     * 전화번호로 고객 조회
     * @param phone 전화번호
     * @return 고객 정보 (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findByPhone(String phone) {
        return customerRepository.findByPhone(phone);
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
    
    /**
     * 고객 삭제 - CASCADE 방식으로 관련 데이터도 함께 삭제
     * @param customerInputId 삭제할 고객의 입력 아이디
     * @throws RuntimeException 고객이 존재하지 않을 경우
     */
    @Transactional
    public void deleteCustomer(String customerInputId) {
        log.info("Deleting customer: {} with CASCADE delete (관련 데이터도 모두 삭제됩니다)", customerInputId);
        
        Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));
        
        // 관리자는 삭제할 수 없음
        if (Role.ROLE_ADMIN.getCode().equals(customer.getAuthority())) {
            throw new RuntimeException("Cannot delete admin user: " + customerInputId);
        }
        
        // 삭제 전 관련 데이터 로깅
        log.info("Customer {} has {} reviews, {} tickets, {} payments, and {} issued coupons that will be deleted",
                customerInputId,
                customer.getReviews() != null ? customer.getReviews().size() : 0,
                customer.getTickets() != null ? customer.getTickets().size() : 0,
                customer.getPayments() != null ? customer.getPayments().size() : 0,
                customer.getIssuedCoupons() != null ? customer.getIssuedCoupons().size() : 0);
        
        customerRepository.delete(customer);
        log.info("Customer deleted successfully with all related data: {}", customerInputId);
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
