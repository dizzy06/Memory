package module;

import java.util.Random;

public class Shuffle {
	int zufallsReihe[] = new int[16]; // Array für Zufallswerte um MemoryFeld zu mischen
	Random random = new Random(); // RandomObjekt um Zufälle zu erzeugen

	void zufall() { // Methode um 16 Zufallszahlen von 0 bis 15 zu erzeugen und pro Zahl nur einmal
					// vorkommen darf
		for (int i = 0; i < 16; i++) {
			zufallsReihe[i] = 20; // Wert 20 als Platzhalter, ausserhalb meines eigentlichn Bereichs->nötig für
									// meine While-Schleife
		}
		for (int i = 0; i < 16; i++) {
			int x;
			x = random.nextInt(16);
			while (zufallsReihe[x] != 20) {
				x = random.nextInt(16);
			}
			zufallsReihe[x] = i;
		}
	}

	public Shuffle() { // Konstruktor
	}

}
