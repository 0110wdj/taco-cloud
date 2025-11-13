package tacos;

import java.util.List;

import lombok.Data;

@Data
public class Taco {
  private String name;

  private List<Ingredient> ingredients;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }
}
