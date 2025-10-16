package JuegoLogica;

public class MainPruebaLogicaJuego {
    public static void main(String[] args) {
        ListaSimple list = new ListaSimple();
        list.insertaFinal(1);
        list.insertaFinal(2);
        System.out.println(list.getFin().toString());
    }
}
