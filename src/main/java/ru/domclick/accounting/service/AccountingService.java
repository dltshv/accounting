package ru.domclick.accounting.service;

import ru.domclick.accounting.entity.*;

import java.util.List;

/**
 * Created by dmitry on 27.06.17
 */
public interface AccountingService {
    PaymentResult deposit(UserEntity user, ExternalPaymentOrder payment) throws Exception;
    PaymentResult withdraw(UserEntity user, ExternalPaymentOrder payment) throws Exception;
    PaymentResult transfer(UserEntity user, InternalPaymentOrder payment) throws Exception;
    List<AccountEntity> list(UserEntity user);
    AccountEntity create(UserEntity user);
}
