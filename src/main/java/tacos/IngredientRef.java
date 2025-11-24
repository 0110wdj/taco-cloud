package tacos;

import jakarta.persistence.Table;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Table(name = "Ingredient_Ref")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class IngredientRef {

  private final String ingredient;

}