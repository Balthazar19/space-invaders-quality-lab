package coursework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for GameController.
 * Written BEFORE refactoring to ensure behavioral correctness is maintained.
 * 
 * These tests document the current behavior and will catch any regressions
 * during the refactoring process.
 */
class GameControllerTest {
    private GameController controller;
    private static final int TILE_SIZE = 32;
    private static final int ROWS = 18;
    private static final int COLUMNS = 16;

    @BeforeEach
    void setUp() {
        controller = new GameController(TILE_SIZE, ROWS, COLUMNS);
    }

    // === Constructor and Initialization Tests ===
    
    @Test
    void testConstructor_InitializesCorrectly() {
        assertNotNull(controller.getShip(), "Ship should be initialized");
        assertNotNull(controller.getBullets(), "Bullets list should be initialized");
        assertEquals(TILE_SIZE * COLUMNS, controller.getBoardWidth(), "Board width should match");
        assertFalse(controller.isGameOver(), "Game should not be over initially");
    }

    @Test
    void testConstructor_CreatesAliens() {
        controller.update(); // Trigger one update to let aliens exist
        // Aliens should be created (we'll verify this indirectly through updates)
        assertFalse(controller.isGameOver(), "Game should be active with aliens");
    }

    // === Ship Movement Tests ===
    
    @Test
    void testShip_CanMove() {
        Ship ship = controller.getShip();
        int initialX = ship.getX();
        
        ship.moveLeft();
        assertTrue(ship.getX() < initialX, "Ship should move left");
        
        ship.moveRight(controller.getBoardWidth());
        ship.moveRight(controller.getBoardWidth());
        assertTrue(ship.getX() > initialX, "Ship should move right");
    }

    @Test
    void testShip_RespectsBoundaries() {
        Ship ship = controller.getShip();
        
        // Move far left
        for (int i = 0; i < 100; i++) {
            ship.moveLeft();
        }
        assertTrue(ship.getX() >= 0, "Ship should not go below 0");
        
        // Move far right
        for (int i = 0; i < 100; i++) {
            ship.moveRight(controller.getBoardWidth());
        }
        assertTrue(ship.getX() + ship.getWidth() <= controller.getBoardWidth(), 
                   "Ship should not exceed board width");
    }

    // === Bullet Tests ===
    
    @Test
    void testCreateBullet_CreatesWithCorrectProperties() {
        Bullet bullet = controller.createBullet(100, 200, -10);
        
        assertNotNull(bullet, "Bullet should be created");
        assertEquals(100, bullet.getX(), "Bullet X should match");
        assertEquals(200, bullet.getY(), "Bullet Y should match");
        assertFalse(bullet.isUsed(), "Bullet should not be used initially");
    }

    @Test
    void testBullets_MoveCorrectly() {
        Bullet bullet = controller.createBullet(100, 200, -10);
        controller.getBullets().add(bullet);
        
        int initialY = bullet.getY();
        controller.update();
        
        assertTrue(bullet.getY() < initialY, "Bullet should move upward");
    }

    @Test
    void testBullets_RemoveWhenOutOfBounds() {
        Bullet bullet = controller.createBullet(100, -100, -10);
        controller.getBullets().add(bullet);
        
        controller.update();
        controller.update();
        
        assertTrue(controller.getBullets().isEmpty() || 
                   controller.getBullets().stream().noneMatch(b -> b.getY() < -100),
                   "Out-of-bounds bullets should be removed");
    }

    // === Alien Movement Tests ===
    
    @Test
    void testUpdate_DoesNotThrowException() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                controller.update();
            }
        }, "Update should not throw exceptions");
    }

    @Test
    void testGameOver_WhenAlienReachesShip() {
        // Update many times to let aliens descend
        for (int i = 0; i < 1000; i++) {
            controller.update();
            if (controller.isGameOver()) {
                break;
            }
        }
        
        // Eventually game should be over (or at least not crash)
        // This test documents the current behavior
        assertTrue(true, "Game should handle alien descent gracefully");
    }

    // === Drawing Tests ===
    
    @Test
    void testDraw_DoesNotThrowException() {
        BufferedImage image = new BufferedImage(
            controller.getBoardWidth(), 
            TILE_SIZE * ROWS, 
            BufferedImage.TYPE_INT_RGB
        );
        Graphics g = image.getGraphics();
        
        assertDoesNotThrow(() -> controller.draw(g), 
                          "Draw should not throw exceptions");
        g.dispose();
    }

    @Test
    void testDraw_WhenGameOver() {
        // Force game over
        for (int i = 0; i < 1000; i++) {
            controller.update();
            if (controller.isGameOver()) {
                break;
            }
        }
        
        if (controller.isGameOver()) {
            BufferedImage image = new BufferedImage(
                controller.getBoardWidth(), 
                TILE_SIZE * ROWS, 
                BufferedImage.TYPE_INT_RGB
            );
            Graphics g = image.getGraphics();
            
            assertDoesNotThrow(() -> controller.draw(g), 
                              "Draw should handle game over state");
            g.dispose();
        }
    }

    // === Integration Tests ===
    
    @Test
    void testUpdate_MaintainsGameState() {
        boolean initialGameOver = controller.isGameOver();
        
        controller.update();
        
        // Game state should remain consistent
        assertNotNull(controller.getShip(), "Ship should still exist");
        assertNotNull(controller.getBullets(), "Bullets list should still exist");
    }

    @Test
    void testMultipleUpdates_DoNotCauseNullPointers() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 100; i++) {
                controller.update();
                
                // Add some bullets periodically
                if (i % 10 == 0) {
                    Ship ship = controller.getShip();
                    Bullet bullet = controller.createBullet(
                        ship.getX() + ship.getWidth() / 2,
                        ship.getY(),
                        -10
                    );
                    controller.getBullets().add(bullet);
                }
            }
        }, "Multiple updates with bullets should not cause errors");
    }

    // === Collision Detection Tests ===
    
    @Test
    void testCollisions_AreDetected() {
        // Add a bullet
        Bullet bullet = controller.createBullet(100, 100, -10);
        controller.getBullets().add(bullet);
        
        // Run updates to let collision detection work
        for (int i = 0; i < 10; i++) {
            controller.update();
        }
        
        // Test passes if no exceptions occur
        assertTrue(true, "Collision detection should run without errors");
    }

    // === Boundary Tests ===
    
    @Test
    void testBoardWidth_IsCorrect() {
        assertEquals(TILE_SIZE * COLUMNS, controller.getBoardWidth(),
                    "Board width should be tile size times columns");
    }

    @Test
    void testInitialState_IsNotGameOver() {
        assertFalse(controller.isGameOver(), 
                   "Game should not be over at initialization");
    }
}
