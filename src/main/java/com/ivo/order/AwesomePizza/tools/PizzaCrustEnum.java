package com.ivo.order.AwesomePizza.tools;

public enum PizzaCrustEnum
{
  THIN("thin"),
  HAND_TOSSED("hand-tossed"),
  DEEP_DISH("deep dish"),
  GLUTEN_FREE("gluten-free", 3.0);
  
  private String description;
  private Double price;
  
  private PizzaCrustEnum(String description){
    this(description, 0.0);
  }
  
  private PizzaCrustEnum(String description, Double price){
    this.description = description;
    this.price = price;
  }

  public String getDescription()
  {
    return description;
  }

  public Double getPrice()
  {
    return price;
  }
  
  
}
