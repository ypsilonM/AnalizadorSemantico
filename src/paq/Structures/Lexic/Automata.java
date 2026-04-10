package paq.Structures.Lexic;

import java.util.Stack;

public class Automata {
    private Estado actual;

    public Automata(){}

    public Estado getActual() {
        return actual;
    }
    public void setActual(Estado actual) {
        this.actual = actual;
    }

    public boolean recorrer(Stack<Character> pilaRecorrer){
        Stack<Character> pila = new Stack<>();
        pila.addAll(pilaRecorrer);
        boolean path = true;
        while(!pila.empty() && path){
            char caracter = pila.pop();

            Estado actual = getActual();
            Estado estado = actual.getCaminos().get(caracter);
            if(estado!=null) {
                setActual(estado);
            }else{
                path=false;
            }
        }
        return path && getActual().isAceptacion();
    }
}

