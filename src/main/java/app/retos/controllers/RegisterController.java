package app.retos.controllers;

import app.retos.models.Roles;
import app.retos.repository.UsersPwRepository;
import app.retos.repository.UsersRepository;
import app.retos.requests.Register;
import app.retos.services.IRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/registro")
public class RegisterController {

    @Autowired
    UsersRepository uRepository;

    @Autowired
    IRegisterService registerService;

    // REGISTRAR UN USUARIO
    @PostMapping("/crear")
    @ResponseStatus(code = HttpStatus.CREATED)
    public String crearUsuarioRegistro(@RequestBody @Validated Register register){
        if(!uRepository.existsByUsernameOrEmail(register.getUsername(), register.getEmail())){
            if(registerService.crearUsuario(register))
                return "Usuario: "+register.getUsername()+" Registrado satisfactoriamente";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al crear el usuario");
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
    }

    // CREAR PRIMER USUARIO
    @PostMapping("/primer-usuario/")
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean nuevoUsuarioMod() {
        if(registerService.crearPrimerUsuario())
            return true;
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al crear el usuario");
    }

}
