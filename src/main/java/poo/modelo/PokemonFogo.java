package poo.modelo;

public class PokemonFogo extends CartaPokemon {
    public static final CartaPokemon.Elementos FRAQUEZA = CartaPokemon.Elementos.AGUA;
    public static final CartaPokemon.Elementos FORCA = CartaPokemon.Elementos.PLANTA;

    public PokemonFogo (String nomePokemon, String nomeAtaque, Carta.Raridade raridade, int hp, int dano, int energiasPAtaque) {
        super(nomePokemon, nomeAtaque, raridade, hp, dano, energiasPAtaque);
    }

    @Override
    public boolean atacar (CartaPokemon outro) {
        Ataque ataque = super.getAtaque();
        
        // verificar se tem energia para fazer o ataque e, se tiver, conusmir e continuar
        boolean energiaSuficiente = super.consumirEnergias(ataque.ENERGIAS_PARA_ATACAR);
        if (!energiaSuficiente) return false;
        
        // verificar se alguem tem bonus de ataque/defesa
        final int BONUS_ATAQUE = outro instanceof PokemonPlanta? CartaPokemon.BONUS_ATAQUE_DEFESA : 1;
        final int BONUS_DEFESA = outro instanceof PokemonAgua? CartaPokemon.BONUS_ATAQUE_DEFESA : 1;

        // calcular o dano do ataque
        int danoAtaque = (ataque.DANO * BONUS_ATAQUE) / BONUS_DEFESA;

        // dar o dano na carta "outro"
        outro.darDano(danoAtaque);

        return true;
    }

    @Override
    public String toString() {
        return "NOME: " + super.getNome() + " | "
            + "RARIDADE: " + super.getRaridade() + " | "
            + "HP: " + super.getHpAtual() + " | " 
            + "ENERGIA CARREGADA: " + super.getEnergiaAtual() + " | "
            + super.getAtaque().toString();
    }
}

