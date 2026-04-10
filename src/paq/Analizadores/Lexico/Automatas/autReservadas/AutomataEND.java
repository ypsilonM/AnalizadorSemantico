package paq.Analizadores.Lexico.Automatas.autReservadas;

import paq.Structures.Lexic.Automata;
import paq.Structures.Lexic.Estado;

public class AutomataEND extends Automata {
    public AutomataEND(){

        Estado inicial = new Estado(false);
        Estado estado1 = new Estado(false);
        Estado estado2 = new Estado(false);
        Estado estado3 = new Estado(true);
        super.setActual(inicial);
        inicial.setWay('E',estado1);
        estado1.setWay('N',estado2);
        estado2.setWay('D',estado3);

    }
}
