import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class DinoGame extends JFrame {
    public DinoGame() {
        setTitle("Java Dino Game");
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel());
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DinoGame::new);
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private int score = 0;
    private boolean gameOver = false;
    private boolean isJumping = false;
    private int dinoY = 200;
    private int dinoVelocity = 0;
    private final int GROUND_Y = 200;
    private final int DINO_X = 50;
    private final int DINO_WIDTH = 40;
    private final int DINO_HEIGHT = 40;
    private ArrayList<Rectangle> obstacles = new ArrayList<>();
    private Random rand = new Random();
    private int obstacleDelay = 0;

    public GamePanel() {
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(20, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw ground
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, GROUND_Y + DINO_HEIGHT, getWidth(), 10);
        // Draw dino
        g.setColor(Color.BLACK);
        g.fillRect(DINO_X, dinoY, DINO_WIDTH, DINO_HEIGHT);
        // Draw obstacles
        g.setColor(Color.GREEN.darker());
        for (Rectangle obs : obstacles) {
            g.fillRect(obs.x, obs.y, obs.width, obs.height);
        }
        // Draw score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 650, 30);
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("GAME OVER", 300, 120);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Press Space to Restart", 310, 150);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            // Dino jump physics
            if (isJumping) {
                dinoVelocity += 1; // gravity
                dinoY += dinoVelocity;
                if (dinoY >= GROUND_Y) {
                    dinoY = GROUND_Y;
                    isJumping = false;
                    dinoVelocity = 0;
                }
            }
            // Move obstacles
            Iterator<Rectangle> iter = obstacles.iterator();
            while (iter.hasNext()) {
                Rectangle obs = iter.next();
                obs.x -= 8;
                if (obs.x + obs.width < 0) {
                    iter.remove();
                    score += 10;
                }
                // Collision
                if (obs.intersects(new Rectangle(DINO_X, dinoY, DINO_WIDTH, DINO_HEIGHT))) {
                    gameOver = true;
                    timer.stop();
                }
            }
            // Add obstacles
            obstacleDelay++;
            if (obstacleDelay > 60 + rand.nextInt(60)) {
                int height = 30 + rand.nextInt(30);
                obstacles.add(new Rectangle(800, GROUND_Y + DINO_HEIGHT - height, 20, height));
                obstacleDelay = 0;
            }
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                // Restart game
                score = 0;
                dinoY = GROUND_Y;
                dinoVelocity = 0;
                obstacles.clear();
                gameOver = false;
                timer.start();
            } else if (!isJumping && dinoY == GROUND_Y) {
                isJumping = true;
                dinoVelocity = -18;
            }
        }
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
