package app.retos.services;

import app.retos.models.Register;

public interface IRegisterService {

    Boolean crearUsuario(Register register);

    Boolean crearPrimerUsuario();

    String codificar(String password);

    void crearNuevoUsuario(Register register);

}
