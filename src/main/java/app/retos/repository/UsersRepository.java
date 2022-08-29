package app.retos.repository;


import app.retos.models.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface UsuarioRepository extends MongoRepository<Users, String> {

    @RestResource(path = "find-username")
    public Users findByUsername(@Param("username") String username);

    @RestResource(path = "find-cellPhone")
    public Users findByCellPhone(@Param("cellPhone") String cellPhone);

    @RestResource(path = "find-email")
    public Users findByEmail(@Param("email") String email);

    @RestResource(path = "find-user-email-cellPhone")
    public Users findByUsernameOrEmailOrCellPhone(@Param("username") String username, @Param("username") String email,
                                                  @Param("username") String cellPhone);

    @RestResource(path = "exist-user")
    public Boolean existsByUsername(@Param("username") String username);

    @RestResource(path = "exist-email")
    public Boolean existsByEmail(@Param("email") String email);

    @RestResource(path = "exist-phone")
    public Boolean existsByCellPhone(@Param("phone") String phone);

    @RestResource(path = "existe-user-email-cellPhone")
    public Boolean existsByUsernameOrEmailOrCellPhone(@Param("username") String username,
                                                      @Param("username") String email, @Param("username") String cellPhone);

    @RestResource(path = "delete-username")
    public Boolean deleteByUsername(@Param("username") String username);

    @RestResource(path = "delete-cellPhone")
    public Boolean deleteByCellPhone(@Param("username") String username);

    @RestResource(path = "delete-email")
    public Boolean deleteByEmail(@Param("username") String username);

    @RestResource(path = "delete-user-email-cellPhone")
    public Boolean deleteByUsernameOrEmailOrCellPhone(@Param("username") String username,
                                                      @Param("username") String email,
                                                      @Param("username") String cellPhone);
}