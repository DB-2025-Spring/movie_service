package com.dbs.movieservice.controller.thater;

import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.dto.ConfirmPaymentDTO;
import com.dbs.movieservice.service.member.CustomerService;
import com.dbs.movieservice.service.ticketing.PaymentService;
import com.dbs.movieservice.util.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "결제 관련 API", description = "토스 페이먼트용 결제 API")
public class PaymentController {
    @Value("${toss.secret-key}")
    private String secretKey;
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    private final CustomerService customerService;

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        ConfirmPaymentDTO dto=paymentService.confirmPayment(9L,"asdff",1,"asdf");
        return ResponseEntity.ok("성공");
    }


    @RequestMapping(value = "/success", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> confirmPayment(@RequestParam String paymentKey,
                                                 @RequestParam String orderId,
                                                 @RequestParam Long amount,
                                                 @RequestParam Long couponId) {
        String url = "https://api.tosspayments.com/v1/payments/confirm";
        System.out.println("/success 호출됨!!!");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //헤더 구성
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
        headers.set("Authorization", "Basic " + encodedKey);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId", orderId);
        body.put("amount", amount);
        Long usedCouponId = couponId;
        System.out.println(usedCouponId);
        //toss 페이먼트로 post요청
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        
        //응답
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            TossApproveResponse approveResponse = objectMapper.readValue(response.getBody(), TossApproveResponse.class);
            log.info("orderId: {}, paymentKey: {}, amount: {}", approveResponse.getOrderId(), approveResponse.getPaymentKey(), approveResponse.getTotalAmount());
            String brokenMethod = approveResponse.getMethod(); // 깨진 문자열
            String fixedMethod = new String(brokenMethod.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            log.info("paymentId: {} paymentKey: {}, paymentNo: {}, method: {}",approveResponse.getOrderId(),approveResponse.getPaymentKey(), approveResponse.getApproveNo(),fixedMethod );

            String approveNumber = approveResponse.approveNo;
            String numericPart = orderId.replaceAll("\\D+", ""); //숫자가 아니면 전부 공백처리. payment3 -> 3
            Long paymentId = Long.parseLong(numericPart);
            ConfirmPaymentDTO payment = paymentService.confirmPayment(paymentId,paymentKey,0,fixedMethod);
            return ResponseEntity.ok("결제 승인 성공. paymentId: "+payment.getPaymentId());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body("승인 실패: " + e.getResponseBodyAsString());
        }catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패", e);
            return ResponseEntity.internalServerError().body("JSON 파싱 오류: " + e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPayment(@RequestParam Long paymentId) {
        Payment payment;
        try {
            // 1. 현재 로그인한 사용자 조회 (회원 및 비회원 모두 지원)
            String currentCustomerInputId = SecurityUtils.getCurrentCustomerInputId();
            Customer currentCustomer = customerService.getCustomerByInputId(currentCustomerInputId);
            
            // 2. paymentId로 결제 정보 조회 (결제 상태 등 확인)
            payment = paymentService.getPayment(paymentId);
            
            // 3. 권한 확인 - 결제한 고객과 현재 로그인한 고객이 같은지 확인 (회원/비회원 구분 없이)
            if (!payment.getCustomer().getCustomerId().equals(currentCustomer.getCustomerId())) {
                return ResponseEntity.status(403).body("해당 결제를 취소할 권한이 없습니다.");
            }
            
            // 4. 결제 상태 확인
            if (!"Approve".equalsIgnoreCase(payment.getPaymentStatus())) {
                return ResponseEntity.badRequest().body("아직 승인되지 않았거나 이미 취소된 결제입니다.");
            }
        } catch (RuntimeException e) {
            log.error("결제 취소 권한 확인 실패", e);
            return ResponseEntity.status(401).body("인증이 필요합니다.");
        }

        String paymentKey = payment.getPaymentKey();
//        String paymentKey = "tviva202506100131022RYy6";
        int cancelAmount = payment.getPaymentAmount();
//        int cancelAmount = 10000;
        String cancelReason = "사용자 요청에 의한 환불";

        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedKey);

        Map<String, Object> body = new HashMap<>();
        body.put("cancelReason", cancelReason);
        body.put("cancelAmount", cancelAmount);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> tossResponse = restTemplate.postForEntity(url, request, String.class);
            log.info("✅ Toss 환불 성공: {}", tossResponse.getBody());

            // 2. 로컬 결제 취소 처리 (DB 상태 변경, 티켓 삭제 등)
            Payment cancelledPayment = paymentService.cancelPayment(paymentId);

            return ResponseEntity.ok("결제 취소 성공. paymentId: " + cancelledPayment.getPaymentId());
        } catch (HttpClientErrorException e) {
            log.error("❌ Toss 환불 실패: {}", e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body("환불 실패: " + e.getResponseBodyAsString());
        }
    }

    @PostMapping("/fail")
    public ResponseEntity<String> failPayment(@RequestParam String orderId) {
        String numericPart = orderId.replaceAll("\\D+", "");
        Long paymentId = Long.parseLong(numericPart);
        Payment payment = paymentService.cancelPayment(paymentId);
        return ResponseEntity.ok("결제 취소 완료");

    }


    @Data
    public static class TossApproveResponse {
        private String orderId;
        private String paymentKey;
        private Integer totalAmount;
        private String approveNo;
        private String method;
        // 필요한 필드만 선언하면 됨
    }
}
