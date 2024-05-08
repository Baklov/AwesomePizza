package com.ivo.order.AwesomePizza.model.dto;

import com.ivo.order.AwesomePizza.model.OrderPizza;
import com.ivo.order.AwesomePizza.tools.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPizzaDTO {

    private Long orderId;
    private String orderCode;
    private OrderStatus status;
    private Double finalPrice;
    private List<PizzaDTO> pizzas;

    public static OrderPizzaDTO fromOrderPizza(OrderPizza orderPizza) {
        List<PizzaDTO> pizzaDTOs = orderPizza.getPizzas().stream()
                .map(PizzaDTO::fromPizza)
                .collect(Collectors.toList());

        return new OrderPizzaDTO(
                orderPizza.getOrderId(),
                orderPizza.getOrderCode(),
                orderPizza.getStatus(),
                orderPizza.getFinalPrice(),
                pizzaDTOs
        );
    }
}
