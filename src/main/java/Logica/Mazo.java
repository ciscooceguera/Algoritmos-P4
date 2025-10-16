package Logica;

import java.util.Collections;
import java.util.List;

public class Mazo {
    ListaCircularDoble<Carta> mazo;
    public Mazo(){
        mazo = new ListaCircularDoble<>();
        llenar();
        mezclar();
    }
    public void mezclar(){
        mazo.revolverLista();
    }
    public void llenar(){
        for (int i = 2; i<=14 ; i++){
            for (Palo palo : Palo.values()){
                CartaInglesa carta = new CartaInglesa(i, palo, palo.getColor());
                carta.makeFaceUp();
                mazo.insertaFin(carta);
            }
        }
    }
    public void ordenar(){
        mazo.ordenarLista();
    }
    public String toString(){
        return mazo.mostrarLista();
    }
    public ListaCircularDoble<Carta> getMazo(){
        return mazo;
    }


}
