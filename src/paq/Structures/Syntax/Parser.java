package paq.Structures.Syntax;

import paq.Principal;
import paq.Structures.Token;
import paq.Structures.TT;

import java.util.*;

public class Parser {

    private Stack<Token> stack;
    private List<Token> tokens;
    private int index = 0;
    private String temporalErrorMsg;

    private List<Token> errores;
    private boolean huboError = false;

    private List<Nodo> arbol;
    private int counter;


    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.stack = new Stack<>();
        this.errores = new ArrayList<>();
        this.arbol = new LinkedList<>();
        counter = 1;
    }

    private Token mirarDelante() {
        if (index < tokens.size())
            return tokens.get(index);
        return new Token(TT.TK_$, "$");
    }

    private void avanzar() { index++; }
    private void apilarDeLista(Token token) {
        stack.push(token);
    }
    public void parse() {

        stack.clear();
        errores.clear();
        huboError = false;
        index = 0;

        stack.push(new Token(TT.TK_$, "$"));

        while (index < tokens.size()) {

            Token tokenDelante = mirarDelante();
            if (intentarReducir(tokenDelante)) {
                continue;
            }
            if (puedeApilar(tokenDelante)) {
                apilarDeLista(tokenDelante);
                avanzar();
                continue;
            }
            reportError(tokenDelante);
            recuperarse();
        }

        while (intentarReducir(new Token(TT.TK_$, "$"))) {}

        boolean aceptado = false;
        for (Token t : stack) {
            if (t.getTipo() == TT.PROGRAM) {
                aceptado = true;
                Nodo PROGRAM = new Nodo(counter++, stack.pop());
                arbol.add(PROGRAM);
                asignarHijos(PROGRAM, 1);
                break;
            }
        }
        System.out.println(stack.peek().getTipo().toString());
        aceptado = aceptado &&
                stack.get(0).getTipo() == TT.TK_$ &&
                !huboError;

        if (aceptado) {
            System.out.println(" ACEPTADO :D");
            arbol.forEach( e-> {
                System.out.println(e.getNumero() +".-"+e.getToken().getTipo()+ " ("+e.getToken().getLexeme()+")  -> "+ e.getPadre()+".");
            });

            Principal.treeNodes.addAll(arbol);
        } else {
            System.out.println(" PROGRAMA CON ERRORES");
            Principal.errorsTokenList.addAll(errores);


        }
    }

    private void recuperarse() {
        System.out.println("  Recuperación de error...");
        while (index < tokens.size()) {
            TT t = mirarDelante().getTipo();

            if (t == TT.TK_SMC ) {
                apilarDeLista(mirarDelante());
                avanzar();
                apilarDeLista(mirarDelante());
                avanzar();
                return;
            }
            if(esInicioDeStatement(t) || t == TT.TK_CBR){
                apilarDeLista(mirarDelante());
                avanzar();
                return;
            }
            apilarDeLista(mirarDelante());
            avanzar();
        }
    }

    private void reportError(Token token) {
        huboError = true;
        String mensaje = "Error sintáctico en línea " + token.getLine() + ": token inesperado '" + token.getLexeme() + "'";
        System.out.println(mensaje);

        token.setSyntax(true);
        token.setError(temporalErrorMsg);
        errores.add(token);
    }
    private boolean puedeApilar(Token tokenDelante) {
        TT tipoTokenDelante = tokenDelante.getTipo();
        TT tipoTopToken = stack.peek().getTipo();

        if (tipoTokenDelante == TT.TK_$)
            return false;

        if(tipoTokenDelante != TT.TK_STRT && tipoTopToken==TT.TK_$){
            boolean key = false;
            temporalErrorMsg = "El primer token debe ser START";;
            return key;
        }
        if (tipoTopToken == TT.TK_READ || tipoTopToken == TT.TK_PRNT) {
            boolean key = tipoTokenDelante == TT.TK_OPR;
            temporalErrorMsg = key?temporalErrorMsg: "READ/PRINT esperaba un (";;
            return key;
        }
        if (tipoTopToken == TT.TK_EQU){
            boolean key = isValue(tipoTokenDelante) || tipoTokenDelante == TT.TK_OPR;
            temporalErrorMsg = key?temporalErrorMsg: "Despues de = se espera un inicio de operación";;
            return key;
        }
        if (tipoTopToken == TT.TK_OPR){
            boolean key;
            if(estaAntes(TT.READ)){
                key = tipoTokenDelante == TT.TK_ID;
                temporalErrorMsg = key?temporalErrorMsg: "Despues de READ( se espera un id";;
                return key;
            }
            key = isValue(tipoTokenDelante) || tipoTokenDelante == TT.TK_OPR;
            temporalErrorMsg = key?temporalErrorMsg: "Despues de ( se espera un valor";
            return key;
        }
        if (tipoTopToken == TT.TK_CPR){
            boolean key = tipoTokenDelante == TT.TK_SMC || isOperator(tipoTokenDelante)|| tipoTokenDelante == TT.TK_CPR;
            temporalErrorMsg = key?temporalErrorMsg: "Despues de ) espera un ';', un operador o un ) ";;
            return key;
        }
        if(tipoTopToken == TT.TK_ID){
            if(estaDosAntes(TT.TK_READ)){
                boolean key = tipoTokenDelante == TT.TK_CPR;
                temporalErrorMsg = key?temporalErrorMsg: "Despues de READ(id se espera un )";
                return key;
            }else{
                boolean key = tipoTokenDelante == TT.TK_CMA || tipoTokenDelante == TT.TK_EQU;
                temporalErrorMsg = key?temporalErrorMsg: "Despues de un id se espera una ',' o '='";
                return key;
            }
        }
        if (tipoTopToken == TT.TK_ADD || tipoTopToken == TT.TK_SUB || tipoTopToken == TT.TK_MUL || tipoTopToken == TT.TK_DIV) {
            boolean key = isValue(tipoTokenDelante) || tipoTokenDelante == TT.TK_OPR;
            temporalErrorMsg = key?temporalErrorMsg: "Operador esperaba operando o (";
            return key;
        }
        if (tipoTopToken == TT.T || tipoTopToken == TT.F || tipoTopToken == TT.E){
            boolean key = isOperator(tipoTokenDelante) || tipoTokenDelante == TT.TK_CPR;
            temporalErrorMsg = key?temporalErrorMsg: "Operando esperaba operador o )";
            return key;
        }
        if (tipoTopToken == TT.TYPE) {
            boolean key = tipoTokenDelante == TT.TK_ID;
            temporalErrorMsg = key?temporalErrorMsg: "TYPE esperaba id";
            return key;
        }
        if (tipoTopToken == TT.TK_CMA) {
            boolean key = tipoTokenDelante == TT.TK_ID;
            temporalErrorMsg = key?temporalErrorMsg: "Despues de una coma se espera un id";
            return key;
        }
        if (tipoTopToken == TT.DECLARE) {
            boolean key = tipoTokenDelante == TT.TK_SMC || tipoTokenDelante == TT.TK_CMA;
            temporalErrorMsg = key?temporalErrorMsg: "Declaraciones esperan un ';' o ','";
            return key;
        }
        if (tipoTopToken == TT.ASIGN || tipoTopToken == TT.PRINT || tipoTopToken == TT.READ) {
            boolean key = tipoTokenDelante == TT.TK_SMC;
            temporalErrorMsg = key?temporalErrorMsg: "Statements esperan un ;";
            return key;
        }
        if(tipoTopToken == TT.TK_STRT){
            boolean key = tipoTokenDelante == TT.TK_OBR;
            temporalErrorMsg = key?temporalErrorMsg: "START espera un {";
            return key;
        }
        if(tipoTopToken == TT.TK_OBR){
            boolean key = esInicioDeStatement(tipoTokenDelante) || tipoTokenDelante == TT.TK_CBR;
            temporalErrorMsg = key?temporalErrorMsg: "{ espera después un inicio de statement o }";
            return key;
        }
        if(tipoTopToken == TT.BODY){
            boolean key = esInicioDeStatement(tipoTokenDelante) || tipoTokenDelante == TT.TK_CBR;
            temporalErrorMsg = key?temporalErrorMsg: "BODY espera después un inicio de statement o }";
            return key;
        }
        if (tipoTopToken == TT.TK_END){
            boolean key = false;
            temporalErrorMsg = "";
            return key;
        }
        if(tipoTopToken == TT.TK_SMC){
            return true;
        }
        if (tipoTopToken == TT.TK_CBR){
            boolean key = tipoTokenDelante == TT.TK_END;
            temporalErrorMsg = key?temporalErrorMsg: "} espera un END";
            return key;
        }

        return true;
    }

    private boolean isValue(TT t){
        return t == TT.TK_ID || t == TT.TK_INTV || t == TT.TK_DECV;
    }

    private boolean isOperator(TT t){
        return t == TT.TK_ADD || t == TT.TK_SUB || t == TT.TK_MUL || t == TT.TK_DIV;
    }

    private List<TT[]> obtenerPatrones(TT tipo) {
        Map<TT, List<TT[]>> patrones = new HashMap<>();
        patrones.put(TT.PROGRAM, Collections.singletonList(
                new TT[]{TT.TK_STRT, TT.TK_OBR, TT.BODY, TT.TK_CBR, TT.TK_END}
        ));
        patrones.put(TT.BODY, List.of(
                new TT[]{TT.BODY, TT.STATEMENT},
                new TT[]{TT.STATEMENT}
        ));
        patrones.put(TT.STATEMENT, List.of(
                new TT[]{TT.DECLARE, TT.TK_SMC},
                new TT[]{TT.ASIGN, TT.TK_SMC},
                new TT[]{TT.PRINT, TT.TK_SMC},
                new TT[]{TT.READ, TT.TK_SMC}
        ));

        patrones.put(TT.DECLARE, List.of(
                new TT[]{TT.TYPE, TT.TK_ID},
                new TT[]{TT.TYPE, TT.ASIGN},
                new TT[]{TT.DECLARE, TT.TK_CMA, TT.TK_ID},
                new TT[]{TT.DECLARE, TT.TK_CMA, TT.ASIGN}
        ));
        patrones.put(TT.ASIGN, Collections.singletonList(
                new TT[]{TT.TK_ID, TT.TK_EQU, TT.E}
        ));
        patrones.put(TT.PRINT, Collections.singletonList(
                new TT[]{TT.TK_PRNT, TT.TK_OPR, TT.E, TT.TK_CPR}
        ));
        patrones.put(TT.READ, Collections.singletonList(
                new TT[]{TT.TK_READ, TT.TK_OPR, TT.TK_ID, TT.TK_CPR}
        ));
        patrones.put(TT.TYPE, List.of(
                new TT[]{TT.TK_INTT},
                new TT[]{TT.TK_DECT}
        ));
        patrones.put(TT.E, List.of(
                new TT[]{TT.E, TT.TK_ADD, TT.T},
                new TT[]{TT.E, TT.TK_SUB, TT.T},
                new TT[]{TT.T}
        ));
        patrones.put(TT.T, List.of(
                new TT[]{TT.T, TT.TK_MUL, TT.F},
                new TT[]{TT.T, TT.TK_DIV, TT.F},
                new TT[]{TT.F}
        ));
        patrones.put(TT.F, List.of(
                new TT[]{TT.TK_OPR, TT.E, TT.TK_CPR},
                new TT[]{TT.TK_INTV},
                new TT[]{TT.TK_DECV},
                new TT[]{TT.TK_ID}
        ));
        return patrones.get(tipo);
    }

    private void asignarHijos(Nodo padre, int omitir) {
        TT tipoPadre = padre.getToken().getTipo();
        List<TT[]> patrones = obtenerPatrones(tipoPadre);
        if (patrones == null) return;

        for (TT[] patron : patrones) {
            TT[] patronInvertido = invertir(patron);
            List<Nodo> hijos = buscarHijos(patronInvertido, omitir);
            if (hijos != null) {
                for (Nodo h : hijos) {
                    h.setPadre(padre.getNumero());
                }
                return;
            }
        }

    }
    private List<Nodo> buscarHijos(TT[] patron, int omitirDesdeTope) {
        List<Nodo> encontrados = new ArrayList<>();
        int j = patron.length - 1;
        int limite = arbol.size() - 1 - omitirDesdeTope;
        for (int i = limite; i >= 0 && j >= 0; i--) {
            Nodo actual = arbol.get(i);
            if (actual.getPadre() != 0) continue;
            if (actual.getToken().getTipo() == patron[j]) {
                encontrados.add(0, actual);
                j--;
            } else {
                return null;
            }
        }
        if (j == -1) return encontrados;

        return null;
    }
    private TT[] invertir(TT[] patron) {
        TT[] inv = new TT[patron.length];
        for (int i = 0; i < patron.length; i++) {
            inv[i] = patron[patron.length - 1 - i];
        }
        return inv;
    }


    private boolean intentarReducir(Token tokenDelante) {
        TT tipoTokenDelante = tokenDelante.getTipo();

        if (stack.isEmpty()) return false;

        Token topToken = stack.peek();
        TT tipoTop = topToken.getTipo();

        if (tipoTop == TT.TK_INTT) {
            System.out.println("    APLICANDO: TYPE -> TK_INTT");

            arbol.add(new Nodo(counter++, stack.pop()));

            Token typeToken = new Token(TT.TYPE, "TYPE");
            typeToken.setLine(topToken.getLine());
            stack.push(typeToken);
            return true;
        }
        if (tipoTop == TT.TK_DECT) {
            System.out.println("    APLICANDO: TYPE -> TK_DECT");

            arbol.add(new Nodo(counter++, stack.pop()));

            Token typeToken = new Token(TT.TYPE, "TYPE");
            typeToken.setLine(topToken.getLine());
            stack.push(typeToken);
            return true;
        }
        if(igualTOP(TT.TK_PRNT, TT.TK_OPR, TT.E, TT.TK_CPR)){
            System.out.println("APLICANDO PRINT -> TK_PRNT(E)");
            Token prnt = stack.get(stack.size() - 4);

            arbol.add(new Nodo(counter++, stack.pop()));
            Nodo nodoE = new Nodo(counter++, stack.pop());
            arbol.add(nodoE); //E
            asignarHijos(nodoE, 2);
            arbol.add(new Nodo(counter++, stack.pop()));
            arbol.add(new Nodo(counter++, stack.pop()));


            Token printToken = new Token(TT.PRINT, "PRINT");
            printToken.setLine(prnt.getLine());
            stack.push(printToken);
            return true;
        }
        if(igualTOP(TT.TK_READ, TT.TK_OPR, TT.TK_ID, TT.TK_CPR)){
            System.out.println("APLICANDO READ -> TK_READ(TK_ID)");
            Token prnt = stack.get(stack.size() - 4);

            arbol.add(new Nodo(counter++, stack.pop()));
            arbol.add(new Nodo(counter++, stack.pop()));
            arbol.add(new Nodo(counter++, stack.pop()));
            arbol.add(new Nodo(counter++, stack.pop()));

            Token printToken = new Token(TT.READ, "READ");
            printToken.setLine(prnt.getLine());
            stack.push(printToken);
            return true;
        }
        if (igualTOP(TT.TK_OPR, TT.E, TT.TK_CPR)) {
            System.out.println("    APLICANDO: (E) → F");
            Token opr = stack.get(stack.size() - 3);

            arbol.add(new Nodo(counter++, stack.pop()));
            Nodo nodoE = new Nodo(counter++, stack.pop());
            arbol.add(nodoE); //E
            asignarHijos(nodoE, 2);
            arbol.add(new Nodo(counter++, stack.pop()));

            Token fToken = new Token(TT.F, "F");
            fToken.setLine(opr.getLine());
            stack.push(fToken);
            return true;
        }
        if (tipoTop == TT.TK_INTV ||tipoTop == TT.TK_DECV) {
            System.out.println("    APLICANDO: num → F");

            arbol.add(new Nodo(counter++, stack.pop()));

            Token fToken = new Token(TT.F, "F");
            fToken.setLine(topToken.getLine());
            stack.push(fToken);
            return true;
        }
        if (tipoTop == TT.TK_ID) {
            if (tipoTokenDelante != TT.TK_EQU && !estaAntes(TT.TYPE) && !estaAntes(TT.TK_CMA) && !estaDosAntes(TT.TK_READ)) {
                System.out.println("    APLICANDO: id → F");

                arbol.add(new Nodo(counter++, stack.pop()));

                Token fToken = new Token(TT.F, "F");
                fToken.setLine(topToken.getLine());
                stack.push(fToken);
                return true;
            }
        }
        if (igualTOP(TT.T, TT.TK_MUL, TT.F) || igualTOP(TT.T, TT.TK_DIV, TT.F)) {
            TT operador = stack.get(stack.size() - 2).getTipo();
            if (prescedencia(operador) >= prescedencia(tipoTokenDelante)) {
                System.out.println("    APLICANDO: T */ F → T");
                Token tToken = stack.get(stack.size() - 3);

                Nodo nodoF = new Nodo(counter++, stack.pop());
                arbol.add(nodoF); //F
                asignarHijos(nodoF, 1);

                arbol.add(new Nodo(counter++, stack.pop()));

                Nodo nodoT = new Nodo(counter++, stack.pop());
                arbol.add(nodoT); //T
                asignarHijos(nodoT, 3);

                Token newT = new Token(TT.T, "T");
                newT.setLine(tToken.getLine());
                stack.push(newT);
                return true;
            }
        }
        if (tipoTop == TT.F) {
            System.out.println("    APLICANDO: F → T");

            Nodo nodoF = new Nodo(counter++, stack.pop());
            arbol.add(nodoF);
            asignarHijos(nodoF, 1);

            Token tToken = new Token(TT.T, "T");
            tToken.setLine(topToken.getLine());
            stack.push(tToken);
            return true;
        }
        if (igualTOP(TT.E, TT.TK_ADD, TT.T) || igualTOP(TT.E, TT.TK_SUB, TT.T)) {
            TT operador = stack.get(stack.size() - 2).getTipo();
            if (prescedencia(operador) >= prescedencia(tipoTokenDelante)) {
                System.out.println("    APLICANDO: E +- T → E");
                Token eToken = stack.get(stack.size() - 3);

                Nodo nodoT = new Nodo(counter++, stack.pop());
                arbol.add(nodoT);
                asignarHijos(nodoT, 1);
                arbol.add(new Nodo(counter++, stack.pop()));
                Nodo nodoE = new Nodo(counter++, stack.pop());
                arbol.add(nodoE);
                asignarHijos(nodoE, 3);

                Token newE = new Token(TT.E, "E");
                newE.setLine(eToken.getLine());
                stack.push(newE);
                return true;
            }
        }
        if (tipoTop == TT.T) {
            if (tipoTokenDelante == TT.TK_SMC || tipoTokenDelante == TT.TK_CPR || prescedencia(tipoTokenDelante) < 2) {
                System.out.println("    APLICANDO: T → E");

                Nodo nodoT = new Nodo(counter++, stack.pop());
                arbol.add(nodoT);
                asignarHijos(nodoT, 1);

                Token eToken = new Token(TT.E, "E");
                eToken.setLine(topToken.getLine());
                stack.push(eToken);
                return true;
            }
        }
        if (igualTOP(TT.TK_ID, TT.TK_EQU, TT.E)) {
            if(prescedencia(tipoTokenDelante)==0){
                System.out.println("    APLICANDO: ASIGN -> id = E");
                Token idToken = stack.get(stack.size() - 3);

                Nodo nodoE = new Nodo(counter++, stack.pop());
                arbol.add(nodoE);
                asignarHijos(nodoE, 1);

                arbol.add(new Nodo(counter++, stack.pop()));
                arbol.add(new Nodo(counter++, stack.pop()));

                Token asignToken = new Token(TT.ASIGN, "ASIGN");
                asignToken.setLine(idToken.getLine());
                stack.push(asignToken);
                return true;
            }
        }
        if(igualTOP(TT.TYPE, TT.TK_ID)){
            if(tipoTokenDelante!=TT.TK_EQU){
                System.out.println("    APLICANDO: DECLARE -> TYPE TK_ID");
                Token typeToken = stack.get(stack.size() - 2);

                arbol.add(new Nodo(counter++, stack.pop()));
                Nodo nodoType = new Nodo(counter++, stack.pop());
                arbol.add(nodoType);
                asignarHijos(nodoType, 2);

                Token declareToken = new Token(TT.DECLARE, "DECLARE");
                declareToken.setLine(typeToken.getLine());
                stack.push(declareToken);
                return true;
            }
        }
        if(igualTOP(TT.TYPE, TT.ASIGN)){
            System.out.println("    APLICANDO: DECLARE -> TYPE ASIGN");
            Token typeToken = stack.get(stack.size() - 2);

            Nodo nodoASIGN = new Nodo(counter++, stack.pop());
            arbol.add(nodoASIGN);
            asignarHijos(nodoASIGN, 1);
            Nodo nodoTYPE = new Nodo(counter++, stack.pop());
            arbol.add(nodoTYPE);
            asignarHijos(nodoTYPE, 2);

            Token declareToken = new Token(TT.DECLARE, "DECLARE");
            declareToken.setLine(typeToken.getLine());
            stack.push(declareToken);
            return true;
        }
        if(igualTOP(TT.DECLARE, TT.TK_CMA, TT.TK_ID)){
            if(tipoTokenDelante!=TT.TK_EQU) {
                System.out.println("    APLICANDO: DECLARE -> DECLARE TK_CMA TK_ID");
                Token declareToken = stack.get(stack.size() - 3);

                arbol.add(new Nodo(counter++, stack.pop()));
                arbol.add(new Nodo(counter++, stack.pop()));
                Nodo nodoDECLARE = new Nodo(counter++, stack.pop());
                arbol.add(nodoDECLARE);
                asignarHijos(nodoDECLARE, 3);

                Token newDeclare = new Token(TT.DECLARE, "DECLARE");
                newDeclare.setLine(declareToken.getLine());
                stack.push(newDeclare);
                return true;
            }
        }
        if(igualTOP(TT.DECLARE, TT.TK_CMA, TT.ASIGN)){
            System.out.println("    APLICANDO: DECLARE -> DECLARE TK_CMA ASIGN");
            Token declareToken = stack.get(stack.size() - 3);

            Nodo nodoASIGN = new Nodo(counter++, stack.pop());
            arbol.add(nodoASIGN);
            asignarHijos(nodoASIGN, 1);
            arbol.add(new Nodo(counter++, stack.pop()));
            Nodo nodoDECLARE = new Nodo(counter++, stack.pop());
            arbol.add(nodoDECLARE);
            asignarHijos(nodoDECLARE, 3);

            Token newDeclare = new Token(TT.DECLARE, "DECLARE");
            newDeclare.setLine(declareToken.getLine());
            stack.push(newDeclare);
            return true;
        }
        if(igualTOP(TT.TK_READ, TT.TK_OPR, TT.TK_ID, TT.TK_CPR)){
            System.out.println("    APLICANDO: READ -> TK_READ(TK_ID)");
            Token readToken = stack.get(stack.size() - 4);

            arbol.add(new Nodo(counter++, stack.pop()));
            arbol.add(new Nodo(counter++, stack.pop()));
            arbol.add(new Nodo(counter++, stack.pop()));
            arbol.add(new Nodo(counter++, stack.pop()));

            Token newRead = new Token(TT.READ, "READ");
            newRead.setLine(readToken.getLine());
            stack.push(newRead);
            return true;
        }
        if (igualTOP(TT.DECLARE, TT.TK_SMC)) {
            System.out.println("    STATEMENT -> DECLARE;");
            Token declareToken = stack.get(stack.size() - 2);

            arbol.add(new Nodo(counter++, stack.pop()));
            Nodo nodoDECLARE = new Nodo(counter++, stack.pop());
            arbol.add(nodoDECLARE);
            asignarHijos(nodoDECLARE, 2);

            Token stmtToken = new Token(TT.STATEMENT, "STATEMENT");
            stmtToken.setLine(declareToken.getLine());
            stack.push(stmtToken);
            return true;
        }
        if (igualTOP(TT.ASIGN, TT.TK_SMC)) {
            System.out.println("    APLICANDO: STATEMENT -> ASIGN;");
            Token asignToken = stack.get(stack.size() - 2);

            arbol.add(new Nodo(counter++, stack.pop()));
            Nodo nodoASIGN = new Nodo(counter++, stack.pop());
            arbol.add(nodoASIGN);
            asignarHijos(nodoASIGN, 2);

            Token stmtToken = new Token(TT.STATEMENT, "STATEMENT");
            stmtToken.setLine(asignToken.getLine());
            stack.push(stmtToken);
            return true;
        }
        if (igualTOP(TT.PRINT, TT.TK_SMC)) {
            System.out.println("    APLICANDO: STATEMENT -> PRINT;");
            Token printToken = stack.get(stack.size() - 2);

            arbol.add(new Nodo(counter++, stack.pop()));
            Nodo nodoPRINT = new Nodo(counter++, stack.pop());
            arbol.add(nodoPRINT);
            asignarHijos(nodoPRINT, 2);

            Token stmtToken = new Token(TT.STATEMENT, "STATEMENT");
            stmtToken.setLine(printToken.getLine());
            stack.push(stmtToken);
            return true;
        }
        if (igualTOP(TT.READ, TT.TK_SMC)) {
            System.out.println("    APLICANDO: STATEMENT -> READ;");
            Token readToken = stack.get(stack.size() - 2);

            arbol.add(new Nodo(counter++, stack.pop()));
            Nodo nodoREAD = new Nodo(counter++, stack.pop());
            arbol.add(nodoREAD);
            asignarHijos(nodoREAD, 2);

            Token stmtToken = new Token(TT.STATEMENT, "STATEMENT");
            stmtToken.setLine(readToken.getLine());
            stack.push(stmtToken);
            return true;
        }
        if (igualTOP(TT.BODY, TT.STATEMENT)) {
            System.out.println("    APLICANDO: BODY -> BODY STATEMENT");
            Token bodyToken = stack.get(stack.size() - 2);

            Nodo nodoST = new Nodo(counter++, stack.pop());
            arbol.add(nodoST);
            asignarHijos(nodoST, 1);
            Nodo nodoBODY = new Nodo(counter++, stack.pop());
            arbol.add(nodoBODY);
            asignarHijos(nodoBODY, 2);

            Token newBody = new Token(TT.BODY, "BODY");
            newBody.setLine(bodyToken.getLine());
            stack.push(newBody);
            return true;
        }
        if (igualTOP(TT.STATEMENT)) {
            System.out.println("    APLICANDO: BODY -> STATEMENT");
            Token stmtToken = stack.peek();

            Nodo nodoST = new Nodo(counter++, stack.pop());
            arbol.add(nodoST);
            asignarHijos(nodoST, 1);

            Token bodyToken = new Token(TT.BODY, "BODY");
            bodyToken.setLine(stmtToken.getLine());
            stack.push(bodyToken);
            return true;
        }
        if (igualTOP(TT.TK_STRT, TT.TK_OBR, TT.BODY, TT.TK_CBR, TT.TK_END)) {
            System.out.println("    APLICANDO: PROGRAM -> START { BODY } END");
            Token startToken = stack.get(stack.size() - 5);

            arbol.add(new Nodo(counter++, stack.pop()));
            arbol.add(new Nodo(counter++, stack.pop()));
            Nodo nodoBODY = new Nodo(counter++, stack.pop());
            arbol.add(nodoBODY);
            asignarHijos(nodoBODY, 3);

            arbol.add(new Nodo(counter++, stack.pop()));
            arbol.add(new Nodo(counter++, stack.pop()));

            Token programToken = new Token(TT.PROGRAM, "PROGRAM");
            programToken.setLine(startToken.getLine());
            stack.push(programToken);
            return true;
        }

        return false;
    }

    private boolean esInicioDeStatement(TT t) {
        return t == TT.TK_INTT ||
                t == TT.TK_DECT ||
                t == TT.TK_ID ||
                t == TT.TK_PRNT ||
                t == TT.TK_READ;
    }

    private boolean igualTOP(TT... symbols) {
        if (stack.size() < symbols.length) {
            return false;
        }
        for (int i = 0; i < symbols.length; i++) {
            if (stack.get(stack.size() - symbols.length + i).getTipo() != symbols[i]) {
                return false;
            }
        }
        return true;
    }
    private boolean estaAntes(TT type) {
        if (stack.size() >= 2)
            return stack.get(stack.size() - 2).getTipo() == type;
        return false;
    }
    private boolean estaDosAntes(TT type) {
        if (stack.size() >= 3)
            return stack.get(stack.size() - 3).getTipo() == type;
        return false;
    }
    private int prescedencia(TT op) {
        switch (op) {
            case TK_ADD:
            case TK_SUB:
                return 1;

            case TK_MUL:
            case TK_DIV:
                return 2;

            default:
                return 0;
        }
    }
}