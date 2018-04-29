

import java.awt.*;
import java.io.*;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import jm.util.*;

public class Song extends JPanel
{
	private String title;
	private String artist;
	private String album;
	private String songPath;
	private float[] songData;
	private String imagePath;
	private Image image;
	
	// constructor
	public Song (String title, String artist, String album, String songPath, String imagePath)
	{
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.songPath = songPath;
		this.imagePath = imagePath;
		this.songData= Read.audio(songPath);
		this.image = new ImageIcon(imagePath, "album cover").getImage();
	}

	// accessors
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public String getArtist() {
		return this.artist;
	}
	
	public void setAlbum(String album) {
		this.album = album;
	}
	
	public String getAlbum() {
		return this.album;
	}
	
	public void setSongPath(String songPath) {
		this.songPath = songPath;
	}
	
	public String getSongPath() {
		return this.songPath;
	}
	
	public void setSongData(float[] songData) {
		this.songData = songData;
	}
	
	public float[] getSongData() {
		return this.songData;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public String getImagePath() {
		return this.imagePath;
	}

	public void setImage(Image image) {
		this.image = image;
	}
	
	public Image getImage() {
		return this.image;
	}
	
	public void play() {
		Write.audio(this.songData, "mix.wav",2, 44100, 16);
		Play.au("mix.wav", false);  
	}
	
	public void stop() {
		Play.stopAudio();
	}
	
	public void paintComponent(Graphics g)
	{
		g.drawImage(image, 0,0, this);
	}
}
