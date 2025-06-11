package com.dbs.movieservice.service.member;

import com.dbs.movieservice.controller.dto.CustomerProfileResponse;
import com.dbs.movieservice.controller.dto.CustomerUpdateRequest;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.repository.member.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerProfileService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 고객 프로필 조회
     */
    @Transactional(readOnly = true)
    public CustomerProfileResponse getCustomerProfile(String customerInputId) {
        log.info("Fetching profile for customer: {}", customerInputId);
        
        Customer customer = customerRepository.findByCustomerInputIdWithLevel(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));

        log.info("Customer found: {}, Level: {}", customer.getCustomerInputId(), customer.getLevel().getLevelName());

        return CustomerProfileResponse.builder()
                .customerId(customer.getCustomerId())
                .customerInputId(customer.getCustomerInputId())
                .customerName(customer.getCustomerName())
                .birthDate(customer.getBirthDate())
                .phone(customer.getPhone())
                .joinDate(customer.getJoinDate())
                .points(customer.getPoints() != null ? customer.getPoints() : 0)
                .levelId(customer.getLevel().getLevelId().intValue())
                .levelName(customer.getLevel().getLevelName())
                .rewardRate(customer.getLevel().getRewardRate())
                .build();
    }

    /**
     * 고객 프로필 수정
     */
    @Transactional
    public CustomerProfileResponse updateCustomerProfile(String customerInputId, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findByCustomerInputIdWithLevel(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));

        // 관리자 페이지에서는 비밀번호 변경 기능을 제공하지 않음

        // 기타 정보 업데이트
        if (request.getCustomerName() != null && !request.getCustomerName().trim().isEmpty()) {
            customer.setCustomerName(request.getCustomerName());
        }
        
        if (request.getBirthDate() != null) {
            customer.setBirthDate(request.getBirthDate());
        }
        
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            customer.setPhone(request.getPhone());
        }

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Profile updated for customer: {}", customerInputId);

        return CustomerProfileResponse.builder()
                .customerId(updatedCustomer.getCustomerId())
                .customerInputId(updatedCustomer.getCustomerInputId())
                .customerName(updatedCustomer.getCustomerName())
                .birthDate(updatedCustomer.getBirthDate())
                .phone(updatedCustomer.getPhone())
                .joinDate(updatedCustomer.getJoinDate())
                .points(updatedCustomer.getPoints())
                .levelId(updatedCustomer.getLevel().getLevelId())
                .levelName(updatedCustomer.getLevel().getLevelName())
                .rewardRate(updatedCustomer.getLevel().getRewardRate())
                .build();
    }
} 