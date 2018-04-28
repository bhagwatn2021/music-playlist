import java.awt.*;
import java.net.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.io.*;

import javax.swing.*;
public class TextWindow extends JFrame implements ActionListener
{
	JTextField text;
	JTextArea textArea;
	Socket sock;
	InputStreamReader isr;
	OutputStreamWriter outWriter;
	BufferedReader reader;
	PrintWriter writer;
	
	public void go()
	{
		text= new JTextField("Enter text here");
		textArea = new JTextArea(10,20);
		textArea.setEditable(false);
		text.addActionListener(this);
		this.getContentPane().add(BorderLayout.SOUTH, text);
		this.getContentPane().add(BorderLayout.CENTER, textArea);
		this.setTitle("Text Window Client");
		this.setSize(600, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		setUpNetworking();
		//text.setFont(Font.createFont(22, FON));
		
	}
	public void setUpNetworking()
	{
		try
		{
			sock = new Socket("127.0.0.1",8000);
			isr = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(isr);
			writer = new PrintWriter(sock.getOutputStream());
			String message =reader.readLine();
			while(message!=null)
			{
				textArea.append("Message sent from server to client: "+message+"\n");
				message= reader.readLine();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	public void actionPerformed(ActionEvent e)
	{
		System.out.println("Inside Action Performed for textbox");
		writer.println(text.getText());
		writer.flush();
	}
	public static void main(String[] args)
	{
		TextWindow win = new TextWindow();
		win.go();
	}
}