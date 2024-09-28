import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;

public class Map
{
    public int map [][];
    public int brickWidth;
    public int brickHeight;


    public Map(int row, int col)
    {
        map = new int [row][col];
        //jeder Brick beginnt mit zwei "Leben"
        for(int i = 0; i < map.length; i++)
        {
            for(int j = 0; j < map[0].length; j++)
            {
                map[i][j] = 2;
            }
        }
        brickWidth = 540/col;
        brickHeight = 150/row;
    }

    public void draw(Graphics2D g)
    {
        for(int i = 0; i < map.length; i++)
        {
            for(int j = 0; j < map[0].length; j++)
            {
                // Unterschiedliche Farbe abhängig von übrigen Leben
                if(map[i][j] > 1)
                {
                    g.setColor(Color.blue);
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.black);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }

                else if(map[i][j] > 0)
                {
                    g.setColor(Color.white);
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.black);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }

    public void setBrickValue(int value, int row, int col)
    {
        map[row][col] = value;
    }

    public int getBrickValue(int row, int col)
    {
        return map[row][col];
    }
}