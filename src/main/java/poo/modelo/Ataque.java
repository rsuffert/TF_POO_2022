package poo.modelo;

public class Ataque {
    public final int DANO;
    public final int ENERGIAS_PARA_ATACAR;
    public final String NOME;

    public Ataque(String nome, int dano, int energia) {
        this.DANO = dano;
        this.ENERGIAS_PARA_ATACAR = energia;
        this.NOME = nome.toUpperCase();
    }

    public String toString() {
        return "NOME DO ATAQUE: " + this.NOME + " | "
             +  "DANO: " + this.DANO + " | "
             + "ENERGIAS P/ ATAQUE: " + this.ENERGIAS_PARA_ATACAR;
    }
}

