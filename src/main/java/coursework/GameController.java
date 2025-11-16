package coursework;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GameController {
    // Constants for magic numbers - improves readability and maintainability
    private static final int ALIEN_SHOOT_PROBABILITY = 2;  // 2% chance per frame
    private static final int ALIEN_SHOOT_RANGE = 100;
    private static final int DIRECTION_MULTIPLIER = -1;
    private static final int SCORE_PER_ALIEN = 100;
    private static final int LEFT_BOUNDARY = 0;
    
    private int tileSize, boardWidth, boardHeight;
    private Ship ship;
    private ArrayList<Alien> aliens;
    private ArrayList<Bullet> bullets;
    private ArrayList<Bullet> alienBullets;
    private int alienColumns = 10, alienRows = 5;
    private int score = 0;
    private boolean gameOver = false;
    private int alienVelocityX = 2;
    private Random random = new Random();
    private int highScore = 0;

    private Image shipImage;
    private Image alienImage;

    private AlienFactory alienFactory;

    public GameController(int tileSize, int rows, int columns) {
        this.tileSize = tileSize;
        this.boardWidth = tileSize * columns;
        this.boardHeight = tileSize * rows;

        // Use classpath resources when available; fall back to a placeholder image so tests/CI don't depend on absolute paths.
        shipImage = loadImage("/coursework/ship.png", tileSize, tileSize);
        alienImage = loadImage("/coursework/alien.png", tileSize * 2, tileSize);

        ship = new Ship(tileSize, boardWidth, boardHeight, shipImage);
        aliens = new ArrayList<>();
        bullets = new ArrayList<>();
        alienBullets = new ArrayList<>();

        alienFactory = new StandardAlienFactory();
        createAliens();
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public void update() {
        if (!gameOver) {
            moveAliens();
            moveBullets();
            moveAlienBullets();
            checkCollisions();
            checkGameStatus();
        }
    }

    /**
     * Factory method to create a bullet.
     * @param x initial X position
     * @param y initial Y position
     * @param velocityY vertical velocity
     * @return new Bullet instance
     */
    public Bullet createBullet(int x, int y, int velocityY) {
        return new Bullet(x, y, velocityY);
    }

    private void moveAlienBullets() {
        cleanupBullets(alienBullets);
        for (Bullet bullet : alienBullets) {
            bullet.move();
        }
    }

    private void createAliens() {
        for (int c = 0; c < alienColumns; c++) {
            for (int r = 0; r < alienRows; r++) {
                Alien alien = alienFactory.createAlien(tileSize + c * tileSize * 2, tileSize + r * tileSize, tileSize * 2, tileSize, alienImage, this);
                aliens.add(alien);
            }
        }
    }

    /**
     * Manages all alien-related updates: movement, boundaries, shooting, and game over conditions.
     * Refactored to follow SRP - delegates to specialized helper methods.
     */
    private void moveAliens() {
        updateAlienPositions();
        handleAlienShooting();
    }
    
    /**
     * Updates alien positions and handles boundary detection.
     * Applies SRP: Single responsibility for position updates.
     */
    private void updateAlienPositions() {
        for (Alien alien : aliens) {
            if (alien.isAlive()) {
                alien.move(alienVelocityX);
                
                if (isAlienAtBorder(alien)) {
                    reverseAlienDirection();
                    moveAllAliensDown();
                    break; // Only reverse once per frame
                }
                
                if (hasAlienReachedShip(alien)) {
                    gameOver = true;
                }
            }
        }
    }
    
    /**
     * Checks if an alien has reached the screen boundaries.
     * Extract Method refactoring: improves readability and testability.
     *
     * @param alien the alien to check
     * @return true if alien is at left or right boundary
     */
    private boolean isAlienAtBorder(Alien alien) {
        return alien.getX() + alien.getWidth() >= boardWidth || 
               alien.getX() <= LEFT_BOUNDARY;
    }
    
    /**
     * Reverses the horizontal movement direction of all aliens.
     * Extract Constant: Uses DIRECTION_MULTIPLIER instead of magic number -1.
     */
    private void reverseAlienDirection() {
        alienVelocityX *= DIRECTION_MULTIPLIER;
    }
    
    /**
     * Moves all aliens down by one tile height.
     * Extract Method refactoring: separates movement logic from boundary checking.
     */
    private void moveAllAliensDown() {
        for (Alien alien : aliens) {
            alien.moveDown(tileSize);
        }
    }
    
    /**
     * Checks if an alien has reached the ship's vertical position.
     * Extract Method: makes game-over condition explicit and testable.
     *
     * @param alien the alien to check
     * @return true if alien has reached ship
     */
    private boolean hasAlienReachedShip(Alien alien) {
        return alien.getY() >= ship.getY();
    }
    
    /**
     * Handles alien shooting behavior.
     * Extract Method refactoring + Extract Constant: uses named constants for probability.
     * Applies SRP: Single responsibility for shooting logic.
     */
    private void handleAlienShooting() {
        for (Alien alien : aliens) {
            if (alien.canShoot(ship) && shouldAlienShoot()) {
                alienBullets.add(alien.shoot());
            }
        }
    }
    
    /**
     * Determines if an alien should shoot based on random probability.
     * Extract Method: makes probability check explicit and configurable.
     *
     * @return true if alien should shoot this frame
     */
    private boolean shouldAlienShoot() {
        return random.nextInt(ALIEN_SHOOT_RANGE) < ALIEN_SHOOT_PROBABILITY;
    }

    private void moveBullets() {
        // previously cleaned alienBullets here by mistake; ensure we clean player bullets
        cleanupBullets(bullets);
        for (Bullet bullet : bullets) {
            bullet.move();
        }
    }

    /**
     * Handles collision detection for player bullets and alien bullets.
     * Refactored to follow SRP - delegates to specialized methods.
     */
    private void checkCollisions() {
        checkPlayerBulletCollisions();
        checkAlienBulletCollisions();
    }
    
    /**
     * Checks collisions between player bullets and aliens.
     * Extract Method refactoring: Single responsibility for player bullet logic.
     * Removes nested loops and break statement for better readability.
     */
    private void checkPlayerBulletCollisions() {
        for (Bullet bullet : bullets) {
            if (!bullet.isUsed()) {
                checkBulletAgainstAliens(bullet);
            }
        }
    }
    
    /**
     * Checks a single bullet against all aliens.
     * Further extraction to reduce nesting and improve testability.
     *
     * @param bullet the bullet to check
     */
    private void checkBulletAgainstAliens(Bullet bullet) {
        for (Alien alien : aliens) {
            if (alien.isAlive() && detectCollision(bullet, alien)) {
                handleAlienHit(bullet, alien);
                break; // Stop checking other aliens for this bullet
            }
        }
    }
    
    /**
     * Handles the outcome when an alien is hit by a player bullet.
     * Extract Method: Separates collision consequences from detection.
     * Uses SCORE_PER_ALIEN constant instead of magic number.
     *
     * @param bullet the bullet that hit
     * @param alien the alien that was hit
     */
    private void handleAlienHit(Bullet bullet, Alien alien) {
        bullet.setUsed(true);
        alien.setAlive(false);
        score += SCORE_PER_ALIEN;
    }
    
    /**
     * Checks collisions between alien bullets and the player ship.
     * Extract Method refactoring: Single responsibility for alien bullet logic.
     */
    private void checkAlienBulletCollisions() {
        for (Bullet bullet : alienBullets) {
            if (!bullet.isUsed() && detectCollision(bullet, ship)) {
                handleShipHit(bullet);
            }
        }
    }
    
    /**
     * Handles the outcome when the ship is hit by an alien bullet.
     * Extract Method: Makes game-over trigger explicit.
     *
     * @param bullet the bullet that hit the ship
     */
    private void handleShipHit(Bullet bullet) {
        bullet.setUsed(true);
        gameOver = true;
    }

    private boolean detectCollision(Bullet bullet, Block block) {
        Rectangle bulletRect = new Rectangle(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
        Rectangle blockRect = new Rectangle(block.getX(), block.getY(), block.getWidth(), block.getHeight());
        return bulletRect.intersects(blockRect);
    }

    private void checkGameStatus() {
        if (aliens.stream().noneMatch(Alien::isAlive)) {
            alienColumns++;
            alienRows++;
            aliens.clear();
            bullets.clear();
            createAliens();
        }

        if (score > highScore) {
            highScore = score;
        }
    }
    /**
     * Removes out-of-bounds and used bullets from a list.
     * Extract Method: Eliminates duplication in bullet cleanup logic.
     *
     * @param bulletList the list of bullets to clean up
     */
    private void cleanupBullets(ArrayList<Bullet> bulletList) {
        bulletList.removeIf(bullet -> bullet.isOutOfBounds(boardHeight) || bullet.isUsed());
    }

    /**
     * Main drawing method - delegates to appropriate state-specific drawing.
     * Refactored to reduce cognitive complexity: split game and game-over drawing.
     * Applies SRP: delegates to specialized drawing methods.
     */
    public void draw(Graphics g) {
        if (gameOver) {
            drawGameOverScreen(g);
        } else {
            drawActiveGame(g);
        }
    }
    
    /**
     * Draws the game over screen with scores.
     * Extract Method: Single responsibility for game-over rendering.
     */
    private void drawGameOverScreen(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, boardWidth, boardHeight);
        drawGameOverScores(g);
    }
    
    /**
     * Draws the active game state: ship, aliens, bullets, and score.
     * Extract Method: Single responsibility for active game rendering.
     * Further delegates to specialized entity drawing methods.
     */
    private void drawActiveGame(Graphics g) {
        ship.draw(g);
        drawAliens(g);
        drawPlayerBullets(g);
        drawAlienBullets(g);
        drawScore(g);
    }
    
    /**
     * Draws all alive aliens.
     * Extract Method: Separates alien rendering logic.
     */
    private void drawAliens(Graphics g) {
        for (Alien alien : aliens) {
            if (alien.isAlive()) {
                alien.draw(g);
            }
        }
    }
    
    /**
     * Draws all active player bullets.
     * Extract Method: Separates player bullet rendering.
     */
    private void drawPlayerBullets(Graphics g) {
        for (Bullet bullet : bullets) {
            if (!bullet.isUsed()) {
                bullet.draw(g);
            }
        }
    }
    
    /**
     * Draws all active alien bullets.
     * Extract Method: Separates alien bullet rendering.
     */
    private void drawAlienBullets(Graphics g) {
        for (Bullet bullet : alienBullets) {
            if (!bullet.isUsed()) {
                bullet.draw(g);
            }
        }
    }

    private void drawGameOverScores(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        String gameOverText = "Game Over";
        String currentScoreText = "Score: " + score;
        String highScoreText = "High Score: " + highScore;

        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int gameOverWidth = metrics.stringWidth(gameOverText);
        int currentScoreWidth = metrics.stringWidth(currentScoreText);
        int highScoreWidth = metrics.stringWidth(highScoreText);

        int xGameOver = (boardWidth - gameOverWidth) / 2;
        int xCurrentScore = (boardWidth - currentScoreWidth) / 2;
        int xHighScore = (boardWidth - highScoreWidth) / 2;

        g.drawString(gameOverText, xGameOver, boardHeight / 2 - 40);
        g.drawString(currentScoreText, xCurrentScore, boardHeight / 2);
        g.drawString(highScoreText, xHighScore, boardHeight / 2 + 40);
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        String scoreText = "" + score;

        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int stringWidth = metrics.stringWidth(scoreText);

        int x = (boardWidth - stringWidth) / 2;

        g.drawString(scoreText, x, 35);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Ship getShip() {
        return ship;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    /**
     * Loads an image from the classpath. If not found or an error occurs,
     * returns a simple placeholder BufferedImage so tests and CI don't fail.
     */
    private Image loadImage(String resourcePath, int defaultWidth, int defaultHeight) {
        URL url = getClass().getResource(resourcePath);
        if (url != null) {
            try {
                BufferedImage img = ImageIO.read(url);
                if (img != null) {
                    return img;
                }
            } catch (IOException ignored) {
                // fall through to placeholder
            }
        }

        // Create a simple placeholder image (transparent or solid color)
        BufferedImage placeholder = new BufferedImage(defaultWidth, defaultHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = placeholder.createGraphics();
        try {
            g2.setColor(Color.MAGENTA); // visible fallback color for debugging
            g2.fillRect(0, 0, defaultWidth, defaultHeight);
        } finally {
            g2.dispose();
        }
        return placeholder;
    }
}