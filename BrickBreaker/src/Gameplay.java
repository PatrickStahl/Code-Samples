import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.Random;

public class Gameplay extends JPanel implements KeyListener, ActionListener
{
    private boolean play = false;
    private int score = 0;
    private int totalBricks = 21;

    private Timer timer;
    private int delay = 8;

    // Spielfeld generieren
    private int playerX = 310;
    private int ballX = 100;
    private int ballY = 350;
    private int ballXdirection = -3;
    private int ballYdirection = -4;
    private int ballLength = 20;
    private int ballHeight = 20;
    private int playerLength = 100;
    private Map map;

    // PowerUps einbinden
    private boolean powerUpActive = false;
    private int powerUpX, powerUpY;
    private int currentPowerUpType;

    public Gameplay()
    {
        map = new Map(3, 7); // Erstelle Map mit 3 Reihen und 7 Spalten
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g)
    {
        // Hintergrund
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // Map
        map.draw((Graphics2D) g);

        // Rahmen
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(681, 0, 3, 592);

        // Punkte
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("" + score, 590, 30);

        // Plattform
        g.setColor(Color.green);
        g.fillRect(playerX, 550, playerLength, 8);

        // Ball
        g.setColor(Color.yellow);
        g.fillOval(ballX, ballY, ballLength, ballHeight);

        // PowerUp zeichnen
        if (powerUpActive)
        {
            switch (currentPowerUpType)
            {
                case 0: // Plattform vergrößern
                    g.setColor(Color.blue);
                    g.fillRect(powerUpX, powerUpY, 20, 20);
                    break;
                case 1: // Plattform verkleinern
                    g.setColor(Color.red);
                    g.fillOval(powerUpX, powerUpY, 20, 20);
                    break;
                case 2: // Ball verlangsamen
                    g.setColor(Color.white);
                    int[] xPoints = {powerUpX, powerUpX + 20, powerUpX};
                    int[] yPoints = {powerUpY + 20, powerUpY + 10, powerUpY};
                    g.fillPolygon(xPoints, yPoints, 3);
                    break;
            }
        }

        // Überprüfung auf Gewinn/Verlust
        if (totalBricks <= 0)
        {
            play = false;
            ballXdirection = 0;
            ballYdirection = 0;
            g.setColor(Color.green);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Gewonnen! Punktzahl: " + score, 190, 300);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Drücke (Enter) zum Neustarten", 230, 350);
        }

        if (ballY > 570)
        {
            play = false;
            ballXdirection = 0;
            ballYdirection = 0;
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Verloren! Punktzahl: " + score, 190, 300);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Drücke (Enter) zum Neustarten", 230, 350);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        timer.start();
        if (play)
        {
            // Plattform Kollision mit Ball
            if (new Rectangle(ballX, ballY, ballLength, ballHeight).intersects(new Rectangle(playerX, 550, playerLength, 8)))
            {
                ballYdirection = -ballYdirection;
            }

            // PowerUp Bewegung und Kollision mit Plattform
            if (powerUpActive)
            {
                powerUpY += 1; // PowerUp bewegt sich nach unten

                if (new Rectangle(powerUpX, powerUpY, 20, 20).intersects(new Rectangle(playerX, 550, playerLength, 8)))
                {
                    powerUpActive = false;  // PowerUp wurde eingesammelt
                    applyPowerUp(); // Wende das PowerUp an
                }

                if (powerUpY > 570)
                {
                    powerUpActive = false;  // PowerUp fällt aus dem Spielfeld
                }
            }

            // Ball Bewegung
            ballX += ballXdirection;
            ballY += ballYdirection;

            // Ball Kollision mit Rahmen
            if (ballX < 0 || ballX > 670)
            {
                ballXdirection = -ballXdirection;
            }
            if (ballY < 0)
            {
                ballYdirection = -ballYdirection;
            }

            // Ball Kollision mit Blöcken
            for (int i = 0; i < map.map.length; i++)
            {
                for (int j = 0; j < map.map[0].length; j++)
                {
                    if (map.getBrickValue(i, j) > 0)
                    {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballX, ballY, ballLength, ballHeight);

                        if (ballRect.intersects(rect))
                        {
                            map.setBrickValue(map.getBrickValue(i, j) - 1, i, j); // Lebensanzahl von Block reduzieren
                            score += 5;

                            // Überprüfen, ob der Block zerstört wurde
                            if (map.getBrickValue(i, j) <= 0)
                            {
                                totalBricks--;
                                // PowerUp Spawn mit 20% Wahrscheinlichkeit (nur wenn kein anderes aktiv ist)
                                if (!powerUpActive && new Random().nextInt(100) < 20)
                                {
                                    spawnPowerUp(brickX + brickWidth / 2, brickY + brickHeight / 2);
                                }
                            }

                            // Bestimme die Kollision
                            if (ballX + 19 <= rect.x || ballX + 1 >= rect.x + rect.width)
                            {
                                ballXdirection = -ballXdirection;
                            }
                            else
                            {
                                ballYdirection = -ballYdirection;
                            }
                        }
                    }
                }
            }

            repaint();
        }
    }

    // PowerUp Spawn
    public void spawnPowerUp(int x, int y)
    {
        powerUpActive = true;
        powerUpX = x;
        powerUpY = y;
        currentPowerUpType = new Random().nextInt(3); // Zufälliger PowerUpTyp
    }

    // PowerUp anwenden
    public void applyPowerUp()
    {
        switch (currentPowerUpType)
        {
            case 0:
                enlargePaddle();
                break;
            case 1:
                shrinkPaddle();
                break;
            case 2:
                slowBall();
                break;
        }
    }

    // Paddle Vergrößerung
    public void enlargePaddle()
    {
        playerLength += 30; // Vergrößere Plattform
    }

    // Paddle Verkleinerung
    public void shrinkPaddle()
    {
        playerLength = Math.max(20, playerLength - 60); // Verkleinere Plattform, Mindestlänge 20
    }

    // Ball Verlangsamung
    public void slowBall()
    {
        ballXdirection = (int) (ballXdirection * 0.8);
        ballYdirection = (int) (ballYdirection * 0.8);
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            if (playerX >= 600)
            {
                playerX = 600;
            }
            else
            {
                moveRight();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            if (playerX < 10)
            {
                playerX = 10;
            }
            else
            {
                moveLeft();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if (!play)
            {
                play = true;
                ballX = 70 + new Random().nextInt(100);
                ballY = 350;
                ballXdirection = -3;
                ballYdirection = -4;
                playerX = 310;
                playerLength = 100;
                score = 0;
                totalBricks = 21;
                map = new Map(3, 7);

                repaint();
            }
        }
    }

    public void moveRight()
    {
        play = true;
        playerX += 20;
    }

    public void moveLeft()
    {
        play = true;
        playerX -= 20;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
