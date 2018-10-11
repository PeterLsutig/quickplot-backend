package eu.nasuta.repository;

import eu.nasuta.model.IUser;
import org.springframework.data.repository.CrudRepository;

public interface IUserRepository extends CrudRepository<IUser,String> {
}
