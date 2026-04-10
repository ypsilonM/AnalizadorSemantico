package paq.Analizadores.Lexico;

import paq.Principal;
import paq.Structures.Semantic.Symbol;
import paq.Structures.Token;
import paq.Structures.TT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

public class AnalizadorLexico {

    public static void execute(String codigo) {

        Stack<Token> tokens = new Stack<>();

        try (BufferedReader br = new BufferedReader(new StringReader(codigo))) {
            int caracter;

            Token token = new Token();
            int linea = 1;
            int cont = 0;
            while ((caracter = br.read()) != -1) {
                char c = (char) caracter;
                if (c =='\n' || c=='\r' ||  c==' ' || c=='\t' || esSimbolo(c)){ // Lectura de separación de tokens
                    if(!token.isEmpty()){ //Token con caracteres acumulados
                        if(c=='.'){ // Caracter leido es punto
                            TT id = Validador.isNumero(token.getLexeme());
                            if(id == TT.TK_INTV){
                                token.addChar(c);
                            }else{
                                tokens.push(token);
                                token=new Token();
                                token.addChar(c);
                                token.setStart(cont);
                                token.setLine(linea);
                                tokens.push(token);
                                token=new Token();
                            }
                        }else{ //Cualquier otro separador
                            tokens.push(token);
                            token=new Token();
                            token.addChar(c);
                            token.setStart(cont);
                            token.setLine(linea);

                            if(!token.isEmpty())
                                tokens.push(token);
                            token=new Token();
                        }
                    }else{ //Token sin texto
                        token.addChar(c);
                        token.setStart(cont);
                        token.setLine(linea);
                        if(!token.isEmpty()) {
                            tokens.push(token);
                        }
                        token=new Token();
                    }
                }else{ // Seguir acumulando caracteres normalmente
                    if(!token.isEmpty()){
                        token.addChar(c);
                    }else{
                        token.addChar(c);
                        token.setStart(cont);
                        token.setLine(linea);
                    }
                }
                if(c=='\n') {
                    linea++;
                    cont=0;
                }else if(c!='\r'){
                    cont++;
                }
            }
            if(!token.isEmpty()){
                tokens.push(token);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        saveTokensToMain(tokens);

    }
    public static void saveTokensToMain(Stack<Token> tokens){
        for(Token token : tokens){

            if(tokenSend(token, Validador.isIdentificador(token.getLexeme()))) continue;
            if(tokenSend(token, Validador.isReservada(token.getLexeme()))) continue;
            if(tokenSend(token, Validador.isSimbolo(token.getLexeme()))) continue;
            if(tokenSend(token, Validador.isNumero(token.getLexeme()))) continue;
            Principal.errorsTokenList.addLast(token);

        }
    }
    public static boolean tokenSend(Token token, TT tipoToken){
        if(tipoToken!=null) {
            token.setTipo(tipoToken);
            Principal.validTokenList.addLast(token);
            String lexeme = token.getLexeme();
            if(tipoToken == TT.TK_ID) {
                if(!Principal.symbolTable.isEmpty()){
                    boolean alreadyThere = false;
                    for(int i = 0; i<Principal.symbolTable.size(); i++){
                       alreadyThere = Principal.symbolTable.get(i).getToken().getLexeme().equals(lexeme);
                       if(alreadyThere) break;
                    }
                    if(!alreadyThere)
                        Principal.symbolTable.add(new Symbol(token));

                }else{
                    Principal.symbolTable.add(new Symbol(token));
                }
            }
            return true;
        }
        return false;
    }

    public static boolean esSimbolo(char c) {
        return c == '{' || c == '}' || c == '(' || c == ')' ||
                c == ';' || c == ',' || c == '.' || c == '=' ||
                c == '+' || c == '-' || c == '*' || c == '/';
    }
}