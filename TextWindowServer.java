import java.net.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

public class TextWindowServer extends JFrame implements ActionListener, Runnable{
	JTextField text;
	JTextArea textArea;
	InputStreamReader isr;
	OutputStreamWriter outWriter;
	BufferedReader reader;
	PrintWriter writer;
	Socket clientConn;
	ServerSocket serverSock;
	boolean connected = false;
	public void go()
	{
		text= new JTextField("Enter text here");
		textArea = new JTextArea(10,20);
		text.addActionListener(this);
		this.getContentPane().add(BorderLayout.SOUTH, text);
		this.getContentPane().add(BorderLayout.CENTER, textArea);
		this.setSize(600, 600);
		this.setTitle("Text Window Server");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
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
			writer.println("This is a message from the server!!");
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
	public void actionPerformed(ActionEvent e)
	{
		if(connected)
		{
			writer.println(text.getText());
			writer.flush();
		}
		else
		{
			System.out.println("The server has not established a connection yet.");
		}
	}
	public static void main(String[] args)
	{
		TextWindowServer win = new TextWindowServer();
		win.go();
		Thread t = new Thread(win);
		t.start();
	}
}