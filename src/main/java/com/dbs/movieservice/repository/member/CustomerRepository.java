package com.dbs.movieservice.repository.member;

import com.dbs.movieservice.domain.member.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerInputId(String customerInputId);
    Boolean existsByCustomerInputId(String customerInputId);
}