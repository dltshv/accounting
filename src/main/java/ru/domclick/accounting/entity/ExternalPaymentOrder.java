package ru.domclick.accounting.entity;

/**
 * Created by dmitry on 27.06.17
 */
public class ExternalPaymentOrder extends PaymentOrder {
    private Long account;

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
        this.account = account;
    }
}
