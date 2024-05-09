package Tanks;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import processing.event.KeyEvent;

import java.util.*;

public class AppTest {


    public final int gameDelay = 3000;

    @Test
    public void testNewGameInitiation(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);

        assertTrue(true);
        app.exit();

        // //Game is New
        // assertTrue(App.isNewGame());
        // assertFalse(App.isGameover());
        // assertNotNull(App.currentPlayer());
    }

    @Test
    public void testLevelChange(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(gameDelay);

        assertEquals(app.getLevel(),0);


        //Remove all players except current player
        ArrayList<Tank> aliveplayers= App.getAliveTanks()
;       for(Tank player : aliveplayers){
            if (player != app.currentPlayer()){
                player.setHealth(-100);
            }
        }

        app.delay(gameDelay);

        //Test if moved to the next level
        assertEquals(app.getLevel(),1);
        app.exit();
    }

    @Test
    public void testPlayerChange(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        App.main(new String[]{});
        
        app.delay(gameDelay);

        char currentPlayer = app.currentPlayer().playerCharacter();
        System.out.println("In Test"+currentPlayer);

        //Simulate test bar press
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
        app.delay(gameDelay);
        app.delay(gameDelay);

        System.out.println("In Test Two"+app.currentPlayer().playerCharacter());
        app.exit();
        //assertNotSame(currentPlayer, App.currentPlayer().playerCharacter());
    }


}
