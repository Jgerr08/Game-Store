package Usuario;

import Inventario.Inventario;

public class Administrador extends Usuario{

    public Administrador(String id, String contraseña) {
        super(id, contraseña);

    }

@Override
public void iniciarSesion() {
    System.out.println("Bienvenido administrador " + getId());
}

@Override
public void cerrarSesion() {
    System.out.println("Sesión cerrada");
}


}
