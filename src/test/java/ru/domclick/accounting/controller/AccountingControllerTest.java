package ru.domclick.accounting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.domclick.accounting.entity.AccountEntity;
import ru.domclick.accounting.entity.PaymentOrder;
import ru.domclick.accounting.entity.PaymentResult;
import ru.domclick.accounting.entity.UserEntity;
import ru.domclick.accounting.repository.AccountRepository;
import ru.domclick.accounting.repository.UserRepository;
import ru.domclick.accounting.utils.PaymentOrderFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by dmitry on 27.06.17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountingControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    private MockMvc mvc;
    private ObjectMapper om;
    private List<AccountEntity> accountEntityList;
    private List<UserEntity> userEntityList;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        om = new ObjectMapper();

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
        accountOne.setAmount(BigDecimal.TEN);
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
        BigDecimal depositAmount = BigDecimal.TEN;
        AccountEntity account = accountRepository.findOne(accountEntityList.get(0).getId());
        PaymentResult result = postOrder("/account/deposit",
                PaymentOrderFactory.getDepositPaymentOrder(account.getId(), depositAmount),
                account.getUser().getApiKey());

        assertEquals(account.getId(), result.getAccount());
        assertTrue(account.getAmount().compareTo(result.getAmountBefore()) == 0);
        assertTrue(account.getAmount().add(depositAmount).compareTo(result.getAmountAfter()) == 0);
    }

    @Test
    public void testDepositWithIllegalAmount() throws Exception {
        AccountEntity account = accountRepository.findOne(accountEntityList.get(0).getId());
        PaymentOrder order = PaymentOrderFactory.getDepositPaymentOrder(account.getId(), BigDecimal.valueOf(-10L));
        post("/account/deposit", order, account.getUser().getApiKey()).andExpect(status().is4xxClientError());
    }

    @Test
    public void testWithdraw() throws Exception {
        BigDecimal withdrawAmount = BigDecimal.ONE;
        AccountEntity account = accountRepository.findOne(accountEntityList.get(0).getId());
        PaymentResult result = postOrder("/account/withdraw",
                PaymentOrderFactory.getWithdrawPaymentOrder(account.getId(), withdrawAmount),
                account.getUser().getApiKey());

        assertEquals(account.getId(), result.getAccount());
        assertTrue(account.getAmount().compareTo(result.getAmountBefore()) == 0);
        assertTrue(account.getAmount().subtract(withdrawAmount).compareTo(result.getAmountAfter()) == 0);
    }

    @Test
    public void testWithdrawIllegalAmount() throws Exception {
        AccountEntity account = accountRepository.findOne(accountEntityList.get(0).getId());
        PaymentOrder order = PaymentOrderFactory.getWithdrawPaymentOrder(account.getId(), account.getAmount().add(BigDecimal.TEN));
        post("/account/withdraw", order, account.getUser().getApiKey()).andExpect(status().is4xxClientError());
    }

    @Test
    public void testWithdrawIllegalAccount() throws Exception {
        AccountEntity account = accountRepository.findOne(accountEntityList.get(0).getId());
        UserEntity wrongUser = userEntityList.get(1);
        PaymentOrder order = PaymentOrderFactory.getWithdrawPaymentOrder(account.getId(), account.getAmount().subtract(BigDecimal.ONE));
        post("/account/withdraw", order, wrongUser.getApiKey()).andExpect(status().is4xxClientError());
    }

    @Test
    public void testInternal() throws Exception {
        BigDecimal withdrawAmount = BigDecimal.ONE;
        AccountEntity fromAccount = accountRepository.findOne(accountEntityList.get(0).getId());
        AccountEntity toAccount = accountRepository.findOne(accountEntityList.get(1).getId());
        PaymentResult result = postOrder("/account/internal",
                PaymentOrderFactory.getInternalPaymentOrder(fromAccount.getId(), toAccount.getId(), withdrawAmount),
                fromAccount.getUser().getApiKey());

        assertEquals(fromAccount.getId(), result.getAccount());
        assertTrue(fromAccount.getAmount().compareTo(result.getAmountBefore()) == 0);
        assertTrue(fromAccount.getAmount().subtract(withdrawAmount).compareTo(result.getAmountAfter()) == 0);
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private ResultActions post(String url, Object order, String token) throws Exception {
        ResultActions actions = mvc.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .content(writeValue(order))
                .header("X-token", token)
                .contentType(MediaType.APPLICATION_JSON));
        return actions;
    }

    private PaymentResult postOrder(String url, Object order, String token) throws Exception {
        MvcResult result = post(url, order, token)
                .andExpect(status().isOk())
                .andReturn();
        return om.readValue(result.getResponse().getContentAsString(), PaymentResult.class);
    }

    private String writeValue(Object order) throws IOException {
        final StringWriter sw = new StringWriter();
        om.writeValue(sw, order);
        return sw.toString();
    }
}
