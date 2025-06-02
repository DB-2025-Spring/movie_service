package com.dbs.movieservice.repository.member;

import com.dbs.movieservice.domain.member.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerInputId(String customerInputId);
    Boolean existsByCustomerInputId(String customerInputId);
    
    // 비회원 관련 메서드 추가
    Optional<Customer> findByPhone(String phone);
    Boolean existsByPhone(String phone);
    
    // 비회원 ID 생성을 위한 메서드 - Oracle 21c의 FETCH FIRST 구문 사용 (SQL 표준)
    @Query(value = "SELECT customer_input_id FROM customer " +
                   "WHERE customer_input_id LIKE :prefix || '%' " +
                   "ORDER BY customer_input_id DESC " +
                   "FETCH FIRST 1 ROW ONLY", nativeQuery = true)
    Optional<String> findLatestGuestIdByPrefix(@Param("prefix") String prefix);
}
