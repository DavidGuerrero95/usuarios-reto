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

@Document(collection = "contacts")
@Data
@NoArgsConstructor
public class Contacts {

    @Id
    @JsonIgnore
    private String id;

    @Size(max = 20)
    @JsonIgnore
    private String userId;

    @Pattern(regexp = "[A-Za-z]+", message = "Solo letras")
    private String name;

    @Pattern(regexp = "[A-Za-z]+", message = "Solo letras")
    private String lastName;

    @NotNull(message = "Email no puede ser nulo")
    @Size(min = 6, max = 50, message = "El email debe tener entre 6 y 50 caracteres")
    @Pattern(regexp = "[^ ]*+", message = "Caracter: ' ' (Espacio en blanco) invalido")
    @Email(message = "Debe ser un email valido")
    private String email;

    @NotNull(message = "Numero de celular no puede ser nulo")
    @Pattern(regexp = "[0-9]+", message = "Solo numeros")
    @Size(min = 8, max = 12, message = "Tamaño de celular es incorrecto")
    private String cellPhone;

}
