package app.retos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-notificaciones")
public interface NotificationsFeignClient {

    @PostMapping("/notificaciones/registro/")
	void enviarMensajeSuscripciones(@RequestParam("email") String email, @RequestParam("codigo") String codigo);

    @GetMapping("/notificaciones/edicion-perfil/enviar/{username}")
    public void enviarCodigoEditarContrasenia(@PathVariable("username") String username, @RequestParam(value = "email") String email,
                                              @RequestParam(value = "codigo") Integer codigo);
}
