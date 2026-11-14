import greenfoot.*;  
import java.util.List;
import java.util.ArrayList;

public class NPC extends Actor
{
    private int speed = 2;
    private List<int[]> history = new ArrayList<int[]>();
    private List<NPCTailSegment> tail = new ArrayList<NPCTailSegment>();
    private int segmentSpacing = 8; // Distance between body segments

    public void act()
    {
        // Record position history for the tail to follow
        history.add(0, new int[] {getX(), getY()});
        
        huntOrbsOrPlayer();
        avoidWalls();
        checkOrbCollision();
        updateTail();
    }

    private void huntOrbsOrPlayer()
    {
        // Priority 1: Look for nearby orbs to grab (using Orbs.class)
        Actor nearestOrb = getNearestObject(Orbs.class);
        if (nearestOrb != null)
        {
            turnTowards(nearestOrb.getX(), nearestOrb.getY());
            move(speed);
            return;
        }

        // Priority 2: Track and follow the player if within range (300 pixels)
        List<Player> players = getObjectsInRange(300, Player.class);
        if (!players.isEmpty())
        {
            Player player = players.get(0);
            turnTowards(player.getX(), player.getY());
            move(speed);
        }
        else
        {
            // Wander casually if nothing is nearby
            if (Greenfoot.getRandomNumber(100) < 10)
            {
                turn(Greenfoot.getRandomNumber(60) - 30);
            }
            move(speed);
        }
    }

    private void avoidWalls()
    {
        // Turn away if getting too close to the world borders
        if (getX() <= 15 || getX() >= getWorld().getWidth() - 15 ||
            getY() <= 15 || getY() >= getWorld().getHeight() - 15)
        {
            turn(180);
            move(5);
        }
    }

    private void checkOrbCollision()
    {
        Orbs orb = (Orbs) getOneIntersectingObject(Orbs.class);
        if (orb != null)
        {
            getWorld().removeObject(orb);
            
            // Growth: Spawn a new NPCTailSegment and add it to the NPC's chain
            NPCTailSegment newSegment = new NPCTailSegment();
            getWorld().addObject(newSegment, getX(), getY());
            tail.add(newSegment);
        }
    }

    private void updateTail()
    {
        int maxHistory = (tail.size() + 1) * segmentSpacing;
        while (history.size() > maxHistory)
        {
            history.remove(history.size() - 1);
        }
        
        for (int i = 0; i < tail.size(); i++)
        {
            int historyIndex = (i + 1) * segmentSpacing;
            if (historyIndex < history.size())
            {
                int[] pos = history.get(historyIndex);
                tail.get(i).setLocation(pos[0], pos[1]);
            }
        }
    }

    // Helper method to find the closest object of a specific class type
    private Actor getNearestObject(Class cls)
    {
        List<Actor> list = getObjectsInRange(250, cls);
        Actor nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Actor a : list)
        {
            double dist = Math.hypot(a.getX() - getX(), a.getY() - getY());
            if (dist < minDistance)
            {
                minDistance = dist;
                nearest = a;
            }
        }
        return nearest;
    }
}