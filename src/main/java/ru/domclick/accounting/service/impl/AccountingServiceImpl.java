package ru.domclick.accounting.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.domclick.accounting.entity.*;
import ru.domclick.accounting.exception.ServiceException;
import ru.domclick.accounting.repository.AccountRepository;
import ru.domclick.accounting.repository.PaymentRepository;
import ru.domclick.accounting.service.AccountingService;
import ru.domclick.accounting.utils.PaymentOrderFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Created by dmitry on 27.06.17
 */
@Service
public class AccountingServiceImpl implements AccountingService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PaymentRepository paymentRepository;

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public PaymentResult deposit(UserEntity user, ExternalPaymentOrder externalPaymentOrder) throws Exception {
        return prepareAndProcess(user, externalPaymentOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public PaymentResult withdraw(UserEntity user, ExternalPaymentOrder externalPaymentOrder) throws Exception {;
        return prepareAndProcess(user, externalPaymentOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public PaymentResult transfer(UserEntity user, InternalPaymentOrder payment) throws Exception {

        validateInternal(payment);
        ExternalPaymentOrder deposit = PaymentOrderFactory.getDepositPaymentOrder(payment.getToAccount(), payment.getAmount());
        ExternalPaymentOrder withdraw = PaymentOrderFactory.getWithdrawPaymentOrder(payment.getFromAccount(), payment.getAmount());

        prepareAndProcess(user, deposit);
        PaymentResult result = prepareAndProcess(user, withdraw);
        result.setType(payment.getType());

        return result;
    }

    private PaymentResult prepareAndProcess(UserEntity user, ExternalPaymentOrder externalPaymentOrder) throws Exception {
        AccountEntity balanceBefore = accountRepository.findOne(externalPaymentOrder.getAccount());
        validateOwnership(user, balanceBefore);
        PaymentEntity paymentEntity = process(balanceBefore, externalPaymentOrder);
        return convertToPaymentResult(paymentEntity);
    }

    @Override
    public List<AccountEntity> list(UserEntity user) {
        return accountRepository.findByUser(user);
    }

    @Override
    public AccountEntity create(UserEntity user) {
        AccountEntity account = new AccountEntity();
        account.setUser(user);
        return accountRepository.save(account);
    }

    private PaymentEntity process(AccountEntity balanceBefore, ExternalPaymentOrder externalPaymentOrder) throws Exception {

        validateOrder(balanceBefore, externalPaymentOrder);
        BigDecimal amountBefore = balanceBefore.getAmount();

        switch (externalPaymentOrder.getType()) {
            case DEPOSIT:
                validateDeposit(balanceBefore, externalPaymentOrder);
                balanceBefore.setAmount(balanceBefore.getAmount().add(externalPaymentOrder.getAmount()));
                break;
            case WITHDRAW:
                validateWithdraw(balanceBefore, externalPaymentOrder);
                balanceBefore.setAmount(balanceBefore.getAmount().subtract(externalPaymentOrder.getAmount()));
                break;
        }
        AccountEntity balanceAfter = accountRepository.save(balanceBefore);

        PaymentEntity payment = new PaymentEntity();
        payment.setAmountAfter(balanceAfter.getAmount());
        payment.setAccount(externalPaymentOrder.getAccount());
        payment.setType(externalPaymentOrder.getType());
        payment.setAmountBefore(amountBefore);

        return paymentRepository.save(payment);
    }

    private void validateOrder(AccountEntity balance, ExternalPaymentOrder externalPaymentOrder) throws Exception {
        if (Objects.isNull(balance)) {
            throw new ServiceException(1, "No such account");
        }
        if (Objects.isNull(externalPaymentOrder.getAccount()) || Objects.isNull(externalPaymentOrder.getAmount())) {
            throw new ServiceException(2, "Account and amount cannot be empty");
        }
        if (BigDecimal.ZERO.compareTo(externalPaymentOrder.getAmount()) > 0) {
            throw new ServiceException(3, "Amount cannot be less then zero");
        }
        if (Objects.isNull(externalPaymentOrder.getType())) {
            throw new ServiceException(4, "Payment type cannot be empty");
        }
    }

    private void validateWithdraw(AccountEntity balance, ExternalPaymentOrder externalPaymentOrder) throws Exception {
        if (!PaymentOrder.TransactionType.WITHDRAW.equals(externalPaymentOrder.getType())) {
            throw new ServiceException(5, "Payment type must be WITHDRAW");
        }
        if (balance.getAmount().compareTo(externalPaymentOrder.getAmount()) < 0) {
            throw new ServiceException(6, "You cannot withdraw more then " + balance.getAmount().toString());
        }
    }

    private void validateDeposit(AccountEntity balance, ExternalPaymentOrder externalPaymentOrder) throws Exception {
        if (!PaymentOrder.TransactionType.DEPOSIT.equals(externalPaymentOrder.getType())) {
            throw new ServiceException(7, "Payment type must be DEPOSIT");
        }
    }

    private void validateInternal(InternalPaymentOrder payment) {
        if (Objects.isNull(payment.getFromAccount()) || Objects.isNull(payment.getToAccount())) {
            throw new ServiceException(8, "You must provide both from and to account");
        }
        if (payment.getFromAccount().compareTo(payment.getToAccount()) == 0) {
            throw new ServiceException(9, "From and to account must not be the same");
        }
        if (Objects.isNull(accountRepository.findOne(payment.getFromAccount()))) {
            throw new ServiceException(15, "Source account does not exist");
        }
        if (Objects.isNull(accountRepository.findOne(payment.getToAccount()))) {
            throw new ServiceException(16, "Target account does not exist");
        }
    }

    private void validateOwnership(UserEntity user, AccountEntity account) {
        if (Objects.isNull(user)) {
            throw new ServiceException(10, "Invalid user");
        }
        if (Objects.isNull(account)) {
            throw new ServiceException(11, "Invalid account");
        }
        if (Objects.nonNull(account.getUser()) && !user.getId().equals(account.getUser().getId())) {
            throw new ServiceException(12, "Account is not yours");
        }
    }

    private PaymentResult convertToPaymentResult(PaymentEntity paymentEntity) {
        PaymentResult paymentResult = new PaymentResult();
        paymentResult.setType(paymentEntity.getType());
        paymentResult.setAccount(paymentEntity.getAccount());
        paymentResult.setAmountBefore(paymentEntity.getAmountBefore());
        paymentResult.setAmountAfter(paymentEntity.getAmountAfter());
        return paymentResult;
    }
}
