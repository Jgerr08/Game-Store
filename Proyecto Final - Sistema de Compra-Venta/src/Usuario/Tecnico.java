package Usuario;

public class Tecnico extends Usuario{

    public Tecnico(String id, String contraseña) {
        super(id, contraseña);
    }

 @Override
public void iniciarSesion() {
    System.out.println("Bienvenido técnico " + getId());
}

@Override
public void cerrarSesion() {
    System.out.println("Sesion cerrada");
}
}