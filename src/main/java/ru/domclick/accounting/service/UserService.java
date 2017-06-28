package ru.domclick.accounting.service;

import ru.domclick.accounting.entity.UserEntity;

/**
 * Created by dmitry on 28.06.17
 */
public interface UserService {
    UserEntity create(String name);
    UserEntity getUserByApiKey(String apiKey);
}
