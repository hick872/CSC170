import greenfoot.*;
import java.util.List;
import java.util.ArrayList;

public class NPC extends Actor
{
    private int speed = 2;
    private int currentDir = 0;
    private int dirHoldTicks = 0;
    private int wallFollowDir = 1;
    private int wallFollowTicks = 0;
    private int turnCooldown = 0;
    private int ignoreTailTicks = 0;
    private List<int[]> history = new ArrayList<int[]>();
    private List<NPCTailSegment> tail = new ArrayList<NPCTailSegment>();
    private int segmentSpacing = 8;

    public void act()
    {
        int prevX = getX();
        int prevY = getY();

        if (dirHoldTicks > 0)
        {
            dirHoldTicks--;
        }
        if (wallFollowTicks > 0)
        {
            wallFollowTicks--;
        }
        if (turnCooldown > 0)
        {
            turnCooldown--;
        }
        if (ignoreTailTicks > 0)
        {
            ignoreTailTicks--;
        }

        stepCardinally();

        if (getOneIntersectingObject(Wall.class) != null)
        {
            setLocation(prevX, prevY);
            if (turnCooldown == 0)
            {
                turnRightAndStepForward();
                turnCooldown = 6;
                dirHoldTicks = 8;
                wallFollowTicks = 12;
            }
        }
        else if (ignoreTailTicks == 0 && getOneIntersectingObject(NPCTailSegment.class) != null)
        {
            setLocation(prevX, prevY);
            if (turnCooldown == 0)
            {
                int rightDir = normalizeCardinal(getRotation() + 90);
                currentDir = rightDir;
                setRotation(rightDir);
                if (canMoveCardinal(rightDir, speed))
                {
                    moveInCardinal(rightDir);
                }
                turnCooldown = 6;
                dirHoldTicks = 8;
                ignoreTailTicks = 8;
            }
        }

        if (getWorld() instanceof BodyBlock)
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

    private void turnRightAndStepForward()
    {
        int rightDir = normalizeCardinal(getRotation() + 90);
        currentDir = rightDir;
        setRotation(rightDir);

        int beforeX = getX();
        int beforeY = getY();
        move(speed);
        if (getOneIntersectingObject(Wall.class) != null)
        {
            setLocation(beforeX, beforeY);
            wallFollowDir = -wallFollowDir;
        }
    }

    private void stepCardinally()
    {
        if (dirHoldTicks > 0 && canMoveCardinal(currentDir, speed))
        {
            moveInCardinal(currentDir);
            return;
        }

        Actor nearestOrb = getNearestObject(Orbs.class);
        if (nearestOrb != null)
        {
            moveTowardGoalCardinally(nearestOrb.getX(), nearestOrb.getY());
            return;
        }

        Player preyPlayer = getSmallerPlayerInRange(260);
        if (preyPlayer != null && getSize() > preyPlayer.getSize())
        {
            moveTowardGoalCardinally(preyPlayer.getX(), preyPlayer.getY());
            return;
        }

        Player threatPlayer = getLargerPlayerInRange(260);
        if (threatPlayer != null && threatPlayer.getSize() > getSize())
        {
            int awayX = getX() + (getX() - threatPlayer.getX());
            int awayY = getY() + (getY() - threatPlayer.getY());
            moveTowardGoalCardinally(awayX, awayY);
            return;
        }

        if (!canMoveCardinal(currentDir, speed) || Greenfoot.getRandomNumber(100) < 8)
        {
            int[] cardinals = {0, 90, 180, 270};
            currentDir = cardinals[Greenfoot.getRandomNumber(4)];
            dirHoldTicks = 8;
        }
        moveInCardinal(currentDir);
    }

    private void moveTowardGoalCardinally(int goalX, int goalY)
    {
        int dx = goalX - getX();
        int dy = goalY - getY();
        int primaryDir = getPrimaryCardinal(dx, dy);
        int secondaryDir = getSecondaryCardinal(dx, dy);

        int[] priorityOrder;
        if (!isLineBlocked(goalX, goalY))
        {
            wallFollowTicks = 0;
            priorityOrder = new int[] {
                primaryDir,
                secondaryDir,
                currentDir,
                normalizeCardinal(currentDir + 90),
                normalizeCardinal(currentDir - 90),
                normalizeCardinal(primaryDir + 180)
            };
        }
        else
        {
            if (wallFollowTicks <= 0)
            {
                wallFollowDir = (Greenfoot.getRandomNumber(2) == 0) ? 1 : -1;
                wallFollowTicks = 16;
            }
            int turnStep = wallFollowDir * 90;
            priorityOrder = new int[] {
                currentDir,
                normalizeCardinal(currentDir + turnStep),
                primaryDir,
                secondaryDir,
                normalizeCardinal(currentDir - turnStep),
                normalizeCardinal(currentDir + 180)
            };
        }

        for (int dir : priorityOrder)
        {
            if (canMoveCardinal(dir, speed))
            {
                if (dir != currentDir)
                {
                    currentDir = dir;
                    dirHoldTicks = 8;
                }
                moveInCardinal(currentDir);
                return;
            }
        }
    }

    private int getPrimaryCardinal(int dx, int dy)
    {
        if (Math.abs(dx) >= Math.abs(dy))
        {
            return (dx >= 0) ? 0 : 180;
        }
        return (dy >= 0) ? 90 : 270;
    }

    private int getSecondaryCardinal(int dx, int dy)
    {
        if (Math.abs(dx) >= Math.abs(dy))
        {
            return (dy >= 0) ? 90 : 270;
        }
        return (dx >= 0) ? 0 : 180;
    }

    private int normalizeCardinal(int angle)
    {
        int a = angle % 360;
        if (a < 0)
        {
            a += 360;
        }
        return a;
    }

    private boolean canMoveCardinal(int dir, int dist)
    {
        if (getWorld() == null)
        {
            return false;
        }
        int startX = getX();
        int startY = getY();
        int origRot = getRotation();

        setRotation(normalizeCardinal(dir));
        move(dist);
        boolean clear = (getOneIntersectingObject(Wall.class) == null);

        setLocation(startX, startY);
        setRotation(origRot);
        return clear;
    }

    private void moveInCardinal(int dir)
    {
        setRotation(normalizeCardinal(dir));
        move(speed);
    }

    private boolean isLineBlocked(int targetX, int targetY)
    {
        if (getWorld() == null)
        {
            return false;
        }

        int startX = getX();
        int startY = getY();
        double dist = Math.hypot(targetX - startX, targetY - startY);
        int steps = Math.max(1, (int) (dist / 8));

        for (int i = 1; i < steps; i++)
        {
            double t = (double) i / steps;
            int sampleX = (int) Math.round(startX + (targetX - startX) * t);
            int sampleY = (int) Math.round(startY + (targetY - startY) * t);

            if (!getWorld().getObjectsAt(sampleX, sampleY, Wall.class).isEmpty())
            {
                return true;
            }
        }
        return false;
    }

    private Player getSmallerPlayerInRange(int range)
    {
        List<Player> players = getObjectsInRange(range, Player.class);
        for (Player p : players)
        {
            if (p.getSize() < getSize())
            {
                return p;
            }
        }
        return null;
    }

    private Player getLargerPlayerInRange(int range)
    {
        List<Player> players = getObjectsInRange(range, Player.class);
        for (Player p : players)
        {
            if (p.getSize() > getSize())
            {
                return p;
            }
        }
        return null;
    }

    private void checkOrbCollision()
    {
        Orbs orb = (Orbs) getOneIntersectingObject(Orbs.class);
        if (orb != null)
        {
            getWorld().removeObject(orb);

            NPCTailSegment newSegment = new NPCTailSegment();
            getWorld().addObject(newSegment, getX(), getY());
            tail.add(newSegment);
            ignoreTailTicks = 8;
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