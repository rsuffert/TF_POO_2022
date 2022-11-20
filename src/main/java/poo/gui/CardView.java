package poo.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import poo.modelo.Card;
import poo.modelo.ImageFactory;

public class CardView extends Button implements PropertyChangeListener {
	private Card card;
	private CardView thisCardView;
	private CardViewListener observer;
	private Tooltip tip;

	public CardView(Card aCard) {
		super("", ImageFactory.getInstance().createImage("imgBck"));

		if (aCard.isFacedUp())
		   this.setGraphic(ImageFactory.getInstance().createImage(aCard.getImageId()));
		
		card = aCard;
		card.addPropertyChangeListener(this);
		thisCardView = this;

		this.setOnAction(e -> {
			if (observer != null) {
				observer.handle(new CardViewEvent(thisCardView));
			}
		});

		tip = new Tooltip(card.getId());
		this.setTooltip(tip);
	}

	public void setCardViewObserver(CardViewListener obs) {
		observer = obs;
	}

	public Card getCard() {
		return card;
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (card.isFacedUp()) {
			this.setGraphic(ImageFactory.getInstance().createImage(card.getImageId()));
		} else {
			this.setGraphic(ImageFactory.getInstance().createImage("imgBck"));
		}		
	}
}
