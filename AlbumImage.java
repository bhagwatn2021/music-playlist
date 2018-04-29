import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class AlbumImage extends JPanel
{
	Song song; 
	
	public void paintComponent (Graphics g)
	{
		Image pic = new ImageIcon(song.getImagePath(), "cover").getImage();
		g.drawImage(pic, 0,0, this);
	}
	
	public AlbumImage (Song song)
	{
		this.song = song;
	}
}