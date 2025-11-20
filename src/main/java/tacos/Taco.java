package tacos;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Date;

@Data
@Table("Taco")
public class Taco {

  @Id
  private Long id;

  private Date placedAt;

  private Date createdAt;

  @NotNull
  @Size(min = 5, message = "Name must be at least 5 characters long")
  private String name;

  @NotNull
  @Size(min = 1, message = "You must choose at least 1 ingredient")
  private List<Ingredient> ingredients;
}
