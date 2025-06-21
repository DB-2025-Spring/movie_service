package com.dbs.movieservice.service.member;

import com.dbs.movieservice.controller.dto.GuestSignupRequest;
import com.dbs.movieservice.controller.dto.GuestSignupResponse;
import com.dbs.movieservice.domain.member.Role;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.repository.member.ClientLevelRepository;
import com.dbs.movieservice.repository.member.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientLevelRepository clientLevelRepository;

    /**
     * 비회원 등록
     * 전화번호 중복 시 기존 비회원 정보 반환
     */
    @Transactional
    public GuestSignupResponse registerGuest(GuestSignupRequest request) {
        // 전화번호로 기존 비회원 조회
        Optional<Customer> existingCustomer = customerRepository.findByPhone(request.getPhone());
        
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            // 기존 고객이 비회원(GUEST)인 경우에만 정보 반환
            if (Role.ROLE_GUEST.getCode().equals(customer.getAuthority())) {
                log.info("기존 비회원 정보 반환: {}", customer.getCustomerInputId());
                return GuestSignupResponse.builder()
                        .customerInputId(customer.getCustomerInputId())
                        .customerName(customer.getCustomerName())
                        .phone(customer.getPhone())
                        .joinDate(customer.getJoinDate())
                        .message("기존 비회원 정보로 진행합니다.")
                        .type("Bearer")
                        .build();
            } else {
                // 이미 정회원으로 가입된 전화번호인 경우
                throw new RuntimeException("이미 정회원으로 가입된 전화번호입니다.");
            }
        }

        // 새로운 비회원 등록
        Customer guest = createNewGuest(request);
        Customer savedGuest = customerRepository.save(guest);
        
        log.info("새로운 비회원 등록 완료: {}", savedGuest.getCustomerInputId());
        
        return GuestSignupResponse.builder()
                .customerInputId(savedGuest.getCustomerInputId())
                .customerName(savedGuest.getCustomerName())
                .phone(savedGuest.getPhone())
                .joinDate(savedGuest.getJoinDate())
                .message("비회원 등록이 완료되었습니다.")
                .type("Bearer")
                .build();
    }

    /**
     * 비회원 정보로 로그인 (전화번호 + 비밀번호)
     */
    public Optional<Customer> authenticateGuest(String phone, String password) {
        Optional<Customer> customerOpt = customerRepository.findByPhone(phone);
        
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            // 비회원이고 비밀번호가 일치하는 경우
            if (Role.ROLE_GUEST.getCode().equals(customer.getAuthority()) && 
                passwordEncoder.matches(password, customer.getCustomerPw())) {
                return Optional.of(customer);
            }
        }
        
        return Optional.empty();
    }

    /**
     * 비회원 아이디 자동 생성
     * 오늘 날짜로 시작하는 비회원 아이디 중 가장 큰 번호의 시퀀스를 가져온다
     */
    private String generateGuestId() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "GUEST" + today;
        
        // 오늘 날짜로 시작하는 ID 중 가장 큰 것의 시퀀스를 가져온다
        Optional<String> lastGuestIdOpt = customerRepository.findLatestGuestIdByPrefix(prefix);
        int nextSequence = 0;
    
        if (lastGuestIdOpt.isPresent()) {
            String lastGuestId = lastGuestIdOpt.get();
            if (lastGuestId.length() == prefix.length() + 3) {
                String lastSeqStr = lastGuestId.substring(prefix.length());
                try {
                    nextSequence = Integer.parseInt(lastSeqStr) + 1;
                } catch (NumberFormatException e) {
                    log.warn("비회원 ID 파싱 오류: {}", lastGuestId);
                    nextSequence = 0;
                }
            }
        }
    
        if (nextSequence >= 1000) {
            throw new IllegalStateException("하루 최대 비회원 수(1000명)를 초과했습니다.");
        }
    
        return prefix + String.format("%03d", nextSequence);
    }

    /**
     * 새로운 비회원 Customer 엔티티 생성
     */
    private Customer createNewGuest(GuestSignupRequest request) {
        Customer guest = new Customer();
        guest.setCustomerInputId(generateGuestId());
        guest.setCustomerPw(passwordEncoder.encode(request.getCustomerPw()));
        guest.setCustomerName(request.getCustomerName());
        guest.setBirthDate(request.getBirthDate());
        guest.setPhone(request.getPhone());
        guest.setAuthority(Role.ROLE_GUEST.getCode());
        guest.setJoinDate(LocalDate.now());
        guest.setPoints(0);
        guest.setLevel(clientLevelRepository.findDefaultLevel());
        
        return guest;
    }
} 