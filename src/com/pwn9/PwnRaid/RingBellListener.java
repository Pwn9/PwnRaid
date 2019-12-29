package com.pwn9.PwnRaid;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RingBellListener implements Listener 
{
    private final PwnRaid plugin;
    
	public RingBellListener(PwnRaid plugin) 
	{
	    plugin.getServer().getPluginManager().registerEvents(this, plugin);    
	    this.plugin = plugin;
	}

	// List for the PlayerInteract Event and then do stuff with it
	@EventHandler(ignoreCancelled = false)
	public void onBellRing(PlayerInteractEvent e) 
	{

		// check if this is enabled in this world
		World w = e.getPlayer().getWorld();
		if (!PwnRaid.isEnabledIn(w.getName())) {
			return;
		}
		
		if ((e.getClickedBlock() == null) || (e.getClickedBlock().getType() != Material.BELL)) {
			return;
		}
		
		// items are configured in config.yml with levels
		if (e.getItem() != null) {
			if (plugin.getConfig().getInt("trigger_item." + e.getItem().getType()) > 0) {
				// check to see if a PwnRaid is already in progress
				if (PwnRaid.raidInProgress) {
					e.getPlayer().sendMessage("PwnRaid: A raid is already in progress, cannot start another");
					return;
				}
				else {
					int l = plugin.getConfig().getInt("trigger_item." + e.getItem().getType());
					//TODO: does player have permission to ring bell and start a raid?
					this.doBadOmen(e.getPlayer(), l);
					PwnRaid.logToFile("Checking item trigger for " + e.getItem().getType().toString() + ": value = " + l);
				}
			}
			return;
		}
		return;
	}
	
	// start the raid event by effecting the player
	//TODO: add a bad omen level argument to this
	public void doBadOmen(Player p, int i) 
	{
		PotionEffectType bo = PotionEffectType.BAD_OMEN;
		
		//TODO: the second argument is time, which doesn't really matter, player is probably already in a village.
		
		// we'll make use of a var to handle the omen level and pass it on to the raid listener
		PwnRaid.currentOmenLevel = i;
		
		//TODO: the 3rd argument is intensity, does this set the bad omen level that the raid will inherit? Apparently it does not.	
		PotionEffect b = new PotionEffect(bo, 20, i);
				
		p.addPotionEffect(b);
		
		return;
	}
}
