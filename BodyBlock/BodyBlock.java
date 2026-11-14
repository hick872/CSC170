import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class BodyBlock here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BodyBlock extends World
{

    /**
     * Constructor for objects of class BodyBlock.
     * 
     */
    public BodyBlock()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(600, 400, 1);
        prepare();
    }
    private void prepare() {
        Player player = new Player();
        addObject(player, 300, 200);
        
        //NPC spawn
        for (int i = 0; i < 2; i++) {
            NPC npc = new NPC();
            int x = Greenfoot.getRandomNumber(getWidth());
            int y = Greenfoot.getRandomNumber(getHeight());
            addObject(npc, x, y);
        }
        
        //Orb spawn
        int numberOfOrbs = Greenfoot.getRandomNumber(6) + 3; // Generates a random count from 3 to 8
        
        for (int i = 0; i < numberOfOrbs; i++)
        {
            Orbs orb = new Orbs();
            int x = Greenfoot.getRandomNumber(getWidth());
            int y = Greenfoot.getRandomNumber(getHeight());
            addObject(orb, x, y);
        }
    }
}
