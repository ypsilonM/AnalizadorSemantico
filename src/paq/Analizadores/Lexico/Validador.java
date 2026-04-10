package paq.Analizadores.Lexico;

import paq.Analizadores.Lexico.Automatas.autIdenti.AutomataVariable;
import paq.Analizadores.Lexico.Automatas.autNum.AutomataDecimal;
import paq.Analizadores.Lexico.Automatas.autNum.AutomataEntero;
import paq.Analizadores.Lexico.Automatas.autReservadas.*;
import paq.Structures.TT;

import java.util.Stack;

public class Validador {

    public static TT isIdentificador(String men){
        Stack<Character> pila = getOrdererStack(men);
        if(new AutomataVariable().recorrer(pila)){
            return TT.TK_ID;
        }
        return null;
    }

    public static TT isNumero(String men){
        Stack<Character> pila = getOrdererStack(men);
        if(new AutomataEntero().recorrer(pila)){
            return TT.TK_INTV;
        }else if(new AutomataDecimal().recorrer(pila)){
            return TT.TK_DECV;
        }
        return null;
    }

    public static TT isReservada(String men){
        Stack<Character> pila = getOrdererStack(men);
        if(new AutomataDECIMAL().recorrer(pila)){
            return TT.TK_DECT;
        }else if(new AutomataINTEGER().recorrer(pila)){
            return TT.TK_INTT;
        }else if(new AutomataSTART().recorrer(pila)){
            return TT.TK_STRT;
        }else if(new AutomataEND().recorrer(pila)){
            return TT.TK_END;
        }else if(new AutomataREAD().recorrer(pila)){
            return TT.TK_READ;
        }else if(new AutomataPRINT().recorrer(pila)){
            return TT.TK_PRNT;
        }
        return null;
    }

    public static TT isSimbolo(String men){
        char caracter = men.charAt(0);
        return switch (caracter){
            case '{' -> TT.TK_OBR;
            case '}' -> TT.TK_CBR;
            case '(' -> TT.TK_OPR;
            case ')' -> TT.TK_CPR;
            case ';' -> TT.TK_SMC;
            case ',' -> TT.TK_CMA;
            case '.' -> TT.TK_DOT;
            case '=' -> TT.TK_EQU;
            case '+' -> TT.TK_ADD;
            case '-' -> TT.TK_SUB;
            case '*' -> TT.TK_MUL;
            case '/' -> TT.TK_DIV;
            default -> null;
        };
    }

    public static Stack<Character> getOrdererStack(String msg){
        char[] cadena = msg.toCharArray();
        Stack pila1 = new Stack<>();
        for(char c : cadena){
            pila1.push(c);
        }
        Stack pila2 = new Stack<>();
        while(!pila1.isEmpty()){
            pila2.push(pila1.pop());
        }
        return pila2;
    }
}
