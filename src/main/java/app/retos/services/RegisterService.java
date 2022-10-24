package app.retos.services;

import app.retos.clients.NotificationsFeignClient;
import app.retos.models.*;
import app.retos.repository.RegisterRepository;
import app.retos.repository.UsersPwRepository;
import app.retos.repository.UsersRepository;
import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class RegisterService implements IRegisterService {

    @Autowired
    private CircuitBreakerFactory cbFactory;
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersPwRepository usersPwRepository;

    @Autowired
    RegisterRepository registerRepository;

    @Autowired
    NotificationsFeignClient notificationsFeignClient;

    @Override
    public Boolean crearUsuario(Register register) {
        if (register.getRoles() == null)
            register.setRoles(new ArrayList<>(List.of("user")));
        List<Roles> roles = obtenerRoles(register.getRoles());
        log.info("username: "+register.getUsername());
        Users users = new Users(register.getUsername(), register.getEmail(), "", "",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()),
                new ArrayList<>(), register.getCellPhone());

        UsersPw usersPw = new UsersPw(users.getUsername(), register.getPassword(), true, 0,
                0, roles);
        try {
            usersRepository.save(users);
            usersPwRepository.save(usersPw);
            registerRepository.delete(register);
            return true;
        } catch (MongoException e) {
            log.error("Error en la creacion: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean crearPrimerUsuario() {
        if (!usersRepository.existsByUsername("admin")) {
            String username = "admin";
            Users users = new Users(username, "coo.appcity@gmail.com", "admin",
                    "app", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()),
                    new ArrayList<>(),"");
            Roles admin = new Roles("1", "ROLE_ADMIN");
            Roles mod = new Roles("2", "ROLE_MODERATOR");
            Roles intrvnt = new Roles("3", "ROLE_INTERVENTOR");
            Roles user = new Roles("1", "ROLE_USER");
            List<Roles> roles = new ArrayList<Roles>();
            roles.add(admin);
            roles.add(mod);
            roles.add(intrvnt);
            roles.add(user);
            UsersPw usersPw = new UsersPw(username, codificar("1234567890"), true, 0,
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


    @Override
    public void crearNuevoUsuario(Register register) {
        Long minutos = new Date().getTime();
        Register rg = new Register();
        if (registerRepository.existsByUsername(register.getUsername())) {
            rg = registerRepository.findByUsername(register.getUsername());
            rg.setEmail(register.getEmail());
        } else if (registerRepository.existsByEmail(register.getEmail())) {
            rg = registerRepository.findByEmail(register.getEmail());
            rg.setUsername(register.getUsername());
        } else {
            rg.setEmail(register.getEmail());
            rg.setUsername(register.getUsername());
        }
        rg.setCode(String.valueOf((int) (100000 * Math.random() + 99999)));
        rg.setPassword(codificar(register.getPassword()));
        rg.setMinutes(minutos);
        if (rg.getRoles() == null) {
            rg.setRoles(new ArrayList<>(Arrays.asList("user")));
        }
        registerRepository.save(rg);
        notificationsFeignClient.enviarMensajeSuscripciones(rg.getEmail(), rg.getCode());
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
