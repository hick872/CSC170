import greenfoot.*;

public class BodyBlock extends World
{
    private static final int GRID_SIZE = 20;
    private static final int CELL_SIZE = 20;
    private static final int OFFSET_X = 100;
    private static final int OFFSET_Y = 0;

    private static final int TOP = 1;
    private static final int RIGHT = 2;
    private static final int BOTTOM = 4;
    private static final int LEFT = 8;

    private int[][] grid = new int[GRID_SIZE][GRID_SIZE];

    public BodyBlock()
    {
        super(600, 400, 1);
        buildMazeLayout();
        spawnWalls();
        prepare();
    }

    private void buildMazeLayout()
    {
        for (int r = 0; r < GRID_SIZE; r++)
        {
            for (int c = 0; c < GRID_SIZE; c++)
            {
                int walls = 0;
                if (r == 0 && c != 10)
                {
                    walls |= TOP;
                }
                if (r == GRID_SIZE - 1 && c != 10)
                {
                    walls |= BOTTOM;
                }
                if (c == 0 && r != 9)
                {
                    walls |= LEFT;
                }
                if (c == GRID_SIZE - 1 && r != 9)
                {
                    walls |= RIGHT;
                }
                grid[r][c] = walls;
            }
        }

        addVerticalWall(0, 1, 3);
        addHorizontalWall(1, 1, 4);
        addVerticalWall(1, 5, 3);
        addHorizontalWall(2, 6, 4);
        addVerticalWall(0, 11, 2);
        addHorizontalWall(1, 12, 4);
        addVerticalWall(1, 16, 3);
        addHorizontalWall(2, 16, 3);

        addHorizontalWall(4, 0, 3);
        addVerticalWall(3, 3, 3);
        addHorizontalWall(5, 4, 4);
        addVerticalWall(4, 8, 4);
        addHorizontalWall(4, 10, 3);
        addVerticalWall(5, 13, 3);
        addHorizontalWall(5, 14, 4);
        addVerticalWall(4, 18, 2);

        addVerticalWall(7, 2, 4);
        addHorizontalWall(8, 3, 3);
        addVerticalWall(7, 6, 3);
        addHorizontalWall(8, 9, 4);
        addVerticalWall(7, 12, 4);
        addHorizontalWall(8, 14, 4);
        addVerticalWall(7, 17, 3);

        addHorizontalWall(11, 1, 4);
        addVerticalWall(10, 5, 4);
        addHorizontalWall(11, 7, 3);
        addVerticalWall(11, 10, 4);
        addHorizontalWall(12, 12, 3);
        addVerticalWall(10, 15, 3);
        addHorizontalWall(11, 16, 3);

        addVerticalWall(14, 1, 3);
        addHorizontalWall(15, 2, 4);
        addVerticalWall(14, 6, 3);
        addHorizontalWall(15, 8, 4);
        addVerticalWall(14, 12, 3);
        addHorizontalWall(15, 13, 4);
        addVerticalWall(14, 17, 3);

        addHorizontalWall(17, 0, 3);
        addVerticalWall(16, 4, 3);
        addHorizontalWall(17, 6, 3);
        addVerticalWall(16, 10, 3);
        addHorizontalWall(17, 11, 4);
        addVerticalWall(16, 15, 3);
        addHorizontalWall(17, 16, 3);
    }

    private void addHorizontalWall(int row, int startCol, int length)
    {
        for (int c = startCol; c < startCol + length && c < GRID_SIZE; c++)
        {
            grid[row][c] |= TOP;
            if (row > 0)
            {
                grid[row - 1][c] |= BOTTOM;
            }
        }
    }

    private void addVerticalWall(int startRow, int col, int length)
    {
        for (int r = startRow; r < startRow + length && r < GRID_SIZE; r++)
        {
            grid[r][col] |= LEFT;
            if (col > 0)
            {
                grid[r][col - 1] |= RIGHT;
            }
        }
    }

    private void spawnWalls()
    {
        int thickness = 4;
        for (int r = 0; r < GRID_SIZE; r++)
        {
            for (int c = 0; c < GRID_SIZE; c++)
            {
                int x0 = OFFSET_X + c * CELL_SIZE;
                int y0 = OFFSET_Y + r * CELL_SIZE;
                int w = grid[r][c];

                if ((w & TOP) != 0)
                {
                    addObject(new Wall(CELL_SIZE + thickness, thickness),
                              x0 + CELL_SIZE / 2, y0 + thickness / 2);
                }
                if ((w & BOTTOM) != 0)
                {
                    addObject(new Wall(CELL_SIZE + thickness, thickness),
                              x0 + CELL_SIZE / 2, y0 + CELL_SIZE - thickness / 2);
                }
                if ((w & LEFT) != 0)
                {
                    addObject(new Wall(thickness, CELL_SIZE + thickness),
                              x0 + thickness / 2, y0 + CELL_SIZE / 2);
                }
                if ((w & RIGHT) != 0)
                {
                    addObject(new Wall(thickness, CELL_SIZE + thickness),
                              x0 + CELL_SIZE - thickness / 2, y0 + CELL_SIZE / 2);
                }
            }
        }
        spawnCornerCaps(thickness);
    }

    private void spawnCornerCaps(int thickness)
    {
        for (int r = 0; r <= GRID_SIZE; r++)
        {
            for (int c = 0; c <= GRID_SIZE; c++)
            {
                boolean hasTopLeft = (r > 0 && c > 0) &&
                    ((grid[r - 1][c - 1] & BOTTOM) != 0 || (grid[r - 1][c - 1] & RIGHT) != 0);
                boolean hasTopRight = (r > 0 && c < GRID_SIZE) &&
                    ((grid[r - 1][c] & BOTTOM) != 0 || (grid[r - 1][c] & LEFT) != 0);
                boolean hasBottomLeft = (r < GRID_SIZE && c > 0) &&
                    ((grid[r][c - 1] & TOP) != 0 || (grid[r][c - 1] & RIGHT) != 0);
                boolean hasBottomRight = (r < GRID_SIZE && c < GRID_SIZE) &&
                    ((grid[r][c] & TOP) != 0 || (grid[r][c] & LEFT) != 0);

                int activeCount = (hasTopLeft ? 1 : 0) + (hasTopRight ? 1 : 0)
                                + (hasBottomLeft ? 1 : 0) + (hasBottomRight ? 1 : 0);

                if (activeCount >= 2)
                {
                    int vx = OFFSET_X + c * CELL_SIZE;
                    int vy = OFFSET_Y + r * CELL_SIZE;
                    addObject(new Wall(thickness, thickness), vx, vy);
                }
            }
        }
    }

    public void teleportIfAtEdge(Actor actor)
    {
        if (actor == null)
        {
            return;
        }

        int leftEdge = OFFSET_X + 2;
        int rightEdge = OFFSET_X + GRID_SIZE * CELL_SIZE - 2;
        int topEdge = OFFSET_Y + 2;
        int bottomEdge = OFFSET_Y + GRID_SIZE * CELL_SIZE - 2;

        if (actor.getX() <= leftEdge)
        {
            actor.setLocation(rightEdge - 6, actor.getY());
        }
        else if (actor.getX() >= rightEdge)
        {
            actor.setLocation(leftEdge + 6, actor.getY());
        }

        if (actor.getY() <= topEdge)
        {
            actor.setLocation(actor.getX(), bottomEdge - 6);
        }
        else if (actor.getY() >= bottomEdge)
        {
            actor.setLocation(actor.getX(), topEdge + 6);
        }
    }

    private void prepare()
    {
        Player player = new Player();
        addObject(player, OFFSET_X + CELL_SIZE * 10 + CELL_SIZE / 2, OFFSET_Y + CELL_SIZE * 10 + CELL_SIZE / 2);

        ScoreBoard scoreBoard = new ScoreBoard();
        addObject(scoreBoard, 75, 45);

        Timer timer = new Timer(600);
        addObject(timer, 550, 30);

        for (int i = 0; i < 2; i++)
        {
            NPC npc = new NPC();
            int x = OFFSET_X + (Greenfoot.getRandomNumber(GRID_SIZE - 2) + 1) * CELL_SIZE + CELL_SIZE / 2;
            int y = OFFSET_Y + (Greenfoot.getRandomNumber(GRID_SIZE - 2) + 1) * CELL_SIZE + CELL_SIZE / 2;
            addObject(npc, x, y);
        }

        int numberOfOrbs = Greenfoot.getRandomNumber(6) + 10;
        for (int i = 0; i < numberOfOrbs; i++)
        {
            Orbs orb = new Orbs();
            int x = OFFSET_X + (Greenfoot.getRandomNumber(GRID_SIZE - 2) + 1) * CELL_SIZE + CELL_SIZE / 2;
            int y = OFFSET_Y + (Greenfoot.getRandomNumber(GRID_SIZE - 2) + 1) * CELL_SIZE + CELL_SIZE / 2;
            addObject(orb, x, y);
        }
    }
}