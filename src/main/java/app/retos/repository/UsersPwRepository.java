package app.retos.repository;

import app.retos.models.UsersPw;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface UsersPwRepository extends MongoRepository<UsersPw, String> {

	@RestResource(path = "find-username")
	UsersPw findByUsername(@Param("username") String username);
	@RestResource(path = "find-userId")
	UsersPw findByUserId(@Param("userId") String userId);

	@RestResource(path = "delete-userId")
	void deleteByUserId(@Param("userId") String userId);

}
