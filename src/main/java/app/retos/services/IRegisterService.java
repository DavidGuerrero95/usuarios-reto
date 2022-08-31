package app.retos.services;

import app.retos.models.Roles;
import app.retos.requests.Register;

import java.util.List;

public interface IRegisterService {

    public Boolean crearUsuario(Register register);

    public Boolean crearPrimerUsuario();

}
