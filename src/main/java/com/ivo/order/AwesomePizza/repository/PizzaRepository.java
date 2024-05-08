package com.ivo.order.AwesomePizza.repository;

import com.ivo.order.AwesomePizza.model.Pizza;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface PizzaRepository extends JpaRepository<Pizza, Long>
{

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT min(o.pizzaId) FROM Pizza  o WHERE o.status = 'PENDING' ")
    Optional<Long> findOldestPendingPizza();
}
