package JuegoLogica;

import Logica.CartaInglesa;
import Logica.Palo;
import javafx.application.Platform;

public class MainPruebaLogicaJuego {
    public static void main(String[] args) {
        Platform.startup(() -> {});
        EightOffGame game = new EightOffGame();
        game.limpiarComponentes();
        for (int i = 0; i < 4; i++) {
            Palo palo = game.foundations[i].getPalo();
            for (int v = 1; v <= 13; v++) {
                game.foundations[i].push(new CartaInglesa(v, palo, palo.getColor()));
            }
        }
        Platform.runLater(game::victoria);
    }
}
