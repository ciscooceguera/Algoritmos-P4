package JuegoLogica;

import Logica.Carta;

public class Tableau {
    private final ListaSimple<Carta> tableau = new ListaSimple<>();
    private final boolean soloRey = false;

    public int size(){ return tableau.getSize(); }
    public boolean isEmpty(){ return tableau.getSize() == 0; }
    public Carta peek(){ return tableau.getFin(); }
    public Carta pop(){ return tableau.eliminaFinal(); }
    public void clear(){ while(tableau.eliminaFinal()!= null){} }
    public void pushInicial(Carta c){ tableau.insertaFinal(c); }

    public boolean validarMov(Carta carta){
        if (carta == null) return false;
        Carta topTableau = peek();
        if (topTableau == null) return !soloRey || carta.getValor() == 13;
        return topTableau.getPalo() == carta.getPalo() && topTableau.getValor()-1 == carta.getValor();
    }

    public boolean push(Carta carta){
        if (!validarMov(carta)) return false;
        tableau.insertaFinal(carta);
        return true;
    }

    public ListaSimple<Carta> getTableau(){ return tableau; }

    public ListaSimple<Carta> topN(int n) { return tableau.topN(n); }
    public ListaSimple<Carta> popN(int n) { return tableau.popN(n); }

    public void pushAllInicial(ListaSimple<Carta> run) { tableau.addAllFinal(run); }

    public boolean escaleraValido(ListaSimple<Carta> run) {
        int m = run.getSize();
        if (m == 0) return false;
        for (int i = 1; i < m; i++) {
            Carta abajo  = run.getPosicion(i - 1);
            Carta arriba = run.getPosicion(i);
            if (abajo.getPalo() != arriba.getPalo()) return false;
            if (abajo.getValor() - 1 != arriba.getValor()) return false;
        }
        return true;
    }
    public boolean puedoColocarEscalera(ListaSimple<Carta> run) {
        if (!escaleraValido(run)) return false;
        Carta first = run.getPosicion(0);
        return validarMov(first);
    }
    public boolean pushEscalera(ListaSimple<Carta> run) {
        if (!puedoColocarEscalera(run)) return false;
        pushAllInicial(run);
        return true;
    }
}
