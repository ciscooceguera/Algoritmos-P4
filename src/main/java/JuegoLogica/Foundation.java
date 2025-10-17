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
    // Getters
    public Palo getPalo() {
        return palo;
    }
    public int getSize(){
        return cartas.getSize();
    }
    // Recibe la última carta del foundation
    public Carta peek(){
        return cartas.getFin();
    }
    // Elimina la última carta del foundation y la retorna
    public Carta pop(){
        return cartas.eliminaFinal();
    }
    /*
     * Valida el movimiento, recibe la carta, evalúa que no esté vacía y que
     * el palo de la carta concuerde con el del foundation, recibe la última
     * carta actual del foundation y retorna boolean dependiendo de si se puede o no
     * colocar la nueva carta después de la otra
     */
    public boolean validarMovimiento(Carta carta){
        if (carta == null || carta.getPalo() != palo){
            return false;
        }
        Carta topCarta = cartas.getFin();
        int v = carta.getValorBajo();
        if (topCarta == null){
            return v == 1;
        }
        int tv = topCarta.getValorBajo();
        return tv+1 == v;
    }
    // Recibe una carta, valida el movimiento y la inserta al final del foundation
    public boolean push(Carta carta){
        if (!validarMovimiento(carta)){
            return false;
        }
        cartas.insertaFinal(carta);
        return true;
    }
    // Limpia el foundation
    public void clear(){
        while (cartas.eliminaFinal()!=null){
        }
    }
    // Retorna la ListaSimple del foundation
    public ListaSimple<Carta> getFoundation(){
        return cartas;
    }

}
