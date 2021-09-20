package module;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.JProgressBar;

public class Fenster {
	// Objekte
	JFrame frame;
	ImageLoader imageHelper;
	Shuffle zufallsFeld;
	SoundMachine play = new SoundMachine();
	BackgroundSound playback = new BackgroundSound();
	StopWatch time = new StopWatch();
	ImageIcon iconTemp;
	ImageIcon cover;
	ImageIcon side;
	Image icon;
	JButton btn[] = new JButton[16]; // Array für Buttons bzw. für mein MemoryFelder
	Integer active[] = new Integer[16]; // Array für momentan aufgedeckte MemoryFelder
	Integer paired[] = new Integer[16]; // Array für permanent aufgedekct MemoryFelder
	JLabel label[] = new JLabel[2]; // Array für Labels
	JProgressBar pbar[] = new JProgressBar[1]; // Array für Progessbar
	// --------------------------------------------------------
	// private static final long serialVersionUID = 1L; // zurzeit nicht in
	// Verwendung
	// --------------------------------------------------------

	// Variablen
	int open = 0; // Zähler Variable um Anzahl der aufgedeckten Felder zu zählen
	int activeButton; // Zum merken von aktuell geklickten Button
	int pair1, pair2, pairx, pairy; // zum merken der aufdeckten Felder
	int zug = 0; // Zähler für die Versuche Spieler 1
	int zug2 = 0; // Zähler für die Versuche Spieler 1
	int player = 0; // Wer gerade am Zug ist; 0 -> Spieler 1; 1 -> Spieler 2
	int punkte, punkte2 = 0; // Punktezähler für Spieler 1 und 2
	int progress = 0; // Variable für den Progressbar
	String elapsed; // Vergangene Zeit beim Spielen
	// String gewinn; // Gewinntext, momentan nicht notwendig
	boolean klick = true; // notwendig zum Sperren Felder für den ActionListener
	boolean finished = false; // nicht in Nutzung, vllt später weitere Funktionen
	boolean oneClick = false; // notwendig, damit nur ein Feld aktiv zur selben Zeit sein darf !!
	boolean backgroundMusic;

	/************ Methoden ****************************/

	/** Überprüft ob das Spiel zuende gespielt wurde **/
	public boolean gameOver() {
		int sum = 0;
		for (int value : paired) {
			sum += value;
		}
		boolean x = false;
		if (sum == 16)
			x = true;
		return x;
	}

	/** setzt das Spiel zurück **/
	public void reset() {
		time.stopTime(0);
		for (int i = 0; i < 16; i++) {
			btn[i].setIcon(cover);
			btn[i].setEnabled(true);
			zufallsFeld.zufall();
		}
		for (int i = 0; i < 16; i++) {
			active[i] = 0;
			paired[i] = 0;
		}
		activeButton = 20;
		open = 0;
		zug = 0;
		label[0].setForeground(Color.BLUE);
		label[0].setText("Spieler 1: Punkte: 0");
		label[1].setForeground(Color.RED);
		label[1].setText("Spieler 2: Punkte: 0");
		player = 0;
		punkte = 0;
		punkte2 = 0;
		progress = 0;
		pbar[0].setValue(0);
	}

	/** checkt ob man ein gleiches Paar hat **/
	public void isMatch() { // Soll auf gleiches Paar kontrollieren und nach 2 offenen Feldern wieder zu
							// decken bzw. disablen
		if (open == 2) {
			klick = false;
			if (player == 0)
				zug++;
			else
				zug2++;
			int x = 0;
			for (int i = 0; i < 16; i++) {

				if (active[i] == 1) {
					if (x == 0) {
						pair1 = zufallsFeld.zufallsReihe[i];
						pairx = i;
						x++;
					}
					if (x == 1) {
						pair2 = zufallsFeld.zufallsReihe[i];
						pairy = i;
					}
				}
			}

			boolean matches = false;
			if (pair1 < 8) {
				if ((pair2 - pair1) == 8)
					matches = true;
			}

			if (pair2 < 8) {
				if ((pair1 - pair2) == 8)
					matches = true;
			}

			for (int i = 0; i < 16; i++) {
				active[i] = 0;
			}

			if (matches) {
				if (player == 0)
					punkte++;
				else
					punkte2++;
				progress++;
				pbar[0].setValue(progress * 13);
				play.Sound("res/sound/bell.wav");
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					public void run() {
						btn[pairx].setEnabled(false);
						btn[pairy].setEnabled(false);
						paired[pairx] = 1;
						paired[pairy] = 1;
						open = 0;
						klick = true;
						play.Sound("res/sound/splash.wav");
						if (gameOver()) {
							time.stopTime(1);
							elapsed = time.getElapsedTime();
							// System.out.println(elapsed);
							play.Sound("res/sound/tada.wav");
							finished = true;
							// String z = Integer.toString(zug); // zwing Java dazu, denn Wert der Variable
							// in den Delay zu übernehmen, damit MessageBox mit Variablen arbeiten kann
							// String z2 = Integer.toString(zug2);
							String p = Integer.toString(punkte);
							String p2 = Integer.toString(punkte2);
							JOptionPane.showMessageDialog(null,
									"Juhu, Spiel beendet\nPunkte Spieler 1: " + p + "\nPunkte Spieler 2: " + p2
											+ "\nBenötigte Zeit: " + elapsed,
									"Memory", JOptionPane.INFORMATION_MESSAGE, null);
							reset();
						}
					};
				}, 2000);
			} else {
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					public void run() {
						play.Sound("res/sound/mismatch.wav");
						btn[pairx].setIcon(cover);
						btn[pairy].setIcon(cover);
						open = 0;
						klick = true;
					};
				}, 2000);
				if (player == 0)
					player = 1;
				else
					player = 0;
			}
			if (player == 0) {
				label[0].setForeground(Color.BLUE);
				label[0].setText("Spieler 1: Punkte: " + punkte);
			} else {
				label[1].setForeground(Color.RED);
				label[1].setText("Spieler 2: Punkte: " + punkte2);
			}
		}
	}

	public Fenster() { // Konstruktor, erstellt das Fenster mit seinen Objekten
		playback.Sound("res/sound/memory.wav"); // Hintergrundmusik
		playback.play(); // Abspielen Hintergrundmusik
		backgroundMusic = true;
		zufallsFeld = new Shuffle(); // Objekt aus Klasse Shuffle
		frame = new JFrame();
		imageHelper = new ImageLoader(); // Objekt aus Klasse ImageLoader, eine Hilfsklasse um Bilder auch aus der Jar
											// zu laden
		icon = imageHelper.createImageIcon("res/icon/icon.png", "icon").getImage();
		cover = imageHelper.createImageIcon("res/cover/cover4.png", "cover"); // Deckblatt für MemoryFelder
		side = imageHelper.createImageIcon("res/cover/side.png", "side"); // Dekoration für GUI
		frame.setSize(747, 730); // Fesntergröße
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // scließt das Programm beim Klicken auf das rote X
		frame.setLocationRelativeTo(null); // Fenster wird zentriert
		frame.setResizable(false); // Größe lässt sich nicht ändern
		frame.setTitle("Memory 2.2.4"); // Fenstertitel
		frame.getContentPane().setLayout(null);
		frame.setIconImage(icon);
		// Reset Befehle
		zufallsFeld.zufall(); // Array aus Klasse Shuffle, bevor die Buttons angelegt werden
		open = 0;
		activeButton = 20;

		/*****************************
		 * Buttons für das MemoryFeld
		 ************************************************************/
		JButton btn0 = new JButton("0");
		btn0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn0.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[0]) + ".png", "cover"));
					if (active[0] == 0)
						open++;
					active[0] = 1;
					isMatch();
				}

			}
		});
		btn0.setBounds(10, 44, 150, 150);
		frame.getContentPane().add(btn0);
		btn[0] = btn0;

		JButton btn1 = new JButton("1");
		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn1.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[1]) + ".png", "cover"));
					if (active[1] == 0)
						open++;
					active[1] = 1;
					isMatch();
				}

			}
		});
		btn1.setBounds(160, 44, 150, 150);
		frame.getContentPane().add(btn1);
		btn[1] = btn1;

		JButton btn2 = new JButton("2");
		btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn2.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[2]) + ".png", "cover"));
					if (active[2] == 0)
						open++;
					active[2] = 1;
					isMatch();
				}
			}
		});
		btn2.setBounds(310, 44, 150, 150);
		frame.getContentPane().add(btn2);
		btn[2] = btn2;

		JButton btn3 = new JButton("3");
		btn3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn3.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[3]) + ".png", "cover"));
					if (active[3] == 0)
						open++;
					active[3] = 1;
					isMatch();
				}
			}
		});
		btn3.setBounds(460, 44, 150, 150);
		frame.getContentPane().add(btn3);
		btn[3] = btn3;

		JButton btn4 = new JButton("4");
		btn4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn4.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[4]) + ".png", "cover"));
					if (active[4] == 0)
						open++;
					active[4] = 1;
					isMatch();
				}
			}
		});
		btn4.setBounds(10, 194, 150, 150);
		frame.getContentPane().add(btn4);
		btn[4] = btn4;

		JButton btn5 = new JButton("5");
		btn5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn5.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[5]) + ".png", "cover"));
					if (active[5] == 0)
						open++;
					active[5] = 1;
					isMatch();
				}
			}
		});
		btn5.setBounds(160, 194, 150, 150);
		frame.getContentPane().add(btn5);
		btn[5] = btn5;

		JButton btn6 = new JButton("6");
		btn6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn6.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[6]) + ".png", "cover"));
					if (active[6] == 0)
						open++;
					active[6] = 1;
					isMatch();
				}
			}
		});
		btn6.setBounds(310, 194, 150, 150);
		frame.getContentPane().add(btn6);
		btn[6] = btn6;

		JButton btn7 = new JButton("7");
		btn7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn7.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[7]) + ".png", "cover"));
					if (active[7] == 0)
						open++;
					active[7] = 1;
					isMatch();
				}
			}
		});
		btn7.setBounds(460, 194, 150, 150);
		frame.getContentPane().add(btn7);
		btn[7] = btn7;

		JButton btn8 = new JButton("8");
		btn8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn8.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[8]) + ".png", "cover"));
					if (active[8] == 0)
						open++;
					active[8] = 1;
					isMatch();
				}
			}
		});
		btn8.setBounds(10, 344, 150, 150);
		frame.getContentPane().add(btn8);
		btn[8] = btn8;

		JButton btn9 = new JButton("9");
		btn9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn9.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[9]) + ".png", "cover"));
					if (active[9] == 0)
						open++;
					active[9] = 1;
					isMatch();
				}
			}
		});
		btn9.setBounds(160, 344, 150, 150);
		frame.getContentPane().add(btn9);
		btn[9] = btn9;

		JButton btn10 = new JButton("10");
		btn10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn10.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[10]) + ".png", "cover"));
					if (active[10] == 0)
						open++;
					active[10] = 1;
					isMatch();
				}
			}
		});
		btn10.setBounds(310, 344, 150, 150);
		frame.getContentPane().add(btn10);
		btn[10] = btn10;

		JButton btn11 = new JButton("11");
		btn11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn11.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[11]) + ".png", "cover"));
					if (active[11] == 0)
						open++;
					active[11] = 1;
					isMatch();
				}
			}
		});
		btn11.setBounds(460, 344, 150, 150);
		frame.getContentPane().add(btn11);
		btn[11] = btn11;

		JButton btn12 = new JButton("12");
		btn12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn12.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[12]) + ".png", "cover"));
					if (active[12] == 0)
						open++;
					active[12] = 1;
					isMatch();
				}
			}
		});
		btn12.setBounds(10, 494, 150, 150);
		frame.getContentPane().add(btn12);
		btn[12] = btn12;

		JButton btn13 = new JButton("13");
		btn13.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn13.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[13]) + ".png", "cover"));
					if (active[13] == 0)
						open++;
					active[13] = 1;
					isMatch();
				}
			}
		});
		btn13.setBounds(160, 494, 150, 150);
		frame.getContentPane().add(btn13);
		btn[13] = btn13;

		JButton btn14 = new JButton("14");
		btn14.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn14.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[14]) + ".png", "cover"));
					if (active[14] == 0)
						open++;
					active[14] = 1;
					isMatch();
				}
			}
		});
		btn14.setBounds(310, 494, 150, 150);
		frame.getContentPane().add(btn14);
		btn[14] = btn14;

		JButton btn15 = new JButton("15");
		btn15.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (klick) {
					btn15.setIcon(imageHelper.createImageIcon(
							"res/deck/" + Integer.toString(zufallsFeld.zufallsReihe[15]) + ".png", "cover"));
					if (active[15] == 0)
						open++;
					active[15] = 1;
					isMatch();
				}
			}
		});
		btn15.setBounds(460, 494, 150, 150);
		frame.getContentPane().add(btn15);
		btn[15] = btn15;

		JButton btnReset = new JButton("Reset"); // Resettaste setzt Werte zurück
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		btnReset.setBounds(620, 44, 100, 30);
		frame.getContentPane().add(btnReset);
		/***********************************
		 * MenuBar
		 ****************************************/
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 731, 22);
		frame.getContentPane().add(menuBar);

		JMenu mnNewMenu = new JMenu("Menu");
		menuBar.add(mnNewMenu);

		JMenuItem mntmNewMenuItem = new JMenuItem("Beenden");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Reset");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		mnNewMenu.add(mntmNewMenuItem_2);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Musik an / aus");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (backgroundMusic) {
					playback.stop();
					backgroundMusic = false;
				} else {
					playback.play();
					backgroundMusic = true;
				}
			}
		});
		mnNewMenu.add(mntmNewMenuItem_3);
		mnNewMenu.add(mntmNewMenuItem);

		JSeparator separator = new JSeparator();
		mnNewMenu.add(separator);

		JMenu mnNewMenu_1 = new JMenu("?");
		menuBar.add(mnNewMenu_1);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Info");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"Autor: Yasir Dizman\nSchulprojekt in Fach PT;\nBerufsförderungswerk Berlin-Brandenburg e. V., Standort Mühlenbeck\nMusik: 'A beautiful Memory' by Dag Reinbott \nKostenlose GEMA-freie Musik: https://www.terrasound.de/gemafreie-musik-kostenlos-downloaden/ ",
						"Über Memory", JOptionPane.INFORMATION_MESSAGE, null);
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_1);

		JLabel jlabel1 = new JLabel("");
		jlabel1.setBounds(620, 78, 100, 560);
		frame.getContentPane().add(jlabel1);

		for (int i = 0; i < 16; i++) { // Deck wird mit Cover bedeckt
			btn[i].setIcon(cover);
			paired[i] = 0;
		}

		// bitte immer am ende der Methode lassen!!!!!!!!!!!!
		frame.setVisible(true); // Fesnter wird sichtbar

		for (int i = 0; i < 16; i++) { // Sorgt dafür, dass die vordefinierten Eigenschaften des Buttons entfernt
										// werden
			btn[i].setBorderPainted(false);
			btn[i].setContentAreaFilled(false);
			btn[i].setFocusPainted(false);
			btn[i].setOpaque(false);
			btn[i].setBorder(null);
			btn[i].setMargin(new Insets(0, -1, 0, -20));
			btn[i].setHorizontalAlignment(SwingConstants.LEFT);
		}

		jlabel1.setIcon(side);

		JLabel lblSpieler = new JLabel("Spieler 1");
		//lblSpieler.setBackground(Color.WHITE);
		lblSpieler.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
		lblSpieler.setForeground(Color.BLUE);
		lblSpieler.setBounds(10, 19, 236, 30);
		frame.getContentPane().add(lblSpieler);
		label[0] = lblSpieler;
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(10, 645, 710, 38);
		frame.getContentPane().add(progressBar);
		pbar[0] = progressBar;
		pbar[0].setBackground(Color.LIGHT_GRAY);
		pbar[0].setForeground(Color.PINK);
		
		JLabel lblSpieler2 = new JLabel("Spieler 1");
		lblSpieler2.setForeground(Color.BLUE);
		lblSpieler2.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
		lblSpieler2.setBounds(254, 19, 236, 30);
		frame.getContentPane().add(lblSpieler2);
		label[1] = lblSpieler2;
		

		reset();
	}
}
