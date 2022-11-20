package poo.modelo;

import java.util.LinkedList;
import java.util.List;

//import poo.modelo.GameEvent.Action;
//import poo.modelo.GameEvent.Target;

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
		if (currentPhase == 5) currentPhase = 0;
		currentPhase++;
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

	public void play(CardDeck deckAcionado) {
		if (currentPhase < 3) { // nao deve fazer nada aqui
			return;
		}
		else if (deckAcionado == deckJ1 || deckAcionado == deckJ2) { // nao permitir mais cliques nas cartas dos decks
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Cartas não podem ser baixadas neste momento.");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
		}

		GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Entrou no play()");
		for (var observer : observers) {
			observer.notify(gameEvent);
		}
		return;
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
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Não é a vez do J1 baixar cartas.");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		else if (jogador == 2 && currentPhase != 2) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Não é a vez do J2 baixar cartas.");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;	
		}
		
		if (jogador != 1 && jogador != 2) return;
		
		CardDeck deckJogador;
		CardDeck mesaJogador;
		if (jogador == 1) {
			deckJogador = deckJ1;
			mesaJogador = mesaJ1;
		}
		else {
			deckJogador = deckJ2;
			mesaJogador = mesaJ2;
		}
		
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
	}
	
	public void addGameListener(GameListener listener) {
		observers.add(listener);
	}

	public int getFaseAtual() {
		return currentPhase;
	}
}
