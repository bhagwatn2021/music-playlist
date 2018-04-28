/*
 * @authors Julia Bristow and Neel Bhagwat
 * 
 */
import jm.audio.*;
import jm.JMC;
import jm.music.*;
import jm.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.io.*;

public class PlayListFrame extends JFrame
{
	JTextArea songInfo;
	JButton playButton; 
	JButton playNext;
	JPanel play;
	LinkedList<Song> playlist;
	int randomSongIndex; 
	int songIndex = 0;
	private BufferedImage coverArt;
	
	class CoverPanel extends JPanel {

		    public void paintComponent (Graphics g) 
		    {
			super.paintComponent (g);
			
			// Draws "hello world" at location 100,100

			 g.drawImage (playlist.get(songIndex).getImage(),100, 100, null);
		    }
		  
		}// End of class NewPanel
	
	public void go(int width, int height, LinkedList<Song> playlist)
	{
		this.playlist = playlist;
		
		playButton = new JButton("Play Random");
		playNext = new JButton("Next");
		play = new JPanel();
		play.setLayout(new BoxLayout(play,BoxLayout.X_AXIS));
		play.add(playButton);
		songInfo = new JTextArea(20,20);
		songInfo.setText("Now playing: ");
		
		class PlayRandom implements ActionListener {
			public void actionPerformed (ActionEvent a) {
				randomSongIndex = (int)Math.random() * playlist.size();
				CoverPanel image = new CoverPanel();
			//	repaint();
				playlist.get(randomSongIndex).play();
				System.out.println(randomSongIndex);
			}
		}
		PlayRandom random = new PlayRandom();
		playButton.addActionListener(random);
		
		play.add(playNext);
		class PlayNext implements ActionListener {
			public void actionPerformed (ActionEvent a) {
				if(playlist.get(songIndex+1) != null) {
					songIndex+=1;
		/*			CoverPanel image = new CoverPanel();
					repaint(); */
					playlist.get(songIndex).play();
				}
				else {
					songInfo.setText("Reached the end of the playlist");
					repaint();
					playlist.get(songIndex).play();
				}
			}
		}
		
		PlayNext next = new PlayNext();
		playNext.addActionListener(next);
		
		randomSongIndex = (int)Math.random() * playlist.size();
		this.getContentPane().add(BorderLayout.SOUTH, play);
		this.getContentPane().add(BorderLayout.CENTER, songInfo);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800,800);
		this.setResizable(true);
		this.setVisible(true);
	}
}