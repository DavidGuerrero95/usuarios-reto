package app.retos.repository;

import app.retos.models.Contacts;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface ContactsRepository extends MongoRepository<Contacts, String> {

    @RestResource(path = "find-username")
    List<Contacts> findByUsername(@Param("username") String username);

    @RestResource(path = "find-userId-email-cellPhone")
    Contacts findByUsernameAndEmailAndCellPhone(@Param("username") String username,
                                              @Param("userId") String email,
                                              @Param("userId") String cellPhone);

    @RestResource(path = "find-username-email")
    Contacts findByUsernameAndEmail(@Param("username") String username,
                                  @Param("email") String email);

    @RestResource(path = "find-username-cellPhone")
    Contacts findByUsernameAndCellPhone(@Param("username") String username, @Param("cellPhone") String cellPhone);

    @RestResource(path = "existe-username-email")
    Boolean existsByUsernameAndEmail(@Param("username") String username, @Param("email") String email);

    @RestResource(path = "existe-username-cellphone")
    Boolean existsByUsernameAndCellPhone(@Param("username") String username, @Param("cellphone") String cellPhone);
    @RestResource(path = "exists-username-email-cellPhone")
    Boolean existsByUsernameAndEmailAndCellPhone(@Param("username") String username,
                                              @Param("email") String email,
                                              @Param("cellPhone") String cellPhone);
    @RestResource(path = "delete-username")
    void deleteByUsername(@Param("username") String username);

    @RestResource(path = "delete-username-email-cellPhone")
    void deleteByUsernameAndEmailAndCellPhone(@Param("username") String username,
                                            @Param("email") String email,
                                            @Param("cellPhone") String cellPhone);
}
