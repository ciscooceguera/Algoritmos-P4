package com.example.practica4algoritmos;
import JuegoLogica.EightOffGame;
import JuegoLogica.Foundation;
import JuegoLogica.ListaDoble;
import JuegoLogica.RegistroMovimiento;
import Logica.Carta;
import Logica.NodoDoble;
import Logica.Palo;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;
public class ControllerInterfazSolitaire {
    // Componentes de la interfaz
    @FXML private HBox reservasHBox;
    @FXML private VBox foundationsVBox;
    @FXML private HBox tableausHBox;
    @FXML private Button btnSalir;
    @FXML private Button btnPista;
    @FXML private Button btnUndo;
    @FXML private Button btnRedo;
    @FXML private Button btnHistorial;
    // Atributos para el historial
    private boolean cambioHistorial = false;
    private int undoSize = 0;
    private int sizeActual = 0;
    ListView<String> undoLV;
    ListView<String> redoLV;
    // Instancia de la clase que contiene la lógica del juego
    private EightOffGame game;
    // Cantidad de cartas seleccionadas
    private int selectedCantidad = 1;
    // Medidas de la carta y separaciones para el tableau
    private static final double CARTA_W = 90;
    private static final double CARTA_H = 130;
    private static final double SEPARACION_Y = 50;
    // Constantes que contienen estilos de diseño para las cartas según su estado
    private static final String ESTILO_CARTA_TRANSPARENTE =
            "-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 16; " +
                    "-fx-background-radius: 16; -fx-background-color: transparent;";
    private static final String ESTILO_CARTA_NO_TRANSPARENTE =
            "-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 16; " +
                    "-fx-background-radius: 16; -fx-background-color: white;";
    private static final String ESTILO_SELECCIONADO  =
            ESTILO_CARTA_NO_TRANSPARENTE +
                    " -fx-effect: dropshadow(gaussian, rgba(255,215,0,0.9), 18, 0.3, 0, 0);";
    private static final String ESTILO_PISTA_RESERVA =
            ESTILO_CARTA_TRANSPARENTE +
                    " -fx-border-color: #33ccff; -fx-border-width: 3;" +
                    " -fx-effect: dropshadow(gaussian, rgba(50,200,255,0.9), 20, 0.3, 0, 0);";
    // Enum para contener la selección en las cartas
    private enum Seleccion {NADA, TABLEAU, RESERVA}
    private Seleccion seleccion = Seleccion.NADA;
    // Atributos para los índices
    private int seleccionIdx = -1;
    private int pistaReservaIdx = -1;
    // Inicializa las instancias necesarias, los botones y dibuja la interfaz
    @FXML
    private void initialize() {
        game = new EightOffGame();
        game.iniciarNuevaPartida();
        btnSalir.setOnAction(e -> ((Stage) btnSalir.getScene().getWindow()).close());
        btnUndo.setOnAction(e -> {
            if (game.deshacer()) limpiarSeleccion();
        });
        btnRedo.setOnAction(e -> {
            if (game.rehacer()) limpiarSeleccion();
        });
        btnPista.setOnAction(e -> mostrarPista());
        dibujar();
        btnHistorial.setOnAction(e -> {
           abrirHistorial();
        });
    }
    /*
     * Invoca los métodos para dibujar los componentes de la interfaz,
     * y evalúa si el jugador ganó, después evalúa si perdió
     */
    private void dibujar() {
        dibujarReservas();
        dibujarFoundations();
        dibujarTableaus();
        if (game.evaluarVictoria()){
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Haz ganado");
            a.setHeaderText(null);
            a.setContentText("VICTORIA, se iniciará un nuevo juego...");
            a.showAndWait();
            initialize();
            return;
        }
        if(game.sinMovimientos()){
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Haz perdido");
            a.setHeaderText(null);
            a.setContentText("DERROTA. Se iniciará un nuevo juego...");
            a.showAndWait();
            initialize();
        }
    }
    // Dibuja el stackPane de reservas
    private void dibujarReservas() {
        int n = reservasHBox.getChildren().size();
        for (int i = 0; i < n; i++) {
            StackPane sp = (StackPane) reservasHBox.getChildren().get(i);
            Carta c = game.getTopReservas(i);
            setLabelsStackPane(sp, c);
            if (c == null && pistaReservaIdx == i) {
                sp.setStyle(ESTILO_PISTA_RESERVA);
            }
            if (c != null && seleccion == Seleccion.RESERVA && seleccionIdx == i) {
                sp.setStyle(ESTILO_SELECCIONADO);
            }
            final int idx = i;
            sp.setOnMouseClicked(e -> onClickReserva(idx));
            sp.setMouseTransparent(false);
        }
    }
    /*
     * Cuando se clickea una reserva se invoca,
     * si ya hay una selección previa se invoca el movimiento identificando
     * la reserva como destino, si no hay NADA seleccionado,
     * se detecta como origen la reserva
     */
    private void onClickReserva(int idxReserva) {
        Carta top = game.getTopReservas(idxReserva);
        pistaReservaIdx = -1;
        switch (seleccion) {
            case NADA -> {
                if (top != null) {
                    seleccion = Seleccion.RESERVA;
                    seleccionIdx = idxReserva;
                    dibujar();
                }
            }
            case RESERVA -> {
                if (seleccionIdx == idxReserva) {
                    if (!moverReservaAFundacion(idxReserva)) limpiarSeleccion();
                } else {
                    seleccionIdx = idxReserva;
                    dibujar();
                }
            }
            case TABLEAU -> {
                game.moverTaR(seleccionIdx, idxReserva);
                limpiarSeleccion();
            }
        }
    }
    // Mueve de reserva a fundación recibiendo el índice de la reserva
    private boolean moverReservaAFundacion(int idxReserva) {
        Carta top = game.getTopReservas(idxReserva);
        if (top == null) return false;
        int fIdx = gameFoundationIndexFromPalo(top.getPalo());
        boolean ok = game.moverRaF(idxReserva, fIdx);
        if (ok) limpiarSeleccion();
        return ok;
    }
    // Recibe el índice del foundation según el palo
    private int gameFoundationIndexFromPalo(Palo p) {
        for (int i = 0; i < game.foundations.length; i++) {
            Foundation f = game.foundations[i];
            if (f != null && f.getPalo() == p) return i;
        }
        return -1;
    }
    // Dibuja los 4 foundations, si no contiene cartas se pone en sus labels
    // sus símbolos según el palo, caso contrario se dispone la carta
    private void dibujarFoundations() {
        for (int ui = 0; ui < foundationsVBox.getChildren().size(); ui++) {
            StackPane sp = (StackPane) foundationsVBox.getChildren().get(ui);
            Palo paloUI = switch (ui) {
                case 0 -> Palo.PICA;
                case 1 -> Palo.CORAZON;
                case 2 -> Palo.TREBOL;
                case 3 -> Palo.DIAMANTE;
                default -> throw new IllegalArgumentException();
            };
            int idx = gameFoundationIndexFromPalo(paloUI);
            Carta cartaTop = (idx >= 0) ? game.getTopFoundation(idx) : null;
            if (cartaTop == null) {
                setFoundationVacia(sp,paloUI);
            }else {
                setLabelsStackPane(sp, cartaTop);
            }
            final int uiIdx = ui;
            sp.setOnMouseClicked(e -> onClickFoundation(uiIdx));
            sp.setMouseTransparent(false);
        }
    }
    // Recibe un StackPane y lo diseña para cuando no contiene una carta
    public void setFoundationVacia(StackPane sp, Palo palo) {
        if (sp.getChildren().size() < 2) return;
        Label labelCentro  = (Label) sp.getChildren().get(0);
        Label labelEsquina = (Label) sp.getChildren().get(1);
        String fig = palo.getFigura();
        labelCentro.setText(fig);
        labelEsquina.setText(fig);
        labelCentro.setFont(Font.font(48));
        labelEsquina.setFont(Font.font(20));
        String color = (palo == Palo.CORAZON || palo == Palo.DIAMANTE)
                ? "-fx-text-fill: red;" : "-fx-text-fill: black;";
        labelCentro.setStyle(color);
        labelEsquina.setStyle(color);
        sp.setStyle(ESTILO_CARTA_TRANSPARENTE);
    }
    // Se invoca cuando  se selecciona un foundation, invoca
    // el movimiento de la clase de la lógica del juego
    private void onClickFoundation(int uiIdx) {
        Palo paloDestino = switch (uiIdx) {
            case 0 -> Palo.PICA;
            case 1 -> Palo.CORAZON;
            case 2 -> Palo.TREBOL;
            case 3 -> Palo.DIAMANTE;
            default -> throw new IllegalArgumentException();
        };
        int fGameIdx = gameFoundationIndexFromPalo(paloDestino);
        if (fGameIdx < 0) return;

        pistaReservaIdx = -1;

        switch (seleccion) {
            case TABLEAU -> { game.moverTaF(seleccionIdx, fGameIdx); limpiarSeleccion(); }
            case RESERVA -> { game.moverRaF(seleccionIdx, fGameIdx); limpiarSeleccion(); }
            case NADA -> {}
        }
    }
    /*
     * Dibuja los tableaus, dejando separación entre sus cartas
     * para c/d carta crea un stackPane y determina si puede o no
     * clickearse, solo es clickeable si se encuentra en el Top,
     * o si forma una escalera con la carta top
     */
    private void dibujarTableaus() {
        final double SALTO = SEPARACION_Y;
        for (int i = 0; i < tableausHBox.getChildren().size(); i++) {
            VBox colVBox = (VBox) tableausHBox.getChildren().get(i);
            colVBox.getChildren().clear();
            Pane tableau = new Pane();
            tableau.setPrefSize(CARTA_W, 522);
            tableau.setMinSize(CARTA_W, 0);
            StackPane fondo = hacerCartaVacia();
            fondo.relocate(0, 0);
            final int colIdx = i;
            fondo.setOnMouseClicked(e -> onClickTableau(colIdx, -1));
            fondo.setMouseTransparent(false);
            tableau.getChildren().add(fondo);
            List<Carta> cartas = game.getTableau(i).toList();
            int n = cartas.size();
            for (int k = 0; k < n; k++) {
                StackPane cartaNode = hacerCartaNoTransparente();
                setLabelsStackPane(cartaNode, cartas.get(k));
                cartaNode.relocate(0, k * SALTO);
                boolean esTop = (k == n - 1);
                boolean escaleraValida = esTop || escaleraHastaTope(cartas, k);
                cartaNode.setMouseTransparent(!escaleraValida);
                if (escaleraValida) {
                    final int startIdx = k;
                    cartaNode.setOnMouseClicked(e -> onClickTableau(colIdx, startIdx));
                    if (esTop && seleccion == Seleccion.TABLEAU && seleccionIdx == i) {
                        cartaNode.setStyle(ESTILO_SELECCIONADO);
                    }
                }
                tableau.getChildren().add(cartaNode);
            }
            colVBox.getChildren().add(tableau);
        }
    }
    /*
    * Verifica si desde el idx inicial hasta la carta top, se conforma una escalera;
    * mismo palo y valores descendentes
    * retorna true si puede moverse ese conjunto, false en caso contrario
     */
    private boolean escaleraHastaTope(List<Carta> cartas, int inicioIdx) {
        int n = cartas.size();
        if (inicioIdx < 0 || inicioIdx >= n) return false;
        Carta cTop = cartas.get(inicioIdx);
        if (inicioIdx == n - 1) return cTop != null;
        for (int t = inicioIdx + 1; t < n; t++) {
            Carta abajo  = cartas.get(t - 1);
            Carta arriba = cartas.get(t);
            if (abajo == null || arriba == null) return false;
            boolean mismoPalo   = (arriba.getPalo() == abajo.getPalo());
            boolean descendente = (abajo.getValor() - 1) == arriba.getValor();
            if (!(mismoPalo && descendente)) return false;
        }
        return true;
    }
    /*
    * Se invoca al clickear sobre un tableau
    * Si no hay una selección previa toma desde la carta hasta el tope
    * Si ya hay selección previa desde otro tableau intenta mover la Lista de Cartas contenida hacia el tableau
    * Si se da click en la misma columna ya seleccionada intenta mover al foundation
     */
    private void onClickTableau(int colDestino, int startIndexClic) {
        pistaReservaIdx = -1;
        List<Carta> cartasCol = game.getTableau(colDestino).toList();
        int n = cartasCol.size();
        switch (seleccion) {
            case NADA -> {
                if (n == 0) return;
                if (startIndexClic < 0 || startIndexClic >= n) return;
                seleccion = Seleccion.TABLEAU;
                seleccionIdx = colDestino;
                selectedCantidad = n - startIndexClic;
                dibujar();
            }
            case TABLEAU -> {
                int colOrigen = seleccionIdx;
                if (colOrigen == colDestino) {
                    if (!moverTableauAFundacion(colDestino)) limpiarSeleccion();
                    return;
                }
                game.moverTaT(colOrigen, colDestino, selectedCantidad);
                limpiarSeleccion();
            }
            case RESERVA -> {
                game.moverRaT(seleccionIdx, colDestino);
                limpiarSeleccion();
            }
        }
    }
    // Intenta mover la carta top del tableau de origen a su foundation, retorna boolean
    private boolean moverTableauAFundacion(int colOrigen) {
        Carta top = game.getTopTableau(colOrigen);
        if (top == null) return false;
        int fIdx = gameFoundationIndexFromPalo(top.getPalo());
        boolean ok = game.moverTaF(colOrigen, fIdx);
        if (ok) limpiarSeleccion();
        return ok;
    }
    // Reestablece los estados de selección y redibuja
    private void limpiarSeleccion() {
        seleccion = Seleccion.NADA;
        seleccionIdx = -1;
        selectedCantidad = 1;
        pistaReservaIdx = -1;
        dibujar();
    }
    // Pinta el contenido de un stackPane (centro y esquina superior izquierda)
    // si la carta=null, carta transparente, si no, coloca su palo y valor
    public void setLabelsStackPane(StackPane sp, Carta c){
        if (sp.getChildren().size() < 2) return;
        Label labelCentro  = (Label) sp.getChildren().get(0);
        Label labelEsquina = (Label) sp.getChildren().get(1);
        if (c == null){
            labelCentro.setText("");
            labelEsquina.setText("");
            sp.setStyle(ESTILO_CARTA_TRANSPARENTE);
            return;
        }
        String paloFig = c.getPalo().getFigura();
        String valor   = switch (c.getValor()) {
            case 14, 1 -> "A";
            case 11     -> "J";
            case 12     -> "Q";
            case 13     -> "K";
            default     -> String.valueOf(c.getValor());
        };
        labelCentro.setText(paloFig);
        labelEsquina.setText(valor + paloFig);
        labelCentro.setFont(Font.font(50));
        labelEsquina.setFont(Font.font(20));
        String color = (c.getPalo() == Palo.CORAZON || c.getPalo() == Palo.DIAMANTE)
                ? "-fx-text-fill: red;" : "-fx-text-fill: black;";
        labelCentro.setStyle(color);
        labelEsquina.setStyle(color);
        sp.setStyle(ESTILO_CARTA_NO_TRANSPARENTE);
    }
    // Crea un stackPane vacío
    // Se usa como hueco inicial de c/d columna y como destino clickeable
    // para soltar cartas
    private StackPane hacerCartaVacia() {
        StackPane sp = new StackPane();
        sp.setPrefSize(CARTA_W, CARTA_H);
        sp.setMinSize(CARTA_W, CARTA_H);
        sp.setMaxSize(CARTA_W, CARTA_H);
        sp.setStyle(ESTILO_CARTA_TRANSPARENTE);
        Label center = new Label("");
        Label corner = new Label("");
        center.setFont(Font.font(46));
        corner.setFont(Font.font(27));
        StackPane.setAlignment(center, javafx.geometry.Pos.CENTER);
        StackPane.setAlignment(corner, javafx.geometry.Pos.TOP_LEFT);
        StackPane.setMargin(corner, new Insets(6, 0, 0, 6));
        sp.getChildren().addAll(center, corner);
        return sp;
    }
    // Crea un stackPane de una carta visible: con fondo, borde, etc
    // Sus labels se colocan con setLabelStackPane()
    private StackPane hacerCartaNoTransparente() {
        StackPane sp = new StackPane();
        sp.setPrefSize(CARTA_W, CARTA_H);
        sp.setMinSize(CARTA_W, CARTA_H);
        sp.setMaxSize(CARTA_W, CARTA_H);
        Label centro = new Label("");
        centro.setFont(Font.font(46));
        StackPane.setAlignment(centro, javafx.geometry.Pos.CENTER);
        Label esquina = new Label("");
        esquina.setFont(Font.font(27));
        StackPane.setAlignment(esquina, javafx.geometry.Pos.TOP_LEFT);
        StackPane.setMargin(esquina, new Insets(6, 0, 0, 6));
        sp.getChildren().addAll(centro, esquina);
        sp.setStyle(ESTILO_CARTA_NO_TRANSPARENTE);
        return sp;
    }
    /*
     * Solicita una pista a la lógica del juego y refleja en la interfaz la sugerencia
     * Si la pista es tableau -> reserva, pinta el espacio de reserva de azul (sugerencia)
     * Si la pista es tableau o reserva como origen, marca esa carta como seleccionada
     * Si no hay más movimientos muestra un Alert
     */
    private void mostrarPista() {
        RegistroMovimiento mov = game.pista();
        if (mov == null) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Sin movimientos disponibles");
            a.setHeaderText(null);
            a.setContentText("No hay más movimientos posibles.");
            a.showAndWait();
            return;
        }
        pistaReservaIdx = -1;
        if (mov.tipo == RegistroMovimiento.Tipo.TR) {
            pistaReservaIdx = mov.toIdx;
            dibujar();
            return;
        }
        switch (mov.from) {
            case TABLEAU -> { seleccion = Seleccion.TABLEAU; seleccionIdx = mov.fromIdx; }
            case RESERVE -> { seleccion = Seleccion.RESERVA; seleccionIdx = mov.fromIdx; }
            default -> { seleccion = Seleccion.NADA; seleccionIdx = -1; }
        }
        dibujar();
    }
    /*
    * HISTORIAL
     */
    public void abrirHistorial(){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Historial de movimientos");
        undoLV = new ListView<>();
        redoLV = new ListView<>();
        Button btnUndoHistorial = new Button("Undo");
        Button btnRedoHistorial = new Button("Redo");
        Button btnConfirmar     = new Button ("Confirmar");
        undoSize = game.getUndoSize();
        sizeActual = undoSize;
        cambioHistorial = false;
        btnUndoHistorial.setOnAction(e -> {
            if (game.deshacer()) {
                sizeActual--;
                dibujar();
            }
        });
        btnRedoHistorial.setOnAction(e -> {
            if (game.rehacer()) {
                sizeActual++;
                dibujar();
            }
        });
        setHistorialLV();
        undoLV.setOnMouseClicked(e -> {
            if (undoLV.getSelectionModel().getSelectedIndex() < 0) return;
            previewHistorial(undoLV.getSelectionModel().getSelectedIndex());
        });
        btnConfirmar.setOnAction(e -> {
            game.clearRedo();                 // eliminamos movimientos “futuros”
            cambioHistorial = true;
            setHistorialLV();                 // ahora sí, la lista refleja el nuevo undo
            dibujar();
        });
        stage.setOnCloseRequest(e -> {
            if (!cambioHistorial) {
                ajustarHastaUndoSize(undoSize);
                dibujar();
            }
        });
        HBox barraBtns = new HBox(10, btnUndoHistorial, btnRedoHistorial, btnConfirmar);
        barraBtns.setPadding(new Insets(12,12,6,12));
        VBox vbox = new VBox(8, new Label("Movimientos"), undoLV);
        vbox.setPadding(new Insets(6,12,12,12));
        VBox.setVgrow(undoLV, Priority.ALWAYS);
        VBox main = new VBox(0, barraBtns, vbox);
        stage.setScene(new Scene(main, 520, 420));
        stage.show();
    }
    public void setHistorialLV() {
        undoLV.getItems().clear();
        redoLV.getItems().clear();
        ListaDoble<RegistroMovimiento> historial = game.getHistorialMovimientos();
        if (historial==null) return;
        int numMov = historial.getSize();
        NodoDoble<RegistroMovimiento> mov = historial.getInicio();
        while (mov!=null){
            undoLV.getItems().add("Movimiento " + mov.getInfo().toString());
            numMov--;
            System.out.println(numMov);
            mov = mov.getSiguiente();
        }
    }
    public void aplicarSeleccionHistorial() {
        int idxSeleccion = undoLV.getSelectionModel().getSelectedIndex();
        if (idxSeleccion < 0) return;
        ListaDoble<RegistroMovimiento> mov = game.getHistorialMovimientos();
        if (mov == null) return;
        int size = mov.getSize();
        if (size <= 0) return;
        int undos = size - idxSeleccion;
        if (undos < 0) undos = 0;
        for (int i = 0; i < undos; i++) {
            if (!game.deshacer()) break;
        }
        seleccion = Seleccion.NADA;
        seleccionIdx = -1;
        selectedCantidad = 1;
        pistaReservaIdx = -1;
        setHistorialLV();
        dibujar();
    }
    private void ajustarHastaUndoSize(int target) {
        while (sizeActual > target) {
            if (!game.deshacer()) break;
            sizeActual--;
        }
        while (sizeActual < target) {
            if (!game.rehacer()) break;
            sizeActual++;
        }
    }
    private void previewHistorial(int idxSeleccion) {
        int undoSizeIdeal = idxSeleccion;
        if (undoSizeIdeal < 0) undoSizeIdeal = 0;
        ajustarHastaUndoSize(undoSizeIdeal);
        seleccion = Seleccion.NADA;
        seleccionIdx = -1;
        selectedCantidad = 1;
        pistaReservaIdx = -1;
        dibujar();
    }

}
