package tacos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// import javax.validation.constraints.Digits;
// import javax.validation.constraints.NotBlank;
// import javax.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class TacoOrder implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  private Date placedAt = new Date();

  // delivery and credit card properties omitted for brevity's sake
  // @Column("customer_name")
  @NotBlank(message = "Delivery name is required")
  private String deliveryName;
  @NotBlank(message = "Delivery street is required")
  private String deliveryStreet;
  @NotBlank(message = "Delivery city is required")
  private String deliveryCity;
  @NotBlank(message = "Delivery state is required")
  private String deliveryState;
  @NotBlank(message = "Delivery zip is required")
  private String deliveryZip;
  // @CreditCardNumber(message = "Invalid credit card number")
  private String ccNumber;
  // @Pattern(regexp = "^(0[1-9]|1[0-2])([\\/])([2-9][0-9])$", message = "Must be
  // formatted MM/YY")
  private String ccExpiration;
  // @Digits(integer = 3, fraction = 0, message = "Invalid CVV")
  private String ccCVV;

  private List<Taco> tacos = new ArrayList<>();

  public void addTaco(Taco taco) {
    this.tacos.add(taco);
  }
}