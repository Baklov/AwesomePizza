package com.ivo.order.AwesomePizza.controller;

import com.ivo.order.AwesomePizza.model.dto.OrderPizzaDTO;
import com.ivo.order.AwesomePizza.model.dto.PizzaDTO;
import com.ivo.order.AwesomePizza.model.dto.ToppingDTO;
import org.modelmapper.ModelMapper;
import com.ivo.order.AwesomePizza.exception.OrderNotFoundException;
import com.ivo.order.AwesomePizza.exception.InvalidStatusException;
import com.ivo.order.AwesomePizza.model.*;
import com.ivo.order.AwesomePizza.service.OrderService;
import com.ivo.order.AwesomePizza.tools.OrderPizzaStatus;
import com.ivo.order.AwesomePizza.tools.OrderStatus;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pizzaOrders")
public class OrderController {
    private final ModelMapper modelMapper;
    private final OrderService orderService;

    public OrderController(ModelMapper modelMapper, OrderService orderService) {
        this.modelMapper = modelMapper;
        this.orderService = orderService;
    }

    @PostMapping("")
    public ResponseEntity<OrderPizza> createOrder(@RequestBody(required = false)  OrderPizza pizza) {
        OrderPizza order = orderService.createOrder(pizza);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
    @GetMapping("")
    public ResponseEntity<List<OrderPizzaDTO>> getAllOrders() {
        List<OrderPizza> order = orderService.getAllOrder();
        List<OrderPizzaDTO> orderDTO = order.stream().map(o->convertToDto(o)).toList();

        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }

    @GetMapping("/{orderCode}")
    public ResponseEntity<?> getOrder(@PathVariable String orderCode) {
        try{
            OrderPizza order = orderService.getOrderByCode(orderCode);
            OrderPizzaDTO orderDTO = convertToDto(order);
            return new ResponseEntity<>(orderDTO, HttpStatus.OK);
        } catch ( OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order with code " + orderCode + " not found");
        }
    }


    @PostMapping("/{orderCode}/newPizza")
    public ResponseEntity<?> addPizzaToOrder(@PathVariable String orderCode, @RequestBody Pizza pizza) {
        try {
            OrderPizza order = orderService.addPizzaToOrder(orderCode, pizza);
            OrderPizzaDTO orderDTO = convertToDto(order);
            return ResponseEntity.ok(orderDTO);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/pizza/{pizzaId}")
    public ResponseEntity<?> delPizzaToOrder(@PathVariable Long pizzaId ) {
        try {
            OrderPizza order = orderService.delPizzaFromOrder(pizzaId);
            OrderPizzaDTO orderDTO = convertToDto(order);
            return ResponseEntity.ok(orderDTO);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @DeleteMapping("{orderCode}")
    public ResponseEntity<?> delOrder(@PathVariable String orderCode ) {
        try {
            OrderPizza order = orderService.deleteOrder(orderCode);
            OrderPizzaDTO orderDTO = convertToDto(order);
            return ResponseEntity.ok(orderDTO);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{pizzaId}/status_pizza/{newStatus}")
    public ResponseEntity<?> updateStatusePizzaToOrder(@PathVariable Long pizzaId, @PathVariable OrderPizzaStatus newStatus) {
        try{
            Pizza pizza = orderService.updatePizzaStatus(pizzaId,newStatus);
            PizzaDTO pizzaDTO = convertPizzaToDto(pizza);
            return ResponseEntity.ok(pizzaDTO);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/toppings")
    public ResponseEntity<?> getTopping() {
        try{
           List<Topping> topping = orderService.getAllToppings();

            return new ResponseEntity<>(topping, HttpStatus.OK);
        } catch ( OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Toppings  not found");
        }
    }

    @PostMapping("/topping")
    public ResponseEntity<?> setTopping(@RequestBody(required = true) Topping newTopping) {
        try{
            List<Topping> topping = orderService.createNewTopping(newTopping);
            return new ResponseEntity<>(topping, HttpStatus.OK);
        } catch ( OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Toppings  not found");
        }
    }

    @PostMapping("/nextOrder")
    public ResponseEntity<OrderPizzaDTO> getNextOrderForProcessing() {
        OrderPizza nextOrder = orderService.getNextOrderForProcessing();
        OrderPizzaDTO orderDTO = convertToDto(nextOrder);
        if (nextOrder != null) {
            return ResponseEntity.ok(orderDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/nextPizza")
    public ResponseEntity<PizzaDTO> getNextPizzaForProcessing() {
       Pizza nextPizza = orderService.getNextPizzaForProcessing();
        PizzaDTO pizzaDTO = convertPizzaToDto(nextPizza);
        if (nextPizza != null) {
            return ResponseEntity.ok(pizzaDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderCode}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String orderCode, @RequestParam OrderStatus newStatus) {
        try {
            OrderPizza updatedOrderPizza = orderService.updateOrderStatus(orderCode, newStatus);
            OrderPizzaDTO orderDTO = convertToDto(updatedOrderPizza);
            return ResponseEntity.ok(orderDTO);
        } catch (InvalidStatusException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid status transition: " + e.getMessage());
        } catch (OrderNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Order with code " + orderCode + " not found");
        }
    }

    public OrderPizzaDTO convertToDto(OrderPizza orderPizza) {
        OrderPizzaDTO orderPizzaDTO = modelMapper.map(orderPizza, OrderPizzaDTO.class);
        List<PizzaDTO> pizzaDTOs = new ArrayList<>();
        for (Pizza pizza : orderPizza.getPizzas()) {
            PizzaDTO pizzaDTO = convertPizzaToDto(pizza);
            pizzaDTOs.add(pizzaDTO);
        }
        orderPizzaDTO.setPizzas(pizzaDTOs);
        return orderPizzaDTO;
    }

    private PizzaDTO convertPizzaToDto(Pizza pizza) {
        PizzaDTO pizzaDTO = modelMapper.map(pizza, PizzaDTO.class);
        List<ToppingDTO> toppingDTOs = new ArrayList<>();
        for (Topping topping : pizza.getToppings()) {
            ToppingDTO toppingDTO = modelMapper.map(topping, ToppingDTO.class);
            toppingDTOs.add(toppingDTO);
        }
        pizzaDTO.setToppings(toppingDTOs);
        return pizzaDTO;
    }
}
