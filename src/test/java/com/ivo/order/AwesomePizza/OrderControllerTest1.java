package com.ivo.order.AwesomePizza;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivo.order.AwesomePizza.model.Pizza;
import com.ivo.order.AwesomePizza.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class OrderControllerTest1 {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private OrderService orderService;

    private List<Pizza> pizzas;
    @BeforeEach
    public void setupTestData() throws Exception {
        createSampleOrder();
    }

    private void createSampleOrder() throws Exception {
        String orderJson1 = "{\n" +
                "  \"orderCode\": \"ABC123\",\n" +
                "  \"status\": \"START\",\n" +
                "  \"finalPrice\": 0.0,\n" +
                "  \"pizzas\": [\n" +
                "    {\n" +
                "      \"size\": \"LARGE\",\n" +
                "      \"crustType\": \"THIN\",\n" +
                "      \"toppings\": [{\n" +
                "       \"topping_id\": 1,\n" +
                "      \"description\": \"Pepperoni\",\n" +
                "      \"price\": 1.0\n" +
                "    }],\n" +
                "      \"status\": \"PENDING\"\n" +
                "    },\n" +
                "    {\n" +
                "       \"size\": \"MEDIUM\",\n" +
                "      \"crustType\": \"HAND_TOSSED\",\n" +
                "      \"toppings\": [{\n" +
                "       \"topping_id\": 1,\n" +
                "      \"description\": \"Pepperoni\",\n" +
                "      \"price\": 1.0\n" +
                "    },\n" +
                "    {\n" +
                "      \"description\": \"Cheese\",\n" +
                "      \"price\": 1.5\n" +
                "    }],\n" +
                "      \"status\": \"PENDING\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String orderJson2 = "{\n" +
                "  \"orderCode\": \"ABC123\",\n" +
                "  \"status\": \"START\",\n" +
                "  \"finalPrice\": 0.0,\n" +
                "  \"pizzas\": [\n" +
                "    {\n" +
                "      \"size\": \"LARGE\",\n" +
                "      \"crustType\": \"THIN\",\n" +
                "      \"toppings\": [{\n" +
                "       \"topping_id\": 1,\n" +
                "      \"description\": \"Pepperoni\",\n" +
                "      \"price\": 1.0\n" +
                "    }],\n" +
                "      \"status\": \"PENDING\"\n" +
                "    },\n" +
                "    {\n" +
                "       \"size\": \"MEDIUM\",\n" +
                "      \"crustType\": \"HAND_TOSSED\",\n" +
                "      \"toppings\": [{\n" +
                "       \"topping_id\": 1,\n" +
                "      \"description\": \"Pepperoni\",\n" +
                "      \"price\": 1.0\n" +
                "    },\n" +
                "    {\n" +
                "      \"description\": \"Cheese\",\n" +
                "      \"price\": 1.5\n" +
                "    }],\n" +
                "      \"status\": \"PENDING\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/pizzaOrders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson1))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/pizzaOrders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson2))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
    @Test
    public void testGetNextPizzaForProcessing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/pizzaOrders/nextPizza")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    public void testUpdateStatusPizzaToOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/pizzaOrders/1/status_pizza/FINISHED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    public void testDeletePizzaToOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/pizzaOrders/pizza/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
