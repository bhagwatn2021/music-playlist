/* Authors: Julia Bristow and Neel Bhagwat
 * Song class			
 * Purpose: to make a song class that stores cover art, title, artist, album, and songData.
 * Plays the specified song. 
 * 
 */

import java.awt.*;
import javax.imageio.*;
import java.io.*;
import jm.util.*;
import java.awt.image.*;

public class SongInfo
{
	private String title;
	private String artist;
	private String album;
	private String songPath;
	private float[] songData;
	private String imagePath;
	private BufferedImage image;
	
	// constructor
	public SongInfo (String title, String artist, String album, String songPath, String imagePath)
	{
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.songPath = songPath;
		this.imagePath = imagePath;
		this.songData= Read.audio(songPath);
	// From Stack Overflow: @Tafari
	// Creates a BufferedImage based on the given file path for an image
	try 
		{
	    this.image = ImageIO.read(new File(imagePath)); 
		} 
		catch (IOException e) 
		{
		    e.printStackTrace();
		} 
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

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public Image getImage() {
		return this.image;
	}
	
	public void play() {
		Write.audio(this.songData, "mix.wav",2, 44100, 16);
		Play.au("mix.wav", false);  
	}
}
