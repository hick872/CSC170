import greenfoot.*;
import java.util.List;
import java.util.ArrayList;

public class Player extends Actor
{
    private int score = 0;
    private List<int[]> history = new ArrayList<int[]>();
    private List<TailSegment> tail = new ArrayList<TailSegment>();
    private int segmentSpacing = 8; // Distance between body segments
    
    // Movement velocity (dx, dy) to ensure strict single-direction control
    private int dx = 4; // Start moving right by default
    private int dy = 0;

    public void act()
    {
        checkDirectionInput();
        
        // Move continuously in the current locked direction
        setLocation(getX() + dx, getY() + dy);
        
        // Record position history for the tail to follow
        history.add(0, new int[] {getX(), getY()});
        
        checkOrbCollision();
        updateTail();
    }
    
    private void checkDirectionInput()
    {
        // Only allow switching directions if moving perpendicularly, preventing instant reverse into self
        if (Greenfoot.isKeyDown("left") && dx == 0)
        {
            dx = -4;
            dy = 0;
        }
        else if (Greenfoot.isKeyDown("right") && dx == 0)
        {
            dx = 4;
            dy = 0;
        }
        else if (Greenfoot.isKeyDown("up") && dy == 0)
        {
            dx = 0;
            dy = -4;
        }
        else if (Greenfoot.isKeyDown("down") && dy == 0)
        {
            dx = 0;
            dy = 4;
        }
    }

    private void checkOrbCollision()
    {
        Orbs orb = (Orbs) getOneIntersectingObject(Orbs.class);
        if (orb != null)
        {
            getWorld().removeObject(orb);
            score++;
            
            // Growth: Spawn a new TailSegment and add it to the chain
            TailSegment newSegment = new TailSegment();
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
}