package com.pwn9.PwnRaid;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class BombListener implements Listener
{
	private final PwnRaid plugin;
	
	public BombListener (PwnRaid plugin)
	{
	    plugin.getServer().getPluginManager().registerEvents(this, plugin);    
	    this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onBomb(ExplosionPrimeEvent e) 
	{	
		// get the entity and if its certain type like fireball make it blow bigger
		if ((e.getEntity().getCustomName() != null) && (e.getEntity().getCustomName() == "Death From Above"))
		{
			e.setRadius(e.getRadius() + 3);
		}
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onLaunch(ProjectileLaunchEvent e) 
	{	
		// get the entity and if its certain type like fireball make it blow bigger
		
		Projectile p = e.getEntity();
		Entity s = (Entity) p.getShooter();
		
		if ((s.getCustomName() != null) && (s.getCustomName() == "Raid-A-Ghast"))
		{
			p.setCustomName("Death From Above");
			p.setCustomNameVisible(true);
			PwnRaid.logToFile("Raid ghast firing death from above!");
		}
	
	}	
}