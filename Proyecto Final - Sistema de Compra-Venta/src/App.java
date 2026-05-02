
import Banco.Banco;
import Banco.Tarjeta;
import Menu.Menu;
import Pago.PagoElectronico;
import Pago.Transaccion;

public class App {
    public static void main(String[] args) throws Exception {
        Tarjeta tarjeta = new Tarjeta("4588583844484748", "Jesús", "383", 12, 24, 199);
        Banco banco = new Banco("Gringotts");
        banco.agregarTarjeta(tarjeta);
        PagoElectronico pagoElectronico = new PagoElectronico(200, banco);
        pagoElectronico.realizarPago();
        System.out.println(pagoElectronico.getEstado()); 
    }
}
