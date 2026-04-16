package paq.Structures.Lexic.Automatas.autReservadas;

import paq.Structures.Lexic.Automata;
import paq.Structures.Lexic.Estado;

public class AutomataREAD extends Automata {
    public AutomataREAD(){

        Estado inicial = new Estado(false);
        Estado estado1 = new Estado(false);
        Estado estado2 = new Estado(false);
        Estado estado3 = new Estado(false);
        Estado estado4 = new Estado(true);
        super.setActual(inicial);
        inicial.setWay('R',estado1);
        estado1.setWay('E',estado2);
        estado2.setWay('A',estado3);
        estado3.setWay('D',estado4);

    }
}
