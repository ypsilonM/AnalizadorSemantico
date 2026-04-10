package paq.Structures.Semantic;

import org.w3c.dom.Node;
import paq.Principal;
import paq.Structures.Syntax.Nodo;
import paq.Structures.TT;
import java.util.*;

public class Semantic {
    private static ArrayList<TT> terminalList =
            new ArrayList<>(List.of(TT.TK_ID, TT.TK_DECV, TT.TK_INTV));
    private static ArrayList<TT> dontNeedToEvaluate =
            new ArrayList<>(List.of(
                    TT.TK_STRT, TT.TK_END, TT.TK_READ, TT.TK_PRNT,
                    TT.TK_OBR, TT.TK_CBR, TT.TK_OPR, TT.TK_CPR, TT.TK_SMC, TT.TK_CMA, TT.TK_DOT,
                    TT.TK_EQU, TT.TK_ADD, TT.TK_SUB, TT.TK_MUL, TT.TK_DIV,
                    TT.TK_$,
                    TT.PROGRAM, TT.BODY, TT.PRINT, TT.READ
            ));

    public static void semanticAnalyze(LinkedList<Nodo> treeNodes){
        Type typifier = null;

        for(Nodo node: treeNodes){
            if(notEvaluate(node)) continue;

            if(isTypifier(node)) { //Type definer: (Integer / Decimal) token
                typifier = getEq(node.getToken().getTipo());
                node.setType(typifier);
                continue;
            }
            if(isTerminal(node)){ // Terminal -> No children
                if(idSymbol(node)){ // Symbol -> Search in table
                    int i = searchInSymbolTable(node);
                    Symbol symbol = Principal.symbolTable.get(i);
                    boolean symbolTypeDefined = symbol.getType()!=null;
                    if(typifier == null) {
                        if (symbolTypeDefined) {
                            node.setType(symbol.getType());
                            //symbol.setType(symbol.getType());
                        } else {
                            node.setType(Type.ERROR);
                            // < ERROR DE VARIABLE NO DECLARADA >
                        }
                    }else{ //Hay typifier
                        if (symbolTypeDefined) {
                            if (isAsigned(node)) {
                                node.setType(Type.ERROR);
                                // < ERROR DE DOBLE DECLARACION >
                            } else {
                                node.setType(symbol.getType());
                            }
                        } else {
                            if (isAsigned(node)) {
                                node.setType(typifier);
                                symbol.setType(typifier);
                            } else {
                                node.setType(Type.ERROR);
                                // < ERROR DE VARIABLE NO DECLARADA >
                            }
                        }
                    }
                }else { // Numeric Constant -> Auto-Asign Type
                    Type type = getEq(node.getToken().getTipo());
                    node.setType(type);
                }
            }else{ // Not Terminal -> Verify children types
                if(node.getToken().getTipo() == TT.STATEMENT) {
                    typifier = null;
                }else {

                    node.setType(searchTypeInTree(node));

                }
            }

        }
        for(Nodo node : treeNodes){
            char tab = '\t';
            System.out.println( " | "+tab+
                    node.getNumero() +tab+ " - " +
                    node.getToken().getLexeme()+tab+tab+" : " +
                    node.getPadre() +tab+ " -> " +
                    node.getType() +tab+ " | "
            );
        }
    }

    public static Type getEq(TT tt){
        return switch (tt){
            case TT.TK_INTT, TT.TK_INTV -> Type.INTEGER;
            case TT.TK_DECT, TT.TK_DECV -> Type.DECIMAL;
            default -> null;
        };
    }
    public static int searchInSymbolTable(Nodo node){
        for(int i=0; i<Principal.symbolTable.size(); i++){
            if(Principal.symbolTable.get(i).getToken().equals(node.getToken())){
                return i;
            }
        }
        return -1;
    }

    public static Type searchTypeInTree(Nodo parentNode){
        int idPadre = parentNode.getNumero();
        Set<Type> typeSet = new LinkedHashSet<>();
        for(int i = 0; i<Principal.treeNodes.size(); i++) {
            Nodo node = Principal.treeNodes.get(i);
            if(node.getPadre() == idPadre && !notEvaluate(node)){
                typeSet.add(node.getType());
                continue;
            }
            if(node.getNumero()==idPadre)
                break;
        }
        boolean oneType =  typeSet.size()<2;
        if(oneType){
            //Asignacion de valores
            if(parentNode.getToken().getTipo() == TT.ASIGN){
                Nodo rightValue = Principal.treeNodes.get(searchNode(parentNode, TT.E));
                Nodo leftValue = Principal.treeNodes.get(searchNode(parentNode, TT.TK_ID));

                if(isUniqueValue(rightValue)){
                    //Asignar x = val
                    String val = getLexemeValue(rightValue);
                    if(val != null){
                        int i = searchInSymbolTable(leftValue);
                        Symbol symbol = Principal.symbolTable.get(i);
                        symbol.setValue(val);
                    }else {
                        System.out.println("VALOR NO DEFINIDO EN LA TABLA DE VALORES");
                    }
                }
            }

            return getType(typeSet);
        }else{
            if(!typeSet.contains(Type.ERROR)) {
                System.out.println();
                // < ERROR DE INCOMPATIBILIDAD DE TIPOS >
            }
            return Type.ERROR;
        }
    }
    public static int searchNode(Nodo asign, TT tt){
        int index = asign.getNumero();
        for(int i = index-1; i>=0; i--){
            Nodo n = Principal.treeNodes.get(i);
            if(n.getToken().getTipo() == tt) return n.getNumero()-1;
        }
        return -1;
    }

    public static Type getType(Set<Type> set){
        if(set.contains(Type.INTEGER))
            return Type.INTEGER;
        if(set.contains(Type.DECIMAL))
            return Type.DECIMAL;
        if(set.contains(Type.ERROR))
            return Type.ERROR;
        return null;
    }

    public static boolean isTypifier(Nodo nodo){
        TT tt = nodo.getToken().getTipo();
        return tt == TT.TK_INTT || tt == tt.TK_DECT;
    }

    public static boolean isTerminal(Nodo node){
        return terminalList.contains(node.getToken().getTipo());
    }
    public static boolean idSymbol(Nodo node){
        return node.getToken().getTipo() == TT.TK_ID;
    }
    public static boolean notEvaluate(Nodo node){
        return dontNeedToEvaluate.contains(node.getToken().getTipo());
    }

    public static boolean isUniqueValue(Nodo node) {
        int nodoPadre = node.getNumero();
        List<Nodo> children = new LinkedList<>();

        while (true){
            for (int i = nodoPadre - 1; i >= 0; i--) {
                if(Principal.treeNodes.get(i).getPadre() == nodoPadre
                        && !notEvaluate(Principal.treeNodes.get(i))) {
                    children.add(Principal.treeNodes.get(i));
                }
            }

            if(children.size()>1) return false;

            nodoPadre = children.getFirst().getNumero();
            TT t = Principal.treeNodes.get(nodoPadre-1).getToken().getTipo();

            if( t == TT.TK_INTV || t == TT.TK_DECV || t == TT.TK_ID ) return true;

            children.clear();
        }
    }
    public static String getLexemeValue(Nodo node){
        int nodoActual= node.getNumero();
        for (int i = nodoActual - 1; i >= 0; i--) {
            if(Principal.treeNodes.get(i).getPadre() == nodoActual){
                nodoActual = Principal.treeNodes.get(i).getNumero();
            }
        }
        Nodo nodo = Principal.treeNodes.get(nodoActual-1);
        TT t = nodo.getToken().getTipo();
        if( t == TT.TK_INTV )
            return nodo.getToken().getLexeme();
        if( t == TT.TK_DECV )
            return nodo.getToken().getLexeme();
        if( t == TT.TK_ID ) {
            int i = searchInSymbolTable(nodo);
            Symbol symbol = Principal.symbolTable.get(i);
            if(symbol != null)
                return symbol.getValue();
        }
        return null;
    }


    public static boolean isRightSideAsign(Nodo node){
        //Up search (from children to parent Asign
        if(comesFromAsign(node)){
                Nodo actualNode = node;
                int i = actualNode.getNumero()-1;
                while(i<Principal.treeNodes.size() && actualNode.getPadre()!=0){
                    i = actualNode.getPadre()-1;
                    actualNode = Principal.treeNodes.get(i);
                    if(actualNode.getToken().getTipo()==TT.E)
                        return true;
                }
            return false;
        }else{ //Not inside an ASIGNMENT
            return false;
        }
    }
    public static boolean isAsigned(Nodo node){
        return !isRightSideAsign(node);
    }
    public static boolean comesFromAsign(Nodo node){
        Nodo actualNode = node;
        int i = actualNode.getNumero()-1;
        while(i<Principal.treeNodes.size() && actualNode.getPadre()!=0){
            i = actualNode.getPadre()-1;
            actualNode = Principal.treeNodes.get(i);
            if(actualNode.getToken().getTipo()==TT.ASIGN)
                break;
        }
        if(actualNode.getToken().getTipo()==TT.ASIGN){
            return true;
        }else{
            return false;
        }
    }

}
