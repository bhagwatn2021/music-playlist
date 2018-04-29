import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddSongFrame extends JFrame 
{
	// elements needed for Song
	private JTextField songTitle;
	private JTextField artistName;
	private JTextField albumName;
	private JTextField songfilePath;
	private JTextField coverartPath; 
	private JTextField inputUsername;
	private String username;
	
	// buttons
	private JButton addSong;
	private JButton goToPlaylist;
	
	// list
	private JTextArea songList; 
	private Playlist playlist;
	
	
	void go (int width, int height)
	{	
		// create playlist
		playlist = new Playlist();
		// create area for printed list
		songList = new JTextArea();
		
		// set up initial frame
		this.setTitle("Music Player");
		this.setResizable(true);
		this.setSize(width, height);
		
		// get the content pane
		Container cPane = this.getContentPane();
		cPane.setLayout(new BorderLayout());
		
		// put instructions in each JTexField
		inputUsername = new JTextField("Enter your username here");
		songTitle = new JTextField("Enter the song title here");
		artistName = new JTextField("Enter the artist name here");
		albumName = new JTextField("Enter the album name here");
		songfilePath = new JTextField("Enter the path for the song file here");
		coverartPath = new JTextField("Enter the path for the album cover art here");
		
		// add all the text fields to the center of addPanel
		// create a new panel
		JPanel inputs = new JPanel();
		inputs.setLayout(new BoxLayout(inputs,BoxLayout.Y_AXIS));
		// add each JTextField to the panel
		inputs.add(inputUsername);
		inputs.add(songTitle);
		inputs.add(artistName);
		inputs.add(albumName);
		inputs.add(songfilePath);
		inputs.add(coverartPath);
		// add this panel to the content pane
		cPane.add(inputs);
		
		
		// make the addSong button add the song entered by user to the playlist
		addSong = new JButton("Add Song");
		
		// Local class definition for button Action Listener
		// adds song with the info provided to the playlist
		class AddSongAL implements ActionListener {
			public void actionPerformed (ActionEvent a) {
				// get the text entered in each field in Song
				String title = songTitle.getText();
				String artist = artistName.getText();
				String album = albumName.getText();
				String songPath = songfilePath.getText();
				String artPath = coverartPath.getText();
				// get username input
				username = inputUsername.getText();
				// make a new Song object
				Song newSong = new Song(title,artist,album,songPath,artPath);
				// add song to playlist
				playlist.add(newSong);
				// add song title to text list
				songList.append(newSong.getTitle()+" \n");
				// test that songs are being added
				System.out.println(newSong.getTitle()+" was added!");
			}
		} // end of local class AddSongAL
		
		AddSongAL songAL = new AddSongAL();
		// add Action Listener to addSong button
		addSong.addActionListener(songAL);
		
		
		// make the button to view the playlist 
		goToPlaylist = new JButton("View Playlist");
		// local class definition for the goTo Action Listener
		// switch to the playlist frame 
		class GoToPlaylistAL implements ActionListener {
			public void actionPerformed (ActionEvent e) {
				// make a new frame that shows the music info
				PlaylistFrame musicFrame = new PlaylistFrame();
				musicFrame.go(500,500,playlist,songList,username);
			}
		} // end of local class goToPlaylistAL
		
		GoToPlaylistAL goToAL = new GoToPlaylistAL();
		// add Action Listener to the goTo button
		goToPlaylist.addActionListener(goToAL);
		
		
		// add buttons to a panel
		JPanel buttons = new JPanel();
		buttons.add(addSong);
		buttons.add(goToPlaylist);
		// add this panel to the content pane
		cPane.add(BorderLayout.SOUTH,buttons);
		
		// exit operation
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// make visible
		this.setVisible(true);
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public static void main (String[] args) 
	{
		AddSongFrame addFrame = new AddSongFrame();
		addFrame.go(500,500);
	}
}
