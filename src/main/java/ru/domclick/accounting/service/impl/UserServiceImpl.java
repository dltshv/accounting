package ru.domclick.accounting.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.domclick.accounting.entity.UserEntity;
import ru.domclick.accounting.exception.ServiceException;
import ru.domclick.accounting.repository.UserRepository;
import ru.domclick.accounting.service.UserService;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;

/**
 * Created by dmitry on 28.06.17
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    private SecureRandom random = new SecureRandom();

    @Override
    public UserEntity create(String name) {
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(13, "You must provide a username");
        }
        UserEntity user = new UserEntity();
        user.setName(name);
        user.setApiKey(new BigInteger(130, random).toString(32));
        user = userRepository.save(user);
        return user;
    }

    @Override
    public UserEntity getUserByApiKey(String apiKey) {
        UserEntity user = userRepository.findOneByApiKey(apiKey);
        if (Objects.isNull(user)) {
            throw new ServiceException(14, "User not found");
        }
        return user;
    }
}
