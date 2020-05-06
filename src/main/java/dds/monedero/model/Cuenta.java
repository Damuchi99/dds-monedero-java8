package dds.monedero.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) {
	validarMontoNegativo(cuanto);
    
    if (getDepositosDeFecha(LocalDate.now()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    new Deposito(LocalDate.now(), cuanto).agregateA(this);
  }
  
  public Stream<Movimiento> getDepositosDeFecha(LocalDate fecha){
	  return getMovimientos().stream()
		.filter(movimiento -> movimiento.isDeposito() && movimiento.getFecha().equals(fecha));
  }
  
  public void sacar(double cuanto) {
	  validarMontoNegativo(cuanto);
	  validarSaldoMenor(cuanto);
	  validarMaximaExtraccionDiaria(cuanto);
	  
	  new Extraccion(LocalDate.now(), cuanto).agregateA(this);
  }
  
  public void validarMontoNegativo(double cuanto) {
	  if (cuanto <= 0) {
	      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
	  }
  }
  
  public void validarSaldoMenor(double cuanto) {
	  if (getSaldo() - cuanto < 0) {
	      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
	  }
  }
  
  public void validarMaximaExtraccionDiaria(double cuanto) {
	  double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
	  double limite = 1000 - montoExtraidoHoy; 
	    
	  if (cuanto > limite) {
	      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
	          + " diarios, límite: " + limite);
	  }
  }
  
  public void agregarMovimiento(Movimiento movimiento) {
    movimientos.add(movimiento);
  }
  
  public double getMontoExtraidoA(LocalDate fecha) {
    return getExtraccionesDeFecha(fecha)
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }
  
  public Stream<Movimiento> getExtraccionesDeFecha(LocalDate fecha){
	  return getMovimientos().stream()
		.filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha));
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
