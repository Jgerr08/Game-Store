package Usuario;

public class Administrador extends Usuario{

    public Administrador(String id, String contraseña) {
        super(id, contraseña);

    }

    @Override
    void iniciarSesion() {
    }

    @Override
    void cerrarSesion() {
    }
    
}
