package paq.Structures.Lexic;

import java.util.HashMap;

public class Estado {
    private boolean aceptacion;
    private HashMap<Character,Estado> caminos;

    public Estado(boolean aceptacion){
        this.aceptacion = aceptacion;
        caminos = new HashMap<>();
    }

    public boolean isAceptacion() {
        return aceptacion;
    }
    public void setAceptacion(boolean aceptacion) {
        this.aceptacion = aceptacion;
    }

    public HashMap<Character,Estado> getCaminos() {
        return caminos;
    }
    public void setWay(Character c, Estado estado){
        this.caminos.put(c,estado);
    }

    public void setSameWay(Character[] characters, Estado estado){
        for(Character c: characters) {
            this.caminos.put(c, estado);
        }
    }

}

