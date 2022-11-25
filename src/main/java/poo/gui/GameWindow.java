package poo.gui;

import javax.swing.JOptionPane;

import javafx.application.Application;
//import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import poo.modelo.Game;
import poo.modelo.GameEvent;
import poo.modelo.GameListener;

public class GameWindow extends Application implements GameListener {
	public static String nomeJ1, nomeJ2;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		Game.getInstance().addGameListener(this);

		String nomeJ1Digitado = JOptionPane.showInputDialog(null, "Digite o nome do J1");
		String nomeJ2Digitado = JOptionPane.showInputDialog(null, "Agora, digite o nome do J2");
		nomeJ1 = nomeJ1Digitado != null? nomeJ1Digitado : "J1";
		nomeJ2 = nomeJ2Digitado != null? nomeJ2Digitado : "J2";

		primaryStage.setTitle("Batalha de Cartas");

		Group root = new Group();

        TabPane tabPane = new TabPane();

        Tab tab1 = new Tab("Jogador " + nomeJ1);
        Tab tab2 = new Tab("Jogador " + nomeJ2);
        Tab tab3 = new Tab("Mesa");
        //Tab tab4 = new Tab("Mesa Jogador 2");

		tabPane.getTabs().add(tab3);
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);


		GridPane grid1 = new GridPane();
		grid1.setAlignment(Pos.CENTER);
		grid1.setHgap(10);
		grid1.setVgap(10);
		grid1.setPadding(new Insets(25, 25, 25, 25));

		DeckView deckJ1 = new DeckView(1);
		ScrollPane sd1 = new ScrollPane();
		sd1.setPrefSize(1000, 250);
		sd1.setContent(deckJ1);
		grid1.add(sd1, 0, 0);

		Button butBaixar1 = new Button("Baixar\ncarta");
		grid1.add(butBaixar1, 0, 1);
		butBaixar1.setOnAction(e -> Game.getInstance().baixarCartas(1));

		GridPane grid2 = new GridPane();
		grid2.setAlignment(Pos.CENTER);
		grid2.setHgap(10);
		grid2.setVgap(10);
		grid2.setPadding(new Insets(25, 25, 25, 25));

		
		DeckView deckJ2 = new DeckView(2);
		ScrollPane sd2 = new ScrollPane();
		sd2.setPrefSize(1000, 250);
		sd2.setContent(deckJ2);
		grid2.add(sd2, 0, 2);

		Button butBaixar2 = new Button("Baixar\ncarta");
		grid2.add(butBaixar2, 0, 3);
		butBaixar2.setOnAction(e -> Game.getInstance().baixarCartas(2));
	
		GridPane grid3 = new GridPane();
		grid3.setAlignment(Pos.CENTER);
		grid3.setHgap(10);
		grid3.setVgap(10);
		grid3.setPadding(new Insets(25, 25, 25, 25));

		DeckView mesaJ1 = new DeckView(-1);
		ScrollPane sdM1 = new ScrollPane();
		sdM1.setPrefSize(1200, 225);
		sdM1.setContent(mesaJ1);
		grid3.add(sdM1, 0, 0);

		PlacarView placar = PlacarView.getInstance();
		grid3.add(placar, 0, 1);

		Button butEndTurn = new Button("Finalizar\nturno");
		grid3.add(butEndTurn, 0, 1);
		butEndTurn.setOnAction(e -> Game.getInstance().endTurn());

		Button butRestart = new Button("Reset");
		grid3.add(butRestart, 1, 1);
		butRestart.setOnAction(e -> Game.getInstance().restart());

		Button butAddEnergyJ1 = new Button("Add\nenergy");
		grid3.add(butAddEnergyJ1, 1, 0);
		butAddEnergyJ1.setOnAction(e -> Game.getInstance().addEnergy(1));

		Button butAddEnergyJ2 = new Button("Add\nenergy");
		grid3.add(butAddEnergyJ2, 1, 2);
		butAddEnergyJ2.setOnAction(e -> Game.getInstance().addEnergy(2));

		DeckView mesaJ2 = new DeckView(-2);
		ScrollPane sdM2 = new ScrollPane();
		sdM2.setPrefSize(1200, 225);
		sdM2.setContent(mesaJ2);
		grid3.add(sdM2, 0, 2);

		tab1.setContent(grid1);
        tab2.setContent(grid2);
        tab3.setContent(grid3);


		root.getChildren().add(tabPane);
		
        Scene scene = new Scene(root);

		primaryStage.setResizable(true);
        primaryStage.setScene(scene);
        primaryStage.show();
	}
	

	@Override
	public void notify(GameEvent eg) {
		Alert alert;
		if (eg == null) return;
		if (eg.getTarget() == GameEvent.Target.GWIN) {
			switch (eg.getAction()) {
				case INVPLAY:
					alert = new Alert(AlertType.WARNING);
					alert.setTitle("ATENÇÃO!");
					alert.setHeaderText("JOGADA INVÁLIDA!");
					alert.setContentText(eg.getArg());
					alert.showAndWait();
					break;
				case MUSTCLEAN:
					alert = new Alert(AlertType.WARNING);
					alert.setTitle("ATENÇÃO!");
					alert.setHeaderText(null);
					alert.setContentText("Utilize o botao \"Clean\"");
					alert.showAndWait();
					break;
				case ENDGAME:
					String text = "FIM DE JOGO!\n";
					if (Game.getInstance().getPokemonsJ1() == 0) {
						text += String.format("O jogado %s ganhou!", nomeJ2);
					} else {
						text += String.format("O jogador %s ganhou!", nomeJ1);
					}
					alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("GAME OVER");
					alert.setHeaderText(null);
					alert.setContentText(text);
					alert.showAndWait();
					break;
				case SHOWMESSAGE:
					alert = new Alert(AlertType.INFORMATION);
					alert.setTitle(null);
					alert.setHeaderText(null);
					alert.setContentText(eg.getArg());
					alert.showAndWait();
					break;
				case REMOVESEL:
					// Esse evento não vem para cá
				case SHOWTABLE:
					// Esse evento não vem para cá
				case RESTART:
					// Esse evento não vem para cá
				}
		}
	}

	public static String getNomeJ1() {
		return nomeJ1;
	}
	public static String getNomeJ2() {
		return nomeJ2;
	}
}
