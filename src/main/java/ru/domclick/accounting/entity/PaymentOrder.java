package ru.domclick.accounting.entity;

import java.math.BigDecimal;

/**
 * Created by dmitry on 27.06.17
 */
public abstract class PaymentOrder {
    private BigDecimal amount;
    private TransactionType type;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public enum TransactionType {
        DEPOSIT, WITHDRAW, INTERNAL
    }
}
