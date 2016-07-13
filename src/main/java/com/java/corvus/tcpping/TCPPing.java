package com.java.corvus.tcpping;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Parses console line arguments and creates pitcher and catcher.
*   
* See {@link #constructOptions(Options options)} for more information.
*  
* @author Antonio Orenda
*/
 public class TCPPing { 
	//Maximum and minimum message size in bytes
	private static final int MAX_SIZE = 3000;
	private static final int MIN_SIZE = 50;
	
	//Mutual arguments
	private static int port;
	
	//Catcher arguments
	private static String bind;
	
	//Pitcher arguments
	private static int mps = 1; //optional
	private static int size = 300; //optional
	private static String hostname;
	
	private static final Logger logger = LoggerFactory.getLogger(TCPPing.class);

	public static void main(final String[] args) {
		
		if(args.length == 0){
			logger.info("No arguments provided!");
			throw new IllegalArgumentException("You need to provide arguments!");
		}
		
		//Console line arguments parser
	    CommandLineParser parser = new DefaultParser();
	    Options options = new Options();
	    CommandLine commandLine = null;
	    
	    //Construct parser options
	    constructOptions(options);

		try {
			commandLine = parser.parse(options, args);
			
			//if catcher
			if(commandLine.hasOption("c")){
				
				validateCatcherArguments(commandLine);
				
				(new Thread(new Catcher(port, bind))).start();
			}
			
			//if pitcher
			if(commandLine.hasOption("p")){
			
				validatePitcherArguments(commandLine);
				
				(new Thread(new Pitcher(port, mps, size, hostname))).start();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					logger.error(e.toString());
				}
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
	}
	
	/**
	 * Constructs console line options.
	 * 
	 * @param options
	 */
	private static void constructOptions(Options options) {
		//Modes, no arguments
	    Option pitcherMode = new Option("p", "pitcher", false, "Pitcher mode");
		Option catcherMode = new Option("c", "catcher", false, "Catcher mode");
	    
		//Modes option group, one of the group options is required
	    OptionGroup modes = new OptionGroup();
	    modes.addOption(pitcherMode);
	    modes.addOption(catcherMode);
	    modes.setRequired(true);
	    
	    options.addOptionGroup(modes);
	    
	    //Other options
	    options.addOption("port", "port", true, "TCP socket port used for connection");
		options.addOption("bind", "bind", true, "TCP socket bind address on which listener will run");
		options.addOption("mps", "mps", true, "Sending speed in 'messages per second'");
		options.addOption("size", "size", true, "Message length");
	}
	
	/**
	 * Validates if pitcher arguments are correct.
	 * 
	 * @param cmd
	 */
	private static void validatePitcherArguments(CommandLine cmd) {
		
		//list containing arguments without option, should contain only hostname
		List<String> arguments = cmd.getArgList();
		
		//only <hostname> can be without option, if there is more than 1 leftover argument throw an exception
		if(arguments.size() > 1) {
			logger.error("Invalid arguments " + arguments.toString());
			throw new IllegalArgumentException("Invalid arguments");
		}
		
		//port and hostname are required options, mps and size are optional
		if(cmd.hasOption("port") && arguments.size() == 1){
			
			port = Integer.parseInt(cmd.getOptionValue("port"));

			if(port < 0 || port > 65535) {
				logger.error("Invalid port " + port);
				throw new IllegalArgumentException("Invalid port!");
			}
			if(port > 0 && port < 1024) {
				logger.error("Reserved port " + port);
				throw new IllegalArgumentException("Reserved port!");
			}
			
			if(cmd.hasOption("mps")){
				mps = Integer.parseInt(cmd.getOptionValue("mps"));
			}
			
			if(cmd.hasOption("size")){
				size = Integer.parseInt(cmd.getOptionValue("size"));
			}
			
			if(size < MIN_SIZE || size > MAX_SIZE){
				logger.error("Invalid message size input " + size);
				throw new IllegalArgumentException("Invalid size! (50 < size < 3000)");
			}
			
			hostname = arguments.get(0);
			
			logger.info("Pitcher run with options: -port " + port + " -mps " + mps + " -size " + size + " " + hostname);
			
		}
		else{
			logger.error("Invalid arguments " + arguments.toString());
			throw new IllegalArgumentException("Invalid arguments!");
		}
	}
	
	/**
	 * Validates if catcher arguments are correct.
	 * 
	 * @param cmd 
	 */
	private static void validateCatcherArguments(CommandLine cmd) {
		
		//list containing arguments without option, should count only hostname
		List<String> arguments = cmd.getArgList();
		
		//bind and port are required options
		if(cmd.hasOption("bind") && cmd.hasOption("port")){
			
			bind = cmd.getOptionValue("bind");
				
			port = Integer.parseInt(cmd.getOptionValue("port"));
			
			if(port < 0 || port > 65535){
				logger.error("Invalid port " + port);
				throw new IllegalArgumentException("Invalid port!");
			}
			if(port > 0 && port < 1024){
				logger.error("Reserved port " + port);
				throw new IllegalArgumentException("Reserved port!");
			}
			
			logger.info("Catcher run with options: -bind " + bind + " -port " + port);
			
		}
		else{
			logger.error("Invalid arguments " + arguments.toString());
			throw new IllegalArgumentException("Invalid arguments!");
		}
	}
}