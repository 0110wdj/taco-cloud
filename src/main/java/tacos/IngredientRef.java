package tacos;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Table("Ingredient_Ref")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class IngredientRef {

  private final String ingredient;

}