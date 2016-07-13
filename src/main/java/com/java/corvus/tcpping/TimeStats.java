package com.java.corvus.tcpping;

/**
* Calculates statistics. Statistics are displayed in pitcher.
*   
* @author Antonio Orenda
*/
public class TimeStats {
	//calculated for messages sent previous second
	public static long totalABPerSecond = 0;
	public static long totalBAPerSecond = 0;
	public static long totalABAPerSecond = 0;
	
	//maximum times are calculated for all send messages, not only for messages sent previous second
	//maxAB - maximum time needed for message to come from pitcher to catcher
	public static long maxAB = 0;
	//maximum time needed for message to come from catcher to pitcher
	public static long maxBA = 0;
	//maxABA - maximum time needed for message to finish cycle
	public static long maxABA = 0;
	//used for calculating statistics - messages aren't always sent at specified rate due to network latency, counter increases every time message is sent
	public static int messageCounter = 0;
	
	/**
	 * Updates statistics every time message finishes cycle.
	 * 
	 * @param pitcherStartTime - time pitcher sends message
	 * @param catcherTime - time catcher sends back response
	 * @param pitcherEndTime - time response comes to pitcher
	 */
	public static void updateStats(long pitcherStartTime, long catcherTime, long pitcherEndTime){
		
		totalABPerSecond += catcherTime - pitcherStartTime;
		totalBAPerSecond += pitcherEndTime -catcherTime;
		totalABAPerSecond += pitcherEndTime - pitcherStartTime;
		
		if(catcherTime - pitcherStartTime > maxAB){
			maxAB = catcherTime - pitcherStartTime;
		}
		
		if(pitcherEndTime -catcherTime > maxBA){
			maxBA = pitcherEndTime -catcherTime;
		}
		
		if(pitcherEndTime - pitcherStartTime > maxABA){
			maxABA = pitcherEndTime - pitcherStartTime;
		}
		
		messageCounter++;
	}
	
	/**
	 * Resets all statistics that are calculated every second.
	 */
	public static void reset(){
		
		totalABPerSecond = 0;
		totalBAPerSecond = 0;
		totalABAPerSecond = 0;
		messageCounter = 0;
	}
	
}