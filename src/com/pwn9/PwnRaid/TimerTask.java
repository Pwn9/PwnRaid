package com.pwn9.PwnRaid;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Raid;
import org.bukkit.Sound;
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
				
				//Block b = r.getTargetBlockExact(2);
				
				//if (b!= null) {
					
					//String bb = b.getType().toString();

					//PwnRaid.logToFile("Ravager Targetting Block " + bb);

					int size = PwnRaid.ravagerBlocks.length;

					for (int i = 0; i < size; i++)
					{
					    int radius = 1;
					    //Block middle = r.getLocation().getBlock();
					    Block middle = r.getTargetBlockExact(2);
					    for (int x = radius; x >= -radius; x--) {
					        for (int y = 2; y >= 0; y--) {
					            for (int z = radius; z >= -radius; z--) {
					                if (middle.getRelative(x, y, z).getType().toString().toUpperCase().contains(PwnRaid.ravagerBlocks[i].toUpperCase())) {
					                	
					                	int xr = PwnRaid.randomNumberGenerator.nextInt(10);
					                	if (xr > 5) {
						                	//make this a chance the ravager breaks it. 
						                	Block br = middle.getRelative(x, y, z);				                	
						                	PwnRaid.logToFile("Ravager broke " + br.getType().toString());
											br.breakNaturally();
											r.getWorld().playSound(r.getLocation(), Sound.BLOCK_GRASS_BREAK , 10, 29);
					                	}
										break;
					                }
					            }
					        }
					    }						
						
						//PwnRaid.logToFile("Compare: " + bb.toUpperCase() + " to: " + PwnRaid.ravagerBlocks[i].toUpperCase());
						//if (bb.toUpperCase().contains(PwnRaid.ravagerBlocks[i].toUpperCase())) {
							// we can break this block
							//PwnRaid.logToFile("Ravager broke " + b.getType().toString());
							//b.breakNaturally();
							//break;
						//}						
					}
				//}    
			}	
		}	
	}	
}