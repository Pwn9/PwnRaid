package com.pwn9.PwnRaid;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
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
	public void run() 
	{
		
		List<Raider> Raiders = raid.getRaiders();
		
		Iterator<Raider> iterator = Raiders.iterator();
		
		while(iterator.hasNext()) 
		{
			// do things on current raiders here
			Raider r = iterator.next();
			EntityType et = r.getType();
			
			// if raider is ravager, attempt to break blocks that might be in the way like walls.
			if (et == EntityType.RAVAGER) 
			{
				
	            Block[] blocks = new Block[8];

	            blocks[0] = this.getBreakableTargetBlock(r);
	            blocks[1] = blocks[0].getRelative(BlockFace.DOWN);
	            blocks[2] = blocks[0].getRelative(BlockFace.UP);
	            blocks[3] = blocks[2].getRelative(BlockFace.UP);
	            blocks[4] = blocks[0].getRelative(BlockFace.NORTH);
	            blocks[5] = blocks[0].getRelative(BlockFace.SOUTH);
	            blocks[6] = blocks[0].getRelative(BlockFace.EAST);
	            blocks[7] = blocks[0].getRelative(BlockFace.WEST);
	            
	            for (Block block : blocks) {
	                this.attemptBreakBlock(r, block);
	            }  
			}	
		}
		
		//TODO: we should build a routine to get the location of each entity that is alive, and their last 10 locations, and if they haven't moved much, and are far away from the raid center, teleport them
	}
	
	// This routine attempts to break a block with the ravager and plays the grass break sound when it does.
    protected void attemptBreakBlock(Raider r, Block br) {
        
    	Material type = br.getType();
    	
		int size = PwnRaid.ravagerBlocks.length;
		for (int i = 0; i < size; i++)
		{
			if (type.toString().toUpperCase().contains(PwnRaid.ravagerBlocks[i].toUpperCase()))
			{
				//TODO: make the chance ravagers can break blocks configurable
		    	int xr = PwnRaid.randomNumberGenerator.nextInt(10);
		    	if (xr > 2) 
		    	{
		        	//make this a chance the ravager breaks it. 
		    		if (PwnRaid.logEnabled)
		    		{
		    			PwnRaid.logToFile("Ravager broke " + br.getType().toString());
		    		}
					br.breakNaturally();
					r.getWorld().playSound(r.getLocation(), Sound.BLOCK_GRASS_BREAK , 10, 29);
					break;
		    	}
			}
		}
    }
    
    protected Block getBreakableTargetBlock(Raider r) 
    {
    	//Block tb = r.getTargetBlock(null,  100);
    	
    	// Is it targeting an entity? If so, get that block for direction.
    	Entity target = r.getTarget();
    	Block tb = null;
    	
    	if (target != null)
    	{
    		tb = r.getTarget().getLocation().getBlock();
    	}
    	
    	if (tb == null) {
    		// fall back to the raid center to give the ravager direction if it's not targeting.
    		tb = PwnRaid.currentRaidTracker.getLocation().getBlock();
    		
    		if (tb == null) 
    		{
    			// this shouldn't happen but if it's still null, fall back to the eye location
	    		Location loc = r.getEyeLocation();
	    		return loc.getBlock();
    		}
    	}

        Location direction = tb.getLocation().subtract(r.getEyeLocation());

        double dx = direction.getX();
        double dz = direction.getZ();

        int bdx = 0;
        int bdz = 0;

        if (Math.abs(dx) > Math.abs(dz)) {
            bdx = (dx > 0) ? 1 : -1;
        } else {
            bdz = (dx > 0) ? 1 : -1;
        }

        Block ret = r.getWorld().getBlockAt((int) Math.floor(r.getLocation().getBlockX() + bdx), (int) Math.floor(r.getLocation().getBlockY() + 1), (int) Math.floor(r.getLocation().getBlockZ() + bdz));
        
        if (PwnRaid.logEnabled) {
			// this was for dev debugging this routing and can probably be removed.
        	//PwnRaid.logToFile("Block to break: " + ret.getLocation().toString() + " Ravager at: " + r.getEyeLocation().toString());
        }
        
        return ret;
    }	
}