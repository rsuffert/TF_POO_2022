package poo.modelo;

public abstract class CartaPokemon extends Carta {
    public enum Elementos { AGUA, FOGO, PLANTA };

    public static final int BONUS_ATAQUE_DEFESA = 2;

    public final int MAX_HP;

    private int hpAtual;
    private Ataque ataque;
    private int energiaAtual;

    public CartaPokemon (String nomePokemon, String nomeAtaque, Carta.Raridade raridade, int hp, int dano, int energiasPAtaque) {
        super(nomePokemon, raridade);

        if (hp > 0) this.MAX_HP = hpAtual = hp;
        else this.MAX_HP = hpAtual = 100;

        if (dano > 0 && energiasPAtaque > 0) ataque = new Ataque(nomeAtaque, dano, energiasPAtaque);
        else ataque = new Ataque (nomeAtaque, 0, 0);
    }

    public int getHpAtual() {
        return this.hpAtual;
    }

    public void darDano(int dano) {
        if (dano <= this.hpAtual) this.hpAtual -= dano;
        else this.hpAtual = 0;
    }

    public int getEnergiaAtual() {
        return this.energiaAtual;
    }

    public boolean carregarEnergias(int energia) {
        if (energia > 0) {
            this.energiaAtual += energia;
            return true;
        }

        return false;
    }

    public boolean consumirEnergias(int energiaConsumida) {
        if (this.energiaAtual >= energiaConsumida) {
            this.energiaAtual -= energiaConsumida;
            return true;
        }

        return false;
    }

    public Ataque getAtaque() {
        return this.ataque;
    }

    public abstract boolean ataque (CartaPokemon outro);
}

