package paq.Analizadores.Lexico.Automatas.autReservadas;

import paq.Structures.Lexic.Automata;
import paq.Structures.Lexic.Estado;

public class AutomataPRINT extends Automata {
    public AutomataPRINT(){

        Estado inicial = new Estado(false);
        Estado estado1 = new Estado(false);
        Estado estado2 = new Estado(false);
        Estado estado3 = new Estado(false);
        Estado estado4 = new Estado(false);
        Estado estado5 = new Estado(true);
        super.setActual(inicial);
        inicial.setWay('P',estado1);
        estado1.setWay('R',estado2);
        estado2.setWay('I',estado3);
        estado3.setWay('N',estado4);
        estado4.setWay('T',estado5);

    }
}
