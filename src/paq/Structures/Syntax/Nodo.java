package paq.Structures.Syntax;

import paq.Structures.Semantic.Type;
import paq.Structures.Token;

public class Nodo {
    private int numero;
    private Token token;
    private int padre;
    private Type type;

    public Nodo(int numero, Token token){
        this.numero = numero;
        this.token = token;
        padre = 0;
    }

    public int getNumero() {
        return numero;
    }
    public Token getToken() {
        return token;
    }
    public int getPadre() {
        return padre;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setPadre(int padre) {
        this.padre = padre;
    }
}
