package JuegoLogica;
import Logica.Carta;

public class Tableau {
    private final ListaSimple<Carta> tableau = new ListaSimple<>();
    /*
    * soloRey = true: solo se puede colocar 'K' en tableaus vacíos
    * soloRey = false: puede colocarse cualquier carta en tableaus vacíos
     */
    private final boolean soloRey = true;
    public int size(){ return tableau.getSize(); }
    public boolean isEmpty(){ return tableau.getSize() == 0; }
    public Carta peek(){ return tableau.getFin(); }
    public Carta pop(){ return tableau.eliminaFinal(); }
    // Limpia el tableau
    public void clear(){ while(tableau.eliminaFinal()!= null){} }
    // Inserta carta sin evaluar si se puede colocar (obliga el movimiento)
    public void pushInicial(Carta c){ tableau.insertaFinal(c); }
    // Retorna boolean; si se puede o no colocar la carta
    public boolean validarMov(Carta carta){
        if (carta == null) return false;
        Carta topTableau = peek();
        if (topTableau == null) return !soloRey || carta.getValor() == 13;
        return topTableau.getPalo() == carta.getPalo() && topTableau.getValor()-1 == carta.getValor();
    }
    // Inserta al final y retorna si se pudo o no colocar
    public boolean push(Carta carta){
        if (!validarMov(carta)) return false;
        tableau.insertaFinal(carta);
        return true;
    }
    // Retorna la ListaSimple (tableau)
    public ListaSimple<Carta> getTableau(){ return tableau; }
    // Regresa el último elemento
    public ListaSimple<Carta> topN(int n) { return tableau.topN(n); }
    // Elimina el último elemento
    public ListaSimple<Carta> popN(int n) { return tableau.popN(n); }
    // Recibe una ListaSimple<Carta> y la concatena con el tableau
    public void pushAllInicial(ListaSimple<Carta> escalera) { tableau.addAllFinal(escalera); }
    // Retorna si hay escalera
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
    // Retorna si la escalera recibida se puede colocar
    public boolean puedoColocarEscalera(ListaSimple<Carta> escalera) {
        if (!escaleraValido(escalera)) return false;
        Carta first = escalera.getPosicion(0);
        return validarMov(first);
    }
    // Mete al tableau varias cartas contenidas en una Lista Simple
    public boolean pushEscalera(ListaSimple<Carta> run) {
        if (!puedoColocarEscalera(run)) return false;
        pushAllInicial(run);
        return true;
    }
}
