package paq.Analizadores.Lexico.Automatas.autNum;

import paq.Structures.Lexic.Automata;
import paq.Structures.Lexic.Estado;

public class AutomataDecimal extends Automata {
    public AutomataDecimal(){

        Estado inicial = new Estado(false);
        Estado estadoN1 = new Estado(false);
        Estado estadoN2 = new Estado(false);
        Estado estadoN3 = new Estado(false);
        Estado estadoN4 = new Estado(false);
        Estado estadoN5 = new Estado(false);
        Estado estadoN6 = new Estado(false);

        Estado estado7 = new Estado(false);
        Estado estado8 = new Estado(true);
        Estado estado9 = new Estado(true);
        Estado estado10 = new Estado(true);

        Character[] numeros = {
                '0','1','2','3','4','5','6','7','8','9'
        };

        super.setActual(inicial);
        inicial.setSameWay(numeros, estadoN1);
        estadoN1.setSameWay(numeros, estadoN2);
        estadoN1.setWay('.',estado7);
        estadoN2.setSameWay(numeros, estadoN3);
        estadoN2.setWay('.',estado7);
        estadoN3.setSameWay(numeros, estadoN4);
        estadoN3.setWay('.',estado7);
        estadoN4.setSameWay(numeros, estadoN5);
        estadoN4.setWay('.',estado7);
        estadoN5.setSameWay(numeros, estadoN6);
        estadoN5.setWay('.',estado7);
        estadoN6.setWay('.',estado7);

        estado7.setSameWay(numeros,estado8);
        estado8.setSameWay(numeros,estado9);
        estado9.setSameWay(numeros,estado10);
    }
}
