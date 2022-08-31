package app.retos.controllers;

import app.retos.models.Users;
import app.retos.models.UsersPw;
import app.retos.repository.UsersPwRepository;
import app.retos.repository.UsersRepository;
import app.retos.services.IRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/usuarios")
public class UsersController {

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	UsersRepository uRepository;

	@Autowired
	UsersPwRepository upRepository;

	@Autowired
	IRegisterService uService;

	// LISTAS TODOS LOS USUARIOS
	@GetMapping("/listar/")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Users> listarUsuarios() throws IOException {
		try {
			return uRepository.findAll();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en listar usuarios: " + e.getMessage());
		}
	}

	// PREGUNTAR SI UN USUARIO EXISTE POR: USERNAME, MAIL, CELLPHONE
	@GetMapping("/existe/todos/{dato}")
	@ResponseStatus(HttpStatus.FOUND)
	public Boolean EmailUsernameUsuarioExiste(@PathVariable("dato") String dato) {
		return uRepository.existsByUsernameOrEmail(dato, dato);
	}

	// BUSCAR USUARIO POR USERNAME, EMAIL, CELLPHONE
	@GetMapping("/encontrar/cualquier-valor/{dato}")
	@ResponseStatus(HttpStatus.OK)
	public Users EncontrarPorCelularEmailUsername(@PathVariable("dato") String dato) {
		if(EmailUsernameUsuarioExiste(dato)) {
			return uRepository.findByUsernameOrEmail(dato, dato);
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario con el parametro: " + dato + " no existe");
	}

	// PETICION A INTERVENTOR PARA ELIMINAR USUARIO
	@PutMapping("/users/eliminarAdmin/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public void eliminarAdmin(@PathVariable("username") String username) {
		if (EmailUsernameUsuarioExiste(username)) {
			if (cbFactory.create("usuario").run(() -> iClient.peticionEliminarUsuarios(username),
					e -> errorConexion(e))) {
				log.info("Peticion de eliminacion enviada");
			}
		}
	}

	// BORRAR PETICION PARA ELIMINAR USUARIO
	@PutMapping("/users/eliminarPeticionAdmin/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public void eliminarPeticionUsuario(@PathVariable("username") String username) {
		if (EmailUsernameUsuarioExiste(username)) {
			if (cbFactory.create("usuario").run(() -> iClient.eliminarPeticionUsuarios(username),
					e -> errorConexion(e))) {
				log.info("Eliminacion de peticion lista");
			}
		}
	}

	// ELIMINAR USUARIO
	@DeleteMapping("/eliminar/{username}")
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public Boolean eliminarUsuario(@PathVariable("username") String username) throws IOException {
		if (EmailUsernameUsuarioExiste(username)) {
			uRepository.deleteByUsername(username);

			return true;
		}
		return false;
	}

	// EDITAR USUARIO
	@PutMapping("/users/editar/{username}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> editarUsuario(@PathVariable("username") String username, @RequestBody Usuario usuario) {
		if (UsernameUsuarioExiste(username)) {
			Usuario uDb = uRepository.findByUsername(username);
			uDb = uService.editUser(uDb, usuario);
			try {
				uRepository.save(uDb);
				return ResponseEntity.ok("Edicion Exitosa");
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la edicion");
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
	}

	// EDITAR CONTRASEÑA
	@PutMapping("/users/editarContrasena/{username}")
	@ResponseStatus(HttpStatus.OK)
	public Boolean eContrasena(@PathVariable("username") String username,
			@RequestParam(value = "password") String password) {
		if (UsernameUsuarioExiste(username)) {
			try {
				UsuarioPw uDb = upRepository.findByUsername(username);
				String newPassword = uService.codificar(password);
				uDb.setPassword(newPassword);
				upRepository.save(uDb);
				return true;
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la edicion");
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
	}

	@GetMapping("/users/verUsuario/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public String verUsername(@PathVariable("username") String username) {
		if (EmailUsernameUsuarioExiste(username)) {
			Users usuario = uRepository.findByUsernameOrEmailOrCellPhone(username, username, username);
			return usuario.getUsername();
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
	}

	// INICIAR SESION
	@GetMapping("/users/iniciarSesion/{username}")
	public UsersPw autenticacion(@PathVariable String username) throws InterruptedException {
		if (EmailUsernameUsuarioExiste(username)) {
			username = verUsername(username);
			return upRepository.findByUsername(username);
		}
		return null;
	}

	// ELIMINAR TODOS LOS USUARIOS
	@DeleteMapping("/eliminar/all/usuarios/")
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public void eliminarAllUsuarios() {
		uRepository.deleteAll();
		upRepository.deleteAll();
	}

//  ****************************	FUNCIONES TOLERANCIA A FALLOS	***********************************  //

	public Boolean errorConexion(Throwable e) {
		log.info(e.getMessage());
		return false;
	}

	public Boolean errorCreacionRecomendacion(Throwable e) {
		log.info(e.getMessage());
		return false;
	}

	public Boolean errorCreacionNotificaciones(Throwable e) {
		log.info(e.getMessage());
		return false;
	}

	public Boolean errorCreacionEstadisticas(Throwable e) {
		log.info(e.getMessage());
		return false;
	}

}
