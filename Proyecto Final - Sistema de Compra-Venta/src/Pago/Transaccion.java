package Pago;

public abstract class Transaccion {
    
    //Atributos
    protected double monto;
    protected Estado estado;

    //Métodos
    public Transaccion (double monto ) {
        if (monto <= 0){
            throw new IllegalArgumentException("El valor del monto debe de ser mayor a $0");
        }
        this.monto = monto;
    }

    public abstract void realizarPago();

    public  double getMonto(){
        return monto;
    };
    public Estado getEstado(){
        return estado;
    };

}
