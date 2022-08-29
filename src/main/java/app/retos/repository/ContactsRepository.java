package app.retos.repository;

import app.retos.models.Contacts;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface ContactsRepository extends MongoRepository<Contacts, String> {

    @RestResource(path = "find-username")
    public List<Contacts> findByUsername(@Param("username") String username);

    @RestResource(path = "find-username-email-cellPhone")
    public Contacts findByUsernameAndEmailAndCellPhone(@Param("username") String username,
                                                       @Param("username") String email,
                                                       @Param("username") String cellPhone);

    @RestResource(path = "delete-username")
    public Boolean deleteByUsername(@Param("username") String username);

    @RestResource(path = "delete-user-email-cellPhone")
    public Boolean deleteByUsernameAndEmailAndCellPhone(@Param("username") String username,
                                                      @Param("username") String email,
                                                      @Param("username") String cellPhone);
}
