package JuegoLogica;
import Logica.Carta;
public class Reserva {
    private Carta carta;
    public Reserva() {
        this.carta = null;
    }
    public boolean isEmpty(){
        return carta == null;
    }
    public Carta peek(){
        return carta;
    }
    // Retorna la carta en la reserva
    public Carta pop(){
        Carta cartaTemp = carta;
        carta = null;
        return cartaTemp;
    }
    // Valida el movimiento; que la carta no sea null y la reserva no contenga una carta previa
    public boolean validarMovimiento(Carta carta){
        return carta!= null && isEmpty();
    }
    // mete una carta en la reserva, valida el movimiento con el metodo previo
    public boolean push(Carta carta){
        if (!validarMovimiento(carta)){
            return false;
        }
        this.carta = carta;
        return true;
    }
    // Limpia la reserva
    public void clear(){
        carta = null;
    }
}
