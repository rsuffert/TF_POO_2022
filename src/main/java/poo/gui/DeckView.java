package poo.gui;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import poo.modelo.Card;
import poo.modelo.CardDeck;
import poo.modelo.Game;
import poo.modelo.GameEvent;
import poo.modelo.GameListener;
//import poo.modelo.GameEvent.Action;
//import poo.modelo.GameEvent.Target;

public class DeckView extends HBox implements CardViewListener, GameListener {
	private int jogador;
	private CardDeck cDeck;
	private Card selectedCard;

	public DeckView(int nroJog) {
		super(4);
		this.setAlignment(Pos.CENTER);

		jogador = nroJog;
		selectedCard = null;

		cDeck = null;
		if (jogador == 1) {
			cDeck = Game.getInstance().getDeckJ1();
		} else if (jogador == 2) {
			cDeck = Game.getInstance().getDeckJ2();
		} else if (jogador == -1) {
			cDeck = Game.getInstance().getMesaJ1();
	    } else if (jogador == -2) {
			cDeck = Game.getInstance().getMesaJ2();
		}

		cDeck.addGameListener(this);

		for (Card card : cDeck.getCards()) {
			CardView cv = new CardView(card);
			cv.setCardViewObserver(this);
			this.getChildren().add(cv);
		}

		Game.getInstance().addGameListener(this);
	}

	private void removeSel() {
		ObservableList<Node> cards = getChildren();
		for (int i = 0; i < cards.size(); i++) {
			CardView cv = (CardView) cards.get(i);
			if (cv.getCard() == selectedCard) {
				getChildren().remove(cv);
				selectedCard = null;
			}
		}
	}

	private void showDeck() {
		//ObservableList<Node> cards = getChildren();
		//cDeck.addGameListener(this);

		this.getChildren().clear();

		System.out.println("m1.len>" + cDeck.getNumberOfCards());
		for (Card card : cDeck.getCards()) {
			System.out.println("show>" + card);
			CardView cv = new CardView(card);
			//cv.setCardViewObserver(this);
			this.getChildren().add(cv);
		}
		System.out.println("Deck atualizado");
	}

	@Override
	public void notify(GameEvent event) {
		System.out.println("ev: "+ event);
		if (event.getTarget() != GameEvent.Target.DECK) {
			return;
		}
		if (event.getAction() == GameEvent.Action.REMOVESEL) {
			removeSel();
		}
		if (event.getAction() == GameEvent.Action.SHOWTABLE) {
			showDeck();
		}
		if (event.getAction() == GameEvent.Action.RESTART) {
			if (jogador == 1) {
				cDeck = Game.getInstance().getDeckJ1();
				System.out.println("Deck J1 reiniciado");
			} else if (jogador == 2) {
				cDeck = Game.getInstance().getDeckJ2();
				System.out.println("Deck J2 reiniciado");
			} else if (jogador == -1) {
				cDeck = Game.getInstance().getMesaJ1();
				System.out.println("Mesa J1 reiniciado");
			} else if (jogador == -2) {
				cDeck = Game.getInstance().getMesaJ2();
				System.out.println("Mesa J2 reiniciado");
			}
			showDeck();
		}
	}

	@Override
	public void handle(CardViewEvent event) {
		CardView cv = event.getCardView();
		selectedCard = cv.getCard();
		cDeck.setSelectedCard(selectedCard);
		Game.getInstance().play(cDeck);
	}
}
