package poo.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

//import poo.modelo.GameEvent.Action;
//import poo.modelo.GameEvent.Target;

public class CardDeck {
	public static final int N_POKEMON_CARDS = 5;
	public static final int N_ENERGY_CARDS = 10;
	public static final int ENERGY_CARDS_WHEN_KILL = 3;
	public static final int NCARDS = N_POKEMON_CARDS + N_ENERGY_CARDS;

	private List<Card> cartas;
	private Card selected;
	private List<GameListener> observers;
	private int pokemonsNoDeck;

	public CardDeck(boolean mesa) {
		cartas = new ArrayList<>();
		selected = null;

		if (!mesa) { // adicionar cartas
			pokemonsNoDeck = N_POKEMON_CARDS;
			Random r = new Random();
			for (int i = 0; i < N_POKEMON_CARDS; i++) {
				int n = r.nextInt(35) + 1;
				Card c = new Card("C" + n, "img" + n);
				//c.flip();
				cartas.add(c);
			}
	
			for (int i = 0; i < N_ENERGY_CARDS; i++) {
				cartas.add(new Card("Energia", "img36"));
			}
		}
		else pokemonsNoDeck = 0;

		observers = new LinkedList<>();

		System.out.println("Deck construido");
	}

	public List<Card> getCards() { return Collections.unmodifiableList(cartas); }

	public int getNumberOfCards() { return cartas.size(); }

	public int getPokemonsNoDeck() { return pokemonsNoDeck; }

	public Card getSelectedCard() { return selected; }

	public void setSelectedCard(Card card) { selected = card; }

	public void removeSel() {
		if (selected == null) {
			return;
		}
		cartas.remove(selected);
		selected = null;
		GameEvent gameEvent = new GameEvent(this, GameEvent.Target.DECK, GameEvent.Action.REMOVESEL, "");
		for (var observer : observers) {
			observer.notify(gameEvent);
		}
	}

	public int removeKilled() {
		int pokemonsRemovidos = 0;

		for (int i=0; i<cartas.size(); i++) { // para cada carta no deck
			Card c = cartas.get(i);
			if (c.getValue() instanceof CartaPokemon) { // se for uma carta de pokemon
				CartaPokemon carta = (CartaPokemon) c.getValue(); // cast
				if (carta.getHpAtual() == 0) { // se estiver morto
					cartas.remove(i); // remover
					pokemonsNoDeck--;
					pokemonsRemovidos++; // incrementar quantidade de removidos
				}
			}
		}

		return pokemonsRemovidos;
	}

	public void addEnergyForEachKill(int kills) {
		int n = kills * ENERGY_CARDS_WHEN_KILL;
		
		for (int i=0; i<n; i++) {
			cartas.add(new Card("Energia", "img36"));
		}
	}

	public boolean removeEnergyCard() {
		for (Card c : cartas) {
			if (c.getValue() instanceof CartaEnergia) {
				cartas.remove(c);
				return true;
			}
		}

		return false;
	}

	public void addCard(Card card) {
		System.out.println("add: "+ card);
		cartas.add(card);
		GameEvent gameEvent = new GameEvent(this, GameEvent.Target.DECK, GameEvent.Action.SHOWTABLE, "");
		for (var observer : observers) {
			observer.notify(gameEvent);
		}
	}

	public void addGameListener(GameListener listener) {
		observers.add(listener);
	}
}