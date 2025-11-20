package tacos.data;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import tacos.Ingredient;
import tacos.TacoOrder;

public interface OrderRepository extends CrudRepository<TacoOrder, Long>{

  // TacoOrder save(TacoOrder order);

}