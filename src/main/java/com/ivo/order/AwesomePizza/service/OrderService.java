package com.ivo.order.AwesomePizza.service;

import com.ivo.order.AwesomePizza.exception.InvalidStatusException;
import com.ivo.order.AwesomePizza.model.Pizza;
import com.ivo.order.AwesomePizza.model.Topping;
import com.ivo.order.AwesomePizza.repository.PizzaRepository;
import com.ivo.order.AwesomePizza.repository.ToppingRepository;
import com.ivo.order.AwesomePizza.tools.OrderPizzaStatus;
import com.ivo.order.AwesomePizza.exception.OrderNotFoundException;
import com.ivo.order.AwesomePizza.model.OrderPizza;
import com.ivo.order.AwesomePizza.repository.OrderRepository;
import com.ivo.order.AwesomePizza.tools.OrderStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PizzaRepository pizzaRepository;
    private final ToppingRepository toppingRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, PizzaRepository pizzaRepository, ToppingRepository toppingRepository) {
        this.orderRepository = orderRepository;
        this.pizzaRepository = pizzaRepository;
        this.toppingRepository = toppingRepository;
    }
    @Transactional
    public OrderPizza getNextOrderForProcessing() {
        String nextOrderCode = orderRepository.findOldestPendingOrder()
                .orElseThrow(() -> new OrderNotFoundException("No pending order found"));
        OrderPizza nextOrder = orderRepository.findByOrderCode(nextOrderCode).
                orElseThrow(() -> new OrderNotFoundException("Order with code " + nextOrderCode + " not found"));
        nextOrder.getPizzas().stream().forEach(
                a->a.setStatus(OrderPizzaStatus.PREPARING));

        return orderRepository.save(nextOrder);
    }
    @Transactional
    public OrderPizza createOrder(OrderPizza orderPizzas) {
        OrderPizza order = new OrderPizza();
        order.setOrderCode(generateOrderCode());
        order.setStatus(OrderStatus.START);
        order.setFinalPrice(0.0);
        OrderPizza newOrder = orderRepository.save(order);

        if (orderPizzas != null && orderPizzas.getPizzas() != null) {
            orderPizzas.getPizzas().forEach(pizza -> {
                pizza.setOrder(newOrder);
                if (pizza.getToppings() != null) {
                    List<Topping> uniqueToppings = pizza.getToppings().stream()
                            .map(topping -> {
                                Topping existingTopping = toppingRepository.findByDescriptionAndPrice(topping.getDescription(), topping.getPrice());
                                return existingTopping != null ? existingTopping : topping;
                            })
                            .collect(Collectors.toList());
                    pizza.setToppings(uniqueToppings);
                }
                pizzaRepository.save(pizza);
            });
        } else {
            order.setPizzas(new ArrayList<>());
        }

        return newOrder;
    }

    @Transactional
    public OrderPizza addPizzaToOrder(String orderCode, Pizza pizza) {
        OrderPizza order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new OrderNotFoundException("Order with code " + orderCode + " not found"));

        for (Topping topping : pizza.getToppings()) {
            Topping existingTopping = toppingRepository.findByDescriptionAndPrice(topping.getDescription(), topping.getPrice());
            if (existingTopping == null) {
                toppingRepository.save(topping);
            }
        }
        pizza.setOrder(order);
        pizzaRepository.save(pizza);
        order.getPizzas().add(pizza);
        return order;
    }
    @Transactional
    public OrderPizza updateOrderStatus(String orderCode, OrderStatus newStatus) {
        OrderPizza order = orderRepository.findByOrderCode(orderCode).
                orElseThrow(() -> new OrderNotFoundException("Order with code " + orderCode + " not found"));
        if (!isValidOrderStatusTransition(order.getStatus(), newStatus)) {
            throw new InvalidStatusException("Invalid status transition from " + order.getStatus() + " to " + newStatus);
        }
        long countFinishedPizzasForOrder = order.getPizzas().stream().filter(p->p.getStatus()==OrderPizzaStatus.FINISHED).count();
        long countPreparingizzasForOrder = order.getPizzas().stream().filter(p->p.getStatus()==OrderPizzaStatus.PREPARING).count();
        long countPendingPizzasForOrder = order.getPizzas().stream().filter(p->p.getStatus()==OrderPizzaStatus.PENDING).count();
        long countCanceledPizzasForOrder = order.getPizzas().stream().filter(p->p.getStatus()==OrderPizzaStatus.CANCELLED).count();
        long countPizzasForOrder=order.getPizzas().size();

        if (countPreparingizzasForOrder!=0)
            throw new InvalidStatusException("There is preparing pizzas and there is impossible to change status " + order.getStatus() + " to " + newStatus);


        order.setStatus(newStatus);
        order.getPizzas().forEach(pizza -> {
            if (OrderPizzaStatus.CANCELLED.toString()==newStatus.toString())
                pizza.setStatus(OrderPizzaStatus.CANCELLED);
            if (OrderPizzaStatus.FINISHED.toString()==newStatus.toString())
                pizza.setStatus(OrderPizzaStatus.FINISHED);
        });

        return orderRepository.save(order);
    }

    private boolean isValidStatusTransition(OrderPizzaStatus status, OrderPizzaStatus newStatus) {
        var isCorrectNewStatus = switch (status) {
            case PENDING -> newStatus == OrderPizzaStatus.PREPARING || newStatus == OrderPizzaStatus.FINISHED || newStatus == OrderPizzaStatus.CANCELLED;
            case PREPARING -> newStatus == OrderPizzaStatus.FINISHED || newStatus == OrderPizzaStatus.CANCELLED;
            case FINISHED -> false; // Once the order is finished, it cannot transition to another status
            default -> newStatus == OrderPizzaStatus.CANCELLED; // Allow any status to transition to CANCELLED
        };
        return  isCorrectNewStatus;
    }
    private boolean isValidOrderStatusTransition(OrderStatus status, OrderStatus newStatus) {
        var isCorrectNewStatus = switch (status) {
            case START -> newStatus == OrderStatus.FINISHED || newStatus == OrderStatus.CANCELLED;
            case FINISHED -> false;
            default -> newStatus == OrderStatus.CANCELLED;
        };
        return  isCorrectNewStatus;
    }

    public OrderPizza getOrderByCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new OrderNotFoundException("Order with code " + orderCode + " not found"));
    }
    public List<OrderPizza> getAllPizzas() {
        return orderRepository.findAll();
    }

    private String generateOrderCode() {
        return UUID.randomUUID().toString();
    }

    public List<Topping> getAllToppings() {
        return toppingRepository.findAll();
    }
    @Transactional
    public List<Topping> createNewTopping( Topping newTopping) {
        Optional<Topping> existingTopping = toppingRepository.findByDescription(newTopping.getDescription());
        if (existingTopping.isEmpty()) {
            toppingRepository.save(newTopping);
        }

        return toppingRepository.findAll();
    }

    public List<OrderPizza> getAllOrder() {
        return orderRepository.findAll();
    }

    @Transactional
    public Pizza updatePizzaStatus(Long pizzaId, OrderPizzaStatus newStatus) {
        Pizza pizza = pizzaRepository.findById(pizzaId).get();
        if (!isValidStatusTransition(pizza.getStatus(), newStatus)) {
            throw new InvalidStatusException("Invalid status transition from " + pizza.getStatus() + " to " + newStatus);
        }
        pizza.setStatus(newStatus);
        Pizza savePizza =pizzaRepository.save(pizza);
        OrderPizza order =savePizza.getOrder();
        checkForFinishedOrder(order);

        return pizzaRepository.save(savePizza);
    }
    @Transactional
    public Pizza getNextPizzaForProcessing() {
        Long nextId = pizzaRepository.findOldestPendingPizza()
                .orElseThrow(() -> new OrderNotFoundException("No pending order found"));
        Pizza oldestPizza = pizzaRepository.findById(nextId).get();
        oldestPizza.setStatus(OrderPizzaStatus.PREPARING);
        Pizza savePizza=pizzaRepository.save(oldestPizza);
        OrderPizza order =savePizza.getOrder();
        checkForFinishedOrder(order);
        return savePizza;
    }
    public void checkForFinishedOrder(OrderPizza order ) {
        long countPendingPizza = order.getPizzas().stream().filter(s->s.getStatus()==OrderPizzaStatus.FINISHED).count();
        if (countPendingPizza==order.getPizzas().size()){
            order.setStatus(OrderStatus.FINISHED);
            orderRepository.save(order);
        }
    }
    public OrderPizza delPizzaFromOrder(Long pizzaId) {
        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new OrderNotFoundException("Pizza with ID " + pizzaId + " not found"));
        OrderPizza order = pizza.getOrder();
        if (order == null) {
            throw new OrderNotFoundException("Order for pizza with ID " + pizzaId + " not found");
        }
        order.getPizzas().removeIf(p -> p.getPizzaId().equals(pizzaId));

        pizza.setOrder(null);

        pizzaRepository.delete(pizza);
        return order;
    }

    @Transactional
    public OrderPizza deleteOrder(String orderCode) {
        OrderPizza order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderCode + " not found"));

        order.getPizzas().forEach(pizza -> {
            pizza.setOrder(null);
            pizzaRepository.delete(pizza);
        });

        orderRepository.delete(order);
        return order;
    }
}
