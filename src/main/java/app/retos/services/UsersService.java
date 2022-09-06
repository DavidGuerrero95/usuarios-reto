package app.retos.services;

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

    @Override
    public Boolean editarUsuario(String username, Users users) {
        Users userInitial = usersRepository.findByUsername(username);
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
        UsersPw usersPw = usersPwRepository.findByUsername(username);
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
    public UsersPw encontrarUsuarioPw(String username) {;
        return usersPwRepository.findByUsername(username);
    }

    @Override
    public Boolean eliminarUsuario(String username) {
        try {
            usersRepository.deleteByUsername(username);
            usersPwRepository.deleteByUsername(username);
            contactsRepository.deleteByUsername(username);
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
}
