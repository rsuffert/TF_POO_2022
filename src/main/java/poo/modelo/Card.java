package poo.modelo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Card {
	private String id;
	private String imageId;
	private boolean faceUp;
	private final PropertyChangeSupport pcs;

	public Card(String anId, String anImageId) {
		id = anId;
		imageId = anImageId;
		faceUp = true;
		pcs = new PropertyChangeSupport(this);
	}

	public String getId() {
		return id;
	}

	public String getImageId() {
		return imageId;
	}

	public Carta getValue() {
		switch (imageId) {
			case "img1":  return new PokemonAgua("Blastoise", "Hydro Pump", Carta.Raridade.RARA, 140, 60, 4);
			case "img2":  return new PokemonAgua("Croconaw", "Bite", Carta.Raridade.RARA, 90, 60, 3);
			case "img3":  return new PokemonAgua("Feraligatr", "Hydro Splash", Carta.Raridade.RARA, 160, 130, 4);
			case "img4":  return new PokemonAgua("Squirtle", "Water Gun", Carta.Raridade.COMUM, 50, 20, 2);
			case "img5":  return new PokemonAgua("Totodile", "Water Gun", Carta.Raridade.COMUM, 70, 10, 1);
			case "img6":  return new PokemonAgua("Vaporeon", "Hydro Pump", Carta.Raridade.RARA, 110, 60, 3);
			case "img7":  return new PokemonAgua("Wartortle", "Double Spin", Carta.Raridade.INCOMUM, 80, 30, 3);
			case "img8":  return new PokemonFogo("Charizard", "Flare Blitz", Carta.Raridade.RARA, 170, 170, 4);
			case "img9":  return new PokemonFogo("Charmander", "Tail on Fire", Carta.Raridade.COMUM, 60, 10, 1);
			case "img10": return new PokemonFogo("Charmeleon", "Flamethrower", Carta.Raridade.RARA, 90, 100, 4);
			case "img11": return new PokemonFogo("Cyndaquil", "Ember", Carta.Raridade.COMUM, 60, 30, 2);
			case "img12": return new PokemonFogo("Flareon", "Kindle", Carta.Raridade.RARA, 110, 120, 3);
			case "img13": return new PokemonFogo("Quilava", "Mini Eruption", Carta.Raridade.INCOMUM, 80, 30, 2);
			case "img14": return new PokemonFogo("Typhlosion", "Flare Destroy", Carta.Raridade.RARA, 150, 130, 3);
			case "img15": return new PokemonNeutro("Aipom", "Double Hit", Carta.Raridade.COMUM, 60, 10, 1);
			case "img16": return new PokemonNeutro("Audino", "Doubleslap", Carta.Raridade.COMUM, 80, 30, 2);
			case "img17": return new PokemonNeutro("Dodrio", "Endeavor", Carta.Raridade.INCOMUM, 90, 50, 3);
			case "img18": return new PokemonNeutro("Doduo", "Double Hit", Carta.Raridade.COMUM, 70, 30, 3);
			case "img19": return new PokemonNeutro("Eevee", "Bite", Carta.Raridade.COMUM, 60, 30, 3);
			case "img20": return new PokemonNeutro("Fearow", "Drill Run Double", Carta.Raridade.RARA, 100, 70, 2);
			case "img21": return new PokemonNeutro("Kangaskhan", "Parental Fury", Carta.Raridade.COMUM, 120, 40, 2);
			case "img22": return new PokemonNeutro("Meowth", "Pay Day", Carta.Raridade.COMUM, 70, 10, 1);
			case "img23": return new PokemonNeutro("Miltank", "Sitdown Splash", Carta.Raridade.COMUM, 100, 50, 3);
			case "img24": return new PokemonNeutro("Pidgey", "Quick Attack", Carta.Raridade.COMUM, 60, 10, 1);
			case "img25": return new PokemonNeutro("Raticate", "Shadowy Bite", Carta.Raridade.INCOMUM, 60, 60, 1);
			case "img26": return new PokemonNeutro("Rattatta", "Bite", Carta.Raridade.COMUM, 40, 10, 1);
			case "img27": return new PokemonNeutro("Sentret", "Tail Smack", Carta.Raridade.COMUM, 50, 20, 2);
			case "img28": return new PokemonNeutro("Spearow", "Glide", Carta.Raridade.COMUM, 60, 10, 1);
			case "img29": return new PokemonPlanta("Bayleef", "Vine Whip", Carta.Raridade.INCOMUM, 90, 30, 2);
			case "img30": return new PokemonPlanta("Bulbassaur", "Vine Whip", Carta.Raridade.COMUM, 70, 10, 1);
			case "img31": return new PokemonPlanta("Chikorita", "Tackle", Carta.Raridade.COMUM, 60, 10, 1);
			case "img32": return new PokemonPlanta("Ivysaur", "Razor Leaf", Carta.Raridade.RARA, 100, 60, 3);
			case "img33": return new PokemonPlanta("Leafeon", "Grass Knot", Carta.Raridade.RARA, 110, 50, 2);
			case "img34": return new PokemonPlanta("Meganium", "Green Force", Carta.Raridade.RARA, 150, 50, 3);
			case "img35": return new PokemonPlanta("Venusaur", "Solar Beam", Carta.Raridade.RARA, 180, 130, 4);
			case "img36": return new CartaEnergia();
			default:      throw new IllegalArgumentException("Invalid image Id");
		}
	}

	public boolean isFacedUp() {
		return faceUp;
	}

	public void flip() {
		boolean old = faceUp;
		faceUp = !faceUp;
		pcs.firePropertyChange("facedUp", old, faceUp);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
}
