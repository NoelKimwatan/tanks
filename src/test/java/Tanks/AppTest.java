package Tanks;
import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import processing.event.KeyEvent;

import java.util.*;

public class AppTest {

    //Test game is properly initialised
    @Test
    public void testGameInitialization(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);

        //Assert Terrain properly initialised.
        assertNotNull(app.getTerrain());

        //Assert game initialised and starts with game not over
        assertFalse(app.isGameover()); //Test 

        //Assert can get Tanks
        assertNotNull(app.getTank(app.getSortedPlayerLetters().get(0)));

        //Assert there is a current player
        assertNotNull(app.currentPlayer());

        //test Tanks are alive
        assertTrue(app.getAliveTanks().size() > 1);

        //test get level
        assertTrue(app.getLevel() >= 0 &&  app.getLevel() <= 2);

        //Test projectile Queue is empty
        assertNull(app.getProjectile(0));

        //Test Explossion Queue is empty
        assertNull(app.getExplossion(0));
        
    }


    //Test set current player and change current player
    @Test
    public void testChangePlayer(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);

        Tank currentPlayer = app.currentPlayer();

        //Change current player
        app.changeCurrentPlayer();

        //Assert player has changed
        assertNotSame(currentPlayer,app.currentPlayer());
        
        //Reset game to level 0
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());
    }

    @Test
    public void testResetGame(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);
        
        app.resetGame();
        app.delay(4000);

        //Assert level is reset to 0
        assertTrue(app.getLevel() == 0);

        //Assert game is not over
        assertFalse(app.isGameover());

        //Assert is new game
        assertTrue(app.isNewGame());

    }


    @Test
    public void testChangeLevelAndGameReset(){
        //Test game resets after all levels finish
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);

        while(!app.isGameover()){
            System.out.println("Change level loop");

            int initialLevel = app.getLevel();
            app.changeLevel();
            app.delay(2000);

            //assertLevel Has changed if game is Not over
            if(!app.isGameover()){
                assertNotEquals(initialLevel,app.getLevel());
            }
            
        }

        //assert Game is Over
        assertTrue(app.isGameover());

        //assert Game is on level 2
        assertEquals(app.getLevel(),2);
        assertEquals(app.getLevel(),2);
        

        //Reset game after game
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 82));
        app.delay(3000);

        //Assert is new game
        assertTrue(app.isNewGame());

        //Assert game is not over
        assertFalse(app.isGameover());
    }

    @Test
    public void testLevelChangeWhenPlayersdestroyed(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);

        ArrayList<Tank> Tanks = app.getAliveTanks();
        int initialLevel = app.getLevel();

        for (Tank tank: Tanks){
            //Destroy a Tank
            //app.delay(100);
            tank.setHealth(-100);
        }

        //Change level when no player alive
        app.delay(3000);
        app.changeCurrentPlayer();

        //Assert level has changed because app players are destroyed
        assertNotEquals(initialLevel,app.getLevel());

        //Reset game to level 0
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());
    }

    @Test
    public void testLevelChangeWhenAllButOneplayerDies(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);

        ArrayList<Tank> Tanks = app.getAliveTanks();
        int initialLevel = app.getLevel();

        for (Tank tank: Tanks){
            //Destroy a Tank
            if(tank != app.currentPlayer()){
                tank.setHealth(-100);
            }
        }


        app.delay(2000);

        app.changeCurrentPlayer();
        assertNotEquals(initialLevel,app.getLevel());

        //Reset game to level 0
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());
    }
    
}
