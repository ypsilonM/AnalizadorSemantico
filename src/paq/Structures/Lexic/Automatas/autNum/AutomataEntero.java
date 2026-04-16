package paq.Structures.Lexic.Automatas.autNum;

import paq.Structures.Lexic.Automata;
import paq.Structures.Lexic.Estado;

public class AutomataEntero extends Automata {
    public AutomataEntero(){

        Estado inicial = new Estado(false);
        Estado estado1 = new Estado(true);
        Estado estado2 = new Estado(true);
        Estado estado3 = new Estado(true);
        Estado estado4 = new Estado(true);
        Estado estado5 = new Estado(true);
        Estado estado6 = new Estado(true);

        Character[] numeros = {
                '0','1','2','3','4','5','6','7','8','9'
        };

        super.setActual(inicial);
        inicial.setSameWay(numeros, estado1);
        estado1.setSameWay(numeros, estado2);
        estado2.setSameWay(numeros, estado3);
        estado3.setSameWay(numeros, estado4);
        estado4.setSameWay(numeros, estado5);
        estado5.setSameWay(numeros, estado6);

    }
}
