package com.pwn9.PwnRaid;

import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
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
		
		// get some infos
		Player p = e.getPlayer();
		Raid r = e.getRaid();
		World w = e.getWorld();
		Location loc = e.getRaid().getLocation();
		
		// set the bad omen level based on the item used to ring the bell and message the server
		r.setBadOmenLevel(PwnRaid.currentOmenLevel);	
		String msg = "PwnRaid: " + p.getDisplayName() + " has triggered a level " + r.getBadOmenLevel() + " raid!";
		
		// send the message if p is a player with a name
		if (p != null) {
			plugin.getServer().broadcastMessage(msg);
		}
		
		//TODO: create mob spawning function for waves that will spawn random mobs in random locations, and perhaps every x ticks
		this.spawnMob(w, loc);
		
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onRaidWave(RaidSpawnWaveEvent e) 
	{	
		// get some infos
		World w = e.getWorld();
		Location loc = e.getRaid().getLocation();
		this.spawnMob(w, loc);
	}
		
	@EventHandler(ignoreCancelled = false)
	public void onRaidFinish(RaidFinishEvent e) 
	{	
		// raid no longer in progress
		PwnRaid.raidInProgress = false;
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onRaidStop(RaidStopEvent e) 
	{	
		// raid no longer in progress
		PwnRaid.raidInProgress = false;
	}

	// spawn a mob 
	public void spawnMob(World w, Location loc)
	{
		int x = loc.getBlockX();
		int z = loc.getBlockZ();

		int xr = PwnRaid.randomNumberGenerator.nextInt(40);
		int zr = PwnRaid.randomNumberGenerator.nextInt(40);
		int ixr = PwnRaid.randomNumberGenerator.nextInt(40);
		int izr = PwnRaid.randomNumberGenerator.nextInt(40);
		
		int fxr = x + (xr - ixr);
		int fzr = z + (zr - izr);
		
		Block b = loc.getWorld().getHighestBlockAt(fxr, fzr);

		Location newLoc = b.getLocation();
		
		// spawn a charged creeper for kicks
		Creeper creeper = (Creeper)w.spawnEntity(newLoc, EntityType.CREEPER);
		creeper.setPowered(true);		
		
	}
	
}