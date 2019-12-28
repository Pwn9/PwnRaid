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
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.pwn9.PwnRaid.RingBellListener;
import com.pwn9.PwnRaid.RaidListener;

public class PwnRaid extends JavaPlugin 
{
	// For convenience, a reference to the instance of this plugin
	public static PwnRaid instance;
	
	// Init vars
	public static File dataFolder;
	public final Logger logger = Logger.getLogger("Minecraft.PwnRaid");   	
	public static List<String> enabledWorlds;
	public static Boolean logEnabled;
	
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
    	
    	// Get Data Folder
    	PwnRaid.dataFolder = getDataFolder();
    	
    	// Load Config File
    	this.loadConfig();
    	
    	// Load plugin.yml
    	PwnRaid.pdfFile = this.getDescription(); //Gets plugin.yml
    	    		
		// Start Metrics
		MetricsLite metricslite = new MetricsLite(this);
		
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
	
	// Generate a random number and return bool
	static boolean random(int percentChance) 
	{
		return randomNumberGenerator.nextInt(100) > percentChance;
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