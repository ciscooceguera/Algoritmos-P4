package Logica;

public enum Palo {
    TREBOL(1,"♣\uFE0E","negro","Trebol"),
    DIAMANTE(2,"♦\uFE0F","rojo","Diamante"),
    CORAZON(3,"❤\uFE0F","rojo","Corazon"),
    PICA(4,"♠\uFE0F","negro","Pica");

    private final int peso;
    private final String figura;
    private final String color;
    private final String paloString;

    Palo(int peso, String figura, String color, String paloString) {
        this.peso = peso;
        this.figura = figura;
        this.color = color;
        this.paloString = paloString;
    }
    public int getPeso() {
        return peso;
    }
    public String getFigura() {
        return figura;
    }
    public String getColor() {
        return color;
    }
    public String getPaloString() {
        return paloString;
    }
}
