package app.retos.repository;

import app.retos.models.Contacts;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface ContactsRepository extends MongoRepository<Contacts, String> {

    @RestResource(path = "find-userId")
    List<Contacts> findByUserId(@Param("userId") String userId);

    @RestResource(path = "find-userId-email-cellPhone")
    Contacts findByUserIdAndEmailAndCellPhone(@Param("userId") String userId,
                                              @Param("userId") String email,
                                              @Param("userId") String cellPhone);

    @RestResource(path = "find-userId-email")
    Contacts findByUserIdAndEmail(@Param("userId") String userId,
                                  @Param("email") String email);

    @RestResource(path = "find-userId-cellPhone")
    Contacts findByUserIdAndCellPhone(@Param("userId") String userId, @Param("cellPhone") String cellPhone);

    @RestResource(path = "existe-userId-email")
    Boolean existsByUserIdAndEmail(@Param("userId") String userId, @Param("email") String email);

    @RestResource(path = "existe-userId-cellphone")
    Boolean existsByUserIdAndCellPhone(@Param("userId") String userId, @Param("cellphone") String cellPhone);
    @RestResource(path = "exists-userId-email-cellPhone")
    Boolean existsByUserIdAndEmailAndCellPhone(@Param("userId") String userId,
                                              @Param("email") String email,
                                              @Param("cellPhone") String cellPhone);
    @RestResource(path = "delete-userId")
    void deleteByUserId(@Param("userId") String userId);

    @RestResource(path = "delete-userId-email-cellPhone")
    void deleteByUserIdAndEmailAndCellPhone(@Param("userId") String userId,
                                            @Param("email") String email,
                                            @Param("cellPhone") String cellPhone);
}
