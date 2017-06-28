package ru.domclick.accounting.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import ru.domclick.accounting.entity.*;
import ru.domclick.accounting.exception.ServiceError;
import ru.domclick.accounting.exception.ServiceException;
import ru.domclick.accounting.service.AccountingService;
import ru.domclick.accounting.service.UserService;

import java.util.List;

/**
 * Created by dmitry on 27.06.17
 */
@RestController
public class AccountingController {

    @Autowired
    private AccountingService accountingService;
    @Autowired
    private UserService userService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/account/deposit", method = RequestMethod.POST)
    public PaymentResult deposit(@RequestBody ExternalPaymentOrder order,
                                 @RequestHeader(name = "X-token") String token) throws Exception{
        return accountingService.deposit(userService.getUserByApiKey(token), order);
    }

    @RequestMapping(value = "/account/withdraw", method = RequestMethod.POST)
    public PaymentResult withdraw(@RequestBody ExternalPaymentOrder order,
                                  @RequestHeader(name = "X-token") String token) throws Exception {
        return accountingService.withdraw(userService.getUserByApiKey(token), order);
    }

    @RequestMapping(value = "/account/internal", method = RequestMethod.POST)
    public PaymentResult internal(@RequestBody InternalPaymentOrder order,
                                  @RequestHeader(name = "X-token") String token) throws Exception {
        return accountingService.transfer(userService.getUserByApiKey(token), order);
    }

    @RequestMapping(value = "/account/list", method = RequestMethod.GET)
    public List<AccountEntity> listAccounts(@RequestHeader(name = "X-token") String token) {
        UserEntity user = userService.getUserByApiKey(token);
        return accountingService.list(user);
    }

    @RequestMapping(value = "/account/new", method = RequestMethod.POST)
    public AccountEntity newAccount(@RequestHeader(name = "X-token") String token)  {
        UserEntity user = userService.getUserByApiKey(token);
        return accountingService.create(user);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ServiceError handleException(Exception e) {
        if (e instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) e;
            logger.error("CODE " + serviceException.error.getCode() + ", MESSAGE " + serviceException.error.getMsg());
            return serviceException.error;
        } else if (e instanceof HttpMessageNotReadableException) {
            return new ServiceError(91, "Invalid Request");
        } else {
            e.printStackTrace();
            return new ServiceError(92, "Internal Server Error");
        }
    }
}
