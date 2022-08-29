package app.retos.repository;

import app.retos.models.UsersPw;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface UsuarioPwRepository extends MongoRepository<UsersPw, String> {

	@RestResource(path = "find-user")
	public UsersPw findByUsername(@Param("username") String username);

}
