package dds.monedero.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
    
    //si se hace esta comparacion en el filter, entonces el metodo fueDepositado(fecha) estaria al pedo
  	//¿por qué no usar dicho metodo?
    //Aparte que en este if no se pregunta si el depósito fue de la fecha de hoy
    //Toda esta comparacion del if la podriamos meter en un metodo
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    new Deposito(LocalDate.now(), cuanto).agregateA(this);
  }
  
  //Long Method: hora de dividir estas comparaciones en varios metodos
  public void sacar(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy; 
    
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, lÃ­mite: " + limite);
    }
    new Extraccion(LocalDate.now(), cuanto).agregateA(this);
  }
  
  //Long Parameter List: Podemos pasarle un Movimiento como parametro en vez de los datos del Movimiento
  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }
  
  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
    		//si se hace esta comparacion en el filter, entonces el metodo fueExtraido(fecha) estaria al pedo
    		//¿por qué no usar dicho metodo?
    		//Toda esta comparacion del filter la podriamos meter en un metodo
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
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
