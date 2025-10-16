package JuegoLogica;

import Logica.Carta;

public class Tableau {
    private final ListaSimple<Carta> tableau = new ListaSimple<>();
    private final boolean soloRey = true;
    public int size(){
        return tableau.getSize();
    }
    public boolean isEmpty(){
        return tableau.getSize() == 0;
    }
    public Carta peek(){
        return tableau.getFin();
    }
    public Carta pop(){
        return tableau.eliminaFinal();
    }
    public void clear(){
        while(tableau.eliminaFinal()!= null){

        }
    }
    public boolean validarMov(Carta carta){
        if (carta ==null ){
            return false;
        }
        Carta topTableau = peek();
        // Si está vacío se puede poner si no se permiten reyes, o si se
        // permite pero es un rey la carta
        if (topTableau == null){
            return !soloRey || carta.getValor() == 13;
        }
        // Mismo palo y son consecutivas
        return topTableau.getPalo() == carta.getPalo() && topTableau.getValor()-1 == carta.getValor();
    }
    public boolean push(Carta carta){
        if (!validarMov(carta)){
            return false;
        }
        tableau.insertaFinal(carta);
        return true;
    }
}
