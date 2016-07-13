package com.java.corvus.tcpping;

import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.*;

public class TimeTest {

	@Test
	public void time(){
		
		long timeOffset = 0L;
		
		try {
			timeOffset = Time.TimeOffsetNTP();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertNotNull(timeOffset);
	}
	
}
