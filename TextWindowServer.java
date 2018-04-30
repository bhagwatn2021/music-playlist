import java.net.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

public class TextWindowServer extends JPanel implements Runnable{
	
	String songInfo;
	InputStreamReader isr;
	OutputStreamWriter outWriter;
	BufferedReader reader;
	PrintWriter writer;
	Socket clientConn;
	ServerSocket serverSock;
	boolean connected = false;
	String message;
	
	public void go()
	{
	//	text.addActionListener(this);
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
			while(reader.readLine() != null)
			{
				System.out.println("Receiving " + reader.readLine());
				songInfo = reader.readLine();
				
				if(connected)
				{
					System.out.println("Sending " + songInfo);
					writer.println(songInfo);
					writer.flush();
				}
				else
				{
					System.out.println("The server has not established a connection yet.");
				
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
/*	public void actionPerformed(ActionEvent a)
	{
	
			if(connected)
				{
					System.out.println("Sending " + text.getText());
					writer.println(text.getText());
					writer.flush();
				}
				else
				{
					System.out.println("The server has not established a connection yet.");
				
				}
	} */
	public static void main(String[] args)
	{
		TextWindowServer win = new TextWindowServer();
		win.go();
		Thread t = new Thread(win);
		t.start();
	}
}