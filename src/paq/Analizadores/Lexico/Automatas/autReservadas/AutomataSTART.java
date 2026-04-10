package paq.Analizadores.Lexico.Automatas.autReservadas;

import paq.Structures.Lexic.Automata;
import paq.Structures.Lexic.Estado;

public class AutomataSTART extends Automata {
    public AutomataSTART(){

        Estado inicial = new Estado(false);
        Estado estado1 = new Estado(false);
        Estado estado2 = new Estado(false);
        Estado estado3 = new Estado(false);
        Estado estado4 = new Estado(false);
        Estado estado5 = new Estado(true);
        super.setActual(inicial);
        inicial.setWay('S',estado1);
        estado1.setWay('T',estado2);
        estado2.setWay('A',estado3);
        estado3.setWay('R',estado4);
        estado4.setWay('T',estado5);

    }
}


