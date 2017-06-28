package ru.domclick.accounting.repository;

import org.springframework.data.repository.CrudRepository;
import ru.domclick.accounting.entity.PaymentEntity;

/**
 * Created by dmitry on 27.06.17
 */
public interface PaymentRepository extends CrudRepository<PaymentEntity, Long> {
}
