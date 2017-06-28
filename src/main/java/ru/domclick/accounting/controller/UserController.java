package ru.domclick.accounting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.domclick.accounting.entity.UserEntity;
import ru.domclick.accounting.service.UserService;

/**
 * Created by dmitry on 28.06.17
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user/new", method = RequestMethod.POST)
    public UserEntity createUser(@RequestBody String name) {
        return userService.create(name);
    }
}
