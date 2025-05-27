package com.dbs.movieservice.domain.member;

import com.dbs.movieservice.domain.movie.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "CUSTOMER")
public class Customer {
    @Id
    @SequenceGenerator(name="customer_seq",sequenceName = "customer_seq",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @Column(name="customer_id")
    private Long customerId;

    @Column(name = "customer_input_id", length = 100, nullable = false)
    private String customerInputId;

    @Column(name = "customer_pw", length = 100, nullable = false)
    private String customerPw;

    @Column(name = "customer_name", length = 100, nullable = false)
    private String customerName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "authority", length = 1)
    private String authority;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "points")
    private Integer points;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private ClientLevel level;

/*
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
*/

}
