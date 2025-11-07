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
    private final ListaDoble<RegistroMovimiento> undo;
    private final ListaDoble<RegistroMovimiento> redo;

    public EightOffGame() {
        mazo = new Mazo();
        tableaus = new Tableau[8];
        reservas = new Reserva[8];
        foundations = new Foundation[4];
        undo = new ListaDoble<>();
        redo = new ListaDoble<>();
        iniciarComponentes();
    }
    // ====== Iniciar partida ======
    // Inicializa los tableaus, las reservas y foundations
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
    /*
    * Inicia la partida, primero limpia todos los componentes
    * involucrados en la lógica del juego, después utiliza el mazo
    * para llenar los tableaus: 8 columnas y 6 filas, las 4 cartas
    * restantes las coloca en la reserva
     */
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
        for (int i = 0; i<4; i++){
            Carta carta = mazo.getMazo().eliminaFin();
            reservas[i].push(carta);
        }
    }
    /*
     * Limpia los componentes involucrados en la lógica del juego:
     * Los tableaus, los slots de reserva, los foundations y todos
     * los movimientos registrados
     */
    public void limpiarComponentes() {
        for(Tableau tableau : tableaus) tableau.clear();
        for (Reserva reserva : reservas) reserva.clear();
        for (Foundation foundation : foundations) foundation.clear();
        while (undo.eliminarFin()!=null){}
    }
    // ========= MOVIMIENTOS =========
    /*
     * Funciones para identificar los movimientos:
     * reciben el índice de origen ya sea del tableau o
     * de la reserva, y el índice de destino; ya sea de reserva,
     * tableau o foundation, después guarda la carta más externa
     * del tableau respectivo, y evalúa si el movimiento es válido,
     * en caso de que así sea, quita la carta del tableau y
     * agrega al registro de movimientos el movimiento realizado.
     * El funcionamiento es prácticamente el mismo para todos
     * los movimientos
     */
    public boolean moverTaR(int from, int to){
        Carta mov = tableaus[from].peek();
        if (mov == null) return false;
        if (reservas[to].push(mov)){
            tableaus[from].pop();
            undo.insertarFin(RegistroMovimiento.tr(from,to,mov));
            redo.clear();
            return true;
        }
        redo.clear();
        return false;
    }
    public boolean moverRaT(int from, int to){
        Carta mov = reservas[from].peek();
        if (mov == null) return false;
        if (tableaus[to].push(mov)){
            reservas[from].pop();
            undo.insertarFin(RegistroMovimiento.rt(from,to,mov));
            redo.clear();
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
            undo.insertarFin(RegistroMovimiento.tf(from,idx,mov));
            redo.clear();
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
            undo.insertarFin(RegistroMovimiento.rf(from,idx,mov));
            redo.clear();
            return true;
        }
        return false;
    }
    /*
    * Movimiento tableau -> tableau, moviendo varias cartas en conjunto,
    * recibe la cantidad de cartas que contiene el conjunto o escalera,
    * realiza validaciones para verificar que los índices sean coherentes,
    * y almacena la escalera utilizando topN(int n) de ListaSimple, que
    * obtiene los últimos N nodos contenidos en la lista, posteriormente
    * evalúa la validez de la escalera, y si se puede colocar en el idx
    * de destino, si no pueden moverse, regresa las cartas al origen,
    * caso contrario realiza el movimiento y registra el movimiento utilizando
    * la ListaSimple undo
     */
    public boolean moverTaT(int from, int to, int cantidad) {
        if (from == to || cantidad <= 0) return false;
        if (from < 0 || from >= tableaus.length || to < 0 || to >= tableaus.length) return false;
        if (tableaus[from].size() < cantidad) return false;
        ListaSimple<Carta> escalera = tableaus[from].topN(cantidad);
        if (escalera == null || escalera.getSize() == 0) return false;
        if (!tableaus[from].escaleraValido(escalera)) return false;
        if (!tableaus[to].puedoColocarEscalera(escalera)) return false;
        ListaSimple<Carta> movidas = tableaus[from].popN(cantidad);
        if (!tableaus[to].pushEscalera(movidas)) {
            tableaus[from].pushAllInicial(movidas);
            redo.clear();
            return false;
        }
        Carta topMovida = movidas.getPosicion(movidas.getSize() - 1);
        undo.insertarFin(RegistroMovimiento.tt(from, to, topMovida, cantidad));
        return true;
    }
    // Retorna el índice del foundation, recibe el palo y recorre los foundations hasta
    // encontrar el que se busca
    private int getIdxFoundation(Palo palo){
        for (int i = 0; i<foundations.length; i++) {
            if (foundations[i].getPalo().equals(palo)) return i;
        }
        return -1;
    }
    /*
    * Da una pista, primeramente recorre las reservas y evalúa si la
    * carta de c/d una ellas puede colocarse en algún foundation, si
    * encuentra una coincidencia retorna el movimiento, caso contrario
    * continúa a evaluar la misma condición pero para realizar un movimiento
    * tableau a foundation, si no se encontró coincidencia procede a
    * recorrer las reservas para ver si se puede realizar un movimiento
    * reserva -> tableau, si no encuentra coincidencia, recorre los
    * tableaus para evaluar movimientos tableau -> tableau
    * y finalmente evalúa los últimos movimientos posibles que es
    * mover una carta de un tableau a la reserva, si no haya coincidencias
    * muestra una alerta de derrota, y reinicia el juego
     */
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
    // Retorna boolean y evalúa si hay movimientos disponibles para retornar
    // la pista, o el mensaje de victoria
    public boolean sinMovimientos(){
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
    /*
     * Evalúa si se gano, para ello se deben validar 2 situaciones:
     * 1. Ya no hay movimientos disponibles
     * 2. Todos los foundations tienen sus reyes
     */
    public boolean evaluarVictoria(){
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
    // Muestra un alert de victoria
    public void victoria(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Victoria!");
        alert.setHeaderText(null);
        alert.setContentText("Felicidades, ganaste !");
        alert.showAndWait();
    }
    /*
    * Undo, recibe de la ListaDoble<RegistroMovimiento> undo el último
    * movimiento registrado, evalúa que no sea null, y hace un switch-case
    * para c/d caso en cada movimiento posible, y realiza la operación inversa
    * al movimiento que se haya realizado, el único case distinto
    * es que en el tableau->tableau se recibe una ListaSimple<Carta> por si
    * se movieron varias cartas juntas. Finalmente inserta el movimiento en redo
     */
    public boolean deshacer(){
        RegistroMovimiento ultimoMov = undo.eliminarFin();
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
        redo.insertarFin(ultimoMov);
        return true;
    }
    /*
     * Undo, recibe de la ListaDoble<RegistroMovimiento> undo el último
     * movimiento registrado, evalúa que no sea null, y hace un switch-case
     * para c/d caso en cada movimiento posible, y realiza la operación inversa
     * al movimiento que se haya realizado, el único case distinto
     * es que en el tableau->tableau se recibe una ListaSimple<Carta> por si
     * se movieron varias cartas juntas. Finalmente inserta el movimiento en undo
     */
    public boolean rehacer() {
        RegistroMovimiento mov = redo.eliminarFin();
        if (mov == null) return false;
        switch (mov.tipo) {
            case TT -> {
                int k = Math.max(1, mov.cantidad);
                ListaSimple<Carta> pack = tableaus[mov.fromIdx].popN(k);
                tableaus[mov.toIdx].pushAllInicial(pack);
            }
            case TR -> {
                Carta c = tableaus[mov.fromIdx].pop();
                reservas[mov.toIdx].push(c);
            }
            case RT -> {
                Carta c = reservas[mov.fromIdx].pop();
                tableaus[mov.toIdx].pushInicial(c);
            }
            case TF -> {
                Carta c = tableaus[mov.fromIdx].pop();
                foundations[mov.toIdx].push(c);
            }
            case RF -> {
                Carta c = reservas[mov.fromIdx].pop();
                foundations[mov.toIdx].push(c);
            }
        }
        undo.insertarFin(mov);
        return true;
    }
    // Getters
    public Carta getTopTableau(int col){ return tableaus[col].peek(); }
    public Carta getTopReservas(int i){ return reservas[i].peek(); }
    public Carta getTopFoundation(int i){ return foundations[i].peek(); }
    public ListaSimple<Carta> getTableau(int idx){ return tableaus[idx].getTableau(); }
    public ListaDoble<RegistroMovimiento> getHistorialMovimientos(){
        return undo;
    }
    // Auxiliares para historial
    public void clearRedo() {
        while (redo.eliminarFin() != null) {}
    }
    public int getUndoSize() {
        return undo.getSize();
    }
}
