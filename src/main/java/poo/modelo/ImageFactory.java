package poo.modelo;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageFactory {
	private static ImageFactory imgf = new ImageFactory();
	private Map<String, Image> images;

	public static ImageFactory getInstance() {
		return imgf;
	}

	private ImageFactory() {
		images = new HashMap<>();
	}

	private String id2File(String imgId) {
		switch (imgId) {
			case "img1":   return ("/imagens/Agua/Blastoise.png");
			case "img2":   return ("/imagens/Agua/Croconaw.png");
			case "img3":   return ("/imagens/Agua/Feraligatr.png");
			case "img4":   return ("/imagens/Agua/Squirtle.png");
			case "img5":   return ("/imagens/Agua/Totodile.png");
			case "img6":   return ("/imagens/Agua/Vaporeon.png");
			case "img7":   return ("/imagens/Agua/Wartortle.png");
			case "img8":   return ("/imagens/Fogo/Charizard.png");
			case "img9":   return ("/imagens/Fogo/Charmander.png");
			case "img10":  return ("/imagens/Fogo/Charmeleon.png");
			case "img11":  return ("/imagens/Fogo/Cyndaquil.png");
			case "img12":  return ("/imagens/Fogo/Flareon.png");
			case "img13":  return ("/imagens/Fogo/Quilava.png");
			case "img14":  return ("/imagens/Fogo/Typhlosion.png");
			case "img15":  return ("/imagens/Normal/Aipom.png");
			case "img16":  return ("/imagens/Normal/Audino.png");
			case "img17":  return ("/imagens/Normal/Dodrio.png");
			case "img18":  return ("/imagens/Normal/Doduo.png");
			case "img19":  return ("/imagens/Normal/Eevee.png");
			case "img20":  return ("/imagens/Normal/Fearow.png");
			case "img21":  return ("/imagens/Normal/Kangaskhan.png");
			case "img22":  return ("/imagens/Normal/Meowth.png");
			case "img23":  return ("/imagens/Normal/Miltank.png");
			case "img24":  return ("/imagens/Normal/Pidgey.png");
			case "img25":  return ("/imagens/Normal/Raticate.png");
			case "img26":  return ("/imagens/Normal/Rattatta.png");
			case "img27":  return ("/imagens/Normal/Sentret.png");
			case "img28":  return ("/imagens/Normal/Spearow.png");
			case "img29":  return ("/imagens/Planta/Bayleef.png");
			case "img30":  return ("/imagens/Planta/Bulbasaur.png");
			case "img31":  return ("/imagens/Planta/Chikorita.png");
			case "img32":  return ("/imagens/Planta/Ivysaur.png");
			case "img33":  return ("/imagens/Planta/Leafeon.png");
			case "img34":  return ("/imagens/Planta/Meganium.png");
			case "img35":  return ("/imagens/Planta/Venusaur.png");
			case "img36":  return ("/imagens/Energia.png");
			case "imgBck": return ("/imagens/imgBck.png");    
			default:       throw new IllegalArgumentException("Invalid image Id: " + imgId);
		}
	}


	public ImageView createImage(String imgId) {
		Image img = images.get(imgId);
		
		if (img == null) {
//			img = new Image(id2File(imgId));
			img = new Image(getClass().getResourceAsStream(id2File(imgId)),400,250,true,true);
			images.put(imgId, img);
		}

		ImageView imgv = new ImageView(img);
		return imgv;
	}
}
