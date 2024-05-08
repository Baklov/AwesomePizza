package com.ivo.order.AwesomePizza.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ivo.order.AwesomePizza.tools.OrderPizzaStatus;
import com.ivo.order.AwesomePizza.tools.PizzaCrustEnum;
import com.ivo.order.AwesomePizza.tools.PizzaSizeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "pizzaId")

public class Pizza implements Comparable<Pizza>
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pizza_id")
  private Long pizzaId;

  private PizzaSizeEnum size;

  @Column(name = "crust_type")
  private PizzaCrustEnum crustType;

//  @JsonIgnoreProperties("toppings") // Ignore the 'toppings' property when serializing Pizza objects

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "pizza_topping", joinColumns=@JoinColumn(name = "pizza_id"), inverseJoinColumns = @JoinColumn(name = "topping_id"))
  private List<Topping> toppings = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "order_id")
  @JsonIgnore
  private OrderPizza order;

  private Double price;

  public OrderPizzaStatus getStatus() {
    return status;
  }

  @Enumerated(EnumType.STRING)
  private OrderPizzaStatus status;

  public OrderPizza getOrder()
  {
    return order;
  }

  public void setOrder(OrderPizza order)
  {
    this.order = order;
  }

  public Long getPizzaId()
  {
    return pizzaId;
  }

  public void setPizzaId(Long pizzaId)
  {
    this.pizzaId = pizzaId;
  }

  public PizzaSizeEnum getSize()
  {
    return size;
  }

  public void setSize(PizzaSizeEnum size)
  {
    this.size = size;
  }

  public PizzaCrustEnum getCrustType()
  {
    return crustType;
  }

  public void setCrustType(PizzaCrustEnum crustType)
  {
    this.crustType = crustType;
  }

  public List<Topping> getToppings()
  {
    return toppings;
  }

  public void setToppings(List<Topping> toppings)
  {
    this.toppings = toppings;
  }



  public Double getPrice()
  {
    return price;
  }

  public void setPrice(Double price)
  {
    this.price = price;
  }

  @Override
  public int compareTo(Pizza otherPizza)
  {
    if(this.getPizzaId() == null){
      return 1;
    }
    if( otherPizza.getPizzaId() == null){
      return -1;
    }
    return this.getPizzaId().compareTo(otherPizza.getPizzaId());


  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((pizzaId == null) ? 0 : pizzaId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Pizza other = (Pizza) obj;
    if (pizzaId == null)
    {
      if (other.pizzaId != null)
        return false;
    } else if (!pizzaId.equals(other.pizzaId))
      return false;
    return true;
  }





}
