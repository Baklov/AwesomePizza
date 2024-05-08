package com.ivo.order.AwesomePizza.model;
import com.ivo.order.AwesomePizza.tools.OrderPizzaStatus;
import com.ivo.order.AwesomePizza.tools.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderPizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;
    private String orderCode;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private Double finalPrice;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER , mappedBy = "order")
    private List<Pizza> pizzas = new ArrayList<>();
}
