package ru.domclick.accounting.entity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by dmitry on 27.06.17
 */
@Entity
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account")
    private Long account;
    @Enumerated(EnumType.STRING)
    private PaymentOrder.TransactionType type;
    @Column(name = "amount_after")
    private BigDecimal amountAfter;
    @Column(name = "amount_before")
    private BigDecimal amountBefore;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    public PaymentOrder.TransactionType getType() {
        return type;
    }

    public void setType(PaymentOrder.TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmountAfter() {
        return amountAfter;
    }

    public void setAmountAfter(BigDecimal amountAfter) {
        this.amountAfter = amountAfter;
    }

    public BigDecimal getAmountBefore() {
        return amountBefore;
    }

    public void setAmountBefore(BigDecimal amountBefore) {
        this.amountBefore = amountBefore;
    }
}
