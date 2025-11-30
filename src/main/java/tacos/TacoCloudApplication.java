package tacos;

import java.util.Arrays;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import tacos.Ingredient.Type;
import tacos.data.IngredientRepository;
import tacos.data.TacoRepository;
import tacos.data.UserRepository;

@SpringBootApplication
public class TacoCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(TacoCloudApplication.class, args);
	}

	@Bean
	public ApplicationRunner userLoader(IngredientRepository ingredientRepo, UserRepository userRepo,
			PasswordEncoder passwordEncoder) {
		return args -> {
			// Initialize ingredients
			ingredientRepo.save(new Ingredient("FLTO", "Flour Tortilla", Type.WRAP));
			ingredientRepo.save(new Ingredient("COTO", "Corn Tortilla", Type.WRAP));
			ingredientRepo.save(new Ingredient("GRBF", "Ground Beef", Type.PROTEIN));
			ingredientRepo.save(new Ingredient("CARN", "Carnitas", Type.PROTEIN));
			ingredientRepo.save(new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES));
			ingredientRepo.save(new Ingredient("LETC", "Lettuce", Type.VEGGIES));
			ingredientRepo.save(new Ingredient("CHED", "Cheddar", Type.CHEESE));
			ingredientRepo.save(new Ingredient("JACK", "Monterrey Jack", Type.CHEESE));
			ingredientRepo.save(new Ingredient("SLSA", "Salsa", Type.SAUCE));
			ingredientRepo.save(new Ingredient("SRCR", "Sour Cream", Type.SAUCE));

			// Initialize admin user
			if (userRepo.findByUsername("admin") == null) {
				User adminUser = new User("admin", passwordEncoder.encode("admin123"), "Administrator", "123 Admin St",
						"Admin City", "AC", "12345", "555-0000");
				adminUser.setRole("ROLE_ADMIN");
				userRepo.save(adminUser);
			}
		};
	}

	@Bean
	public CommandLineRunner tacoLoader(
			IngredientRepository repo,
			UserRepository userRepo,
			PasswordEncoder encoder,
			TacoRepository tacoRepo) {
		return args -> {
			Ingredient flourTortilla = new Ingredient(
					"FLTO", "Flour Tortilla", Type.WRAP);
			Ingredient cornTortilla = new Ingredient(
					"COTO", "Corn Tortilla", Type.WRAP);
			Ingredient groundBeef = new Ingredient(
					"GRBF", "Ground Beef", Type.PROTEIN);
			Ingredient carnitas = new Ingredient(
					"CARN", "Carnitas", Type.PROTEIN);
			Ingredient tomatoes = new Ingredient(
					"TMTO", "Diced Tomatoes", Type.VEGGIES);
			Ingredient lettuce = new Ingredient(
					"LETC", "Lettuce", Type.VEGGIES);
			Ingredient cheddar = new Ingredient(
					"CHED", "Cheddar", Type.CHEESE);
			Ingredient jack = new Ingredient(
					"JACK", "Monterrey Jack", Type.CHEESE);
			Ingredient salsa = new Ingredient(
					"SLSA", "Salsa", Type.SAUCE);
			Ingredient sourCream = new Ingredient(
					"SRCR", "Sour Cream", Type.SAUCE);

			repo.save(flourTortilla);
			repo.save(cornTortilla);
			repo.save(groundBeef);
			repo.save(carnitas);
			repo.save(tomatoes);
			repo.save(lettuce);
			repo.save(cheddar);
			repo.save(jack);
			repo.save(salsa);
			repo.save(sourCream);

			Taco taco1 = new Taco();
			taco1.setName("Carnivore");
			taco1.setIngredients(Arrays.asList(
					flourTortilla, groundBeef, carnitas,
					sourCream, salsa, cheddar));
			tacoRepo.save(taco1);

			Taco taco2 = new Taco();
			taco2.setName("Bovine Bounty");
			taco2.setIngredients(Arrays.asList(
					cornTortilla, groundBeef, cheddar,
					jack, sourCream));
			tacoRepo.save(taco2);

			Taco taco3 = new Taco();
			taco3.setName("Veg-Out");
			taco3.setIngredients(Arrays.asList(
					flourTortilla, cornTortilla, tomatoes,
					lettuce, salsa));
			tacoRepo.save(taco3);
		};
	}
}
