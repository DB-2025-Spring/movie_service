package com.dbs.movieservice.controller.thater;

import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.dto.ConfirmPaymentDTO;
import com.dbs.movieservice.service.ticketing.PaymentService;
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

    //todo cancel할때 권한조회 하기
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPayment(@RequestParam Long paymentId) {
        // 1. paymentId로 결제 정보 조회 (결제 상태 등 확인)
        Payment payment = paymentService.getPayment(paymentId); // 별도 조회 메서드가 없다면 findById 직접 호출해도 됨
        if (!"Approve".equalsIgnoreCase(payment.getPaymentStatus())) {
            return ResponseEntity.badRequest().body("아직 승인되지 않았거나 이미 취소된 결제입니다.");
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
