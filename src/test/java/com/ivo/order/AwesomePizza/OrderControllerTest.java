package com.ivo.order.AwesomePizza;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivo.order.AwesomePizza.controller.OrderController;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ivo.order.AwesomePizza.model.OrderPizza;
import com.ivo.order.AwesomePizza.model.Pizza;
import com.ivo.order.AwesomePizza.model.dto.OrderPizzaDTO;
import com.ivo.order.AwesomePizza.service.OrderService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;
    @Test
    public void testCreateOrder() {
        OrderService orderServiceMock = mock(OrderService.class);
        OrderController orderController = new OrderController(null, orderServiceMock);
        OrderPizza mockOrder = new OrderPizza();
        when(orderServiceMock.createOrder(any(OrderPizza.class))).thenReturn(mockOrder);
        ResponseEntity<OrderPizza> responseEntity = orderController.createOrder(mockOrder);
        verify(orderServiceMock).createOrder(mockOrder);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(mockOrder, responseEntity.getBody());
    }
    @Test
    public void testAddPizzaToOrder() {
        OrderService orderServiceMock = mock(OrderService.class);
        ModelMapper modelMapper = new ModelMapper();
        OrderController orderController = new OrderController(modelMapper, orderServiceMock);
        String orderCode = "123";
        Pizza mockPizza = new Pizza();
        OrderPizza mockOrder = new OrderPizza();
        when(orderServiceMock.addPizzaToOrder(orderCode, mockPizza)).thenReturn(mockOrder);
        ResponseEntity<?> responseEntity = orderController.addPizzaToOrder(orderCode, mockPizza);
        verify(orderServiceMock).addPizzaToOrder(orderCode, mockPizza);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof OrderPizzaDTO);
    }

}
