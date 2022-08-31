package app.retos.repository;


import app.retos.models.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface UsersRepository extends MongoRepository<Users, String> {

    @RestResource(path = "find-username")
    public Users findByUsername(@Param("username") String username);

    @RestResource(path = "find-email")
    public Users findByEmail(@Param("email") String email);

    @RestResource(path = "find-user-email-cellPhone")
    public Users findByUsernameOrEmail(@Param("username") String username, @Param("username") String email);

    @RestResource(path = "exist-user")
    public Boolean existsByUsername(@Param("username") String username);

    @RestResource(path = "existe-user-email-cellPhone")
    public Boolean existsByUsernameOrEmail(@Param("username") String username, @Param("username") String email);

    @RestResource(path = "delete-username")
    public Boolean deleteByUsername(@Param("username") String username);

    @RestResource(path = "delete-email")
    public Boolean deleteByEmail(@Param("username") String username);

    @RestResource(path = "delete-user-email-cellPhone")
    public Boolean deleteByUsernameOrEmail(@Param("username") String username,
                                                      @Param("username") String email);
}