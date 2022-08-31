package app.retos.services;

import app.retos.models.Users;
import app.retos.models.UsersPw;

public interface IUsersService {

    Boolean editarUsuario(String username, Users users);

    Boolean editarContrasena(String username, String password);

    UsersPw encontrarUsuarioPw(String username);
    
    Boolean eliminarUsuario(String username);

    void eliminarTodosUsuarios();
}
