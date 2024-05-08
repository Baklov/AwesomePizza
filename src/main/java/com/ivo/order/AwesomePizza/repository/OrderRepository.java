package com.ivo.order.AwesomePizza.repository;

import com.ivo.order.AwesomePizza.model.OrderPizza;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderPizza, Long> {
    Optional<OrderPizza> findByOrderCode(String orderCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT min(o.orderCode) FROM OrderPizza  o WHERE o.status = 'START' Group BY o.status")
    Optional<String> findOldestPendingOrder();
}
