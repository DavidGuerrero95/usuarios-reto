package app.retos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.*;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
public class Users {

    @Id
    @JsonIgnore
    private String id;

    @Pattern(regexp = "[A-Za-z0-9_.-]+", message = "Solo se permite:'_' o '.' o '-'")
    @Size(min = 4, max = 20, message = "El username debe tener entre 4 y 20 caracteres")
    @Indexed(unique = true)
    private String username;

    @Size(min = 6, max = 50, message = "El email debe tener entre 6 y 50 caracteres")
    @Pattern(regexp = "[^ ]*+", message = "Caracter: ' ' (Espacio en blanco) invalido")
    @Email(message = "Debe ser un email valido")
    @Indexed(unique = true)
    private String email;

    @Pattern(regexp = "[A-Za-z ]+", message = "Solo letras")
    private String name;

    @Pattern(regexp = "[A-Za-z ]+", message = "Solo letras")
    private String lastName;

    private String fechaVerificacion;
    private List<Contacts> contacts;

    @Pattern(regexp = "[0-9+]+", message = "Solo numeros")
    @Size(min = 9, max = 15, message = "El celular debe tener entre 9 y 15 caracteres")
    private String cellPhone;

    private Boolean firstSession;
    private String colour;

    public Users(String username, String email, String name, String lastName,
                 String fechaVerificacion, List<Contacts> contacts, String cellPhone, Boolean firstSession,
                 String colour) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.fechaVerificacion = fechaVerificacion;
        this.contacts = contacts;
        this.cellPhone = cellPhone;
        this.firstSession = firstSession;
        this.colour = colour;
    }
}
