import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

class TextWindowServer implements Runnable{
	JTextArea textArea;
	String message;
	InputStreamReader isr;
	OutputStreamWriter outWriter;
	BufferedReader reader;
	PrintWriter writer;
	Socket clientConn;
	ServerSocket serverSock;
	String[] songData; 
	//information of songs (to be stored in songData)
	private String sendTitle, sendArtist, sendUsername, sendAlbum;
	private String receiveTitle, receiveArtist, receiveUsername, receiveAlbum;
	boolean connected = false;

	public void go()
	{
		textArea = new JTextArea();
		setUpNetworking();
			
	}
	public void setUpNetworking()
	{
		try
		{
			serverSock = new ServerSocket(8000);
			clientConn =serverSock.accept();
			System.out.println("Connection was successful");
			isr = new InputStreamReader(clientConn.getInputStream());
			reader = new BufferedReader(isr);
			outWriter = new OutputStreamWriter(clientConn.getOutputStream());
			writer = new PrintWriter(outWriter);
			connected = true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}	
	}
	public void run()
	{
		try
		{
			String albumInfo=reader.readLine();
			while(albumInfo!= null)
			{
				textArea.append(albumInfo+"\n");
				albumInfo=reader.readLine();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public void send(String username, String title, String artist, String album)
	{
		
		if(connected)
		{
			writer.println(this.sendUsername + "," + this.sendTitle + "," + this.sendArtist + "," + this.sendAlbum);
			writer.flush();
		}
		else
		{
			System.out.println("The server has not established a connection yet.");
		}
	}
}
public class PlaylistFrameServer extends JFrame
{
	private JButton goToAdd,playAll, playNext, playRandom;
	private JTextArea received;
	private JLabel songInfo;
	private JPanel south, playButtons;
	private ListNode currSong;
	private String songMessage;
	private AlbumImage cover;
	
	void go (int width, int height, Playlist playlist, JTextArea list, String username)
	{
		// set up initial frame
		this.setTitle("Music Player Server");
		this.setResizable(true);
		this.setSize(width, height);
		
		// get the content pane
		Container cPane = this.getContentPane();
		cPane.setLayout(new BorderLayout());
		
		// start current song at the front of playlist
		currSong = playlist.head;
	
		JPanel songPanel = new JPanel();
		songPanel.setLayout(new BoxLayout(songPanel,BoxLayout.Y_AXIS));
		
		// initialize songInfo
		songInfo = new JLabel();
		songPanel.add(songInfo);
		// show the first song information
		setSongInfo(playlist);

		TextWindowServer client = new TextWindowServer();
		client.go();
		
		received = client.textArea;
		Thread t = new Thread(client);
		t.start();
		
		client.send(username,currSong.song.getTitle(),currSong.song.getArtist(),currSong.song.getAlbum());
		
		songPanel.add(received);
		// show the first song's album cover
		cover = new AlbumImage(currSong.song);
		
		// add song information panel to the north of the frame
		cPane.add(BorderLayout.NORTH,songPanel);
				
		// play the first song
		playSong(playlist);
		
		// add the album cover to the content pane
		cPane.add(BorderLayout.CENTER,cover);
				
		// show the playlist on the right side of the frame
		cPane.add(BorderLayout.EAST, list);

		// show the playlist on the right side of the frame
		cPane.add(BorderLayout.EAST, list);
		
		
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
					client.send(username,currSong.song.getTitle(),currSong.song.getArtist(),currSong.song.getAlbum());
				/*	Thread t = new Thread(received);
					t.start(); */
					
					// change current song info
					setSongInfo(playlist);

					// change current image
					cPane.remove(cover);
					cover = new AlbumImage(currSong.song);
					cover.repaint();
					songInfo.repaint();
					received.repaint();
					// add new image to panel
					cPane.add(BorderLayout.CENTER,cover);
					// play the song in currNode
					playSong(playlist);
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
				if (currSong == playlist.tail) {
					// go back to the beginning
					currSong = playlist.head;
				}

				else{
					// move currSong to the next song
					currSong = currSong.next;
				}
				
				// change current song info
				client.send(username,currSong.song.getTitle(),currSong.song.getArtist(),currSong.song.getAlbum());
				setSongInfo(playlist);

				// change current song info
				setSongInfo(playlist);
				// remove the current image
				cPane.remove(cover);

				// change current image
				cover = new AlbumImage(currSong.song);
				cover.repaint();
				songInfo.repaint();
				received.repaint();
				// add picture to the panel
				cPane.add(BorderLayout.CENTER,cover);
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
				client.send(username,currSong.song.getTitle(),currSong.song.getArtist(),currSong.song.getAlbum());
				
				// change current song info
				setSongInfo(playlist);

				// change current song info
				setSongInfo(playlist);
				// remove the current image
				cPane.remove(cover);

				// change current image
				cover = new AlbumImage(currSong.song);
				cover.repaint();
				songInfo.repaint();
				received.repaint();
				// add picture to the panel
				cPane.add(BorderLayout.CENTER,cover);
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
		playButtons.add(goToAdd);
		
		// new panel to put all buttons and message at the bottom
		south = new JPanel();
		// give the south panel a Box Layout
		south.setLayout(new BoxLayout(south,BoxLayout.Y_AXIS));
		// add play buttons to south panel
		south.add(playButtons);
		
		// add panel with the message and button to the South area
		cPane.add(BorderLayout.SOUTH, south);
		
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
