package app.retos.controllers;

import app.retos.models.Register;
import app.retos.repository.RegisterRepository;
import app.retos.repository.UsersRepository;
import app.retos.services.IRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/registro")
public class RegisterController {

    @Autowired
    UsersRepository uRepository;

    @Autowired
    IRegisterService registerService;

    @Autowired
    RegisterRepository registerRepository;

    // ENVIAR MENSAJE PARA CREAR UN USUARIO
    @PostMapping("/enviar")
    @ResponseStatus(code = HttpStatus.CREATED)
    public String enviarMensajeDeRegistro(@RequestBody @Validated Register register) {
        if (!uRepository.existsByUsernameOrEmail(register.getUsername(), register.getEmail())) {
            registerService.crearNuevoUsuario(register);
            return "Codigo de verificación enviado a su correo: "+register.getEmail();
        }
        if(uRepository.existsByUsername(register.getUsername()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El username ya existe");
        throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya existe");
    }

    // REGISTRAR UN USUARIO
    @PostMapping("/confirmar/{username}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public String crearUsuarioRegistroCodigo(@PathVariable("username") String username, @RequestParam("codigo") String codigo) {
        Register register = registerRepository.findByUsername(username);
        Long minutes = new Date().getTime();
        long diference = (Math.abs(register.getMinutes() - minutes)) / 1000;
        long limit = (600 * 1000) / 1000L;
        if (diference <= limit) {
            if (register.getCode().equals(codigo)) {
                register.setCode("0");
                if (!uRepository.existsByUsernameOrEmail(register.getUsername(), register.getEmail())) {
                    if (registerService.crearUsuario(register))
                        return "Usuario: " + register.getUsername() + " Registrado satisfactoriamente";
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al crear el usuario");
                }
                registerRepository.delete(register);
                if(uRepository.existsByUsername(register.getUsername()))
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "El username ya existe");
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya existe");
            }
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Los codigos no coinciden");
        }
        registerRepository.delete(register);
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Codigo expirado, intente otra vez");
    }

    // CREAR PRIMER USUARIO
    @PostMapping("/primer-usuario/")
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean nuevoUsuarioMod() {
        if (registerService.crearPrimerUsuario())
            return true;
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al crear el usuario");
    }

}
