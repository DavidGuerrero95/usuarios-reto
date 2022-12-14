package app.retos.controllers;

import app.retos.clients.NotificationsFeignClient;
import app.retos.models.Users;
import app.retos.models.UsersPw;
import app.retos.repository.UsersPwRepository;
import app.retos.repository.UsersRepository;
import app.retos.responses.UserAutentication;
import app.retos.services.IUsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/usuarios")
public class UsersController {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersPwRepository usersPwRepository;

    @Autowired
    IUsersService usersService;

    // LISTAS TODOS LOS USUARIOS
    @GetMapping("/listar/")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Users> listarUsuarios() throws IOException {
        try {
            return usersRepository.findAll();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en listar usuarios: " + e.getMessage());
        }
    }

    @GetMapping("/listarPw/")
    @ResponseStatus(code = HttpStatus.OK)
    public List<UsersPw> listarUsuariosPw() throws IOException {
        try {
            return usersPwRepository.findAll();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en listar usuarios: " + e.getMessage());
        }
    }


    // BUSCAR USUARIO POR USERNAME o EMAIL
    @GetMapping("/encontrar/{dato}")
    @ResponseStatus(HttpStatus.OK)
    public Users EncontrarEmailUsername(@PathVariable("dato") String dato) throws IOException {
        if (EmailUsernameUsuarioExiste(dato)) {
            return usersRepository.findByUsernameOrEmail(dato, dato);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario con el parametro: " + dato + " no existe");
    }

    // PREGUNTAR SI UN USUARIO EXISTE POR: USERNAME, MAIL, CELLPHONE
    @GetMapping("/existe/{dato}")
    @ResponseStatus(HttpStatus.FOUND)
    public Boolean EmailUsernameUsuarioExiste(@PathVariable("dato") String dato) throws IOException {
        try {
            Boolean exist = usersRepository.existsByUsernameOrEmail(dato, dato);
            log.info("Se conecto y el usuario existe?: " + exist);
            return exist;
        } catch (Exception e2) {
            throw new IOException("Error al encontrar usuario: " + e2.getMessage());
        }
    }

    @GetMapping("/preguntar/usuarioExiste")
    public Boolean preguntarUsuarioExiste(@RequestParam(value = "username") String username) throws IOException {
        try {
            return usersService.usuarioExiste(username);
        } catch (Exception e2) {
            throw new IOException("Error al encontrar usuario: " + e2.getMessage());
        }
    }

    // INICIAR SESION
    @GetMapping("/login/{username}")
    public UserAutentication autenticacion(@PathVariable("username") String username) throws InterruptedException, ResponseStatusException, IOException {
        if (EmailUsernameUsuarioExiste(username)) {
            return usersService.encontrarUsuarioPw(username);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
    }

    // MICROSERVICE EVENTS -> USUARIO (ID)
    @GetMapping("/obtener/{username}")
    public String obtenerId(@PathVariable("username") String username) throws IOException, ResponseStatusException {
        log.info("Conexion establecida");
        try {
            if (EmailUsernameUsuarioExiste(username))
                return EncontrarEmailUsername(username).getId();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no existe");
        } catch (Exception e2) {
            throw new IOException("Error al encontrar usuario: " + e2.getMessage());
        }
    }

    // EDITAR USUARIO
    @PutMapping("/editar/{username}")
    @ResponseStatus(HttpStatus.OK)
    public String editarUsuario(@PathVariable("username") String username, @RequestBody @Validated Users users) throws IOException {
        if (EmailUsernameUsuarioExiste(username)) {
            if (usersService.editarUsuario(username, users))
                return "Edici??n del usuario de manera exitosa";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la edicion");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
    }

    @PutMapping("/codigo/{username}")
    @ResponseStatus(code = HttpStatus.OK)
    public String enviarCodigo(@PathVariable("username") String username) {
        if (usersRepository.existsByUsername(username)) {
            return usersService.enviarMensajeVerificacion(username);

        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
    }

    // EDITAR CONTRASE??A
    @PutMapping("/editar-contrasena/{username}")
    @ResponseStatus(HttpStatus.OK)
    public String eContrasena(@PathVariable("username") String username,
                              @RequestParam(value = "new-password") String newPassword,
                              @RequestParam(value = "code") Integer code) throws IOException {
        if (EmailUsernameUsuarioExiste(username)) {
            Users users = usersRepository.findByUsername(username);
            UsersPw usersPw = usersPwRepository.findByUserId(users.getId());
            if (usersPw.getCode().compareTo(code) == 0) {
                if (newPassword.length() >= 6 && newPassword.length() <= 20) {
                    if (usersService.editarContrasena(username, newPassword))
                        return "Contrase??a actualizada correctamente";
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la edicion");
                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contrase??a debe estar entre 6 y 20 caracteres");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los codigos no coinciden");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
    }

    @DeleteMapping("/eliminar-prueba/{username}")
    @ResponseStatus(HttpStatus.OK)
    public String eliminarUsuarioPrueba(@PathVariable("username") String username,
                                        @RequestParam(value = "code") Integer code) throws IOException {
        if (EmailUsernameUsuarioExiste(username)) {
            if (usersService.eliminarUsuario(username))
                return "Usuario eliminado correctamente";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en eliminar usuario");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
    }

    // ELIMINAR USUARIO
    @DeleteMapping("/eliminar/{username}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public Boolean eliminarUsuario(@PathVariable("username") String username) throws IOException {
        if (EmailUsernameUsuarioExiste(username)) {
            if (usersService.eliminarUsuario(username))
                return true;
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en eliminar usuario");

        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no existe");
    }

    // ELIMINAR TODOS LOS USUARIOS
    @DeleteMapping("/eliminar/all/usuarios/")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void eliminarAllUsuarios() {
        usersService.eliminarTodosUsuarios();
    }

    @PutMapping("/arreglar")
    @ResponseStatus(code = HttpStatus.OK)
    public void arreglar(){
        List<Users> users = usersRepository.findAll();
        users.forEach(x -> {
            x.setColour("0xFFEA4949");
            x.setFirstSession(true);
            usersRepository.save(x);
        });
    }


}
