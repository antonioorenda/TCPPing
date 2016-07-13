package com.java.corvus.tcpping;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Schedules tasks that send and receive messages and displays statistics.
*   
* See {@link #Pitcher(int port, int mps, int size, String hostname)} for more information.
*  
* @author Antonio Orenda
*/
public class Pitcher implements Runnable{

	private int port;
	private int mps;
	private int size;
	private String hostname;
	private Socket pitcherSocket;
	private int numberOfSentMessages = 0;
	private long timeOffset;
	private List<Integer> messageIds = new ArrayList<Integer>();
	
	//constants, integer is 4 bytes, long is 8 bytes
	private static final int INT_SIZE = 4;
	private static final int LONG_SIZE = 8;
	
	private static final Logger logger = LoggerFactory.getLogger("pitcher");
	
    /**
	* Crates pitcher constructor.
	* 
	* @param port (required) - TCP socket port used for connection
	* @param size (optional) - Message length
	* @param mps (optional) - Sending speed in 'messages per second'
	* @param hostname (required) - Machine name on which catcher is running
	*/
	public Pitcher(int port, int mps, int size, String hostname) {
	
	    this.port = port;
	    this.mps = mps;
	    this.size = size;
	    this.hostname = hostname;
	}
	
	@Override
	public void run(){
	    //calculates time offset when pitcher is started
	    try {
			timeOffset = Time.TimeOffsetNTP();
		} catch (IOException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
	    
	    Timer timer = new Timer();

    	timer.schedule(new SendMessage(), 1, 1000/mps);
    	
    	timer.schedule(new DisplayStatistics(), 1000, 1000);
	}
	
	/**
	 * Creates message. Send message to catcher. Receives message from catcher. 
	 * 
	 * @author Antonio Orenda
	 */
	private class SendMessage extends TimerTask{
		
	    private int id;
	    private DataOutputStream out;
	    private DataInputStream in;
	    private long pitcherTimestamp;
	    private long currentTimestamp;
	    private long catcherTimestamp;
        private Integer catcherMessageSize;
        private Integer catcherId;
	
	    @Override
	    public void run() {
	
	        try {                       
	        	
	        	pitcherSocket = new Socket(InetAddress.getByName(hostname), port);
		        out = new DataOutputStream(pitcherSocket.getOutputStream());
		        in = new DataInputStream(pitcherSocket.getInputStream());
	        	
	            synchronized(out){
	                 //send message size
	                 out.writeInt(size);
	
	                 //message id is same as number of the sent message
	                 id = numberOfSentMessages + 1;
	                 out.writeInt(id);
	                 messageIds.add(id);
	                 	         		 
	                 //get system timestamp and sync it with Carnet's server
	                 pitcherTimestamp = System.currentTimeMillis() + timeOffset;
	                 out.writeLong(pitcherTimestamp);

	                 //fill in the rest
	                 byte[] rest = new byte[size - 2 * INT_SIZE - LONG_SIZE]; //message size(default 300 bytes) - size(4 bytes) - message id(4 bytse) - timestamp(8 bytes)
	                 new Random().nextBytes(rest);
	                 out.write(rest);
	
	                 out.flush();
	             }
	            
	             numberOfSentMessages++;
	             
	             synchronized(in){
	                 catcherMessageSize = in.readInt();
	                 catcherId = in.readInt();
	                 catcherTimestamp = in.readLong();
	                 messageIds.remove(catcherId);
	             }
	             
	             //get system timestamp and sync it with Carnet's server
	             currentTimestamp = System.currentTimeMillis() + timeOffset;
	             
	             //Update statistics every time cycle finishes
	             TimeStats.updateStats(pitcherTimestamp, catcherTimestamp, currentTimestamp);
	             
	             logger.info("Sent message:     " + size + " " + id + " " + pitcherTimestamp + "...");
	             logger.info("Received message: " + catcherMessageSize + " " + catcherId + " " + catcherTimestamp + "...");
	             
	          }catch(IOException e) {
	             e.printStackTrace();
	             logger.error(e.toString());
	          } 
	
	    }
	
	}
	
	/**
	 * Displays statistics.
	 * 
	 * @author Antonio Orenda
	 */
	private class DisplayStatistics extends TimerTask {

		@Override
		public void run() {
			
		   DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		   Date date = new Date();
		   
		   System.out.println(dateFormat.format(date) + " | Rate " + TimeStats.messageCounter +"mps" + " | Total sent " + numberOfSentMessages);
		   System.out.println("Avg AB " + TimeStats.totalABPerSecond/TimeStats.messageCounter + " | Avg BA " + TimeStats.totalBAPerSecond/TimeStats.messageCounter + " | Avg ABA " 
		   + TimeStats.totalABAPerSecond/TimeStats.messageCounter + " | Max AB " + TimeStats.maxAB + " | Max BA " + TimeStats.maxBA + " | Max ABA " + TimeStats.maxABA + "\n");
		   
		   TimeStats.reset();
			   
		}
		
	}

}