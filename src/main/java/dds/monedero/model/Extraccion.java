package dds.monedero.model;

import java.time.LocalDate;

public class Extraccion extends Movimiento{
	public Extraccion(LocalDate fecha, double monto) {
		super(fecha, monto);
	}
	
	public boolean isExtraccion() {
		return true;
	}

	@Override
	public double calcularValor(Cuenta cuenta) {
		return cuenta.getSaldo() - getMonto();
	}
}
