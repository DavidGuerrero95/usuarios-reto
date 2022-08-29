package app.retos.repository;

import app.retos.models.Roles;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Roles, String> {

	@RestResource(path = "role")
	public Optional<Roles> findByName(@Param("role") String name);
}
