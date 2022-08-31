package app.retos.services;

import app.retos.models.Contacts;
import app.retos.models.Roles;
import app.retos.models.Users;
import app.retos.models.UsersPw;
import app.retos.repository.UsersPwRepository;
import app.retos.repository.UsersRepository;
import app.retos.requests.Register;
import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class RegisterService implements IRegisterService {

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersPwRepository usersPwRepository;

    @Override
    public Boolean crearUsuario(Register register) {
        if (register.getRoles() == null)
            register.setRoles(new ArrayList<>(List.of("user")));
        List<Roles> roles = obtenerRoles(register.getRoles());
        Users users = new Users(register.getUsername(), register.getEmail(), "", "",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()),
                new ArrayList<Contacts>());
        UsersPw usersPw = new UsersPw(users.getId(), codificar(register.getPassword()), true, 0,
                0, roles);
        try {
            usersRepository.save(users);
            usersPwRepository.save(usersPw);
            return true;
        } catch (MongoException e) {
            log.error("Error en la creacion: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean crearPrimerUsuario() {
        if (!usersRepository.existsByUsername("admin")) {
            Users users = new Users("admin", "coo.appcity@gmail.com", "admin",
                    "app", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()),
                    new ArrayList<Contacts>());
            Roles admin = new Roles("1", "ROLE_ADMIN");
            Roles mod = new Roles("2", "ROLE_MODERATOR");
            Roles intrvnt = new Roles("3", "ROLE_INTERVENTOR");
            Roles user = new Roles("1", "ROLE_USER");
            List<Roles> roles = new ArrayList<Roles>();
            roles.add(admin);
            roles.add(mod);
            roles.add(intrvnt);
            roles.add(user);
            UsersPw usersPw = new UsersPw(users.getId(), codificar("1234567890"), true, 0,
                    0, roles);
            try {
                usersRepository.save(users);
                usersPwRepository.save(usersPw);
                return true;
            } catch (MongoException e) {
                log.error("Error en la creacion: " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public String codificar(String password) {
        return encoder.encode(password);
    }

    private List<Roles> obtenerRoles(List<String> roles) {
        List<Roles> rLista = new ArrayList<Roles>();
        roles.forEach(r -> {
            Roles role = new Roles();
            switch (r) {
                case "admin":
                    role = new Roles("1", "ROLE_ADMIN");
                    rLista.add(role);
                    break;
                case "mod":
                    role = new Roles("2", "ROLE_MODERATOR");
                    rLista.add(role);
                    break;
                default:
                    role = new Roles("4", "ROLE_USER");
                    rLista.add(role);
                    break;
            }
        });
        return rLista;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
