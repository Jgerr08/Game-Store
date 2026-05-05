package Usuario;

import Venta.Carrito;

public class Cliente extends Usuario{

    private Carrito carrito; 

    public Cliente(String id, String contraseña) {
        super(id, contraseña);
    }

    @Override
    public void iniciarSesion() {
        System.out.println("Bienvenido cliente " + getId());
    }

    @Override
    public void cerrarSesion() {
        System.out.println("Sesion cerrada");
    }

}
