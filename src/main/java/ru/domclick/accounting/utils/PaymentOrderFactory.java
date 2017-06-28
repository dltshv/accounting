package ru.domclick.accounting.utils;

import ru.domclick.accounting.entity.ExternalPaymentOrder;
import ru.domclick.accounting.entity.InternalPaymentOrder;
import ru.domclick.accounting.entity.PaymentOrder;

import java.math.BigDecimal;

/**
 * Created by dmitry on 27.06.17
 */
public class PaymentOrderFactory {

    public static ExternalPaymentOrder getDepositPaymentOrder(Long accountTo, BigDecimal amount) {
        ExternalPaymentOrder order = new ExternalPaymentOrder();
        order.setType(PaymentOrder.TransactionType.DEPOSIT);
        order.setAccount(accountTo);
        order.setAmount(amount);
        return order;
    }

    public static ExternalPaymentOrder getWithdrawPaymentOrder(Long accountFrom, BigDecimal amount) {
        ExternalPaymentOrder order = new ExternalPaymentOrder();
        order.setType(PaymentOrder.TransactionType.WITHDRAW);
        order.setAccount(accountFrom);
        order.setAmount(amount);
        return order;
    }

    public static InternalPaymentOrder getInternalPaymentOrder(Long accountFrom, Long accountTo, BigDecimal amount) {
        InternalPaymentOrder order = new InternalPaymentOrder();
        order.setType(PaymentOrder.TransactionType.INTERNAL);
        order.setToAccount(accountTo);
        order.setFromAccount(accountFrom);
        order.setAmount(amount);
        return order;
    }
}
