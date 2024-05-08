package com.ivo.order.AwesomePizza;

import com.ivo.order.AwesomePizza.repository.OrderRepository;
import com.ivo.order.AwesomePizza.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class AwesomePizzaApplicationTests {

	@Test
	void contextLoads() {
	}

	@InjectMocks
	private OrderService orderService;

	@Mock
	private OrderRepository orderRepository;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

}
