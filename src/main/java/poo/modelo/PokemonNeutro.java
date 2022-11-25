package poo.modelo;

public class PokemonNeutro extends CartaPokemon {

    public PokemonNeutro (String nomePokemon, String nomeAtaque, Carta.Raridade raridade, int hp, int dano, int energiasPAtaque) {
        super(nomePokemon, nomeAtaque, raridade, hp, dano, energiasPAtaque);
    }

    @Override
    public boolean atacar (CartaPokemon outro) {
        Ataque ataque = super.getAtaque();
        
        // verificar se tem energia para fazer o ataque e, se tiver, conusmir e continuar
        boolean energiaSuficiente = super.consumirEnergias(ataque.ENERGIAS_PARA_ATACAR);
        if (!energiaSuficiente) return false;
        
        // ninguem tem bonus de ataque/defesa
        final int BONUS_ATAQUE = 1;
        final int BONUS_DEFESA = 1;

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
