package app.retos.services;

import app.retos.models.Users;
import app.retos.models.UsersPw;
import app.retos.responses.UserAutentication;

public interface IUsersService {

    Boolean editarUsuario(String username, Users users);

    Boolean editarContrasena(String username, String password);

    UserAutentication encontrarUsuarioPw(String username);
    
    Boolean eliminarUsuario(String username);

    void eliminarTodosUsuarios();

    Boolean usuarioExiste(String username);


    String enviarMensajeVerificacion(String username);
}
