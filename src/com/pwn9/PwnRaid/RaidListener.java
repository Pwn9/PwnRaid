package com.pwn9.PwnRaid;

import org.bukkit.Raid;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidTriggerEvent;

public class RaidListener implements Listener 
{
	private final PwnRaid plugin;
	
	public RaidListener(PwnRaid plugin)
	{
	    plugin.getServer().getPluginManager().registerEvents(this, plugin);    
	    this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onRaidStart(RaidTriggerEvent e) 
	{	
		Player p = e.getPlayer();
		
		Raid r = e.getRaid();
		
		int l = r.getBadOmenLevel();
		
		String msg = p.getDisplayName() + " has triggered a level " + l + " raid!";
		
		if (p != null) {
			plugin.getServer().broadcastMessage(msg);
		}
		
		
	}
	
	
}