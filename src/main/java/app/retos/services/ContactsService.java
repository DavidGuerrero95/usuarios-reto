package app.retos.services;

import app.retos.models.Contacts;
import app.retos.models.Users;
import app.retos.repository.ContactsRepository;
import app.retos.repository.UsersRepository;
import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class ContactsService implements IContactsService {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    ContactsRepository contactsRepository;

    @Override
    public Boolean crearContactos(String userId, Contacts contacts) {
        if (contactsRepository.existsByUserIdAndCellPhone(userId, contacts.getCellPhone())) {
            String name = contacts.getName();
            String lastName = contacts.getLastName();
            String cellPhone = contacts.getCellPhone();
            contacts = contactsRepository.findByUserIdAndCellPhone(userId, contacts.getCellPhone());
            contacts.setName(name);
            contacts.setLastName(lastName);
            contacts.setCellPhone(cellPhone);
        } else if (contactsRepository.existsByUserIdAndEmail(userId, contacts.getEmail())) {
            String name = contacts.getName();
            String lastName = contacts.getLastName();
            String email = contacts.getEmail();
            contacts = contactsRepository.findByUserIdAndEmail(userId, contacts.getEmail());
            contacts.setName(name);
            contacts.setLastName(lastName);
            contacts.setEmail(email);
        } else contacts.setUserId(userId);
        try {
            contactsRepository.save(contacts);
            guardarContactosUsuario(userId);
            return true;
        } catch (MongoException e) {
            log.error("Error en la creación: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean editarContactos(String userId, String email, String cellPhone, Contacts contacts) {
        Contacts c = contactsRepository.findByUserIdAndEmailAndCellPhone(userId, email, cellPhone);
        if (contacts.getName() != null) c.setName(contacts.getName());
        if (contacts.getLastName() != null) c.setLastName(contacts.getLastName());
        if (contacts.getEmail() != null) c.setEmail(contacts.getEmail());
        if (contacts.getCellPhone() != null) c.setCellPhone(contacts.getCellPhone());
        try {
            contactsRepository.save(c);
            guardarContactosUsuario(userId);
            return true;
        } catch (MongoException e) {
            log.error("Error en la edición: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean eliminarContacto(String userId, String email, String cellPhone) {
        try {
            contactsRepository.deleteByUserIdAndEmailAndCellPhone(userId, email, cellPhone);
            guardarContactosUsuario(userId);
            return true;
        } catch (MongoException e) {
            log.error("Error en eliminar contacto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String obtenerUserId(String username) {
        return usersRepository.findByUsername(username).getId();
    }

    @Override
    public String obtenerUsername(String userId) {
        AtomicReference<String> username = new AtomicReference<>("");
        Optional<Users> users = usersRepository.findById(userId);
        users.ifPresent(x -> {
            username.set(x.getUsername());
        });
        return username.get();
    }

    @Override
    public List<Contacts> listarContactos(String userId) {
        return contactsRepository.findByUserId(userId);
    }

    private void guardarContactosUsuario(String userId) {
        String username = obtenerUsername(userId);
        Users u = usersRepository.findByUsername(username);
        u.setContacts(contactsRepository.findByUserId(userId));
        usersRepository.save(u);
    }
}
