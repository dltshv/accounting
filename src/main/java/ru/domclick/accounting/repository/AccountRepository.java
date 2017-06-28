package ru.domclick.accounting.repository;

import org.springframework.data.repository.CrudRepository;
import ru.domclick.accounting.entity.AccountEntity;
import ru.domclick.accounting.entity.UserEntity;

import java.util.List;

/**
 * Created by dmitry on 27.06.17
 */
public interface AccountRepository extends CrudRepository<AccountEntity, Long> {
    List<AccountEntity> findByUser(UserEntity user);
}
