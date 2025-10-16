package JuegoLogica;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListaSimple<T> {
    private Nodo<T> inicio;
    public ListaSimple() {
        this.inicio = null;
    }
    public void insertaInicio(T dato){
        Nodo<T> nodo = new Nodo(dato);
        nodo.setSiguiente(inicio);
        inicio = nodo;
    }
    public void insertaFinal(T dato){
        Nodo<T> nodo = new Nodo(dato);
        // Lista vacía
        if (inicio == null){
            nodo.setSiguiente(inicio);
            inicio = nodo;
        }else{
            Nodo<T> iter = inicio;
            while (iter.getSiguiente() != null){
                iter = iter.getSiguiente();
            }
            iter.setSiguiente(nodo);
            nodo.setSiguiente(null);
        }
    }
    public T eliminaInicio(){
        Nodo<T> nodoEliminado = inicio;
        if (inicio == null){
            System.out.println("Lista vacia");
            return null;
        }
        inicio = inicio.getSiguiente();
        return nodoEliminado.getInfo();
    }
    public T eliminaFinal(){
        if (inicio == null){
            System.out.println("Lista vacia");
            return null;
        }
        if (inicio.getSiguiente() == null){
            T dato = inicio.getInfo();
            inicio = null;
            return dato;
        }
        Nodo<T> r = inicio;
        Nodo<T> a = r;
        while (r.getSiguiente() != null){
            a=r;
            r = r.getSiguiente();
        }
        a.setSiguiente(null);
        return r.getInfo();
    }
    public String mostrarLista(){
        String cadena = "";
        if (inicio == null){
            System.out.println("Lista vacia");
            return "";
        }
        Nodo<T> iter = inicio;
        while (iter.getSiguiente() != null){
            cadena += iter.getInfo().toString() + "\n";
            iter = iter.getSiguiente();
        }
        cadena += iter.getInfo().toString();
        return cadena;
    }
    public String mostrar(){
        if (inicio == null){
            System.out.println("Lista vacia");
            return null;
        }
        Nodo<T> iter = inicio;
        if (inicio.getSiguiente() == null){
            return iter.getInfo().toString();
        }
        return inicio.getInfo() + "\n" + mostrarRecursivo(inicio.getSiguiente());
    }
    public String mostrarRecursivo(Nodo<T> nodo){
        if (nodo.getSiguiente() == null){
            return nodo.getInfo().toString();
        }
        return nodo.getInfo().toString() + "\n" + mostrarRecursivo(nodo.getSiguiente());
    }
    public T eliminaX(T dato){
        Nodo<T> iter = inicio.getSiguiente();
        Nodo<T> iterAnt = inicio;
        // El inicio contiene el dato
        if (iterAnt.getInfo().equals(dato)){
            inicio = iter;
            return dato;
        }
        while (iterAnt.getSiguiente() != null){
            if (iter.getInfo().equals(dato)){
                iterAnt.setSiguiente(iter.getSiguiente());
                return dato;
            }else{
                iterAnt = iter;
                iter = iter.getSiguiente();
            }
        }
        return null;
    }
    public int buscar(T dato){
        Nodo<T> iter = inicio;
        int idx = 0;
        if (iter == null){
            System.out.println("Lista vacia");
            return -1;
        }
        return buscarAux(dato,iter, idx);

    }
    public int buscarAux(T dato,Nodo<T> n, int idx){
        if (n.getInfo().equals(dato)){
            return idx;
        }
        if (n.getSiguiente() == null){
            return -1;
        }
        return buscarAux(dato,n.getSiguiente(),idx+1);
    }
    public T eliminaPosicion(int posicion){
        Nodo<T> iter = inicio.getSiguiente();
        Nodo<T> iterAnt = inicio;
        int count = 1;
        if (iterAnt == null){
            System.out.println("Lista vacia");
            return null;
        }
        if (posicion > getSize()-1 || posicion < 0 ){
            return null;
        }
        if (posicion == 0){
            inicio = iter;
            return iterAnt.getInfo();
        }
        while (iter.getSiguiente() != null){
            if (count == posicion){
                break;
            }else {
                iterAnt = iter;
                iter = iter.getSiguiente();
                count++;
            }
        }
        iterAnt.setSiguiente(iter.getSiguiente());
        return iter.getInfo();
    }
    public int getSize(){
        Nodo<T> r = inicio;
        int size = 1;
        if (r == null){
            System.out.println("Lista vacia");
            return 0;
        }
        while (r.getSiguiente() != null){
            r = r.getSiguiente();
            size++;
        }
        return size;
    }

    public void ordenarLista(){
        Comparator<T> cmp = (a, b) -> ((Comparable<? super T>) a).compareTo(b);
        boolean huboCambios;
        do {
            huboCambios = false;
            Nodo<T> actual = inicio;
            while (actual != null && actual.getSiguiente() != null) {
                Nodo<T> sig = actual.getSiguiente();
                if (cmp.compare(actual.getInfo(), sig.getInfo()) > 0) {
                    T tmp = actual.getInfo();
                    actual.setInfo(sig.getInfo());
                    sig.setInfo(tmp);
                    huboCambios = true;
                }
                actual = sig;
            }
        } while (huboCambios);
    }
    public void ordenarListaInt(){
        if (inicio == null){
            System.out.println("Lista vacia");
            return;
        }
        if (inicio.getSiguiente() == null){
            return;
        }
        boolean huboCambios;
        do{
            huboCambios = false;
            Nodo<T> iter = inicio;
            while (iter.getSiguiente() != null) {
                Nodo<T> iterSig = iter.getSiguiente();
                int num = Integer.parseInt(iter.getInfo().toString());
                int numSig = Integer.parseInt(iterSig.getInfo().toString());
                if (numSig < num) {
                    T dato = iter.getInfo();
                    iter.setInfo(iterSig.getInfo());
                    iterSig.setInfo(dato);
                    huboCambios = true;
                }
                iter = iterSig;
            }
        }while(huboCambios);
    }
    /*
     * Se inserta en posición reemplazando el dato en dicha posición,
     * no puedes insertar en posiciones donde aún no se creo un nodo,
     * tampoco si está vacía
     */
    public void insertaEnPosicion(T dato, int posicion){
        if (posicion < 0 || posicion >= getSize()){
            System.out.println("Posicion invalida");
            return;
        }
        if (inicio.getSiguiente() == null){
            inicio.setInfo(dato);
            return;
        }
        Nodo<T> iter = inicio;
        int idx = 0;
        while (iter != null){
            if (idx == posicion){
                iter.setInfo(dato);
                return;
            }else{
                iter = iter.getSiguiente();
                idx++;
            }
        }
    }
    public void eliminarPares(){
        if (inicio == null){
            return;
        }
        Nodo<T> iter = inicio;
        if (Integer.parseInt(iter.getInfo().toString())%2 == 0){
            inicio = iter.getSiguiente();
        }
        while (iter != null){
            Nodo<T> iterAnt = iter;
            iter = iter.getSiguiente();
            if (Integer.parseInt(iter.getInfo().toString())%2 == 0){
                iterAnt.setSiguiente(iter.getSiguiente());
                iter = iter.getSiguiente();
            }else{
                iter = iter.getSiguiente();
            }
        }
    }
    public T getFin(){
        if (inicio == null){
            return null;
        }
        Nodo<T> iter = inicio;
        while (iter.getSiguiente() != null){
            iter = iter.getSiguiente();
        }
        return iter.getInfo();
    }
    public T getPosicion(int idx){
        if (idx < 0) return null;
        Nodo<T> it = inicio;
        int i = 0;
        while (it != null){
            if (i == idx) return it.getInfo();
            it = it.getSiguiente();
            i++;
        }
        return null;
    }
    public List<T> toList() {
        ArrayList<T> lista = new ArrayList<>();
        Nodo<T> actual = inicio;
        while (actual != null) {
            lista.add(actual.getInfo());
            actual = actual.getSiguiente();
        }
        return lista;
    }
    public ListaSimple<T> getLista(){
        if (inicio == null){
            return null;
        }
        ListaSimple<T> listaNueva = new ListaSimple<>();
        Nodo<T> actual = inicio;
        while (actual != null){
            listaNueva.insertaInicio(actual.getInfo());
            actual = actual.getSiguiente();
        }
        return listaNueva;
    }
    public ListaSimple<T> popN(int n) {
        ListaSimple<T> res = new ListaSimple<>();
        int sz = getSize();
        if (n <= 0 || sz == 0) return res;
        if (n > sz) n = sz;
        for (int i = 0; i < n; i++) {
            T v = eliminaFinal();
            if (v == null) break;
            res.insertaInicio(v);
        }
        return res;
    }

    public ListaSimple<T> topN(int n) {
        ListaSimple<T> res = new ListaSimple<>();
        int sz = getSize();
        if (n <= 0 || n > sz) return res;
        for (int i = sz - n; i < sz; i++) {
            res.insertaFinal(getPosicion(i));
        }
        return res;
    }
    public void addAllFinal(ListaSimple<T> other) {
        for (T x : other.toList()) {
            insertaFinal(x);
        }
    }




}
