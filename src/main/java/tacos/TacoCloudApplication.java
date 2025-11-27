package tacos;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import tacos.Ingredient.Type;
import tacos.data.IngredientRepository;
import tacos.data.UserRepository;

@SpringBootApplication
public class TacoCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(TacoCloudApplication.class, args);
	}

	@Bean
	public ApplicationRunner dataLoader(IngredientRepository ingredientRepo, UserRepository userRepo, PasswordEncoder passwordEncoder) {
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
				User adminUser = new User("admin", passwordEncoder.encode("admin123"), "Administrator", "123 Admin St", "Admin City", "AC", "12345", "555-0000");
				adminUser.setRole("ROLE_ADMIN");
				userRepo.save(adminUser);
			}
		};
	}
}
