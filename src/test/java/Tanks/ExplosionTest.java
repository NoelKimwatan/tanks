package Tanks;
import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import processing.event.KeyEvent;

import java.util.*;

public class ExplosionTest {

    public void testSetExplossionCoordinates(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);

        Explosion newExplossion = new Explosion(100, 150, 100);

        //Test co-ordinates
        assertEquals(newExplossion.getXPosition(),100);
        assertEquals(newExplossion.getYPosition(),150);
        assertFalse(newExplossion.isDeleted());

        //Assert there no explossion in Queue
        assertNull(app.getExplossion(0));

        //Add explossion to Queue
        app.addExplosion(newExplossion);

        //Assert there is an explossion in Queue
        assertNotNull(app.getExplossion(0));

        app.delay(2000);

        //Test if explossion is deleted after sometime. After it has expanded
        assertTrue(newExplossion.isDeleted());

        //Reset game to level 0. Done to ensure no error in other Tests
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());
    }
    
}
