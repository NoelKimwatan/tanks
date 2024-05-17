package Tanks;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import processing.event.KeyEvent;

import java.util.*;

public class AppTest {



    @Test
    public void testNewGameInitiation(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);

        assertTrue(true);

        //Game is New
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertNotNull(app.currentPlayer());
    }

    @Test
    public void testLevelChange(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);


        app.resetGame();
        app.delay(5000);

        //Test starts at level 0
        System.out.println("Initial level check: "+app.getLevel()); //newGame
        System.out.println("Is new game: "+app.isNewGame());

        int initialLevel = app.getLevel();
        System.out.println("Initial level check: "+initialLevel); //newGame

    

        //Remove all players except current player
        ArrayList<Tank> aliveplayers= app.getAliveTanks()
;       for(Tank player : aliveplayers){
            if (player != app.currentPlayer()){
                player.setHealth(-100);
            }
        }

        app.delay(1000);

        app.delay(1000);
        

        System.out.println("After level: "+app.getLevel());
        //Test if moved to the next level
        assertEquals((initialLevel + 1),app.getLevel());

        app.resetGame();
        app.delay(5000);

    }

    @Test
    public void testPlayerChange(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);
        
        Terrain terrain = app.getTerrain();
        terrain.resetTanks();
        app.delay(3000);

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

        Terrain terrain = app.getTerrain();
        terrain.resetTanks();
        app.delay(3000);

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

        //Test back key press
        double initialPosition = currentPlayer.getXPosition();
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 37));
        app.delay(100);
        assertTrue(currentPlayer.getXPosition() < initialPosition);

        //Test back key press
        double initialTurretAngle = currentPlayer.getTurrentAngle();
        System.out.println("Initial Turret angle: "+initialTurretAngle);
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 40));
        app.delay(100);
        System.out.println("After Turret angle: "+currentPlayer.getTurrentAngle());
        assertTrue(currentPlayer.getTurrentAngle() < initialPosition);
    }

    @Test
    public void testGameOver(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);

        app.resetGame();
        app.delay(5000);

        ArrayList<Tank> aliveplayers= app.getAliveTanks();
        int initialLevel = app.getLevel();
        while(!app.isGameover()){
            for(Tank player : aliveplayers){
                if (player != app.currentPlayer()){
                    player.setHealth(-100);
                }
                app.delay(100);

            }

            app.delay(3000); 

            if(!app.isGameover()){
                System.out.println("Game level:"+initialLevel);
                assertEquals(app.getLevel(),(initialLevel+1));
                initialLevel += 1;
            }
        }


        app.delay(5000); 

        //Press restart
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 82));


        app.delay(5000); 

        assertEquals(0,app.getLevel());

        app.resetGame();
        app.delay(5000);
    }

    @Test
    public void testExplossionQueue(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);

        Terrain terrain = app.getTerrain();
        Object color = "0,0,0";

        
        
        //Tank(int initialXPosition, Terrain t, char player, Object colour)
        Tank createdTank = new Tank(50,terrain,'N',color);
        System.out.println("Test");

        //assertExplossionQueue is empty
        assertNull(app.getExplossion(0));


        //Destroy tank
        createdTank.setHealth(-100);

        assertNotNull(app.getExplossion(0));

        app.drawTerrain();
        app.delay(4000);


    }
}
