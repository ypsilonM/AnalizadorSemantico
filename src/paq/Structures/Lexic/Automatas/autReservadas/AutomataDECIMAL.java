package paq.Structures.Lexic.Automatas.autReservadas;

import paq.Structures.Lexic.Automata;
import paq.Structures.Lexic.Estado;

public class AutomataDECIMAL extends Automata {
    public AutomataDECIMAL(){

        Estado inicial = new Estado(false);
        Estado estado1 = new Estado(false);
        Estado estado2 = new Estado(false);
        Estado estado3 = new Estado(false);
        Estado estado4 = new Estado(false);
        Estado estado5 = new Estado(false);
        Estado estado6 = new Estado(false);
        Estado estado7 = new Estado(true);

        super.setActual(inicial);
        inicial.setWay('D',estado1);
        estado1.setWay('E',estado2);
        estado2.setWay('C',estado3);
        estado3.setWay('I',estado4);
        estado4.setWay('M',estado5);
        estado5.setWay('A',estado6);
        estado6.setWay('L',estado7);

    }
}
