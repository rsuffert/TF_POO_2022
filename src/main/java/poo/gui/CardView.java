package poo.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import poo.modelo.Card;
import poo.modelo.ImageFactory;
import poo.modelo.CartaPokemon;

public class CardView extends Button implements PropertyChangeListener {
	private Card card;
	private CardView thisCardView;
	private CardViewListener observer;
	private Tooltip tip;
	private boolean facingUp;

	public CardView(Card aCard) {
		super("", ImageFactory.getInstance().createImage(aCard.getImageId()));
		
		facingUp = true;
		card = aCard;
		card.addPropertyChangeListener(this);
		thisCardView = this;

		this.setOnAction(e -> {
			if (observer != null) {
				observer.handle(new CardViewEvent(thisCardView));
			}
		});

		tip = new Tooltip();
		this.setTooltip(tip);
		tip.setOnShowing(e -> updateTooltip());
	}

	public Card getCard() {
		return card;
	}

	public boolean isFacingUp() { return facingUp; }

	public void flip() {
		System.out.println("\t\t\t\t*CardView.flip() acionado");
		System.out.println("\t\t\t\tisFacingUp()==" + card.isFacingUp());
		if (!card.isFacingUp()) {
			System.out.println("\t\t\t\tVirando a carta para baixo");
			this.setGraphic(ImageFactory.getInstance().createImage("imgBck"));
			this.setTooltip(null); // disable tooltip
		}
		else {
			System.out.println("\t\t\t\tVirando a carta para cima");
			this.setGraphic(ImageFactory.getInstance().createImage(card.getImageId()));
			this.setTooltip(tip); // enable tooltip
		}

		facingUp = !facingUp;
		System.out.println("\t\t\t\tResultado: isFacingUp()==" + card.isFacingUp());
	}

	public void updateTooltip() {
		if (card.getValue() instanceof CartaPokemon) {
			CartaPokemon carta = (CartaPokemon) card.getValue();
			tip.setText(String.format("HP: %d\nEnergia: %d\nRaridade: %s", carta.getHpAtual(), carta.getEnergiaAtual(), carta.getRaridade().toString()));
		}
		else {
			tip.setText(String.format("Fornece 1 energia\nRaridade: %s", card.getValue().getRaridade().toString()));
		}
	}

	public void setCardViewObserver(CardViewListener obs) {
		observer = obs;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("\t\t\t*CardView.propertyChange() acionado.");
		this.flip();
	}
}
