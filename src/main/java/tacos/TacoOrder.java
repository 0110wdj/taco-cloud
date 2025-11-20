package tacos;

// import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Pattern;
// import org.hibernate.validator.constraints.CreditCardNumber;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import lombok.Data;
import java.util.Date;

@Data
@Table("Taco_Order")
public class TacoOrder {

  private static final long serialVersionUID = 1L;

  @Id
  private Long id;

  private Date placedAt;

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

}