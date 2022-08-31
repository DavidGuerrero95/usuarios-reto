package app.retos.services;

import app.retos.requests.Register;

public interface IRegisterService {

    Boolean crearUsuario(Register register);

    Boolean crearPrimerUsuario();

    String codificar(String password);

}
