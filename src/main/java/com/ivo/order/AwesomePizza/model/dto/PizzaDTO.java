package com.ivo.order.AwesomePizza.model.dto;

import com.ivo.order.AwesomePizza.model.Pizza;
import com.ivo.order.AwesomePizza.tools.OrderPizzaStatus;
import com.ivo.order.AwesomePizza.tools.OrderStatus;
import com.ivo.order.AwesomePizza.tools.PizzaCrustEnum;
import com.ivo.order.AwesomePizza.tools.PizzaSizeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PizzaDTO {

    private Long pizzaId;
    private PizzaSizeEnum size;
    private PizzaCrustEnum crustType;
    private Double price;
    private OrderPizzaStatus status;
    private List<ToppingDTO> toppings;

    public static PizzaDTO fromPizza(Pizza pizza) {
        List<ToppingDTO> toppingDTOs = pizza.getToppings().stream()
                .map(ToppingDTO::fromTopping)
                .collect(Collectors.toList());

        return new PizzaDTO(
                pizza.getPizzaId(),
                pizza.getSize(),
                pizza.getCrustType(),
                pizza.getPrice(),
                pizza.getStatus(),
                toppingDTOs
        );
    }
}
