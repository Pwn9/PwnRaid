package com.pwn9.PwnRaid;

import org.bukkit.Raid;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidStopEvent;
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
		// set raid in progress
		PwnRaid.raidInProgress = true;
		
		Player p = e.getPlayer();
		
		Raid r = e.getRaid();
		
		// set the bad omen level based on the item used to ring the bell
		r.setBadOmenLevel(PwnRaid.currentOmenLevel);
		
		int l = r.getBadOmenLevel();
			
		String msg = "PwnRaid: " + p.getDisplayName() + " has triggered a level " + l + " raid!";
		
		if (p != null) {
			plugin.getServer().broadcastMessage(msg);
		}
		
		
	}
	
	
	@EventHandler(ignoreCancelled = false)
	public void onRaidFinish(RaidFinishEvent e) 
	{	
		PwnRaid.raidInProgress = false;
	}

	
	@EventHandler(ignoreCancelled = false)
	public void onRaidStop(RaidStopEvent e) 
	{	
		PwnRaid.raidInProgress = false;
	}
	
}