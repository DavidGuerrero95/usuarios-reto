package app.retos.repository;

import app.retos.models.Register;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface RegisterRepository extends MongoRepository<Register, String> {

    @RestResource(path = "find-user")
    Register findByUsername(@Param("username") String username);

    @RestResource(path = "find-email")
    Register findByEmail(@Param("email") String email);

    @RestResource(path = "existe-user-email")
    Boolean existsByUsernameOrEmail(@Param("username") String username,
                                    @Param("username") String email);

    @RestResource(path = "exists-user")
    Boolean existsByUsername(@Param("username") String username);

    @RestResource(path = "exists-email")
    Boolean existsByEmail(@Param("email") String email);

    @RestResource(path = "delete-username")
    Boolean deleteByUsername(@Param("username") String username);

}
