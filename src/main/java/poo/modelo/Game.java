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
	private int addedCardsMJ1, addedCardsMJ2;
	private boolean mustEndTurn;
	
	public static Game getInstance() { return game; }

	private Game() {
		deckJ1 = new CardDeck(false);
		deckJ2 = new CardDeck(false);
		mesaJ1 = new CardDeck(true);
		mesaJ2 = new CardDeck(true);
		currentPhase = 1;
		observers = new LinkedList<>();
		addedCardsMJ1 = addedCardsMJ2 = 0;
		mustEndTurn = false;
	}

	private void nextPhase() {
		currentPhase++;
		if (currentPhase == 5) {
			currentPhase = 1;
			// adicionar uma energia em cada deck pelo fim da rodada
			deckJ1.addEnergyCard();
			deckJ2.addEnergyCard();
			//renderizar novamente a mesa
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.DECK, GameEvent.Action.SHOWTABLE, null);
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
		}
	}

	public int getPokemonsJ1() {
		return deckJ1.getPokemonsNoDeck() + mesaJ1.getPokemonsNoDeck();
	}

	public int getPokemonsJ2() {
		return deckJ2.getPokemonsNoDeck() + mesaJ2.getPokemonsNoDeck();
	}

	public CardDeck getDeckJ1() { return deckJ1; }

	public CardDeck getDeckJ2() { return deckJ2; }

	public CardDeck getMesaJ1() { return mesaJ1; }

	public CardDeck getMesaJ2() { return mesaJ2; }

	public int getFaseAtual() { return currentPhase; }

	public void play() {
		System.out.println("\n\nENTROU NO PLAY");
		if (mustEndTurn) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.MUSTENDTURN, "");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}

		if (currentPhase < 3) { // nao deve fazer nada aqui
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Ataques n??o podem ser feitos neste momento");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}

		CardDeck deckAtaque = currentPhase == 3? mesaJ1 : mesaJ2;
		CardDeck deckDefesa = currentPhase == 4? mesaJ1 : mesaJ2;

		if (deckAtaque.getSelectedCard() == null || deckDefesa.getSelectedCard() == null) {
			System.out.println(deckAtaque.getSelectedCard());
			System.out.println(deckDefesa.getSelectedCard());
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Selecione uma carta de Pok??mon em cada deck para atacar");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		else if (deckAtaque.getSelectedCard().getValue() instanceof CartaEnergia || 
				 deckDefesa.getSelectedCard().getValue() instanceof CartaEnergia) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Cartas de energia n??o podem atacar/defender");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}

		CartaPokemon pokemonAtaque = (CartaPokemon) deckAtaque.getSelectedCard().getValue();
		CartaPokemon pokemonDefesa = (CartaPokemon) deckDefesa.getSelectedCard().getValue();

		boolean energiaSuficiente = pokemonAtaque.atacar(pokemonDefesa);
		if (!energiaSuficiente) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "O Pok??mon de ataque nao tem energia suficiente");
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

			GameEvent gameEvent2 = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.SHOWMESSAGE, 
								                 String.format("Ataque efetuado com sucesso.\nAGORA, VOC?? DEVE ENCERRAR SEU TURNO!\nAtacante: %s\nDefensor: %s",
												 pokemonAtaque.getNome(), pokemonDefesa.getNome()));
			for (var observer : observers) {
				observer.notify(gameEvent2);
			}

			if (nroMortos > 0) {
				GameEvent gameEvent3 = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.SHOWMESSAGE,
													 String.format("Voc?? ganhou %d cartas de energia por eliminar %d Pok??mons rivais", 
													 nroMortos * CardDeck.ENERGY_CARDS_WHEN_KILL, nroMortos));	
				for (var observer : observers) {
					observer.notify(gameEvent3);
				}
			}

			mustEndTurn = true;
		}
	}

	// Acionada pelo botao de 'Baixar cartas'
	public void baixarCartas(int jogador) {
		System.out.println("\n\nbaixarCartas() acionado");
		
		if (currentPhase != 1 && currentPhase != 2) { // cartas so podem ser baixadas nas fases 1 (J1) e 2 (J2)
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Cartas n??o podem ser baixadas neste momento.");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		else if (jogador == 1 && currentPhase != 1) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, String.format("N??o ?? a vez do %s baixar cartas.", GameWindow.getNomeJ1()));
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		else if (jogador == 2 && currentPhase != 2) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, String.format("N??o ?? a vez do %s baixar cartas.", GameWindow.getNomeJ2()));
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;	
		}
		
		if (jogador != 1 && jogador != 2) return;
		
		CardDeck deckJogador = jogador == 1? deckJ1 : deckJ2;
		CardDeck mesaJogador = jogador == 1? mesaJ1 : mesaJ2;

		if (deckJogador.getSelectedCard() == null) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Selecione uma carta para baixar");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		
		mesaJogador.addCard( deckJogador.getSelectedCard() );
		deckJogador.removeSel();

		if (jogador == 1) addedCardsMJ1++;
		else addedCardsMJ2++;
	}

	// Acionado pelo botao "End Turn"
	public void endTurn() {
		if ( (currentPhase == 1 && addedCardsMJ1 == 0 && deckJ1.getNumberOfCards() > 0) ||
			 (currentPhase == 2 && addedCardsMJ2 == 0 && deckJ2.getNumberOfCards() > 0)) { // se for a vez do J1 baixar cartas, ele nao baixar e tiver cartas para baixar
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Voc?? precisa baixar ao menos uma carta");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}

		mesaJ1.flipAddedCards(addedCardsMJ1);
		mesaJ2.flipAddedCards(addedCardsMJ2);
		addedCardsMJ1 = addedCardsMJ2 = 0;
		mustEndTurn = false;

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
		if (currentPhase == 1 && deckJ1.getNumberOfCards() == 0) {
			nextPhase();
			this.updatePlacarViewLabel();
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.SHOWMESSAGE, String.format("Como %s n??o tem cartas em seu deck, seu turno de baixar cartas foi encerrado.", GameWindow.getNomeJ1()));
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
		}
		else if (currentPhase == 2 && deckJ2.getNumberOfCards() == 0) {
			nextPhase();
			this.updatePlacarViewLabel();
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.SHOWMESSAGE, String.format("Como %s n??o tem cartas em seu deck, seu turno de baixar cartas foi encerrado.", GameWindow.getNomeJ2()));
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
		}

		if (currentPhase == 3) {
			mesaJ1.flipDeckUp();
			mesaJ2.flipDeckUp();
		}
	}

	public void updatePlacarViewLabel() {
		String text;

		if (currentPhase == 1) text = GameWindow.getNomeJ1() + " baixa cartas";
		else if (currentPhase == 2) text = GameWindow.getNomeJ2() + " baixa cartas";
		else if (currentPhase == 3) text = GameWindow.getNomeJ1() + " ataca";
		else text = GameWindow.getNomeJ2() + " ataca";

		PlacarView.getInstance().setFieldFaseAtual(text);
	}

	public void restart() {
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

		GameEvent gameEvent2 = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.SHOWMESSAGE, "O jogo foi reiniciado\nTodos os decks e estat??sticas foram resetados");
		for (var observer : observers) {
			observer.notify(gameEvent2);
		}
	}

	public void addEnergy(int jogador) {
		if (mustEndTurn) {
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.MUSTENDTURN, "");
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}

		System.out.println("\n\n\nENTROU NO ADDENERGY()");
		System.out.println("Jogador atual: " + jogador);
		System.out.println("Fase: " + currentPhase);
		
		if (jogador != 1 && jogador != 2) return;
		else if (jogador == 1 && currentPhase != 3) { // J1 adicionando energia fora de hora
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, String.format("%s n??o pode atacar/adicionar energia neste momento", GameWindow.getNomeJ1()));
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		else if (jogador == 2 && currentPhase != 4) { // J2 adicionando energia fora de hora
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, String.format("%s n??o pode atacar/adicionar energia neste momento", GameWindow.getNomeJ2()));
			for (var observer : observers) {
				observer.notify(gameEvent);
			}
			return;
		}
		
		CardDeck deckAcionado = jogador == 1? mesaJ1 : mesaJ2;
		Card cartaSelecionada = deckAcionado.getSelectedCard();
		if (cartaSelecionada == null || cartaSelecionada.getValue() instanceof CartaEnergia) { // jogador nao selecionou a carta alvo ou ela eh uma carta de energia
			System.out.println("Carta selecionada (addEnergy): " + cartaSelecionada);
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "Selecione uma carta de Pok??mon para aplicar energia");
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
			GameEvent gameEvent = new GameEvent(this, GameEvent.Target.GWIN, GameEvent.Action.INVPLAY, "N??o h?? cartas de energia na mesa");
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
	
	public void addGameListener(GameListener listener) { observers.add(listener); }
}