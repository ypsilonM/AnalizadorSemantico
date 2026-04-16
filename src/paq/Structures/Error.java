package paq.Structures;

public class Error {
    private Token token;
    private ErrorType errorType;
    private String message;

    public Error(Token token){
        this.token = token;
    }
    public Error(){

    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
