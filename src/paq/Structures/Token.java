package paq.Structures;

import java.util.Objects;

public class Token {

    private StringBuilder lexema;
    private int lineNumber;
    private int start;
    private TT tipo;

    private boolean syntax;
    private String error;

    public Token(){
        lexema = new StringBuilder();
        lineNumber = -1;
        start = -1;
        tipo = null;
        syntax = false;
        error = "";
    }
    public Token(TT tipo, String lexema){
        this.lexema = new StringBuilder(lexema);
        lineNumber = -1;
        start = -1;
        this.tipo = tipo;
        syntax = false;
        error = "";
    }

    public void setLine(int line){
        lineNumber=line;
    }
    public int getLine(){
        return this.lineNumber;
    }

    public void setStart(int start){
        this.start=start;
    }
    public int getStart(){
        return this.start;
    }

    public void addChar(char c){
        this.lexema.append(c);
    }
    public String getLexeme(){
        return lexema.toString();
    }
    public void setLexema(String lexema){
        this.lexema = new StringBuilder(lexema);
    }

    public TT getTipo(){ return this.tipo; }
    public void setTipo(TT tipo){ this.tipo=tipo; }

    public boolean isEmpty(){
        return lexema.toString().equals(" ") ||
                lexema.toString().isEmpty()  ||
                lexema.toString().equals("\n") ||
                lexema.toString().equals("\t") ||
                lexema.toString().equals("\r");
    }

    public void copyTo(Token token){
        token.setStart(this.getStart());
        token.setLine(this.getLine());
    }

    public boolean isSyntax() {
        return syntax;
    }

    public void setSyntax(boolean syntax) {
        this.syntax = syntax;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return lexema.toString()+" - Inicia: "+start+" - Renglon: "+lineNumber+" \n";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Token tok) {
            return this.lexema.toString().contentEquals(tok.lexema);
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(lexema.toString());
    }
}
