package com.example.practica4algoritmos;

import JuegoLogica.EightOffGame;
import JuegoLogica.Foundation;
import JuegoLogica.RegistroMovimiento;
import Logica.Carta;
import Logica.Palo;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class ControllerInterfazSolitaire {
    @FXML private HBox reservasHBox;
    @FXML private VBox foundationsVBox;
    @FXML private HBox tableausHBox;
    @FXML private Button btnSalir;
    @FXML private Button btnPista;
    @FXML private Button btnUndo;

    private EightOffGame game;
    private int selectedCantidad = 1;

    private static final double CARTA_W = 90;
    private static final double CARTA_H = 130;
    private static final double SEPARACION_Y = 50;

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

    private enum Seleccion {NADA, TABLEAU, RESERVA}
    private Seleccion seleccion = Seleccion.NADA;
    private int seleccionIdx = -1;
    private int pistaReservaIdx = -1;

    @FXML
    private void initialize() {
        game = new EightOffGame();
        game.iniciarNuevaPartida();
        btnSalir.setOnAction(e -> ((Stage) btnSalir.getScene().getWindow()).close());
        btnUndo.setOnAction(e -> {
            if (game.deshacer()) limpiarSeleccion();
        });
        btnPista.setOnAction(e -> mostrarPista());
        dibujar();
    }
    private void dibujar() {
        dibujarReservas();
        dibujarFoundations();
        dibujarTableaus();
        game.sinMovimientos();
    }
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
    private boolean moverReservaAFundacion(int idxReserva) {
        Carta top = game.getTopReservas(idxReserva);
        if (top == null) return false;
        int fIdx = gameFoundationIndexFromPalo(top.getPalo());
        boolean ok = game.moverRaF(idxReserva, fIdx);
        if (ok) limpiarSeleccion();
        return ok;
    }
    private int gameFoundationIndexFromPalo(Palo p) {
        for (int i = 0; i < game.foundations.length; i++) {
            Foundation f = game.foundations[i];
            if (f != null && f.getPalo() == p) return i;
        }
        return -1;
    }
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
    private boolean escaleraHastaTope(List<Carta> cartas, int inicioIdx) {
        int n = cartas.size();
        if (inicioIdx < 0 || inicioIdx >= n) return false;
        if (inicioIdx == n - 1) return true;
        for (int t = inicioIdx + 1; t < n; t++) {
            Carta abajo  = cartas.get(t - 1);
            Carta arriba = cartas.get(t);
            boolean mismoPalo = arriba.getPalo() == abajo.getPalo();
            boolean descendente = (abajo.getValor() - 1) == arriba.getValor();
            if (!(mismoPalo && descendente)) return false;
        }
        return true;
    }
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
    private boolean moverTableauAFundacion(int colOrigen) {
        Carta top = game.getTopTableau(colOrigen);
        if (top == null) return false;
        int fIdx = gameFoundationIndexFromPalo(top.getPalo());
        boolean ok = game.moverTaF(colOrigen, fIdx);
        if (ok) limpiarSeleccion();
        return ok;
    }
    private void limpiarSeleccion() {
        seleccion = Seleccion.NADA;
        seleccionIdx = -1;
        selectedCantidad = 1;
        pistaReservaIdx = -1;
        dibujar();
    }
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
}
