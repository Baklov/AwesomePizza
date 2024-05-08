package com.ivo.order.AwesomePizza.repository;

import com.ivo.order.AwesomePizza.model.OrderPizza;
import com.ivo.order.AwesomePizza.model.Topping;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@Transactional
public interface ToppingRepository extends JpaRepository<Topping, Long>
{
    Topping findByDescriptionAndPrice(String description, double price);

    Optional<Topping>  findByDescription(String description);
}
