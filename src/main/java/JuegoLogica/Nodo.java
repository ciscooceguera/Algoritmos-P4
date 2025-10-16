package JuegoLogica;
public class Nodo<T>{
    private T info;
    private Nodo<T> siguiente;
    public Nodo(T info) {
        this.info = info;
    }
    public T getInfo() {
        return info;
    }
    public Nodo<T> getSiguiente() {
        return siguiente;
    }
    public void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }
    public void setInfo(T info) {
        this.info = info;
    }
    public String toString() {
        return info.toString();
    }
}
