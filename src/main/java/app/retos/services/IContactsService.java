package app.retos.services;

import app.retos.models.Contacts;

import java.util.List;

public interface IContactsService {

    Boolean crearContactos(String userId, Contacts contacts);

    Boolean editarContactos(String userId, String email, String cellPhone, Contacts contacts);

    Boolean eliminarContacto(String userId, String email, String cellPhone);

    String obtenerUserId(String username);

    String obtenerUsername(String userId);

    List<Contacts> listarContactos(String userId);
}
