package ru.domclick.accounting.entity;

import java.math.BigDecimal;

/**
 * Created by dmitry on 27.06.17
 */
public class PaymentResult {
    private Long account;
    private BigDecimal amountBefore;
    private BigDecimal amountAfter;
    private PaymentOrder.TransactionType type;

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    public BigDecimal getAmountBefore() {
        return amountBefore;
    }

    public void setAmountBefore(BigDecimal amountBefore) {
        this.amountBefore = amountBefore;
    }

    public BigDecimal getAmountAfter() {
        return amountAfter;
    }

    public void setAmountAfter(BigDecimal amountAfter) {
        this.amountAfter = amountAfter;
    }

    public PaymentOrder.TransactionType getType() {
        return type;
    }

    public void setType(PaymentOrder.TransactionType type) {
        this.type = type;
    }
}
