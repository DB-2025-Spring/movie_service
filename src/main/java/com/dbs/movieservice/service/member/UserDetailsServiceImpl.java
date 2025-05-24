package com.dbs.movieservice.service.member;

import com.dbs.movieservice.domain.member.Role;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.repository.member.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String customerInputId) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + customerInputId));

        // Convert authority code to Role enum and then to SimpleGrantedAuthority
        Role role = Role.fromCode(customer.getAuthority());
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());

        return new org.springframework.security.core.userdetails.User(
                customer.getCustomerInputId(),
                customer.getCustomerPw(),
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                Collections.singletonList(authority));
    }
} 
