package com.pwn9.PwnRaid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
import org.bukkit.scheduler.BukkitTask;

public class RaidListener implements Listener 
{
	private final PwnRaid plugin;
	private BukkitTask raidTask;
	
	public RaidListener(PwnRaid plugin)
	{
	    plugin.getServer().getPluginManager().registerEvents(this, plugin);    
	    this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onRaidStart(RaidTriggerEvent e) 
	{	
		//TODO: get rid of this eventually, but for now we only allow 1 raid at a time on the server
		if (PwnRaid.raidInProgress)
		{
			e.setCancelled(true);
			return;
		}
		
		
		// Add this raid to the current raids
		//TODO: PwnRaid.currentRaids.add(e.getRaid());
		
		// with this we can always get the current raid now without passing it between classes, maybe this should be it's own class eventually like PwnRaidRaidClass
		PwnRaid.currentRaidTracker = e.getRaid();
		
		// set raid in progress
		PwnRaid.raidInProgress = true;
		PwnRaid.currentWaveNumber = 0;
		PwnRaid.currentRaidBeginTime = System.currentTimeMillis();
		
		// get some infos
		Player p = e.getPlayer();
		Raid r = e.getRaid();
		
		// set the bad omen level based on the item used to ring the bell and message the server
		r.setBadOmenLevel(PwnRaid.currentOmenLevel);	
		String msg = p.getDisplayName() + " has triggered a level " + r.getBadOmenLevel() + " raid!";
		
		// send the message if p is a player with a name
		if (p != null) {
			plugin.getServer().broadcastMessage(ChatColor.RED + "PwnRaid: " + ChatColor.WHITE + msg);
		}

		// create a timer that listens to this raid
		BukkitTask task = new TimerTask(r).runTaskTimer(this.plugin, 10, 20);
			
		this.raidTask = task;
		
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
		//TODO: remove raid from list of raids
		
		String status = e.getRaid().getStatus().toString();
		
		String msg = "The raid has ended with a status of: " + status;
		// send the message 
		plugin.getServer().broadcastMessage(ChatColor.RED + "PwnRaid: " + ChatColor.WHITE + msg);		
		
		List<Player> Winners = e.getWinners();
		
		// cleanup routine
		this.raidEnded(Winners, status);
		
		return;
	}
	
	// Raid stop is a cancelled raid for some other reason
	@EventHandler(ignoreCancelled = false)
	public void onRaidStop(RaidStopEvent e) 
	{	
		//TODO: remove raid from list of raids
		
		String status = e.getReason().toString();
		
		String msg = "The raid has stopped because of: " + status;
		// send the message 
		plugin.getServer().broadcastMessage(ChatColor.RED + "PwnRaid: " + ChatColor.WHITE + msg);		
		
		Set<UUID> Heroes = e.getRaid().getHeroes();
		
		List<Player> Players = new ArrayList<Player>();
		
		Iterator<UUID> it = Heroes.iterator();
		while(it.hasNext()){
			//System.out.println(it.next());
			Player p = plugin.getServer().getPlayer(it.next());
			Players.add(p);
		}

		// cleanup routine
		this.raidEnded(Players, status);
		
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
		
		String wavemsg = "Round " + wave + " of Pillager raids has begun!";
		plugin.getServer().broadcastMessage(ChatColor.RED + "PwnRaid: " + ChatColor.WHITE + wavemsg);
		
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
			String msg = "Raid Captain ~ Enough messing around... call in artillery!!!";
			plugin.getServer().broadcastMessage(ChatColor.RED + "PwnRaid: " + ChatColor.WHITE + msg);
			this.spawnTnt(w, loc);
			this.spawnTnt(w, loc);
			this.spawnTnt(w, loc);
			this.spawnTnt(w, loc);
			this.spawnTnt(w, loc);
		}
		
		return;
	}
	
	// cleanup routine for when a raid ends
	public void raidEnded(List<Player> Players, String status) 
	{
		// cancel the listener timer
		this.raidTask.cancel();
		
		// raid no longer in progress
		PwnRaid.currentRaidTracker = null;
		PwnRaid.raidInProgress = false;
		PwnRaid.currentWaveNumber = 0;
		PwnRaid.currentRaidEndTime = System.currentTimeMillis();
		
		long millis = PwnRaid.currentRaidEndTime - PwnRaid.currentRaidBeginTime;
		
		String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
			    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
			    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
		
		String msg = "Raid time length was " + hms;
		// send the message 
		plugin.getServer().broadcastMessage(ChatColor.RED + "PwnRaid: " + ChatColor.WHITE + msg);	
		
	}
	
}