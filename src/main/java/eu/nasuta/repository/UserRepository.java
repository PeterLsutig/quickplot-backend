package eu.nasuta.repository;

import eu.nasuta.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,String> {
}
