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

    @NotNull(message = "Numero de celular no puede ser nulo")
    @Pattern(regexp = "[0-9]+", message = "Solo numeros")
    @Size(min = 8, max = 12, message = "Tamaño de celular es incorrecto")
    @Indexed(unique = true)
    private String cellPhone;

    @NotNull(message = "Email no puede ser nulo")
    @Size(max = 50, message = "Tamaño incorrecto")
    @Email(message = "Debe ser un email valido")
    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private Integer botonId;

    @Pattern(regexp = "[A-Za-z]+", message = "Solo letras")
    private String name;

    @Pattern(regexp = "[A-Za-z]+", message = "Solo letras")
    private String lastName;

    private List<Contacts> contacts;

    public Users(String username, String cellPhone, String email, Integer botonId, String name, String lastName,
                 List<Contacts> contacts) {
        this.username = username;
        this.cellPhone = cellPhone;
        this.email = email;
        this.botonId = botonId;
        this.name = name;
        this.lastName = lastName;
        this.contacts = contacts;
    }
}
