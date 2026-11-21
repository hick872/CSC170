import greenfoot.*;
import java.util.List;
import java.util.ArrayList;

public class Player extends Actor
{
    private int score = 0;
    private List<int[]> history = new ArrayList<int[]>();
    private List<TailSegment> tail = new ArrayList<TailSegment>();
    private int segmentSpacing = 8;

    private int dx = 2;
    private int dy = 0;

    public void act()
    {
        checkDirectionInput();

        int prevX = getX();
        int prevY = getY();
        setLocation(prevX + dx, prevY + dy);

        if (getOneIntersectingObject(Wall.class) != null)
        {
            setLocation(prevX, prevY);
            dx = 0;
            dy = 0;
        }
        else if (getWorld() instanceof BodyBlock)
        {
            ((BodyBlock) getWorld()).teleportIfAtEdge(this);
        }

        history.add(0, new int[] {getX(), getY()});

        checkOrbCollision();
        updateTail();
    }

    public int getSize()
    {
        return 1 + tail.size();
    }

    private void checkDirectionInput()
    {
        if (Greenfoot.isKeyDown("left") && dx <= 0)
        {
            dx = -2;
            dy = 0;
        }
        else if (Greenfoot.isKeyDown("right") && dx >= 0)
        {
            dx = 2;
            dy = 0;
        }
        else if (Greenfoot.isKeyDown("up") && dy <= 0)
        {
            dx = 0;
            dy = -2;
        }
        else if (Greenfoot.isKeyDown("down") && dy >= 0)
        {
            dx = 0;
            dy = 2;
        }
    }

    private void checkOrbCollision()
    {
        Orbs orb = (Orbs) getOneIntersectingObject(Orbs.class);
        if (orb != null)
        {
            getWorld().removeObject(orb);
            score++;

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