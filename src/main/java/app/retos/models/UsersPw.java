package app.retos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Document(collection = "usersPw")
@Data
@NoArgsConstructor
public class UsersPw {

    @Id
    @JsonIgnore
    private String id;

    @NotBlank(message = "Username cannot be null")
    @Size(max = 20)
    @Indexed(unique = true)
    private String userId;

    @NotBlank(message = "Password cannot be null")
    @Size(min = 6, max = 20, message = "About Me must be between 6 and 20 characters")
    private String password;

    private Boolean enabled;
    private Integer attempts;
    private Integer code;
    private List<Roles> roles;

    public UsersPw(String userId, String password, Boolean enabled, Integer attempts, Integer code,
                   List<Roles> roles) {
        super();
        this.userId = userId;
        this.password = password;
        this.enabled = enabled;
        this.attempts = attempts;
        this.code = code;
        this.roles = roles;
    }

}
