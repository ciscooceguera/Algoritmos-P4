package JuegoLogica;

import Logica.Carta;
public class RegistroMovimiento {
    // enum de los tipos de movimientos posibles
    public enum Tipo{TT, TR, RT, TF, RF}
    // enum de los posibles destinos
    public enum OrigenDestino {TABLEAU, RESERVE, FOUNDATION}
    public final Tipo tipo;
    public final OrigenDestino from, to;
    public final int fromIdx, toIdx;
    public final Carta carta;
    public int cantidad = 0;
    // Constructor para movimiento de una carta
    public RegistroMovimiento(Tipo tipo,
                              OrigenDestino from, int fromIdx,
                              OrigenDestino to, int toIdx,
                              Carta carta) {
        this.tipo = tipo;
        this.fromIdx = fromIdx;
        this.toIdx = toIdx;
        this.carta = carta;
        this.from = from;
        this.to = to;
    }
    // Constructor para varias cartas, cantidad recibe el tamaño de la lista
    public RegistroMovimiento(Tipo tipo,
                              OrigenDestino from, int fromIdx,
                              OrigenDestino to, int toIdx,
                              Carta carta, int cantidad) {
        this.tipo = tipo;
        this.fromIdx = fromIdx;
        this.toIdx = toIdx;
        this.carta = carta;
        this.from = from;
        this.to = to;
        this.cantidad = cantidad;
    }
    /*
     * Retorna una instancia de la clase, dependiendo del movimiento
     * que se vaya a realizar, se reciben los parámetros necesarios
     * para almacenar el movimiento en caso de requerir un undo
     */
    public static RegistroMovimiento tt(int from, int to, Carta carta) {
        return new RegistroMovimiento(Tipo.TT,
                OrigenDestino.TABLEAU, from,
                OrigenDestino.TABLEAU, to, carta);
    }
    public static RegistroMovimiento tt(int from, int to, Carta carta, int cantidad) {
        return new RegistroMovimiento(Tipo.TT,
                OrigenDestino.TABLEAU, from,
                OrigenDestino.TABLEAU, to, carta, cantidad);
    }
    public static RegistroMovimiento tr(int from, int to, Carta carta) {
        return new RegistroMovimiento(Tipo.TR,
                OrigenDestino.TABLEAU, from,
                OrigenDestino.RESERVE, to, carta);
    }
    public static RegistroMovimiento rt(int from, int to, Carta carta) {
        return new RegistroMovimiento(Tipo.RT,
                OrigenDestino.RESERVE, from,
                OrigenDestino.TABLEAU, to, carta);
    }
    public static RegistroMovimiento tf(int from, int to, Carta carta) {
        return new RegistroMovimiento(Tipo.TF,
                OrigenDestino.TABLEAU, from,
                OrigenDestino.FOUNDATION, to, carta);
    }
    public static RegistroMovimiento rf(int from, int to, Carta carta) {
        return new RegistroMovimiento(Tipo.RF,
                OrigenDestino.RESERVE, from,
                OrigenDestino.FOUNDATION, to, carta);
    }
}
