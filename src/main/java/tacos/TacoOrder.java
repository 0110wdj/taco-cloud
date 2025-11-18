package tacos;

// import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Pattern;
// import org.hibernate.validator.constraints.CreditCardNumber;
import java.util.List;
import java.util.ArrayList;
import lombok.Data;
import java.util.Date;

@Data
public class TacoOrder {

  private static final long serialVersionUID = 1L;

  private Long id;

  private Date placedAt;

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

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setPlacedAt(Date placedAt) {
    this.placedAt = placedAt;
  }

  public Date getPlacedAt() {
    return placedAt;
  }

  public String getCcNumber() {
    return ccNumber;
  }

  public void setCcNumber(String ccNumber) {
    this.ccNumber = ccNumber;
  }

  public String getCcExpiration() {
    return ccExpiration;
  }

  public void setCcExpiration(String ccExpiration) {
    this.ccExpiration = ccExpiration;
  }

  public String getCcCVV() {
    return ccCVV;
  }

  public void setCcCVV(String ccCVV) {
    this.ccCVV = ccCVV;
  }

  public String getDeliveryName() {
    return deliveryName;
  }

  public void setDeliveryName(String deliveryName) {
    this.deliveryName = deliveryName;
  }

  public String getDeliveryStreet() {
    return deliveryStreet;
  }

  public void setDeliveryStreet(String deliveryStreet) {
    this.deliveryStreet = deliveryStreet;
  }

  public String getDeliveryCity() {
    return deliveryCity;
  }

  public void setDeliveryCity(String deliveryCity) {
    this.deliveryCity = deliveryCity;
  }

  public String getDeliveryState() {
    return deliveryState;
  }

  public void setDeliveryState(String deliveryState) {
    this.deliveryState = deliveryState;
  }

  public String getDeliveryZip() {
    return deliveryZip;
  }

  public void setDeliveryZip(String deliveryZip) {
    this.deliveryZip = deliveryZip;
  }

  public List<Taco> getTacos() {
    return tacos;
  }

  public void setTacos(List<Taco> tacos) {
    this.tacos = tacos;
  }
}