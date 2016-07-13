package com.java.corvus.tcpping;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Receives messages from clients and sends back responses. 
*   
* See {@link #Catcher(int port, String bind)} for more information.
*  
* @author Antonio Orenda
*/
public class Catcher implements Runnable{

	private int port;
	private String bind;
	private ServerSocket serverSocket;
	private long timeOffset;
	
	private static final Logger logger = LoggerFactory.getLogger("catcher");
	
	/**
	  * Catcher constructor.
	  * 
	  * @param port (required) - TCP socket port used for connection
	  * @param bind (required) - TCP socket bind address on which listener will run
	  */
	public Catcher(int port, String bind) {
	
	    this.port = port;
	    this.bind = bind;
	}
	
	@Override
	public void run() {
		
		logger.info("Catcher running");
	    System.out.println("Catcher running...\n");

	    try {
	    	serverSocket = new ServerSocket(port, 100, InetAddress.getByName(bind));
	    	
			timeOffset = Time.TimeOffsetNTP();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
	    
	    //never close server
	    while(true){
	         try
	         {              
	           new Thread(new CatcherHandler(serverSocket.accept())).start();
	        }
	        catch(SocketTimeoutException s){
	           s.printStackTrace();
	           logger.error(s.toString());
	           break;
	        }
	        catch(IOException e){
	           e.printStackTrace();
	           logger.error(e.toString());
	           break;
	        } 

	    } 
	}
	
	/**
	 * Handles received messages and sends responses back to clients.
	 * 
	 * @author Antonio Orenda
	 */
	private class CatcherHandler implements Runnable{   
	
	   Socket server;
	   DataOutputStream out;
	   DataInputStream in;
	   
	   //constants, integer is 4 bytes, long is 8 bytes
	   private static final int INT_SIZE = 4;
	   private static final int LONG_SIZE = 8;
	
	   public CatcherHandler(Socket server) {
	
	       this.server = server;
	       
	       try {
	           in = new DataInputStream(server.getInputStream());
	           out = new DataOutputStream(server.getOutputStream());
	
	       } catch (IOException e) {
	           e.printStackTrace();
	           logger.error(e.toString());
	       }
	
	   }
	
	   @Override
	   public void run() {
		   
	       try{
               Integer pitcherMessageSize = in.readInt();
               Integer pitcherId = in.readInt();
               long pitcherTimestamp = in.readLong();

               //message id and size are sent back
               out.writeInt(pitcherMessageSize);
               out.writeInt(pitcherId);

               //get system timestamp and sync it with Carnet's server
               long catcherTimestamp = System.currentTimeMillis() + timeOffset;
               out.writeLong(catcherTimestamp);

               //fill in the rest
               byte[] rest = new byte[pitcherMessageSize - 2 * INT_SIZE - LONG_SIZE]; //message size(default 300 bytes) - size(4 bytes) - message id(4 bytes) - timestamp(8 bytes)
               new Random().nextBytes(rest);
               out.write(rest);

               out.flush();
               
               logger.info("Received message: " + pitcherMessageSize + " " + pitcherId + " " + pitcherTimestamp + "...");
               logger.info("Sent message:     " + pitcherMessageSize + " " + pitcherId + " " + catcherTimestamp + "...");
	
	       } catch (IOException e) {
	           e.printStackTrace();
	           logger.error(e.toString());
	       } 
	       
	   }
	   
	} 

}