package app.retos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-notificaciones")
public interface NotificationsFeignClient {

    @PostMapping("/notificaciones/registro/")
	void enviarMensajeSuscripciones(@RequestParam("email") String email, @RequestParam("codigo") String codigo);
}
