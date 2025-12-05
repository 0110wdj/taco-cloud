package tacos.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import tacos.Ingredient;
import tacos.service.IngredientService;

@Controller
@RequestMapping("/admin/ingredients")
public class IngredientAdminController {

    private final IngredientService ingredientService;

    public IngredientAdminController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public String ingredientList(Model model) {
        model.addAttribute("ingredients", ingredientService.findAll());
        model.addAttribute("ingredient", new Ingredient());
        return "admin/ingredients";
    }

    @PostMapping
    public String addIngredient(@ModelAttribute Ingredient ingredient) {
        ingredientService.addIngredient(ingredient);
        return "redirect:/admin/ingredients";
    }

    @PostMapping("/delete/{id}")
    public String deleteIngredient(@PathVariable String id) {
        ingredientService.deleteIngredient(id);
        return "redirect:/admin/ingredients";
    }
}
