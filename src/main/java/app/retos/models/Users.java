package app.retos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
public class Users {

    @Id
    @JsonIgnore
    private String id;

    @NotNull(message = "Username no puede ser nulo")
    @Size(max = 20)
    @Indexed(unique = true)
    private String username;

    @NotNull(message = "Email no puede ser nulo")
    @Size(max = 50, message = "Tamaño incorrecto")
    @Email(message = "Debe ser un email valido")
    @Indexed(unique = true)
    private String email;

    @Pattern(regexp = "[A-Za-z]+", message = "Solo letras")
    private String name;

    @Pattern(regexp = "[A-Za-z]+", message = "Solo letras")
    private String lastName;

    private String fechaVerificacion;
    private List<Contacts> contacts;

    private String cellPhone;

    public Users(String username, String email, String name, String lastName,
                 String fechaVerificacion, List<Contacts> contacts, String cellPhone) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.fechaVerificacion = fechaVerificacion;
        this.contacts = contacts;
        this.cellPhone = cellPhone;
    }
}
