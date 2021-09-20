package module;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundMachine {
	private AudioInputStream audioInputStream;
	private Clip clip;

	public void Sound(String path) {
		// Get the audio from the file
		try {
			// Convert the file path string to a URL
			URL sound = getClass().getResource(path);
			// System.out.println(sound);

			// Get audio input stream from the file
			audioInputStream = AudioSystem.getAudioInputStream(sound);

			// Get clip resource
			clip = AudioSystem.getClip();

			// Open clip from audio input stream
			clip.open(audioInputStream);
			// Play clip
			clip.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	/* -- Method -- */

	public void play() {
		// Stop clip if it's already running
		if (clip.isRunning())
			stop();

		// Rewind clip to beginning
		clip.setFramePosition(0);

		// Play clip
		clip.start();
	}

	/**
	 * Stop the sound.
	 */
	public void stop() {
		clip.stop();
	}
}
