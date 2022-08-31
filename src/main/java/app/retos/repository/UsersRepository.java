package app.retos.repository;


import app.retos.models.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface UsersRepository extends MongoRepository<Users, String> {

    @RestResource(path = "find-username")
    Users findByUsername(@Param("username") String username);

    @RestResource(path = "find-user-email-cellPhone")
    Users findByUsernameOrEmail(@Param("username") String username, @Param("username") String email);

    @RestResource(path = "exist-user")
    Boolean existsByUsername(@Param("username") String username);

    @RestResource(path = "exist-email")
    Boolean existsByEmail(@Param("email") String email);

    @RestResource(path = "existe-user-email")
    Boolean existsByUsernameOrEmail(@Param("username") String username, @Param("username") String email);

    @RestResource(path = "delete-username")
    void deleteByUsername(@Param("username") String username);

}