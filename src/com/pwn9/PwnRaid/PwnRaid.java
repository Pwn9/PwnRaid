package com.pwn9.PwnRaid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Raid;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PwnRaid extends JavaPlugin 
{
	// For convenience, a reference to the instance of this plugin
	public static PwnRaid instance;
	
	// Init vars
	public static File dataFolder;
	public final Logger logger = Logger.getLogger("Minecraft.PwnRaid");   	
	public static List<String> enabledWorlds;
	public static Boolean logEnabled;
	
	// Tracking Raids - there can be more than one (for now we are actually blocking that from happening)
	//TODO: public static List<Raid> currentRaids;
	
	// track current raid?
	public static Raid currentRaidTracker;
	
	public static String[] ravagerBlocks = new String[] {"DOOR", "FENCE", "WALL", "GATE", "PLANK", "LOG", "SIGN", "CHEST", "GLASS", "BRICK", "COBBLE", "SAND", "DIRT", "GRASS", "CROP", "MELON", "GRANITE", "DIORITE", "ANDESTITE", "PURPUR", "WOOL"};
	
	// Other vars
	public static int currentOmenLevel = 0; 
	public static Boolean raidInProgress = false;
	public static int currentWaveNumber = 0;
	public static long currentRaidBeginTime;
	public static long currentRaidEndTime;
	
	static Random randomNumberGenerator = new Random();
	public static PluginDescriptionFile pdfFile;
	
	@Override
	public void onEnable() 
	{
    	instance = this;
    	
		this.saveDefaultConfig();
    	
    	// Init Listener
    	new RingBellListener(this);
    	new RaidListener(this);
    	new BombListener(this);
    	
    	// Get Data Folder
    	PwnRaid.dataFolder = getDataFolder();
    	
    	// Load Config File
    	this.loadConfig();
    	
    	// Load plugin.yml
    	PwnRaid.pdfFile = this.getDescription(); //Gets plugin.yml
    	    		
		// Start Metrics
		Metrics metricslite = new Metrics(this, 6139);
		
		if (PwnRaid.logEnabled)
		{
			PwnRaid.logToFile(PwnRaid.pdfFile.getName() + " version " + PwnRaid.pdfFile.getVersion() + " [enabled]");
		}	
	}
	
	public void onDisable() 
	{
		if (PwnRaid.logEnabled)
		{		
			PwnRaid.logToFile(PwnRaid.pdfFile.getName() + " version " + PwnRaid.pdfFile.getVersion() + " [disabled]");
		}	
	}	
	
	// Check enabled worlds list and return bool
	public static boolean isEnabledIn(String world) 
	{
		return enabledWorlds.contains(world);
	}	
	
	// Load all of our config file
	public void loadConfig() 
	{
		PwnRaid.enabledWorlds = getConfig().getStringList("enabled_worlds");	
		PwnRaid.logEnabled = getConfig().getBoolean("debug_log");
	}	
	
	// Debug logging
    public static void logToFile(String message) 
    {   
	    	try 
	    	{		    
			    if(!dataFolder.exists()) 
			    {
			    	dataFolder.mkdir();
			    }
			     
			    File saveTo = new File(dataFolder, "pwnraid.log");
			    if (!saveTo.exists())  
			    {
			    	saveTo.createNewFile();
			    }
			    
			    FileWriter fw = new FileWriter(saveTo, true);
			    PrintWriter pw = new PrintWriter(fw);
			    pw.println(getDate() +" "+ message);
			    pw.flush();
			    pw.close();
		    } 
		    catch (IOException e) 
		    {
		    	e.printStackTrace();
		    }
    }
    
    public static String getDate() 
    {
    	  String s;
    	  Format formatter;
    	  Date date = new Date(); 
    	  formatter = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]");
    	  s = formatter.format(date);
    	  return s;
    }		
	
    // function to colorize strings from the config
    public static String colorize(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }    

    // function to colorize strings from the config
    public static List<String> colorize(List<String> message)
    {
        return message.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }    
}