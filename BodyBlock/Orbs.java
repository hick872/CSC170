import greenfoot.*;

public class Orbs extends Actor
{
    public void act()
    {
        // Check if touched by Player or NPC
        Actor player = getOneIntersectingObject(Player.class);
        Actor npc = getOneIntersectingObject(NPC.class);
        
        if (player != null || npc != null)
        {
            getWorld().removeObject(this);
        }
    }
}