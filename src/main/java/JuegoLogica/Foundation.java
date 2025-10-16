package JuegoLogica;

import Logica.Carta;
import Logica.Palo;

public class Foundation {
    private final Palo palo;
    private final ListaSimple<Carta> cartas;
    public Foundation(Palo palo) {
        this.palo = palo;
        cartas = new ListaSimple<>();
    }
    public Palo getPalo() {
        return palo;
    }
    public int getSize(){
        return cartas.getSize();
    }
    public boolean isEmpty(){
        return cartas.getSize() == 0;
    }
    public Carta peek(){
        return cartas.getFin();
    }
    public Carta pop(){
        return cartas.eliminaFinal();
    }
    public boolean validarMovimiento(Carta carta){
        if (carta == null || carta.getPalo() != palo){
            return false;
        }
        Carta topCarta = cartas.getFin();
        if (topCarta == null){
            return carta.getValor() == 1;
        }
        return topCarta.getValor()+1 == carta.getValor();
    }
    public boolean push(Carta carta){
        if (!validarMovimiento(carta)){
            return false;
        }
        cartas.insertaFinal(carta);
        return true;
    }
    public boolean isFull(){
        return getSize() == 13;
    }
    public void clear(){
        while (cartas.eliminaFinal()!=null){

        }
    }

}
