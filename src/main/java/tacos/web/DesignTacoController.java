package tacos.web;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.Taco;

@Controller
@RequestMapping("/design")
@SessionAttributes("tacoOrder")
public class DesignTacoController {

  private static final Logger log = LoggerFactory.getLogger(DesignTacoController.class);

  private List<Ingredient> allIngredients = Arrays.asList(
      new Ingredient("FLTO", "Flour Tortilla", Type.WRAP),
      new Ingredient("COTO", "Corn Tortilla", Type.WRAP),
      new Ingredient("GRBF", "Ground Beef", Type.PROTEIN),
      new Ingredient("CARN", "Carnitas", Type.PROTEIN),
      new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES),
      new Ingredient("LETC", "Lettuce", Type.VEGGIES),
      new Ingredient("CHED", "Cheddar", Type.CHEESE),
      new Ingredient("JACK", "Monterrey Jack", Type.CHEESE),
      new Ingredient("SLSA", "Salsa", Type.SAUCE),
      new Ingredient("SRCR", "Sour Cream", Type.SAUCE));

  @ModelAttribute
  public void addIngredientsToModel(Model model) {
    Type[] types = Ingredient.Type.values();
    for (Type type : types) {
      model.addAttribute(type.toString().toLowerCase(),
          filterByType(allIngredients, type));
    }
  }

  @InitBinder("taco")
  public void initBinder(WebDataBinder binder) {
    binder.setDisallowedFields("ingredients");
  }

  @GetMapping
  public String showDesignForm(Model model) {
    model.addAttribute("taco", new Taco());
    return "design";
  }

  @PostMapping
  public String processTaco(@ModelAttribute("taco") Taco taco,
      @RequestParam(value = "ingredients", required = false) String[] ingredientIds) {
    if (ingredientIds != null && ingredientIds.length > 0) {
      List<Ingredient> ingredients = Arrays.stream(ingredientIds)
          .map(id -> {
            return allIngredients.stream()
                .filter(ing -> ing.getId().equals(id))
                .findFirst()
                .orElse(null);
          })
          .filter(ing -> ing != null)
          .collect(Collectors.toList());

      taco.setIngredients(ingredients);
    }

    // Save the taco...
    // We'll do this in chapter 3
    log.info("Processing taco: " + taco);

    return "redirect:/orders/current";
  }

  private Iterable<Ingredient> filterByType(
      List<Ingredient> ingredients, Type type) {
    return ingredients
        .stream()
        .filter(x -> x.getType().equals(type))
        .collect(Collectors.toList());
  }
}