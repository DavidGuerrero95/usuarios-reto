package app.retos.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
public class Registro {

	@NotBlank(message = "Username no puede ser nulo")
	@Pattern(regexp = "[A-Za-z0-9_.-]+", message = "Solo se permite:'_' o '.' o '-'")
	@Size(min = 4, max = 20, message = "El username debe tener entre 4 y 20 caracteres")
	@Indexed(unique = true)
	private String username;

	@NotBlank(message = "Password no puede ser nulo")
	@Pattern(regexp = "[^ ]*+", message = "Caracter: ' ' (Espacio en blanco) invalido")
	@Size(min = 6, max = 20, message = "La contraseña debe tener entre 6 y 20 caracteres")
	private String password;

	@NotNull(message = "Numero de celular no puede ser nulo")
	@Pattern(regexp = "[0-9]+", message = "Solo numeros")
	@Size(min = 8, max = 12, message = "Tamaño de celular es incorrecto")
	@Indexed(unique = true)
	private String cellPhone;

	@NotBlank(message = "Email no puede ser nulo")
	@Size(max = 50)
	@Pattern(regexp = "[^ ]*+", message = "Caracter: ' ' (Espacio en blanco) invalido")
	@Email(message = "Debe ser un email valido")
	@Indexed(unique = true)
	private String email;

	private List<String> roles;

}
