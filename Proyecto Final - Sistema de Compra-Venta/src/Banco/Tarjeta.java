package Banco;
import java.time.YearMonth;

public class Tarjeta {
    private String numero;
    private String titular;
    private String cvv;
    private YearMonth fechaExpiracion;
    private double saldo;

    public Tarjeta(String numero, String titular, String cvv, int mesExpiracion, int añoExpiracion, double saldo){
        if (añoExpiracion < 100) {
            añoExpiracion += 2000; // 26 -> 2026
        }
        if(mesExpiracion < 1 || mesExpiracion > 12) {
            throw new IllegalArgumentException("Mes inválido");
        }

        this.numero = numero;
        this.titular = titular;
        this.cvv = cvv;
        this.fechaExpiracion = YearMonth.of(añoExpiracion, mesExpiracion);
        this.saldo = saldo;
    }

    public String getNumero(){
        return numero;
    }

    public String getTitular(){
        return titular;
    }
    
    public String getCVV(){
        return cvv;
    }

    public double getSaldo(){
        return saldo;
    } 

    public boolean retirarDinero(double monto){
        if(monto <= saldo){
            saldo -= monto;
            return true;
        } 
        return false;
    }    
    
    public String getFechaExpiracion() {
        
        return String.format("%02d/%02d",
                fechaExpiracion.getMonthValue(),
                fechaExpiracion.getYear() % 100);
    }

     public boolean validarNum() {
        return numero != null && numero.matches("\\d{16}");
    }

    public boolean validarCVV() {
        return cvv != null && cvv.matches("\\d{3}");
    }

    public boolean validarFecha() {
        return fechaExpiracion != null &&
               !fechaExpiracion.isBefore(YearMonth.now());
    }

    public boolean validarTarjeta() {
        return validarNum() && validarCVV() && validarFecha();
    }

}
