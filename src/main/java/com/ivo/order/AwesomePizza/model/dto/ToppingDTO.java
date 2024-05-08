package com.ivo.order.AwesomePizza.model.dto;

import com.ivo.order.AwesomePizza.model.Topping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToppingDTO {

    private Long toppingId;
    private String description;
    private Double price;

    public static ToppingDTO fromTopping(Topping topping) {
        return new ToppingDTO(
                topping.getToppingId(),
                topping.getDescription(),
                topping.getPrice()
        );
    }
}
