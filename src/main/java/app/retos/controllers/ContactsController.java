package app.retos.controllers;

import app.retos.models.Contacts;
import app.retos.repository.ContactsRepository;
import app.retos.repository.UsersRepository;
import app.retos.services.IContactsService;
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
@RequestMapping("/contactos")
public class ContactsController {

    @Autowired
    UsersController usersController;

    @Autowired
    IContactsService contactsService;

    @Autowired
    ContactsRepository contactsRepository;

    @GetMapping("/listar/{username}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Contacts> listarContactos(@PathVariable("username") String username){
        return contactsService.listarContactos(contactsService.obtenerUserId(username));
    }

    @PostMapping("/crear/{username}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public String crearContactoUsuario(@PathVariable("username") String username, @RequestBody @Validated Contacts contacts) throws IOException {
        if (usersController.EmailUsernameUsuarioExiste(username)) {
            if (contactsService.crearContactos(contactsService.obtenerUserId(username), contacts)) return "Contacto creado correctamente";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la creación de contactos");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario: " + username + " no existe");
    }

    @PutMapping("/editar/{username}/email/{email}/cellPhone/{cellPhone}")
    @ResponseStatus(code = HttpStatus.OK)
    public String editarContacto(@PathVariable("username") String username, @PathVariable("email") String email,
                                 @PathVariable("cellPhone") String cellPhone, @RequestBody Contacts contacts) throws IOException {
        if (usersController.EmailUsernameUsuarioExiste(username)) {
            String userId = contactsService.obtenerUserId(username);
            if(contactsRepository.existsByUserIdAndEmailAndCellPhone(userId, email, cellPhone)){
                if (contactsService.editarContactos(userId, email, cellPhone, contacts))
                    return "Contacto editado correctamente";
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la edición de contactos");
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El contacto no existe");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario: " + username + " no existe");
    }

    @DeleteMapping("/eliminar/{username}/email/{email}/cellPhone/{cellPhone}")
    @ResponseStatus(code = HttpStatus.OK)
    public String eliminarContacto(@PathVariable("username") String username, @PathVariable("email") String email,
                                   @PathVariable("cellPhone") String cellPhone) throws IOException {
        if (usersController.EmailUsernameUsuarioExiste(username)) {
            String userId = contactsService.obtenerUserId(username);
            if(contactsRepository.existsByUserIdAndEmailAndCellPhone(userId, email, cellPhone)){
                if (contactsService.eliminarContacto(userId, email, cellPhone)) return "Contacto eliminado correctamente";
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la eliminacion de contactos");
            }throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El contacto no existe");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario: " + username + " no existe");
    }

}
