package paq.Structures.Lexic.Automatas.autIdenti;

import paq.Structures.Lexic.Automata;
import paq.Structures.Lexic.Estado;

public class AutomataVariable extends Automata {
    public AutomataVariable(){

        Estado inicial = new Estado(false);
        Estado estado1 = new Estado(false);
        Estado estado2 = new Estado(true);
        Estado estado3 = new Estado(true);
        Estado estado4 = new Estado(true);

        Character[] letras = {
                'A','B','C','D','E','F','G','H','I','J','K','L','M',
                'N','Ñ','O','P','Q','R','S','T','U','V','W','X','Y','Z',
                'a','b','c','d','e','f','g','h','i','j','k','l','m',
                'n','ñ','o','p','q','r','s','t','u','v','w','x','y','z'};
        Character[] numeros = {
                '0','1','2','3','4','5','6','7','8','9'
        };

        super.setActual(inicial);
        inicial.setSameWay(letras,estado1);
        estado1.setSameWay(numeros,estado2);
        estado2.setSameWay(numeros,estado3);
        estado3.setSameWay(numeros,estado4);
    }
}
