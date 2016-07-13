package com.java.corvus.tcpping;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

/**
 * Used for clock synchronization. Calculates time offset based on carnet's NTP server.
 * 
 * @author Antonio Orenda
 */
public class Time {
	
	/**
	 * Calculates time offset based on carnet's NTP server.
	 * 
	 * @return long offset - clock offset in milliseconds
	 * @throws IOException
	 */
    public static long TimeOffsetNTP() throws IOException{
    	
        String timeServer = "zg1.ntp.carnet.hr";
        
        NTPUDPClient timeClient = new NTPUDPClient();
        timeClient.setDefaultTimeout(1000);
        
        InetAddress inetAddress;
        TimeInfo timeInfo = null;

		inetAddress = InetAddress.getByName(timeServer);
		timeInfo = timeClient.getTime(inetAddress);
        
        timeInfo.computeDetails();
        
        timeClient.close();
                        
        return timeInfo.getOffset();
    }
}