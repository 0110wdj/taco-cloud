package tacos.data;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import tacos.Ingredient;

public interface IngredientRepository extends CrudRepository<Ingredient, String> {

  // Iterable<Ingredient> findAll();

  // Optional<Ingredient> findById(String id);

  // Ingredient save(Ingredient ingredient);

}