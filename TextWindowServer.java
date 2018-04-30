import java.net.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

//From StackOverflow: @Piotr Kochański
class ClientThread extends Thread {
	String songInfo;
	InputStreamReader isr;
	OutputStreamWriter outWriter;
	BufferedReader reader;
	PrintWriter writer;
	Socket clientConn;
	ServerSocket serverSock;
	boolean connected = false;
	
    public ClientThread(Socket clientSocket) {
        this.clientConn = clientSocket;
    }

    public void run() {
	try {
    		System.out.println("Connection was successful");
		isr = new InputStreamReader(clientConn.getInputStream());
		reader = new BufferedReader(isr);
		outWriter = new OutputStreamWriter(clientConn.getOutputStream());
		writer = new PrintWriter(outWriter);
		connected = true;
	}catch (IOException e){e.printStackTrace();}	

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
}
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

	  public void run(){
	        serverSock = null;
	        clientConn = null;

	        try {
	            serverSock = new ServerSocket(3000);
	        } catch (IOException e) {
	            e.printStackTrace();

	        }
	        while (true) {
	            try {
	                clientConn = serverSock.accept();
	            } catch (IOException e) {
	                System.out.println("I/O error: " + e);
	            }
	            // new thread for a client
	            new ClientThread(clientConn).start();
	        }
	    }
	
	public static void main(String[] args)
	{
		TextWindowServer win = new TextWindowServer();
		Thread t = new Thread(win);
		t.start();
	}
}