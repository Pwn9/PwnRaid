package com.pwn9.PwnRaid;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.entity.WitherSkeleton;
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
		PwnRaid.currentWaveNumber = 0;
		
		// get some infos
		Player p = e.getPlayer();
		Raid r = e.getRaid();
		
		// set the bad omen level based on the item used to ring the bell and message the server
		r.setBadOmenLevel(PwnRaid.currentOmenLevel);	
		String msg = "PwnRaid: " + p.getDisplayName() + " has triggered a level " + r.getBadOmenLevel() + " raid!";
		
		// send the message if p is a player with a name
		if (p != null) {
			plugin.getServer().broadcastMessage(ChatColor.RED + msg);
		}

		return;
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onRaidWave(RaidSpawnWaveEvent e) 
	{	
		PwnRaid.currentWaveNumber = PwnRaid.currentWaveNumber + 1;
		// get some infos
		World w = e.getWorld();
		Location loc = e.getRaid().getLocation();
		
		this.spawnWaveExtraMobs(w, loc, PwnRaid.currentWaveNumber);
		
		return;
	}
		
	// Raid finish has a clear winner
	@EventHandler(ignoreCancelled = false)
	public void onRaidFinish(RaidFinishEvent e) 
	{	
		
		String msg = "PwnRaid: The raid has ended with a status of: " + e.getRaid().getStatus().toString();
		// send the message 
		plugin.getServer().broadcastMessage(ChatColor.RED + msg);		
		
		// cleanup routine
		this.raidEnded();
		
		return;
	}
	
	// Raid stop is a cancelled raid for some other reason
	@EventHandler(ignoreCancelled = false)
	public void onRaidStop(RaidStopEvent e) 
	{	
		
		String msg = "PwnRaid: The raid has ended with a status of: " + e.getRaid().getStatus().toString();
		// send the message 
		plugin.getServer().broadcastMessage(ChatColor.RED + msg);		
		
		// cleanup routine
		this.raidEnded();
		
		return;
	}

	public Location getRandomLocNearby(World w, Location loc, int d) 
	{	
		int x = loc.getBlockX();
		int z = loc.getBlockZ();

		int xr = PwnRaid.randomNumberGenerator.nextInt(d);
		int zr = PwnRaid.randomNumberGenerator.nextInt(d);
		int ixr = PwnRaid.randomNumberGenerator.nextInt(d);
		int izr = PwnRaid.randomNumberGenerator.nextInt(d);
		
		int fxr = x + (xr - ixr);
		int fzr = z + (zr - izr);
		
		Block b = loc.getWorld().getHighestBlockAt(fxr, fzr);

		Location newLoc = b.getLocation();
		
		return newLoc;
	}
	
	
	// spawn a charged creeper within a random distance from the raid center
	public void spawnSuperCreeper(World w, Location loc)
	{
		Location newLoc = this.getRandomLocNearby(w, loc, 30);
		
		// spawn a charged creeper for kicks
		Creeper creeper = (Creeper)w.spawnEntity(newLoc, EntityType.CREEPER);
		creeper.setPowered(true);	
		creeper.setCustomName("Raid Bomber");
		creeper.setCustomNameVisible(true);
		
	    List<Entity> near = creeper.getNearbyEntities(50.0D, 50.0D, 50.0D);
	    for(Entity entity : near) {
	        if(entity instanceof Player) {
	            Player nearPlayer = (Player) entity;
	            creeper.setTarget(nearPlayer);
	            return;
	        }
	    }		
		
		return;
	}
	
	
	// spawn a witherjockey random distance from the raid center
	public void spawnWitherJockey(World w, Location loc)
	{
		Location newLoc = this.getRandomLocNearby(w, loc, 40);
		
		// spawn a wither jockey for kicks
		Spider spider = (Spider)w.spawnEntity(newLoc, EntityType.SPIDER);
		WitherSkeleton ws = (WitherSkeleton)w.spawnEntity(newLoc, EntityType.WITHER_SKELETON);
		spider.addPassenger(ws);
		ws.setCustomName("Raid Riding Hood");
		ws.setCustomNameVisible(true);
		
		// target someone
	    List<Entity> near = ws.getNearbyEntities(50.0D, 50.0D, 50.0D);
	    for(Entity entity : near) {
	        if(entity instanceof Player) {
	            Player nearPlayer = (Player) entity;
	            ws.setTarget(nearPlayer);
	            return;
	        }
	    }
	    
		return;
	}	
	// spawn a ghast within a random distance from the raid center
	public void spawnGhast(World w, Location loc)
	{
		Location newLoc = this.getRandomLocNearby(w, loc, 40);
		newLoc.setY(newLoc.getY() + 20.0D);
		
		// spawn a ghast for kicks
		Ghast ghast = (Ghast)w.spawnEntity(newLoc, EntityType.GHAST);	
		ghast.setCustomName("Raid-A-Ghast");
		ghast.setCustomNameVisible(true);
		
		// target someone
	    List<Entity> near = ghast.getNearbyEntities(50.0D, 50.0D, 50.0D);
	    for(Entity entity : near) {
	        if(entity instanceof Player) {
	            Player nearPlayer = (Player) entity;
	            ghast.setTarget(nearPlayer);
	            return;
	        }
	    }
		
		return;
	}

	// spawn primed tnt
	public void spawnTnt(World w, Location loc)
	{
		Location newLoc = this.getRandomLocNearby(w, loc, 80);
		newLoc.setY(newLoc.getY() + 30.0D);
		// spawn tnt for kicks
		w.spawnEntity(newLoc, EntityType.PRIMED_TNT); 
		return;
	}	
	
	//todo: a routine that will spawn a set of extra mobs each wave for extra fun
	public void spawnWaveExtraMobs(World w, Location loc, int wave)
	{
		
		String wavemsg = "PwnRaid: Round " + wave + " of Pillager raids has begun!";
		plugin.getServer().broadcastMessage(ChatColor.RED + wavemsg);
		
		if (wave == 1) 
		{
			this.spawnSuperCreeper(w, loc);
		}
		else if (wave == 2)
		{
			this.spawnGhast(w, loc);
		}
		else if (wave == 3) 
		{
			this.spawnSuperCreeper(w, loc);
			this.spawnGhast(w, loc);
		}
		else if (wave == 4) 
		{
			this.spawnSuperCreeper(w, loc);
			this.spawnGhast(w, loc);
			this.spawnWitherJockey(w, loc);
		}		
		else if (wave > 4 && wave < 9) 
		{
			this.spawnSuperCreeper(w, loc);
			this.spawnSuperCreeper(w, loc);
			this.spawnGhast(w, loc);
			this.spawnGhast(w, loc);
			this.spawnWitherJockey(w, loc);
		}
		else if (wave > 8)
		{
			this.spawnSuperCreeper(w, loc);
			this.spawnSuperCreeper(w, loc);
			this.spawnSuperCreeper(w, loc);
			this.spawnGhast(w, loc);
			this.spawnGhast(w, loc);
			this.spawnWitherJockey(w, loc);
			this.spawnWitherJockey(w, loc);
		}
		else {
			// wave must be 0
		}
		
		// artillery
		if (wave == 3 || wave == 6 || wave > 8)
		{
			String msg = "PwnRaid: Raid Captain ~ Enough messing around... call in artillery!!!";
			plugin.getServer().broadcastMessage(ChatColor.RED + msg);
			this.spawnTnt(w, loc);
			this.spawnTnt(w, loc);
			this.spawnTnt(w, loc);
			this.spawnTnt(w, loc);
			this.spawnTnt(w, loc);
		}
		
		return;
	}
	
	// cleanup routine for when a raid ends
	public void raidEnded() 
	{
		// raid no longer in progress
		PwnRaid.raidInProgress = false;
		PwnRaid.currentWaveNumber = 0;
	}
	
}