import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
/**
 * Write a description of class ScoreBoard here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ScoreBoard extends Actor
{
    private int score;
    private int keyCooldown = 0;

    public ScoreBoard()
    {
        this.score = 0;
        updateDisplay();
    }

    public void act()
    {
        if (keyCooldown > 0)
        {
            keyCooldown--;
        }

        if (keyCooldown == 0)
        {
            if (Greenfoot.isKeyDown("up"))
            {
                addScore(10);
                keyCooldown = 10;
            }
            else if (Greenfoot.isKeyDown("down"))
            {
                addScore(-10);
                keyCooldown = 10;
            }
        }

        updateDisplay();
    }

    public void setScore(int newScore)
    {
        this.score = newScore;
        updateDisplay();
    }

    public int getScore()
    {
        return this.score;
    }

    public void addScore(int points)
    {
        this.score += points;
        updateDisplay();
    }

    private void updateDisplay()
    {
        World w = getWorld();
        int playerSize = 0;
        int npc1Size = 0;
        int npc2Size = 0;

        if (w != null)
        {
            List<Player> players = w.getObjects(Player.class);
            if (!players.isEmpty())
            {
                playerSize = players.get(0).getSize();
            }

            List<NPC> npcs = w.getObjects(NPC.class);
            if (npcs.size() >= 1)
            {
                npc1Size = npcs.get(0).getSize();
            }
            if (npcs.size() >= 2)
            {
                npc2Size = npcs.get(1).getSize();
            }
        }

        String text = "Player Size: " + playerSize
                    + "\nNPC 1 Size: " + npc1Size
                    + "\nNPC 2 Size: " + npc2Size;
        GreenfootImage img = new GreenfootImage(text, 20, Color.WHITE, new Color(0, 0, 0, 150));
        setImage(img);
    }
}