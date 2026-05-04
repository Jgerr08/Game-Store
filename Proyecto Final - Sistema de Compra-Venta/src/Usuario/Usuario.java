package Usuario;

public abstract class Usuario {
    String id;
    String contraseña;

    public Usuario(String id, String contraseña) {
        this.id = id;
        this.contraseña = contraseña;
    }

    public String getId() { return id; }
    public String getContraseña() { return contraseña; }

    public abstract void iniciarSesion();
    public abstract void cerrarSesion();
}