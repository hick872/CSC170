import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Timer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Timer extends Actor
{
    private int secondsRemaining;
    private int tickCounter = 0;
    private final int TICKS_PER_SECOND = 60;

    public Timer(int startSeconds)
    {
        this.secondsRemaining = Math.max(0, startSeconds);
        updateImage();
    }

    public void act()
    {
        if (secondsRemaining <= 0)
        {
            Greenfoot.stop();
            return;
        }

        tickCounter++;
        if (tickCounter >= TICKS_PER_SECOND)
        {
            tickCounter = 0;
            secondsRemaining--;
            updateImage();

            if (secondsRemaining <= 0)
            {
                Greenfoot.stop();
            }
        }
    }

    public int getSecondsRemaining()
    {
        return secondsRemaining;
    }

    private void updateImage()
    {
        GreenfootImage img = new GreenfootImage("TIME: " + secondsRemaining, 24, Color.WHITE, new Color(0, 0, 0, 150));
        setImage(img);
    }
}
