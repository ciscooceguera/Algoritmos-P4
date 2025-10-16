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
    public Carta pop(){
        Carta cartaTemp = carta;
        carta = null;
        return cartaTemp;
    }
    public boolean validarMovimiento(Carta carta){
        return carta!= null && isEmpty();
    }
    public boolean push(Carta carta){
        if (!validarMovimiento(carta)){
            return false;
        }
        this.carta = carta;
        return true;
    }
    public void clear(){
        carta = null;
    }
}
