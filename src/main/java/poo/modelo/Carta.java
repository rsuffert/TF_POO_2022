package poo.modelo;

public abstract class Carta {
    public enum Raridade { COMUM, INCOMUM, RARA }
    
    private String nome;
    private Raridade raridade;

    public Carta (String nome, Raridade raridade) {
        this.nome = nome.toUpperCase();
        this.raridade = raridade;
    }

    public String getNome() { return this.nome; }

    public Raridade getRaridade() { return this.raridade; }
}
