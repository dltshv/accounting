package ru.domclick.accounting.repository;

import org.springframework.data.repository.CrudRepository;
import ru.domclick.accounting.entity.UserEntity;

/**
 * Created by dmitry on 28.06.17
 */
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findOneByApiKey(String apiKey);
}
