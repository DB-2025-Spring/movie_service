package com.dbs.movieservice.repository.member;

import com.dbs.movieservice.domain.member.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
