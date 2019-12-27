package com.pwn9.PwnRaid;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PwnRaidBellListener implements Listener 
{
    private final PwnRaid plugin;
    
	public PwnRaidBellListener(PwnRaid plugin) 
	{
	    plugin.getServer().getPluginManager().registerEvents(this, plugin);    
	    this.plugin = plugin;
	}

	// List for the PlayerInteract Event and then do stuff with it
	@EventHandler(ignoreCancelled = false)
	public void onBellRing(PlayerInteractEvent e) 
	{

		//TODO: make item clicking bell configurable
		if ((e.getItem() != null) && (e.getItem().getType() != Material.WOODEN_HOE)) {
			return;
		}
		
		if ((e.getClickedBlock() != null) && (e.getClickedBlock().getType() != Material.BELL)) {
			return;
		}
		
		//TODO: does player have permission to ring bell and start a raid?
		this.doBadOmen(e.getPlayer());
		
		return;
		
	}
	
	// start the raid event by effecting the player.
	public void doBadOmen(Player p) 
	{
		PotionEffectType bo = PotionEffectType.BAD_OMEN;
		PotionEffect b = new PotionEffect(bo, 10, 5);
				
		p.addPotionEffect(b);
		
		return;
	}
}
