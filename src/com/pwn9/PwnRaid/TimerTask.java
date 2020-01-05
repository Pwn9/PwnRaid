package com.pwn9.PwnRaid;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Raid;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Raider;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerTask extends BukkitRunnable
{

	private Raid raid;
	
	public TimerTask(Raid raid)
	{
		this.raid = raid;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		List<Raider> Raiders = raid.getRaiders();
		
		Iterator<Raider> iterator = Raiders.iterator();
		
		while(iterator.hasNext()) {
			// do things on current raiders here
			
			Raider r = iterator.next();
			
			EntityType et = r.getType();
			
			if (et == EntityType.RAVAGER) {
				
				Block b = r.getTargetBlockExact(3);
				
				Iterator<String> blockList = PwnRaid.ravagerBlocks.iterator();
				
				while(blockList.hasNext()) {
					if (b.getType().toString().contains(blockList.next())) {
						// we can break this block
						b.breakNaturally();
						
						PwnRaid.logToFile("Ravager broke " + b.getType().toString());
						break;
					}
				}
			}	
		}	
	}	
}