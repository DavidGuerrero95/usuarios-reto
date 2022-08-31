package app.retos.services;

import app.retos.models.Contacts;

public interface IContactsService {

    Boolean crearContactos(String username, Contacts contacts);

    Boolean editarContactos(String username, String email, String cellPhone, Contacts contacts);

    Boolean eliminarContacto(String username, String email, String cellPhone);

}
