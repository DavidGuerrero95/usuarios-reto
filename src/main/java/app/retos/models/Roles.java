package app.retos.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
public class Roles {

    @Id
    private String id;

    private String name;

    public Roles(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

}