

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

class TextWindow extends JPanel implements ActionListener, Runnable
{
	JTextField text;
	Socket sock;
	InputStreamReader isr;
	OutputStreamWriter outWriter;
	BufferedReader reader;
	PrintWriter writer;
	String[] songData; 
	private String title;
	private String artist; 
	private String username;
	
	public void go(String username, String title, String artist)
	{
		setUpNetworking();
		this.username = username;
		this.title = title;
		this.artist = artist;
		text = new JTextField(username + "," + title + "," + artist);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawString(username +" is listening to " + title + " by " +  artist, 10, 20);
	}
	
	public void setUpNetworking()
	{
		try
		{
			sock = new Socket("127.0.0.1",8000);
			isr = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(isr);
			writer = new PrintWriter(sock.getOutputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			songData =reader.readLine().split(" ",3);
			while(reader.readLine() != null){
				this.username = songData[0];
				this.title = songData[1];
				this.artist = songData[2];
				songData = reader.readLine().split(",", 3);
			}
		} catch(IOException e) {}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		writer.println(text.getText());
		writer.flush();
	}
	
}

public class PlaylistFrame extends JFrame
{
	private JButton goToAdd;
	private JButton playAll;
	private JButton playNext;
	private JButton playRandom;
	private JPanel south;
	private JPanel playButtons;
	private JTextArea songInfo;
	private ListNode currSong;
	private String songMessage;
	private Image cover;
	private String username;
	
	void go (int width, int height, Playlist playlist, JTextArea list, String username)
	{
		// set up initial frame
		this.setTitle("Music Player");
		this.setResizable(true);
		this.setSize(width, height);
		this.username = username;
		// start current song at the front of playlist
		currSong = playlist.head;
		// initialize songInfo
		songInfo = new JTextArea();
		// show the first song information
		setSongInfo(playlist);
		// show the first song's album cover
		cover = currSong.song.getImage();
		repaint();
		// play the first song
		currSong.song.play();
		
		// get the content pane
		Container cPane = this.getContentPane();
		cPane.setLayout(new BorderLayout());
				
		// show the playlist on the right side of the frame
		cPane.add(BorderLayout.EAST, list);
		
		// filler message that will eventually be received over network
		TextWindow win = new TextWindow();
		
		// button to play ALL the songs in the playlist
		playAll = new JButton("Play All");
		// local class definition for the Action Listener for play
		class PlayAllAL implements ActionListener {
			public void actionPerformed (ActionEvent a) {
				// stop playing the current song
				currSong.song.stop();
				while (currSong != null) {
					// change current song info
					setSongInfo(playlist);
					// change current image
					cover = currSong.song.getImage();
					repaint();
					// play the song in currNode
					currSong.song.play();
					// advance currSong
					currSong = currSong.next;
				}
			}
		}
		// create the action listener
		PlayAllAL playAllAL = new PlayAllAL();
		// add Action Listener to play button
		playAll.addActionListener(playAllAL);
		
		
		// button to play the next song in the playlist
		playNext = new JButton("Play Next Song");
		// local class definition for the Action Listener for play
		class PlayNextAL implements ActionListener {
			public void actionPerformed (ActionEvent a) {
				// stop playing the current song
				currSong.song.stop();
				// move currSong to the next song
				currSong = currSong.next;
				// change current song info
				setSongInfo(playlist);
				
				win.go(username,currSong.song.getTitle(),currSong.song.getArtist());
				Thread t = new Thread(win);
				t.start();
				
				// change current image
				cover = currSong.song.getImage();
				repaint();
				// play the song
				playSong(playlist);
			}
		}
		// create the action listener
		PlayNextAL playNextAL = new PlayNextAL();
		// add Action Listener to play button
		playNext.addActionListener(playNextAL);
		
		
		// button to play a random song in the playlist
		playRandom = new JButton("Play Random Song");
		// local class definition for the Action Listener for playRandom
		class PlayRandomAL implements ActionListener {
			public void actionPerformed (ActionEvent a) {
				// stop playing the current song
				currSong.song.stop();
				// pick a random number from 1 to # of songs
				int random = (int)((Math.random()*playlist.size)+1);
				// move currSong to the first node
				currSong = playlist.head;
				// move currSong to the node chosen by random
				for (int i=0; i<random; i++) {
					currSong = currSong.next;
				}
				// change current song info
				setSongInfo(playlist);
				// change current image
				cover = currSong.song.getImage();
				repaint();
				// play the song
				playSong(playlist);
			}
		}
		// create the action listener
		PlayRandomAL playRandomAL = new PlayRandomAL();
		// add Action Listener to play button
		playRandom.addActionListener(playRandomAL);
		
		
		// button to go back to the Add Songs interface
		goToAdd = new JButton("Make A New Playlist");
		// local class definition for an Action Listener for goToAdd
		class AddMoreAL implements ActionListener {
			public void actionPerformed(ActionEvent a) {
				AddSongFrame addFrame = new AddSongFrame();
				addFrame.go(500,500);
			}
		}
		// create action listener
		AddMoreAL goToAddAL = new AddMoreAL();
		// add action listener to the goToAdd button
		goToAdd.addActionListener(goToAddAL);
		
		
		// new panel to hold play buttons
		playButtons = new JPanel();
		// set Flow Layout
		playButtons.setLayout(new FlowLayout());
		// add buttons to panel
		playButtons.add(playAll);
		playButtons.add(playNext);
		playButtons.add(playRandom);
		
		JPanel songPanel = new JPanel();
		songPanel.setLayout(new BoxLayout(songPanel,BoxLayout.Y_AXIS));
		songPanel.add(songInfo);
		songPanel.add(win);
		
		// new panel to put all buttons and message at the bottom
		south = new JPanel();
		// give the south panel a Box Layout
		south.setLayout(new BoxLayout(south,BoxLayout.Y_AXIS));
		// add play buttons to south panel
		south.add(playButtons);
		// add "go back" button to south panel
		south.add(goToAdd);
		// add panel with the message and button to the South area
		cPane.add(BorderLayout.SOUTH, south);
		
		// add the song information to the content pane
		cPane.add(BorderLayout.NORTH,songPanel);
		
		// put image in a panel
		JPanel pic = new JPanel();
		//****************************************************************************************
		
		// exit operation
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		// make visible
		this.setVisible(true);
	}
	
	public void playSong (Playlist playlist)
	{
		// if at the end
		if (currSong==null) {
			// go back to the beginning
			currSong = playlist.head;
			// play that
			currSong.song.play();
		}
		// else if not at the end
		else {
			// play the current song
		}
	}
	
	public void setSongInfo (Playlist playlist) 
	{
		// if the end has been reached
		if (currSong==null) {
			// go back to the beginning
			currSong = playlist.head;
			// set the song info 
			songInfo.setText("Current Song: " + currSong.song.getTitle() +"\nArtist: " + currSong.song.getArtist() 
																			+ "\nAlbum: " + currSong.song.getAlbum());
		}
		// else if not at the end
		else {
			// set the song info 
			songInfo.setText("Current Song: " + currSong.song.getTitle() +"\nArtist: " + currSong.song.getArtist() 
																			+ "\nAlbum: " + currSong.song.getAlbum());
		}
		// message to be used in server (sent back and forth)
		songMessage = currSong.song.getTitle() + " by " + currSong.song.getArtist();
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponents(g);
		g.drawImage(cover, 0,0, this);
	}
	
	public ListNode getCurrentSong()
	{
		return currSong;
	}
	
	public String getSongMessage ()
	{
		return songMessage;
	}
}
