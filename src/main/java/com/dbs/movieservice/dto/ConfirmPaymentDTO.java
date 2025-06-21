package com.dbs.movieservice.dto;

import com.dbs.movieservice.domain.ticketing.Payment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmPaymentDTO {
    private Long paymentId;
    private String customerInputId;
}
