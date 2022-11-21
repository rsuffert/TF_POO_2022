package poo.modelo;

import java.util.LinkedList;
import java.util.List;

import poo.gui.GameWindow;
import poo.gui.PlacarView;

public class Game {
	private static Game game = new Game();
	private CardDeck deckJ1, deckJ2;
	private CardDeck mesaJ1, mesaJ2;
	private int currentPhase; // 1 = J1 baixa cartas e 'end turn' | 2 = J2 baixa cartas e 'end turn' | 3 = J1 ataca/carrega energias e 'end turn' | 4 = J2 ataca/carrega energias e 'end turn'
	private List<GameListener> observers;
	
	public static Game getInstance() {
		return game;
	}

	private Game() {
		deckJ1 = new CardDeck(false);
		deckJ2 = new CardDeck(false);
		mesaJ1 = new CardDeck(true);
		mesaJ2 = new CardDeck(true);
		currentPhase = 1;
		observers = new LinkedList<>();
	}

	private void nextPhase() {
		currentPhase++;
		if (currentPhase == 4) currentPhase = 0;
	}

	public int getPokemonsJ1() {
		return deckJ1.getPokemonsNoDeck() + mesaJ1.getPokemonsNoDeck();
	}

	public int getPokemonsJ2() {
		return deckJ2.getPokemonsNoDeck() + mesaJ2.getPokemonsNoDeck();
	}

	public CardDeck getDeckJ1() {
		return deckJ1;
	}

	public CardDeck getDeckJ2() {
		return deckJ2;
	}

	public CardDeck getMesaJ1() {
		return mesaJ1;
	}

	public CardDeck getMesaJ2() {
		return mesaJ2;
	}

	public void play() {
		if (currentPhase < 3) { // nao deve fazer nada aqui
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Ataques não podem ser feitos neste momento");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}

		CardDeck deckAtaque = currentPhase == 3? deckJ1 : deckJ2;
		CardDeck deckDefesa = currentPhase == 4? deckJ1 : deckJ2;

		if (deckAtaque.getSelectedCard() == null || deckDefesa == null) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Selecione uma carta de Pokémon em cada deck par atacar");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		else if (deckAtaque.getSelectedCard().getValue() instanceof CartaEnergia || 
				 deckDefesa.getSelectedCard().getValue() instanceof CartaEnergia) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Cartas de energia não podem atacar/defender");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}

		CartaPokemon pokemonAtaque = (CartaPokemon) deckAtaque.getSelectedCard().getValue();
		CartaPokemon pokemonDefesa = (CartaPokemon) deckDefesa.getSelectedCard().getValue();

		boolean energiaSuficiente = pokemonAtaque.ataque(pokemonDefesa);
		if (!energiaSuficiente) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "O Pokémon de ataque nao tem energia suficiente");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		else { // o ataque foi bem sucedido, e podem ter havido mudancas
			int nroMortos = deckDefesa.removeKilled(); //remover mortos do deck de defesa
			deckAtaque.addEnergyForEachKill(nroMortos); //adicionar energia por cada Pokemon morto
			//renderizar novamente a mesa
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.DECK, GameEvent.Action.SHOWTABLE, null);
			for (var observer : observers) {
				observer.notify(gameEvent);
			}

			GameEvent gameEvent2 = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.SHOWMESSAGE, "Ataque efetuado com sucesso!");
			for (var observer : observers) {
				observer.notify(gameEvent2);
			}

			nextPhase();
		}
	}

	// Acionada pelo botao de 'Baixar cartas'
	public void baixarCartas(int jogador) {
		if (currentPhase != 1 && currentPhase != 2) { // cartas so podem ser baixadas nas fases 1 (J1) e 2 (J2)
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Cartas não podem ser baixadas neste momento.");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		else if (jogador == 1 && currentPhase != 1) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, String.format("Não é a vez do %s baixar cartas.", GameWindow.getNomeJ1()));
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		else if (jogador == 2 && currentPhase != 2) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, String.format("Não é a vez do %s baixar cartas.", GameWindow.getNomeJ2()));
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;	
		}
		
		if (jogador != 1 && jogador != 2) return;
		
		CardDeck deckJogador = jogador == 1? deckJ1 : deckJ2;
		CardDeck mesaJogador = jogador == 1? mesaJ1 : mesaJ2; 
		
		mesaJogador.addCard( deckJogador.getSelectedCard() );
		deckJogador.removeSel();
	}

	// Acionado pelo botao "End Turn"
	public void endTurn() {
		// checar final do jogo
		if (this.getPokemonsJ1() == 0 || this.getPokemonsJ2() == 0) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.ENDGAME, "");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
		}

		int removidosJ1 = deckJ1.removeKilled();
		int removidosJ2 = deckJ2.removeKilled();

		 // adicionar energia como bonus por vencer um pokemon adversario
		deckJ1.addEnergyForEachKill(removidosJ2);
		deckJ2.addEnergyForEachKill(removidosJ1);
		
		nextPhase();
		this.updatePlacarViewLabel();
	}

	public void updatePlacarViewLabel() {
		String text;

		if (currentPhase == 1) {
			text = GameWindow.getNomeJ1() + " baixa cartas";
		}
		else if (currentPhase == 2) {
			text = GameWindow.getNomeJ2() + " baixa cartas";
		}
		else if (currentPhase == 3) {
			text = GameWindow.getNomeJ1() + " ataca";
		}
		else {
			text = GameWindow.getNomeJ2() + " ataca";
		}

		PlacarView.getInstance().setFieldFaseAtual(text);
	}

	public void restart() {
		System.out.println("\nRestart acionado");
		deckJ1 = new CardDeck(false);
		deckJ2 = new CardDeck(false);
		mesaJ1 = new CardDeck(true);
		mesaJ2 = new CardDeck(true);
		currentPhase = 1;
		this.updatePlacarViewLabel();
		
		GameEvent gameEvent = new GameEvent(this, GameEvent.Target.DECK, GameEvent.Action.RESTART, "");
		for (var observer : observers) {
			observer.notify(gameEvent);
		}
	}

	public void addEnergy(int jogador) {
		if (jogador != 1 && jogador != 2) return;
		else if (jogador == 1 && currentPhase != 3) { // J1 adicionando energia fora de hora
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, String.format("%s não pode atacar/adicionar energia neste momento", GameWindow.getNomeJ1()));
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		else if (jogador == 2 && currentPhase != 4) { // J2 adicionando energia fora de hora
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, String.format("%s não pode atacar/adicionar energia neste momento", GameWindow.getNomeJ2()));
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		
		CardDeck deckAcionado = jogador == 1? mesaJ1 : mesaJ2;
		Card cartaSelecionada = deckAcionado.getSelectedCard();
		if (cartaSelecionada == null || cartaSelecionada.getValue() instanceof CartaEnergia) { // jogador nao selecionou a carta alvo ou ela eh uma carta de energia
			System.out.println("Carta selecionada (addEnergy): " + cartaSelecionada);
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Selecione uma carta de Pokémon para aplicar energia");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		
		CartaPokemon cartaAlvo = (CartaPokemon) deckAcionado.getSelectedCard().getValue(); // tudo certo, adicionar energia a essa carta
		System.out.println("Todos os testes OK. Carregando energia");
		System.out.println("Carta alvo: " + cartaAlvo.getNome());
		boolean energiaConsumida = deckAcionado.removeEnergyCard();
		if (!energiaConsumida) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Não há cartas de energia na mesa");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		else {
			System.out.println("Energia sendo adicionada");
			cartaAlvo.carregarEnergias(1); // carregar a energia na carta
			System.out.println(cartaAlvo.getEnergiaAtual());
			// renderizar novamente a mesa
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.DECK, GameEvent.Action.SHOWTABLE, null);
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
		}
	}
	
	public void addGameListener(GameListener listener) {
		observers.add(listener);
	}

	public int getFaseAtual() {
		return currentPhase;
	}
}