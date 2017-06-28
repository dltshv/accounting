package ru.domclick.accounting.entity;

/**
 * Created by dmitry on 27.06.17
 */
public class InternalPaymentOrder extends PaymentOrder {
    private Long fromAccount;
    private Long toAccount;

    public Long getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Long fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Long getToAccount() {
        return toAccount;
    }

    public void setToAccount(Long toAccount) {
        this.toAccount = toAccount;
    }
}
