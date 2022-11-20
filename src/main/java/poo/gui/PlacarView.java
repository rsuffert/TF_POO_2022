package poo.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import poo.modelo.Game;
import poo.modelo.GameEvent;
import poo.modelo.GameListener;

public class PlacarView extends GridPane implements GameListener {
	private static PlacarView placarView = new PlacarView();

	private TextField ptsJ1, ptsJ2;
	private Label labelFaseAtual;

	public static PlacarView getInstance() {
		return placarView;
	}

	private PlacarView() {
		this.setAlignment(Pos.CENTER);
		this.setHgap(10);
		this.setVgap(10);
		this.setPadding(new Insets(25, 25, 25, 25));

		Game.getInstance().addGameListener(this);

		ptsJ1 = new TextField();
		ptsJ2 = new TextField();

		ptsJ1.setText("" + Game.getInstance().getPokemonsJ1());
		ptsJ2.setText("" + Game.getInstance().getPokemonsJ2());

		ptsJ1.setEditable(false);
		ptsJ2.setEditable(false);

		this.add(new Label("Pokémons Jogador 1:"), 0, 0);
		this.add(ptsJ1, 1, 0);
		this.add(new Label("Pokémons Jogador 2:"), 0, 1);
		this.add(ptsJ2, 1, 1);
		
		labelFaseAtual = new Label("FASE ATUAL: J1 baixa cartas.");
		this.add(labelFaseAtual, 0, 2);
	}

	public void setLabelFaseAtual(String text) {
		labelFaseAtual.setText(text);
	}

	@Override
	public void notify(GameEvent event) {
		ptsJ1.setText("" + Game.getInstance().getPokemonsJ1());
		ptsJ2.setText("" + Game.getInstance().getPokemonsJ2());
	}
}
