package com.pwn9.PwnRaid;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
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
			e.setRadius(e.getRadius() + 2);
		}
		else if ((e.getEntityType() == EntityType.FIREBALL) && (PwnRaid.raidInProgress)) {
			e.setRadius(e.getRadius() + 2);
			if (PwnRaid.logEnabled)
			{
				PwnRaid.logToFile("Enhancing Fireball During Raid");				
			}
		}
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onLaunch(ProjectileLaunchEvent e) 
	{	
		// get the entity and if its certain type like fireball make it blow bigger
		if (e.getEntity() == null) return;	
		
		Projectile p = e.getEntity();
		
		if (p.getShooter() == null) return;
		
		// Make sure ghast
		if (p.getShooter() instanceof Ghast) {
			Entity s = (Entity) p.getShooter();
			
			if ((s.getType() == EntityType.GHAST) && (s.getCustomName() != null)) {
				p.setCustomName("Death From Above");
				p.setCustomNameVisible(true);
			}
		}
	}	
}