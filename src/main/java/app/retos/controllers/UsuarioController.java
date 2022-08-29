package app.retos.controllers;

import app.retos.models.Users;
import app.retos.repository.UsersPwRepository;
import app.retos.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/usuarios")
public class UsuarioController {

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	UsersRepository uRepository;

	@Autowired
	UsersPwRepository upRepository;

	@Autowired
	IUsuarioService uService;

	// LISTAS TODOS USUARIOS
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
	public Boolean EmailCellPhoneUsernameUsuarioExiste(@PathVariable("dato") String dato) {
		return uRepository.existsByUsernameOrEmailOrCellPhone(dato, dato, dato);
	}

	// BUSCAR USUARIO POR USERNAME, EMAIL, CELLPHONE
	@GetMapping("/encontrar/cualquier-valor/{dato}")
	@ResponseStatus(HttpStatus.OK)
	public Users EncontrarPorCelularEmailUsername(@PathVariable("dato") String dato) {
		if(EmailCellPhoneUsernameUsuarioExiste(dato)) {
			return uRepository.findByUsernameOrEmailOrCellPhone(dato, dato, dato);
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario con el parametro: " + dato + " no existe");
	}


	// MICROSERVICIO REGISTRO -> CREAR USUARIO
	@PostMapping("/users/crearRegistro/")
	public Boolean crearUsuarios(@RequestBody Usuario u, @RequestParam String password,
			@RequestParam List<String> roles) throws IOException, ResponseStatusException {

		List<Roles> listaRoles = uService.obtenerRoles(roles);
		UsuarioPw usuarioPw = new UsuarioPw(u.getUsername(), password, true, 0, 0, listaRoles);
		UsuarioFiles uf = uService.crearUf(u.getUsername());

		if (cbFactory.create("usuario").run(
				() -> rmdClient.crearRecomendacion(u.getUsername(), u.getInterests(), u.getLocation()),
				e -> errorCreacionRecomendacion(e))) {
			log.info("Creacion Recomendacion Correcta");
			if (cbFactory.create("usuario").run(() -> nClient.crearNotificaciones(u.getUsername(), u.getEmail()),
					e -> errorCreacionNotificaciones(e))) {
				log.info("Creacion Notificacion Correcta");
				if (cbFactory.create("usuario").run(() -> eClient.crearUsuarioNotificaciones(u.getUsername()),
						e -> errorCreacionEstadisticas(e))) {
					log.info("Creacion Notificacion Correcta");
					uRepository.save(u);
					upRepository.save(usuarioPw);
					ufRepository.save(uf);
					return true;
				} else {
					rmdClient.eliminarRecomendacion(u.getUsername());
					nClient.eliminarNotificacion(u.getUsername());
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la creacion");
				}
			} else {
				rmdClient.eliminarRecomendacion(u.getUsername());
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la creacion");
			}
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la creacion");
	}

	// PETICION A INTERVENTOR PARA ELIMINAR USUARIO
	@PutMapping("/users/eliminarAdmin/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public void eliminarAdmin(@PathVariable("username") String username) {
		if (UsernameUsuarioExiste(username)) {
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
		if (UsernameUsuarioExiste(username)) {
			if (cbFactory.create("usuario").run(() -> iClient.eliminarPeticionUsuarios(username),
					e -> errorConexion(e))) {
				log.info("Eliminacion de peticion lista");
			}
		}
	}

	// ELIMINAR USUARIO
	@DeleteMapping("/users/eliminar/{username}")
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public Boolean eliminarUsuario(@PathVariable("username") String username) throws IOException {
		if (EmailCellPhoneUsernameUsuarioExiste(username)) {
			Usuario uDelete = uRepository.findByUsernameOrEmailOrCellPhone(username, username, username);
			UsuarioFiles ufDelete = ufRepository.findByUsername(uDelete.getUsername());
			UsuarioPw upDelete = upRepository.findByUsername(uDelete.getUsername());
			uRepository.delete(uDelete);
			ufRepository.delete(ufDelete);
			upRepository.delete(upDelete);

			if (cbFactory.create("usuario").run(() -> rmdClient.eliminarRecomendacion(upDelete.getUsername()),
					e -> errorConexion(e))) {
				log.info("Eliminacion Recomendacion Correcta");
			}
			if (cbFactory.create("usuario").run(() -> bClient.eliminarBusquedasUsername(upDelete.getUsername()),
					e -> errorConexion(e))) {
				log.info("Eliminacion Recomendacion Correcta");
			}
			if (cbFactory.create("usuario").run(() -> nClient.eliminarNotificacion(upDelete.getUsername()),
					e -> errorConexion(e))) {
				log.info("Eliminacion Notificacion Correcta");
			}
			if (cbFactory.create("usuario").run(() -> eClient.borrarEstadisticasUsuario(upDelete.getUsername()),
					e -> errorConexion(e))) {
				log.info("Eliminacion Estadistica Correcta");
			}
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

	// EDITAR UBICACION DE USUARIO
	@PutMapping("/users/editarUbicacion/{username}")
	@ResponseStatus(HttpStatus.OK)
	public Boolean eUbicacion(@PathVariable("username") String username,
			@RequestParam(value = "location") List<Double> usuarioLocation) {
		if (UsernameUsuarioExiste(username)) {
			try {
				Usuario uDb = uRepository.findByUsernameOrEmailOrCellPhone(username, username, username);
				uDb.setLocation(new ArrayList<Double>(Arrays.asList(
						(new BigDecimal(usuarioLocation.get(0)).setScale(5, RoundingMode.HALF_UP)).doubleValue(),
						(new BigDecimal(usuarioLocation.get(1)).setScale(5, RoundingMode.HALF_UP).doubleValue()))));
				if (cbFactory.create("usuario").run(() -> rmdClient.editarUbicacion(username, uDb.getLocation()),
						e -> errorConexion(e))) {
					log.info("Edicion Registro Correcta");
					uRepository.save(uDb);
				}
				return true;
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

	// ASIGNAR ROLE MODERATOR
	@PutMapping("/users/roleModerator/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean asignarModerator(@PathVariable("username") String username) {
		if (UsernameUsuarioExiste(username)) {
			UsuarioPw usuario = upRepository.findByUsername(username);
			Roles userRole1 = new Roles("2", "ROLE_MODERATOR");
			List<Roles> roles = usuario.getRoles();
			if (!roles.contains(userRole1)) {
				roles.add(userRole1);
				usuario.setRoles(roles);
				upRepository.save(usuario);
				return true;
			} else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Usuario: " + username + " ya tiene Role Moderator");
			}
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
		}
	}

	// ASIGNAR ROLE ADMIN
	@PutMapping("/users/roleAdmin/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> asignarAdmin(@PathVariable("username") String username) {
		if (UsernameUsuarioExiste(username)) {
			UsuarioPw usuario = upRepository.findByUsername(username);
			Roles userRole1 = new Roles("1", "ROLE_ADMIN");
			List<Roles> roles = usuario.getRoles();
			if (!roles.contains(userRole1)) {
				roles.add(userRole1);
				usuario.setRoles(roles);
				upRepository.save(usuario);
				return ResponseEntity.ok("Role Admin asignado");
			} else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Usuario: " + username + " ya tiene Role Admin");
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
	}

	// CAMBIAR IMAGEN
	@PutMapping("/users/file/uploadImage/{username}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> uploadImage(@PathVariable("username") String username,
			@RequestParam(value = "image") MultipartFile file) {
		if (UsernameUsuarioExiste(username)) {
			UsuarioFiles uploadFile = uService.ponerImagen(username, file);
			ufRepository.save(uploadFile);
			return ResponseEntity.ok("Imagen añadida");
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
	}

	// IMAGEN A BINARIO
	@GetMapping("/users/file/binary/{username}")
	@ResponseStatus(HttpStatus.OK)
	public String binaryToStringFile(@PathVariable("username") String username) {
		if (ufRepository.existsByUsername(username)) {
			UsuarioFiles uf = ufRepository.findByUsername(username);
			byte[] data = null;
			UsuarioFiles file = ufRepository.findImageById(uf.getId(), UsuarioFiles.class);
			if (file != null) {
				data = file.getContent().getData();
			}
			return Base64.getEncoder().encodeToString(data);
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
	}

	// DESCARGAR IMAGEN
	@GetMapping(value = "/users/file/downloadImage/{username}", produces = { MediaType.IMAGE_JPEG_VALUE,
			MediaType.IMAGE_PNG_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public byte[] image(@PathVariable String username) {
		UsuarioFiles usuario = ufRepository.findByUsername(username);
		byte[] data = null;
		UsuarioFiles file = ufRepository.findImageById(usuario.getId(), UsuarioFiles.class);
		if (file != null) {
			data = file.getContent().getData();
		}
		return data;
	}



	// PREGUNTAR SI UN USUARIO EXISTE: EMAIL, CORREO, USERNAME
	@GetMapping("/users/usuarioExisteDatos/")
	@ResponseStatus(HttpStatus.FOUND)
	public Boolean preguntarUsuarioExiste(@RequestParam(value = "username") String username,
			@RequestParam(value = "email") String email, @RequestParam(value = "cellPhone") String cellPhone)
			throws InterruptedException {
		return uRepository.existsByUsernameOrEmailOrCellPhone(username, email, cellPhone);
	}





	// PREGUNTAR SI UN USUARIO EXISTE POR CEDULA
	@GetMapping("/users/existCedula/{cedula}")
	@ResponseStatus(HttpStatus.FOUND)
	public Boolean existsByCedula(@PathVariable("cedula") String cedula) {
		return uRepository.existsByCedula(cedula);
	}

	// OBTENER EDAD
	@GetMapping("/users/obtenerEdad/{username}")
	public ResponseEntity<?> edadUsurio(@PathVariable("username") String username) {
		if (UsernameUsuarioExiste(username)) {
			Usuario usuario = uRepository.findByUsername(username);
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate fechaNac = LocalDate.parse(usuario.getBirthDate(), fmt);
			LocalDate ahora = LocalDate.now();
			Period periodo = Period.between(fechaNac, ahora);
			return ResponseEntity.ok(periodo.getYears());
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + username + " no existe");
	}

	// CREAR PRIMER USUARIO
	@PostMapping("/users/crearUsuarioMod/")
	@ResponseStatus(HttpStatus.CREATED)
	public Boolean nuevoUsuarioMod(@RequestParam("username") String username,
			@RequestParam("cellPhone") String cellPhone, @RequestParam("email") String email,
			@RequestParam("cedula") String cedula, @RequestParam("name") String name,
			@RequestParam("lastName") String lastName, @RequestParam("birthDate") String birthDate,
			@RequestParam("gender") Integer gender, @RequestParam("phone") String phone,
			@RequestParam("economicActivity") String economicActivity,
			@RequestParam("economicData") List<String> economicData, @RequestParam("interests") List<String> interests,
			@RequestParam("location") List<Double> location, @RequestParam("headFamily") Boolean headFamily,
			@RequestParam("stakeHolders") String stakeHolders, @RequestParam("contrasenia") String password,
			@RequestParam(value = "image") MultipartFile file) {
		if (!UsernameUsuarioExiste(username)) {
			Usuario usuario = new Usuario(username, cellPhone, email, cedula, name, lastName, birthDate, gender, phone,
					economicActivity, economicData, interests, location, headFamily, stakeHolders);
			UsuarioPw uPw = uService.usuarioPasword(usuario.getUsername(), password);
			UsuarioFiles uploadFile = uService.ponerImagen(usuario.getUsername(), file);

			if (cbFactory.create("usuario").run(() -> rmdClient.crearRecomendacion(usuario.getUsername(),
					usuario.getInterests(), usuario.getLocation()), e -> errorCreacionRecomendacion(e))) {
				log.info("Creacion Recomendacion Correcta");
				if (cbFactory.create("usuario").run(
						() -> nClient.crearNotificaciones(usuario.getUsername(), usuario.getEmail()),
						e -> errorCreacionNotificaciones(e))) {
					log.info("Creacion Notificacion Correcta");
					if (cbFactory.create("usuario").run(() -> eClient.crearUsuarioNotificaciones(usuario.getUsername()),
							e -> errorCreacionEstadisticas(e))) {
						log.info("Creacion Notificacion Correcta");
						uRepository.save(usuario);
						upRepository.save(uPw);
						ufRepository.save(uploadFile);
						return true;
					} else {
						rmdClient.eliminarRecomendacion(usuario.getUsername());
						nClient.eliminarNotificacion(usuario.getUsername());
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la creacion");
					}
				} else {
					rmdClient.eliminarRecomendacion(usuario.getUsername());
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la creacion");
				}
			}
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la creacion");
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario ya existe");

	}

	// INICIAR SESION
	@GetMapping("/users/iniciarSesion/{username}")
	public UsuarioPw autenticacion(@PathVariable String username) throws InterruptedException {
		if (EmailCellPhoneUsernameUsuarioExiste(username)) {
			username = verUsername(username);
			return upRepository.findByUsername(username);
		}
		return null;
	}

	// VERIFICAR REGISTRO
	@GetMapping("/users/registroExistencia/")
	public Boolean registroExistenciaUsuarios(@RequestParam(value = "username") String username,
			@RequestParam(value = "email") String email, @RequestParam(value = "cellPhone") String cellPhone)
			throws InterruptedException {
		Boolean bandera = preguntarUsuarioExiste(username, email, cellPhone);
		return bandera;
	}

	// CEDULA EXISTE
	@GetMapping("/users/registroCedula/")
	public Boolean registroCedula(@RequestParam(value = "cedula") String cedula) throws InterruptedException {
		Boolean bandera = existsByCedula(cedula);
		return bandera;
	}

	@PutMapping("/users/arreglar/")
	public String arreglarUsuarios() throws IOException {

		return null;
	}

	// ELIMINAR TODOS LOS USUARIOS
	@DeleteMapping("/users/eliminar/all/usuarios/")
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public void eliminarAllUsuarios() {
		uRepository.deleteAll();
		ufRepository.deleteAll();
		upRepository.deleteAll();

		if (cbFactory.create("usuario").run(() -> rmdClient.eliminarAllUsuario(), e -> errorConexion(e))) {
			log.info("Eliminacion Todos usuarios, Recomendacion Correcta");
		}
		if (cbFactory.create("usuario").run(() -> nClient.eliminarAllUsuario(), e -> errorConexion(e))) {
			log.info("Eliminacion Todos usuarios, Notificacion Correcta");
		}
		if (cbFactory.create("usuario").run(() -> eClient.eliminarAllUsuario(), e -> errorConexion(e))) {
			log.info("Eliminacion Todos usuarios, Correcta");
		}
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
