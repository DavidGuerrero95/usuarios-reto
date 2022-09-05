package app.retos.controllers;

import app.retos.models.Users;
import app.retos.models.UsersPw;
import app.retos.repository.UsersPwRepository;
import app.retos.repository.UsersRepository;
import app.retos.services.IUsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
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
            log.info("Se conecto y el usuario existe?: "+exist);
            return exist;
        } catch (Exception e2) {
            throw new IOException("Error al encontrar usuario: " + e2.getMessage());
        }
    }

    @GetMapping("/preguntar/usuarioExiste")
    public Boolean preguntarUsuarioExiste(@RequestParam(value = "username") String username) throws InterruptedException {
        return usersService.usuarioExiste(username);
    }

    // INICIAR SESION
    @GetMapping("/login/{username}")
    public UsersPw autenticacion(@PathVariable("username") String username) throws InterruptedException, ResponseStatusException, IOException {
        if (EmailUsernameUsuarioExiste(username)) {
            log.info("Conexion establecida: "+username);
            UsersPw usersPw = usersService.encontrarUsuarioPw(username);
            log.info("retorno: "+usersPw.getUsername());
            return usersPw;
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
    public String editarUsuario(@PathVariable("username") String username, @RequestBody Users users) throws IOException {
        if (EmailUsernameUsuarioExiste(username)) {
            if (usersService.editarUsuario(username, users))
                return "Edición del usuario de manera exitosa";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la edicion");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
    }

    // EDITAR CONTRASEÑA
    @PutMapping("/editar-contrasena/{username}")
    @ResponseStatus(HttpStatus.OK)
    public String eContrasena(@PathVariable("username") String username,
                              @RequestParam(value = "password") String password) throws IOException {
        if (password.length() >= 6 && password.length() <= 20) {
            if (EmailUsernameUsuarioExiste(username)) {
                if (usersService.editarContrasena(username, password)) return "Contraseña actualizada correctamente";
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la edicion");
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contraseña debe estar entre 6 y 20 caracteres");
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


}
