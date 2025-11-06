package JuegoLogica;

import Logica.NodoDoble;

public class ListaDoble<T> {
    NodoDoble<T> inicio;
    public ListaDoble() {
        inicio = null;
    }
    public void insertarInicio(T elemento) {
        NodoDoble<T> n = new NodoDoble<>(elemento);
        n.setSiguiente(inicio);
        n.setAnterior(null);
        if (inicio!=null){
            inicio.setAnterior(n);
        }
        inicio = n;
    }
    public void insertarFin(T elemento) {
        NodoDoble<T> n = new NodoDoble<>(elemento);
        n.setSiguiente(null);
        if (inicio ==null){
            n.setAnterior(inicio);
            inicio = n;
        }else{
            NodoDoble<T> r = inicio;
            while (r.getSiguiente()!=null){
                r = r.getSiguiente();
            }
            r.setSiguiente(n);
            n.setAnterior(r);
        }
    }
    public T eliminarInicio() {
        if (inicio == null) {
            return null;
        }else{
            T dato = inicio.getInfo();
            if (inicio.getSiguiente() == null){
                inicio =null;
            }else{
                inicio = inicio.getSiguiente();
                inicio.setAnterior(null);
            }
            return dato;
        }
    }
    public T eliminarFin() {
        if (inicio == null) {
            return null;
        }else{
            T dato;
            if (inicio.getSiguiente() == null){
                dato = inicio.getInfo();
                inicio = null;
            }else{
                NodoDoble<T> r = inicio;
                while (r.getSiguiente()!=null){
                    r = r.getSiguiente();
                }
                dato = r.getInfo();
                r.getAnterior().setSiguiente(null);
            }
            return dato;
        }
    }
    public NodoDoble<T> getInicio() {
        return inicio;
    }
    public int getSize(){
        NodoDoble<T> n = inicio;
        int size = 0;
        while (n != null){
            n = n.getSiguiente();
            size++;
        }
        return size;
    }
    public void clear(){
        if (inicio != null) {
            inicio.setAnterior(null);
            inicio.setSiguiente(null);
            inicio = null;
        }
    }


}
