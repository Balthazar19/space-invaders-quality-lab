package Unit;

import coursework.GameBoard;
import org.junit.jupiter.api.Test;
import coursework.Bullet;

import static org.junit.jupiter.api.Assertions.*;

public class BulletTest {
    @Test
    public void isOutOfBounds() {
        int height = GameBoard.boardHeight;
        Bullet bullet = new Bullet(0, 0, 0);
        boolean outOfBounds = bullet.getY() < 0 || bullet.getY() > height;
        assertFalse(outOfBounds, "Bullet is out of bounds: " + bullet.getY());

    }
    @Test
    public void testBulletMovement() {
        Bullet bullet = new Bullet(0, 0, 5);
        bullet.move();
        assertEquals(5, bullet.getY(), "Bullet did not move correctly.");
    }

    @Test
    public void testBulletUsage() {
        Bullet bullet = new Bullet(0, 0, 0);
        //bullet.setUsed(true);
        assertFalse(bullet.isUsed(), "Bullet should not be used initially.");
        bullet.setUsed(true);
        assertTrue(bullet.isUsed(), "Bullet should be marked as used.");
    }
}