package tacos.service;

import tacos.Ingredient;

public interface IngredientService {
    Iterable<Ingredient> findAll();
    Ingredient addIngredient(Ingredient ingredient);
    void deleteIngredient(String id);
}
