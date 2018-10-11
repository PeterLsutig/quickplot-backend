package eu.nasuta.repository;

import eu.nasuta.model.User;
import org.springframework.data.repository.CrudRepository;

public interface IUserRepository extends CrudRepository<User,String> {
}
