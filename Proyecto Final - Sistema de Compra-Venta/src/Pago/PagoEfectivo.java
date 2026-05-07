package Pago;

import java.util.Scanner;

public class PagoEfectivo extends Transaccion {

    private double montoPagado;
    private double cambio;

    public PagoEfectivo(double monto) {
        super(monto);
    }

    @Override
    public void realizarPago() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== PAGO EN EFECTIVO ===");
        System.out.println("Total a pagar: $" + getMonto());

        System.out.print("Dinero recibido: ");
        double pagado = scanner.nextDouble();

        if (pagado < getMonto()) {

            System.out.println("Monto insuficiente");
            this.estado = Estado.RECHAZADO;
            return;
        }

        this.montoPagado = pagado;
        this.cambio = pagado - getMonto();
        this.estado = Estado.EXITOSO;

        System.out.println("Pago exitoso");
        System.out.println("Cambio: $" + cambio);
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public double getCambio() {
        return cambio;
    }
}