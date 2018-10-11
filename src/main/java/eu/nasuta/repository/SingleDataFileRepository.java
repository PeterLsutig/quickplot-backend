package eu.nasuta.repository;


import eu.nasuta.model.SingleNumericDataFile;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SingleDataFileRepository extends CrudRepository<SingleNumericDataFile,String> {
}
