package Logica;

public abstract class Carta implements Comparable<Carta>{
    private int valor;
    protected Palo palo;
    private String color;
    private int valorBajo;
    private boolean faceUp;

    public Carta(int valor, Palo palo, String color){
        this.valor = valor;
        this.palo = palo;
        this.color = color;
        if (valor == 14){
            valorBajo = 1;
        }else{
            valorBajo = valor;
        }
        faceUp = false;
    }
    public void makeFaceDown(){
        faceUp = false;
    }
    public void makeFaceUp(){
        faceUp = true;
    }
    public boolean isFaceUp(){
        return faceUp;
    }
    public String toString(){
        if (!faceUp){
            return "@";
        }
        return switch(valor){
            case 14 -> "A" + palo.getFigura();
            case 13 -> "K" + palo.getFigura();
            case 12 -> "Q" + palo.getFigura();
            case 11 -> "J" + palo.getFigura();
            default -> valor + palo.getFigura();
        };
    }
    public boolean tieneMismoPalo(Carta carta){
        return this.palo.equals(carta.palo);
    }
    public boolean esLaSiguiente(Carta carta){
        if (valor+1 == carta.valor){
            return true;
        }
        if (valor == 14 && carta.valor == 2){
            return true;
        }
        return false;
    }
    public int getValor() {
        return valor;
    }
    public String getPaloString(){
        return palo.getPaloString();
    };
    public Palo getPalo() {
        return palo;
    }
    public String getColor() {
        return color;
    }
    public int getValorBajo() {
        return valorBajo;
    }

}
