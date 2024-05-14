package Tanks;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import processing.event.KeyEvent;

import java.util.*;

public class AppTest {



    @Test
    public void testNewGameInitiation(){
        System.out.println("App test - Start");
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);

        assertTrue(true);


        //Game is New
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertNotNull(app.currentPlayer());

        //app.exit();
        
    }

    @Test
    public void testLevelChange(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);

        //Test starts at level 0
        System.out.println("Initial level check: "+app.getLevel()); //newGame
        System.out.println("Is new game: "+App.isNewGame());

        int initialLevel = app.getLevel();

    

        //Remove all players except current player
        ArrayList<Tank> aliveplayers= App.getAliveTanks()
;       for(Tank player : aliveplayers){
            if (player != app.currentPlayer()){
                player.setHealth(-100);
            }
        }

        app.delay(1000);

        app.delay(1000);
        

        System.out.println("After level: "+app.getLevel());
        //Test if moved to the next level
        assertEquals(app.getLevel(),(initialLevel + 1));
    }

    @Test
    public void testPlayerChange(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);
        


        char currentPlayer = App.currentPlayer().playerCharacter();

        //Simulate test bar press
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
        app.delay(3000);

        
        assertNotSame(currentPlayer, App.currentPlayer().playerCharacter());
        //app.exit();
        //app.delay(100);
    }

    @Test
    public void testKeyPress(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);

        Tank currentPlayer = App.currentPlayer();
        
        //Test Turrent power increase
        double initialTurretPower = currentPlayer.getTankPower();
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 87));
        app.delay(100);
        assertTrue(initialTurretPower < currentPlayer.getTankPower());

        //Test Turrent power decrease
        initialTurretPower = currentPlayer.getTankPower();
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 83));
        app.delay(100);
        assertTrue(initialTurretPower > currentPlayer.getTankPower());

        //Test powerup key press
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 70));
        int initialFuelLevel = currentPlayer.getTankFuelLevel();
        //Fuel level will not increase since score is 0
        assertEquals(initialFuelLevel,currentPlayer.getTankFuelLevel());
    }

    @Test
    public void testGameOver(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);

        ArrayList<Tank> aliveplayers= app.getAliveTanks();
        int initialLevel = app.getLevel();
        while(!app.isGameover()){
            for(Tank player : aliveplayers){
                if (player != app.currentPlayer()){
                    player.setHealth(-100);
                }
            }

            app.delay(3000); 

            if(!app.isGameover()){
                assertEquals(app.getLevel(),(initialLevel+1));
                initialLevel += 1;
            }
        }


        System.out.println("Level before delay: "+app.getLevel());
        app.delay(5000); 
        System.out.println("Level after delay: "+app.getLevel());
        //Press restart
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 82));

        System.out.println("Level before delay after key press: "+app.getLevel());
        app.delay(5000); 
        System.out.println("Level after delay after key press : "+app.getLevel());
        assertEquals(0,app.getLevel());
    }


}
