package app.retos.services;

import app.retos.clients.NotificationsFeignClient;
import app.retos.models.Users;
import app.retos.models.UsersPw;
import app.retos.repository.ContactsRepository;
import app.retos.repository.UsersPwRepository;
import app.retos.repository.UsersRepository;
import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class UsersService implements IUsersService {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersPwRepository usersPwRepository;

    @Autowired
    IRegisterService registerService;

    @Autowired
    ContactsRepository contactsRepository;

    @Autowired
    NotificationsFeignClient notificationsFeignClient;

    @Override
    public Boolean editarUsuario(String username, Users users) {
        Users userInitial = usersRepository.findByUsername(username);
        if(!userInitial.getFirstSession())
            userInitial.setFirstSession(true);
        if (users.getUsername() != null) {
            if (!usersRepository.existsByUsername(users.getUsername())) userInitial.setUsername(users.getUsername());
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario " + users.getUsername() + " ya existe");
        }
        if (users.getEmail() != null) {
            if (!usersRepository.existsByEmail(users.getEmail())) userInitial.setEmail(users.getEmail());
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email " + users.getEmail() + " ya existe");
        }
        if (users.getName() != null) userInitial.setName(users.getName());
        if (users.getLastName() != null) userInitial.setLastName(users.getLastName());
        if (users.getColour() != null) userInitial.setColour(users.getColour());
        try {
            usersRepository.save(userInitial);
            return true;
        } catch (MongoException e) {
            log.error("Error en la edición: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean editarContrasena(String username, String password) {
        Users users = usersRepository.findByUsername(username);
        UsersPw usersPw = usersPwRepository.findByUserId(users.getId());
        password = registerService.codificar(password);
        usersPw.setPassword(password);
        try {
            usersPw.setCode(-1);
            usersPwRepository.save(usersPw);
            return true;
        } catch (MongoException e) {
            log.error("Error en la edición: " + e.getMessage());
            return false;
        }
    }

    @Override
    public UsersPw encontrarUsuarioPw(String username) {
        return usersPwRepository.findByUserId(usersRepository.findByUsername(username).getId());
    }

    @Override
    public Boolean eliminarUsuario(String username) {
        try {
            Users users = usersRepository.findByUsername(username);
            usersRepository.delete(users);
            usersPwRepository.deleteByUserId(users.getId());
            contactsRepository.deleteByUserId(users.getId());
            return true;
        } catch (MongoException e) {
            log.error("Error en la edición: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void eliminarTodosUsuarios() {
        usersRepository.deleteAll();
        usersPwRepository.deleteAll();
        contactsRepository.deleteAll();
    }

    @Override
    public Boolean usuarioExiste(String username) {
        return usersRepository.existsByUsername(username);
    }

    @Override
    public String enviarMensajeVerificacion(String username) {
        Integer codigo = (int) (100000 * Math.random() + 99999);
        Users users = usersRepository.findByUsername(username);
        UsersPw userPw = usersPwRepository.findByUserId(users.getId());
        userPw.setCode(codigo);
        usersPwRepository.save(userPw);
        notificationsFeignClient.enviarCodigoEditarContrasenia(username, users.getEmail(), codigo);
        return "Codigo de verificación enviado a su correo: "+users.getEmail();
    }


}
