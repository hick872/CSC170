import greenfoot.*;

public class Orbs extends Actor
{
    public Orbs()
    {
        GreenfootImage img = getImage();
        if (img != null)
        {
            img.scale(10, 10);
            setImage(img);
        }
    }
}