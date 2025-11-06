package JuegoLogica;

import Logica.Carta;
import Logica.Mazo;
import Logica.Palo;
import javafx.scene.control.Alert;

public class EightOffGame {
    public final Mazo mazo;
    private final Tableau[] tableaus;
    private final Reserva[] reservas;
    public final Foundation[] foundations;
    private final ListaSimple<RegistroMovimiento> undo;

    public EightOffGame() {
        mazo = new Mazo();
        System.out.println(mazo);
        tableaus = new Tableau[8];
        reservas = new Reserva[8];
        foundations = new Foundation[4];
        undo = new ListaSimple<>();
        iniciarComponentes();
    }
    public void iniciarComponentes() {
        for (int i = 0; i < tableaus.length; i++) {
            tableaus[i] = new Tableau();
            reservas[i] = new Reserva();
        }
        Palo[] ordenPalos = Palo.values();
        for (int i = 0; i < 4; i++) {
            foundations[i] = new Foundation(ordenPalos[i]);
        }
    }
    public void iniciarNuevaPartida(){
        limpiarComponentes();
        for (int fila = 0; fila<6; fila++) {
            for (int col = 0; col<8; col++) {
                Carta carta = mazo.getMazo().eliminaFin();
                if (carta != null) {
                    tableaus[col].pushInicial(carta);
                }
            }
        }
        for (Tableau tableau : tableaus) {
            System.out.println(tableau.getTableau().mostrarLista());
        }
        for (int i = 0; i<4; i++){
            Carta carta = mazo.getMazo().eliminaFin();
            reservas[i].push(carta);
        }
    }
    public void limpiarComponentes() {
        for(Tableau tableau : tableaus) tableau.clear();
        for (Reserva reserva : reservas) reserva.clear();
        for (Foundation foundation : foundations) foundation.clear();
        while (undo.eliminaFinal()!=null){}
    }
    public boolean moverTaR(int from, int to){
        Carta mov = tableaus[from].peek();
        if (mov == null) return false;
        if (reservas[to].push(mov)){
            tableaus[from].pop();
            undo.insertaFinal(RegistroMovimiento.tr(from,to,mov));
            return true;
        }
        return false;
    }
    public boolean moverRaT(int from, int to){
        Carta mov = reservas[from].peek();
        if (mov == null) return false;
        if (tableaus[to].push(mov)){
            reservas[from].pop();
            undo.insertaFinal(RegistroMovimiento.rt(from,to,mov));
            return true;
        }
        return false;
    }

    public boolean moverTaF(int from, int to){
        Carta mov = tableaus[from].peek();
        if (mov == null) return false;
        int idx = getIdxFoundation(mov.getPalo());
        if (foundations[idx].push(mov)){
            tableaus[from].pop();
            undo.insertaFinal(RegistroMovimiento.tf(from,to,mov));
            return true;
        }
        return false;
    }
    public boolean moverRaF(int from, int to){
        Carta mov = reservas[from].peek();
        if (mov == null) return false;
        int idx = getIdxFoundation(mov.getPalo());
        if (foundations[idx].push(mov)){
            reservas[from].pop();
            undo.insertaFinal(RegistroMovimiento.rf(from,to,mov));
            return true;
        }
        return false;
    }
    private int getIdxFoundation(Palo palo){
        for (int i = 0; i<foundations.length; i++) {
            if (foundations[i].getPalo().equals(palo)) return i;
        }
        return -1;
    }
    public RegistroMovimiento pista(){
        for (int r = 0; r < 8; r++) {
            Carta c = reservas[r].peek();
            if (c == null) continue;
            int f = getIdxFoundation(c.getPalo());
            if (foundations[f].validarMovimiento(c)) return RegistroMovimiento.rf(r, f, c);
        }
        for (int t = 0; t < 8; t++) {
            Carta c = tableaus[t].peek();
            if (c == null) continue;
            int f = getIdxFoundation(c.getPalo());
            if (foundations[f].validarMovimiento(c)) return RegistroMovimiento.tf(t, f, c);
        }
        for (int r = 0; r < 8; r++) {
            Carta c = reservas[r].peek();
            if (c == null) continue;
            for (int t = 0; t < 8; t++) {
                if (tableaus[t].validarMov(c)) return RegistroMovimiento.rt(r, t, c);
            }
        }
        for (int a = 0; a < 8; a++) {
            Carta c = tableaus[a].peek();
            if (c == null) continue;
            for (int b = 0; b < 8; b++) if (a != b) {
                if (tableaus[b].validarMov(c)) return RegistroMovimiento.tt(a, b, c);
            }
        }
        for (int t = 0; t < 8; t++) {
            if (reservas[t].peek() == null) {
                for (int z = 0; z < 8; z++) {
                    Carta c = tableaus[z].peek();
                    if (c != null) return RegistroMovimiento.tr(z, t, c);
                }
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sin movimientos disponibles");
        alert.setHeaderText(null);
        alert.setContentText("No hay más movimientos posibles.");
        alert.showAndWait();
        limpiarComponentes();
        iniciarComponentes();
        return null;
    }
    public boolean sinMovimientos(){
        for (int i = 0; i<tableaus.length; i++) {
            System.out.println("Tableau " + i + tableaus[i].getTableau().mostrarLista() + "\n");
        }
        int r;
        for (r = 0; r < foundations.length; r++) {
            if (foundations[r].getFoundation().getFin() == null) break;
            if (foundations[r].getFoundation().getFin().getValor() != 13 ) break;
        }
        if ( r == 3 ){
            victoria();
            return false;
        } else {
            return pista() == null;
        }
    }
    public boolean evaluarVictoria(){
        for (int i = 0; i<tableaus.length; i++) {
            System.out.println("Tableau " + i + tableaus[i].getTableau().mostrarLista() + "\n");
        }
        int r;
        for (r = 0; r < foundations.length; r++) {
            if (foundations[r].getFoundation().getFin() == null) break;
            if (foundations[r].getFoundation().getFin().getValor() != 13 ) break;
        }
        if ( r == 3 ){
            return true;
        }
        return false;
    }
    public void victoria(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Victoria!");
        alert.setHeaderText(null);
        alert.setContentText("Felicidades, ganaste !");
        alert.showAndWait();
    }
    public boolean deshacer(){
        RegistroMovimiento ultimoMov = undo.eliminaFinal();
        if (ultimoMov == null) return false;

        switch (ultimoMov.tipo){
            case TT -> {
                int k = Math.max(1, ultimoMov.cantidad);
                ListaSimple<Carta> pack = tableaus[ultimoMov.toIdx].popN(k);
                tableaus[ultimoMov.fromIdx].pushAllInicial(pack);
            }
            case TR -> {
                Carta c = reservas[ultimoMov.toIdx].pop();
                tableaus[ultimoMov.fromIdx].pushInicial(c);
            }
            case RT -> {
                Carta c = tableaus[ultimoMov.toIdx].pop();
                reservas[ultimoMov.fromIdx].push(c);
            }
            case TF -> {
                Carta c = foundations[ultimoMov.toIdx].pop();
                tableaus[ultimoMov.fromIdx].pushInicial(c);
            }
            case RF -> {
                Carta c = foundations[ultimoMov.toIdx].pop();
                reservas[ultimoMov.fromIdx].push(c);
            }
        }
        return true;
    }
    public boolean moverTaT(int from, int to, int cantidad) {
        if (from == to || cantidad <= 0) return false;
        if (from < 0 || from >= tableaus.length || to < 0 || to >= tableaus.length) return false;
        if (tableaus[from].size() < cantidad) return false;
        ListaSimple<Carta> run = tableaus[from].topN(cantidad);
        if (run == null || run.getSize() == 0) return false;
        if (!tableaus[from].escaleraValido(run)) return false;
        if (!tableaus[to].puedoColocarEscalera(run)) return false;
        ListaSimple<Carta> movidas = tableaus[from].popN(cantidad);
        if (!tableaus[to].pushEscalera(movidas)) {
            tableaus[from].pushAllInicial(movidas);
            return false;
        }
        Carta topMovida = movidas.getPosicion(movidas.getSize() - 1);
        undo.insertaFinal(RegistroMovimiento.tt(from, to, topMovida, cantidad));
        return true;
    }
    public Carta getTopTableau(int col){ return tableaus[col].peek(); }
    public Carta getTopReservas(int i){ return reservas[i].peek(); }
    public Carta getTopFoundation(int i){ return foundations[i].peek(); }
    public ListaSimple<Carta> getTableau(int idx){ return tableaus[idx].getTableau(); }
}
