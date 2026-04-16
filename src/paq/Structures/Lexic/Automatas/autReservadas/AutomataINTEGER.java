package paq.Structures.Lexic.Automatas.autReservadas;

import paq.Structures.Lexic.Automata;
import paq.Structures.Lexic.Estado;

public class AutomataINTEGER extends Automata {
    public AutomataINTEGER(){

        Estado inicial = new Estado(false);
        Estado estado1 = new Estado(false);
        Estado estado2 = new Estado(false);
        Estado estado3 = new Estado(false);
        Estado estado4 = new Estado(false);
        Estado estado5 = new Estado(false);
        Estado estado6 = new Estado(false);
        Estado estado7 = new Estado(true);

        super.setActual(inicial);
        inicial.setWay('I',estado1);
        estado1.setWay('N',estado2);
        estado2.setWay('T',estado3);
        estado3.setWay('E',estado4);
        estado4.setWay('G',estado5);
        estado5.setWay('E',estado6);
        estado6.setWay('R',estado7);

    }
}
