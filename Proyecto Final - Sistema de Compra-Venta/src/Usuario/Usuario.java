package Usuario;

public abstract class Usuario {
    String id;
    String contraseña;

    public Usuario(String id, String contraseña){
        this.id = id;
        this.contraseña = contraseña;
    }

    abstract void iniciarSesion();
    abstract void cerrarSesion();
}
