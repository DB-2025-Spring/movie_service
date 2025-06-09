package com.dbs.movieservice.service.member;

import com.dbs.movieservice.controller.dto.SignupRequest;
import com.dbs.movieservice.domain.member.Role;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.repository.member.ClientLevelRepository;
import com.dbs.movieservice.repository.member.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientLevelRepository clientLevelRepository;

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
        customer.setAuthority(Role.ROLE_GUEST.getCode());
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

} 
