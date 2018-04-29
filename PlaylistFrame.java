

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

class TextWindow extends JLabel implements ActionListener, Runnable
{
	JTextField text;
	JTextArea textArea;
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
		text.addActionListener(this);
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
				textArea = new JTextArea();
				this.textArea.setText(username +" is listening to " + title + " by " +  artist);
				this.add(textArea);
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
	private JLabel received;
	private JPanel south;
	private JPanel playButtons;
	private JTextArea songInfo;
	private ListNode currSong;
	private String songMessage;
	private AlbumImage cover;
	
	void go (int width, int height, Playlist playlist, JTextArea list, String username)
	{
		// set up initial frame
		this.setTitle("Music Player");
		this.setResizable(true);
		this.setSize(width, height);
		
		// get the content pane
		Container cPane = this.getContentPane();
		cPane.setLayout(new BorderLayout());
		
		// start current song at the front of playlist
		currSong = playlist.head;
	
		// initialize songInfo
		songInfo = new JTextArea();

		// show the first song information
		setSongInfo(playlist);

		TextWindow received = new TextWindow();
		received.go(username,currSong.song.getTitle(),currSong.song.getArtist());
		Thread t = new Thread(received);
		t.start();
		
		// show the first song's album cover
		cover = new AlbumImage(currSong.song);
		// play the first song
		currSong.song.play();
		
		// add the album cover to the content pane
		cPane.add(BorderLayout.CENTER,cover);
				
		// show the playlist on the right side of the frame
		cPane.add(BorderLayout.EAST, list);
		
		// filler message that will eventually be received over network

		// show the playlist on the right side of the frame
		cPane.add(BorderLayout.EAST, list);
		
		
		JPanel songPanel = new JPanel();
		// button to play ALL the songs in the playlist
		playAll = new JButton("Play All");
		// local class definition for the Action Listener for play
		class PlayAllAL implements ActionListener {
			public void actionPerformed (ActionEvent a) {
				// stop playing the current song
				if(currSong != null) {
					currSong.song.stop();
				}
				// start current song at the front of playlist
				currSong = playlist.head;
				while (currSong != null) {
					// change current song info
					received.go(username,currSong.song.getTitle(),currSong.song.getArtist());
					Thread t = new Thread(received);
					t.start();
					
					// change current song info
					songPanel.setLayout(new BoxLayout(songPanel,BoxLayout.Y_AXIS));
					songPanel.add(songInfo);
					songPanel.add(received);
					setSongInfo(playlist);
					// change current image
					cPane.remove(cover);
					cover = new AlbumImage(currSong.song);
					repaint();
					// add new image to panel
					cPane.add(BorderLayout.CENTER,cover);
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
				if(currSong != null) {
					currSong.song.stop();
				}

				// if at the end
				if (currSong==null) {
					// go back to the beginning
					currSong = playlist.head;
				}

				// move currSong to the next song
				currSong = currSong.next;
				
				received.go(username,currSong.song.getTitle(),currSong.song.getArtist());
				Thread t = new Thread(received);
				t.start();
				
				// change current song info
				songPanel.setLayout(new BoxLayout(songPanel,BoxLayout.Y_AXIS));
				songPanel.add(songInfo);
				songPanel.add(received);
				setSongInfo(playlist);

				// change current song info
				setSongInfo(playlist);
				// remove the current image
				cPane.remove(cover);

				// change current image
				cover = new AlbumImage(currSong.song);
				repaint();
				// add picture to the panel
				cPane.add(BorderLayout.CENTER,cover);
				// play the song
				currSong.song.play();
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
				if(currSong != null) {
					currSong.song.stop();
				}
				// pick a random number from 1 to # of songs
				int random = (int)((Math.random()*playlist.size)+1);
				// move currSong to the first node
				currSong = playlist.head;
				// move currSong to the node chosen by random
				for (int i=0; i<random; i++) {
					currSong = currSong.next;
				}
				// if at the end
				if (currSong==null) {
					// go back to the beginning
					currSong = playlist.head;
				}
				received.go(username,currSong.song.getTitle(),currSong.song.getArtist());
				Thread t = new Thread(received);
				t.start();
				
				// change current song info
				setSongInfo(playlist);
				songPanel.setLayout(new BoxLayout(songPanel,BoxLayout.Y_AXIS));
				songPanel.add(songInfo);
				songPanel.add(received);

				// change current song info
				setSongInfo(playlist);
				// remove the current image
				cPane.remove(cover);

				// change current image
				cover = new AlbumImage(currSong.song);
				repaint();
				// add picture to the panel
				cPane.add(BorderLayout.CENTER,cover);
				// play the song
				currSong.song.play();
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
		cPane.add(BorderLayout.NORTH,songInfo);
		
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
			currSong.song.play();
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
	
	public ListNode getCurrentSong()
	{
		return currSong;
	}
	
	public String getSongMessage ()
	{
		return songMessage;
	}
}
