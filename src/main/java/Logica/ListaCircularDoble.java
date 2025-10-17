package Logica;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
public class ListaCircularDoble<T> {
    private NodoDoble<T> inicio;
    private NodoDoble<T> fin;
    public ListaCircularDoble() {
        inicio = null;
        fin = null;
    }
    public void insertaInicio(T dato) {
        NodoDoble<T> n = new NodoDoble(dato);
        // Lista vacía
        if (inicio == null) {
            inicio = fin = n;
            n.setSiguiente(inicio);
            n.setAnterior(inicio);
            return;
        }
        n.setSiguiente(inicio);
        n.setAnterior(fin);
        fin.setSiguiente(n);
        inicio.setAnterior(n);
        inicio = n;
    }
    public void insertaFin(T dato) {
        NodoDoble<T> n = new NodoDoble(dato);
        // Lista vacía
        if (fin == null) {
            inicio = fin = n;
            n.setSiguiente(inicio);
            n.setAnterior(inicio);
            return;
        }
        n.setSiguiente(inicio);
        n.setAnterior(fin);
        fin.setSiguiente(n);
        inicio.setAnterior(n);
        fin = n;
    }
    public T eliminaInicio() {
        // Lista vacía
        if (inicio == null) {
            System.out.println("Lista vacía");
            return null;
        }
        T dato = inicio.getInfo();
        // Solo hay un elemento
        if (inicio == fin){
            inicio = fin = null;
            return dato;
        }
        fin.setSiguiente(inicio.getSiguiente());
        inicio.getSiguiente().setAnterior(fin);
        inicio = inicio.getSiguiente();
        return dato;
    }
    public T eliminaFin() {
        if (inicio == null){
            System.out.println("Lista vacía");
            return null;
        }
        T dato = inicio.getInfo();
        if (inicio == fin){
            inicio = fin = null;
            return dato;
        }
        dato = fin.getInfo();
        inicio.setAnterior(fin.getAnterior());
        fin.getAnterior().setSiguiente(inicio);
        fin = fin.getAnterior();
        return dato;
    }
    public String mostrarLista() {
        String cadena = "";
        NodoDoble<T> r = inicio;
        while (r != fin){
            cadena += r.getInfo() + "\n";
            r = r.getSiguiente();
        }
        cadena += r.getInfo() + "\n";
        return cadena;
    }
    public String mostrarRecursivo(){
        if (inicio == null){
            return "";
        }
        if (inicio == fin){
            return inicio.getInfo() + "\n";
        }
        return mostrarRAux(inicio);

    }
    public String mostrarRAux(NodoDoble<T> n){
        if (n == fin){
            return n.getInfo() + "\n";
        }else{
            return n.getInfo() + " " + mostrarRAux(n.getSiguiente());
        }
    }
    public T eliminaX(T x){
        NodoDoble<T> r = inicio;
        do{
            if ( r.getInfo().equals(x)){
                T dato = r.getInfo();
                r.getAnterior().setSiguiente(r.getSiguiente());
                r.getSiguiente().setAnterior(r.getAnterior());
                return dato;
            }
            r = r.getSiguiente();
        }while (r.getSiguiente() != inicio);
        return null;
    }
    public int getSize(){
        NodoDoble r = inicio;
        if (inicio == null){
            return 0;
        }
        if (inicio == fin){
            return 1;
        }
        int count = 1;
        do {
            r = r.getSiguiente();
            count++;
        }while (r!=fin);
        return count;
    }
    public void eliminarPares(){
        // Eliminar pares del inicio
        while (inicio != null && Integer.parseInt(inicio.getInfo().toString()) % 2 == 0){
            if (inicio == fin){
                // Solo hay un elemento y es par
                inicio = fin = null;
                return;
            }
            inicio = inicio.getSiguiente();
            fin.setSiguiente(inicio);
        }
        if (inicio == null){
            return;
        }
        // Ahora eliminar pares del resto
        NodoDoble<T> r = inicio;
        NodoDoble<T> rAnt = inicio;
        do {
            if (Integer.parseInt(r.getInfo().toString()) % 2 == 0){
                // Es par, eliminarlo
                if (r == fin){
                    fin = rAnt;                     // [3,5,7]
                    fin.setSiguiente(inicio);
                    r = inicio;
                } else {
                    rAnt.setSiguiente(r.getSiguiente());
                    r = r.getSiguiente();
                }
            } else {
                // Es impar, avanzar
                rAnt = r;
                r = r.getSiguiente();
            }
        } while (r != inicio);
    }
    public void ordenarLista(){
        Comparator<T> cmp = (a, b) -> ((Comparable<T>)a).compareTo(b);
        boolean huboCambios;
        do{
            huboCambios = false;
            NodoDoble<T> r = inicio;
            while (r!= fin){
                if (cmp.compare(r.getInfo(),r.getSiguiente().getInfo())>0){
                    T dato = r.getInfo();
                    r.setInfo(r.getSiguiente().getInfo());
                    r.getSiguiente().setInfo(dato);
                    huboCambios = true;
                }
                r = r.getSiguiente();
            }
        }while(huboCambios);
    }
    public void revolverLista() {
        if (inicio == null || inicio == fin) return;
        ArrayList<T> listArr = new ArrayList<>(getSize());
        NodoDoble<T> r = inicio;
        do {
            listArr.add(r.getInfo());
            r = r.getSiguiente();
        } while (r != inicio);
        Collections.shuffle(listArr);
        r = inicio;
        int i = 0;
        do {
            r.setInfo(listArr.get(i++));
            r = r.getSiguiente();
        } while (r != inicio);
    }
}
