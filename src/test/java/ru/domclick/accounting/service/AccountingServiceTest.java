package ru.domclick.accounting.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.domclick.accounting.entity.AccountEntity;
import ru.domclick.accounting.entity.PaymentOrder;
import ru.domclick.accounting.entity.PaymentResult;
import ru.domclick.accounting.entity.UserEntity;
import ru.domclick.accounting.repository.AccountRepository;
import ru.domclick.accounting.repository.UserRepository;
import ru.domclick.accounting.utils.PaymentOrderFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by dmitry on 27.06.17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountingServiceTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountingService service;

    private List<AccountEntity> accountEntityList;
    private List<UserEntity> userEntityList;

    @Before
    public void setUp() {

        UserEntity userOne = new UserEntity();
        userOne.setName("U1");
        userOne.setApiKey("KEY1");
        userOne = userRepository.save(userOne);
        UserEntity userTwo = new UserEntity();
        userTwo.setName("U2");
        userTwo.setApiKey("KEY2");
        userTwo = userRepository.save(userTwo);
        userEntityList = new ArrayList<>();
        userEntityList.add(userOne);
        userEntityList.add(userTwo);

        AccountEntity accountOne = new AccountEntity();
        accountOne.setAmount(BigDecimal.ONE);
        accountOne.setUser(userOne);
        accountOne = accountRepository.save(accountOne);
        AccountEntity accountTwo = new AccountEntity();
        accountTwo.setAmount(BigDecimal.TEN);
        accountTwo.setUser(userOne);
        accountTwo = accountRepository.save(accountTwo);
        AccountEntity accountThree = new AccountEntity();
        accountThree.setAmount(BigDecimal.TEN);
        accountThree.setUser(userTwo);
        accountThree = accountRepository.save(accountThree);

        accountEntityList = new ArrayList<>();
        accountEntityList.add(accountOne);
        accountEntityList.add(accountTwo);
        accountEntityList.add(accountThree);

    }

    @Test
    public void testDeposit() throws Exception {

        AccountEntity account = accountEntityList.get(0);
        BigDecimal amount = BigDecimal.TEN;

        PaymentResult expectedResult = new PaymentResult();
        expectedResult.setAccount(account.getId());
        expectedResult.setAmountBefore(BigDecimal.ONE);
        expectedResult.setAmountAfter(BigDecimal.valueOf(11L));
        expectedResult.setType(PaymentOrder.TransactionType.DEPOSIT);

        PaymentResult actualResult =
                service.deposit(account.getUser(), PaymentOrderFactory.getDepositPaymentOrder(account.getId(), amount));

        assertPaymentResultsEqual(expectedResult, actualResult);
    }

    @Test(expected = Exception.class)
    public void testDepositIllegalAmount() throws Exception {
        AccountEntity account = accountEntityList.get(0);
        BigDecimal amount = BigDecimal.valueOf(-10L);
        service.deposit(account.getUser(), PaymentOrderFactory.getDepositPaymentOrder(account.getId(), amount));
    }

    @Test
    public void testWithdraw() throws Exception {

        AccountEntity account = accountEntityList.get(1);
        BigDecimal amount = BigDecimal.ONE;

        PaymentResult expectedResult = new PaymentResult();
        expectedResult.setAccount(account.getId());
        expectedResult.setAmountBefore(BigDecimal.TEN);
        expectedResult.setAmountAfter(expectedResult.getAmountBefore().subtract(amount));
        expectedResult.setType(PaymentOrder.TransactionType.WITHDRAW);

        PaymentResult actualResult =
                service.withdraw(account.getUser(), PaymentOrderFactory.getWithdrawPaymentOrder(account.getId(), amount));

        assertPaymentResultsEqual(expectedResult, actualResult);
    }

    @Test(expected = Exception.class)
    public void testIllegalUser() throws Exception {
        BigDecimal amount = BigDecimal.ONE;
        UserEntity user = userEntityList.get(0);
        UserEntity wrongUser = userEntityList.get(1);
        List<AccountEntity> wrongAccounts = accountRepository.findByUser(wrongUser);
        if (Objects.nonNull(wrongAccounts) && !wrongAccounts.isEmpty()) {
            service.withdraw(user, PaymentOrderFactory.getWithdrawPaymentOrder(wrongAccounts.get(0).getId(), amount));
        }
    }

    @Test(expected = Exception.class)
    public void testWithdrawIllegalAmount() throws Exception {
        AccountEntity account = accountRepository.findOne(2L);
        BigDecimal amountBefore = account.getAmount();
        service.withdraw(account.getUser(),
                PaymentOrderFactory.getWithdrawPaymentOrder(account.getId(), amountBefore.add(BigDecimal.TEN)));
    }

    @Test
    public void testInternal() throws Exception {
        AccountEntity account = accountEntityList.get(0);
        AccountEntity accountEntity = accountRepository.findOne(account.getId());
        BigDecimal amount = BigDecimal.ONE;

        PaymentResult expectedResult = new PaymentResult();
        expectedResult.setAccount(account.getId());
        expectedResult.setAmountBefore(accountEntity.getAmount());
        expectedResult.setAmountAfter(expectedResult.getAmountBefore().subtract(amount));
        expectedResult.setType(PaymentOrder.TransactionType.INTERNAL);

        PaymentResult actualResult =
                service.transfer(account.getUser(), PaymentOrderFactory.getInternalPaymentOrder(
                        account.getId(), accountEntityList.get(1).getId(), amount));

        assertPaymentResultsEqual(expectedResult, actualResult);
    }

    @Test
    public void testCreateAccount() {
        UserEntity user = userEntityList.get(0);
        AccountEntity account = service.create(user);

        assertEquals(user.getName(), account.getUser().getName());
        assertEquals(user.getApiKey(), account.getUser().getApiKey());
        assertEquals(user.getId(), account.getUser().getId());
    }

    @Test
    public void testListAccounts() {
        UserEntity user = userEntityList.get(0);
        List<AccountEntity> expectedAccounts = accountEntityList.stream()
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
        List<AccountEntity> actualAccounts = service.list(user);
        assertEquals(actualAccounts.size(), expectedAccounts.size());
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private void assertPaymentResultsEqual(PaymentResult expected, PaymentResult actual) {
        assertTrue("Expected " + expected.getAmountBefore() + ", but got " + actual.getAmountBefore(),
                expected.getAmountBefore().compareTo(actual.getAmountBefore()) == 0);
        assertTrue("Expected " + expected.getAmountAfter() + ", but got " + actual.getAmountAfter(),
                expected.getAmountAfter().compareTo(actual.getAmountAfter()) == 0);
        assertEquals(expected.getAccount(), actual.getAccount());
        assertEquals(expected.getType(), actual.getType());
    }
}
