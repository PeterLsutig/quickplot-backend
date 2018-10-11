package eu.nasuta.repository;

import eu.nasuta.model.DataSet;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DataSetRepository extends CrudRepository<DataSet, UUID> {
}
