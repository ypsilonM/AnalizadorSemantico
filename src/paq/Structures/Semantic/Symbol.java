package paq.Structures.Semantic;

import paq.Structures.Token;

public class Symbol {
    private Token token;
    private Type type;
    private String value;

    public Symbol(Token token){
        this.token = token;
        this.type = null;
        this.value = null;
    }

    public Token getToken() {
        return token;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
