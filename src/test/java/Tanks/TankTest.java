package Tanks;
import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import processing.event.KeyEvent;

import java.util.*;

public class TankTest {


    @Test
    public void testTankInitialisation(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);

        //Create Tank Object
        Object color = "0,0,0";
        Terrain terrain = app.getTerrain();
        Tank newtank = new Tank(14,terrain,'e',color);

        assertNotNull(newtank);
        assertNotNull(newtank.toString());

        //Test set Tank to current player and Assert current player is Tank player
        app.setCurrentPlayer(newtank);
        assertSame(app.currentPlayer(),newtank);

        //Reset game to level 0. Done to ensure no error in other Tests
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());
    }

    @Test
    public void testSetTanksXPosition(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);
        Object color = "0,0,0";
        Terrain terrain = app.getTerrain();
        Tank newtank = new Tank(14,terrain,'e',color);

        //Set Tanks position to be to the Right
        newtank.setXPosition(900);

        //Assert value set to 848. 16 pixels away from the edge to ensure Tanks Edges are in screen
        assertTrue(newtank.getXPosition() == (App.WIDTH - (Tank.TANKBOTTOMWIDTH/2)));

        //Set Tanks position to be to the Left
        newtank.setXPosition(-100);
        //Assert value set to 16. 16 pixels away from the edge to ensure Tanks Edges are in screen
        assertTrue(newtank.getXPosition() == (Tank.TANKBOTTOMWIDTH/2));

        newtank = null;
    }

    @Test
    public void testSetTanksYPosition(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);

        Object color = "0,0,0";
        Terrain terrain = app.getTerrain();
        Tank newtank = new Tank(14,terrain,'e',color);

        //Set Tanks Y position to be to below Map
        double initialYPosition = newtank.getYPosition();
        newtank.setYPosition(initialYPosition - 150);

        assertEquals((initialYPosition - 150),newtank.getYPosition());

        //Reset game to level 0. Done to ensure no error in other Tests
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());

    }

    @Test
    public void testTankFalling(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);

        Tank currentPlayer = app.currentPlayer();
        Terrain terrain = app.getTerrain();

        //Set Tank floating 100 pixels
        double positionToSet = currentPlayer.getYPosition() - 100;
        currentPlayer.setYPosition(positionToSet);

        Projectile newProjectile = new Projectile(10.0, 10.0,100, 20,terrain,currentPlayer,false);

        //Assert Tank still floating
        assertEquals(currentPlayer.getYPosition(),positionToSet);

        currentPlayer.checkTankFalling(newProjectile);

        app.delay(1000);

        //Assert Tank has fallen
        assertNotEquals(currentPlayer.getYPosition(),positionToSet);

        //Assert Health has not decreased because of Parachute
        assertEquals(currentPlayer.getTankHealth(),100);

        //Reset game to level 0. Done to ensure no error in other Tests
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());
    }

    @Test
    public void testTankFallingWithoutParachute(){
        //Test Tank falling without Parachute. This will result in a decrease of health

        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);

        Tank currentPlayer = app.currentPlayer();
        Terrain terrain = app.getTerrain();

        int initialHealth = currentPlayer.getTankHealth();

        //Set Parachute No to 0
        currentPlayer.setParachuteNo(0);

        //Set Tank floating 50 pixels
        double positionToSet = currentPlayer.getYPosition() - 50;
        currentPlayer.setYPosition(positionToSet);

        //Assert Tank still floating
        assertEquals(currentPlayer.getYPosition(),positionToSet);

        Projectile newProjectile = new Projectile(10.0, 10.0,100, 20,terrain,currentPlayer,false);
        currentPlayer.checkTankFalling(newProjectile);
        app.delay(3000);

        //Assert Tank has fallen
        assertNotEquals(currentPlayer.getYPosition(),positionToSet);

        //Assert Tank has fallen to Terrains level
        assertEquals(terrain.getTerrainHeight((int)currentPlayer.getXPosition()), currentPlayer.getYPosition());

        //Assert Tank has decreased in health by no of pixels fallen (50)
        assertEquals(currentPlayer.getTankHealth(),(initialHealth - 50));

        //Reset game to level 0. Done to ensure no error in other Tests
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());

    }

    @Test
    public void testSetterMethods(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);

        Terrain terrain = app.getTerrain();
        Object color = "0,0,0";
        Tank tankObject = new Tank(14,terrain,'e',color);

        //Test Parachute No cannot be set to lower than 0 and a valid value
        tankObject.setParachuteNo(-100);
        assertEquals(tankObject.getParachuteNo(),0);
        tankObject.setParachuteNo(4);
        assertEquals(tankObject.getParachuteNo(),4);

        //Test Tank power cannot be set to lower than 0 and a valid value
        tankObject.setTankPower(-50);
        assertEquals(tankObject.getTankPower(),0);
        tankObject.setTankPower(50);
        assertEquals(tankObject.getTankPower(),50);

        //Test Tank Turret limited to -+ 1.57 radians and a valid value
        tankObject.setTurrentAngle(-2);
        assertEquals(tankObject.getTurrentAngle(),-1.57);
        tankObject.setTurrentAngle(2);
        assertEquals(tankObject.getTurrentAngle(),1.57);
        tankObject.setTurrentAngle(0);
        assertEquals(tankObject.getTurrentAngle(),0);

        //Check health cannot be set to over 100 and a valid value
        tankObject.setHealth(+200);
        assertEquals(tankObject.getTankHealth(),100);
        tankObject.setHealth(-50);
        assertEquals(tankObject.getTankHealth(),50);

        //Check score cannot be set to below 0 and a valid value
        tankObject.setTankScore(-200);
        assertEquals(tankObject.getTankScore(),0);
        tankObject.setTankScore(50);
        assertEquals(tankObject.getTankScore(),50);

        //Reset game to level 0. Done to ensure no error in other Tests
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());
    }
    
    @Test
    public void testPowerUps(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        Tank currentPlayer = App.currentPlayer();

        //Increase score to 200 to allow powerups
        currentPlayer.setTankScore(200);

        //---------------------------------------------------------------------
        //Test parachute increase power up
        //---------------------------------------------------------------------
        int initialParachuteNo = currentPlayer.getParachuteNo();
        int initialScore = currentPlayer.getTankScore();

        //When 'p' o nkeyboard is pressed
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 80));
        app.delay(200);
        //Assert parachute increse and score decrease
        System.out.println("Parachute no after 'p' press:"+currentPlayer.getParachuteNo() );
        assertEquals((initialParachuteNo + 1), currentPlayer.getParachuteNo());
        assertEquals((initialScore - 15), currentPlayer.getTankScore());


        //---------------------------------------------------------------------
        //Test repar power up
        //---------------------------------------------------------------------
        initialScore = currentPlayer.getTankScore();
        currentPlayer.setHealth(500); // Set to max health
        currentPlayer.setHealth(-50); //Decrease health by 50
        int initialHealth = currentPlayer.getTankHealth();
        assertEquals(initialHealth,50);

        //When 'r' on the keyboard is pressed
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 82));
        app.delay(200);
        
        //Assert health decrease and points increase

        // Assert score has increased by 20
        assertEquals((initialHealth + 20), currentPlayer.getTankHealth()); 

        // Assert score has decreased by 20
        assertEquals((initialScore - 20), currentPlayer.getTankScore()); 


        //---------------------------------------------------------------------
        //Test add fuel power up
        //---------------------------------------------------------------------
        int initialFuel = currentPlayer.getTankFuelLevel();
        initialScore = currentPlayer.getTankScore();

        //When 'f' on keyboard is pressed
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 70));
        app.delay(200);

        //Assert fuel increase by 200 
        assertEquals((initialFuel + 200), currentPlayer.getTankFuelLevel());
        //Assert points decrease by 10
        assertEquals((initialScore - 10), currentPlayer.getTankScore()); 

        //---------------------------------------------------------------------
        //Test larger projectile
        //---------------------------------------------------------------------

        initialScore = currentPlayer.getTankScore();
        //When 'x' on keyboard is pressed
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 88));
        app.delay(200);

        //Assert score decreased by 20
        assertEquals((initialScore - 20), currentPlayer.getTankScore()); 

        //Simulate Tank fire by pressing space bar
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));

        //Assert fired projectile is Larger
        Projectile firedProjectile = app.getProjectile(0);
        assertTrue(firedProjectile.isLargerProjectile());

        //Reset game to level 0. Done to ensure no error in other Tests
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());
    }

    @Test
    public void testTankMove(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        Tank currentPlayer = App.currentPlayer();

        //---------------------------------------------------------------------
        //Test Tank move forward
        //---------------------------------------------------------------------
        double initialXPosition = currentPlayer.getXPosition();
        KeyEvent forwardKey = new KeyEvent(null, 0, 0, 0, ' ', 39);
        //Press forward
        app.keyPressed(forwardKey);
        app.delay(200);
        //Release forward
        app.keyReleased(forwardKey);
        app.delay(200);

        //Assert Tank has moved forward
        assertTrue(currentPlayer.getXPosition() > initialXPosition);

        //---------------------------------------------------------------------
        //Test Tank move backward
        //---------------------------------------------------------------------
        initialXPosition = currentPlayer.getXPosition();
        KeyEvent backwardKey = new KeyEvent(null, 0, 0, 0, ' ', 37);
        //Press forward
        app.keyPressed(backwardKey);
        app.delay(200);
        //Release forward
        app.keyReleased(backwardKey);
        app.delay(200);

        //Assert Tank has moved backward
        assertTrue(currentPlayer.getXPosition() < initialXPosition);

        //Reset game to level 0. Done to ensure no error in other Tests
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());

    }

    @Test
    public void testTurretkMove(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        Tank currentPlayer = App.currentPlayer();

        //---------------------------------------------------------------------
        //Test Turret move left
        //---------------------------------------------------------------------
        KeyEvent upKeyEvent = new KeyEvent(null, 0, 0, 0, ' ', 38);
        double initialTurretAngle = currentPlayer.getTurrentAngle();

        //Press up
        app.keyPressed(upKeyEvent);
        app.delay(200);
        //Release up
        app.keyReleased(upKeyEvent);
        app.delay(200);

        //Assert Turret has turned to the Left
        assertTrue(initialTurretAngle > currentPlayer.getTurrentAngle());

        //---------------------------------------------------------------------
        //Test Turret move right
        //---------------------------------------------------------------------
        KeyEvent downKeyEvent = new KeyEvent(null, 0, 0, 0, ' ', 40);
        initialTurretAngle = currentPlayer.getTurrentAngle();

        //Press down
        app.keyPressed(downKeyEvent);
        app.delay(200);
        //Release down
        app.keyReleased(downKeyEvent);
        app.delay(200);

        //Assert Turret has turned to the Right
        assertTrue(initialTurretAngle < currentPlayer.getTurrentAngle());

        //----------------------------------------------------------------
        //Reset game to level 0. Done to ensure no error in other Tests
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());
    }

    @Test
    public void testPowerChange(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);

        Tank currentPlayer = App.currentPlayer();

        //---------------------------------------------------------------------
        //Test 'W' press
        //---------------------------------------------------------------------
        KeyEvent wKeyEvent = new KeyEvent(null, 0, 0, 0, ' ', 87);
        double initialTankPower = currentPlayer.getTankPower();
        //Press down
        app.keyPressed(wKeyEvent);
        app.delay(200);
        //Release down
        app.keyReleased(wKeyEvent);
        app.delay(200);
        
        //Assert power has increased
        assertTrue(currentPlayer.getTankPower() > initialTankPower);

        //---------------------------------------------------------------------
        //Test 'S' press
        //---------------------------------------------------------------------

        KeyEvent sKeyEvent = new KeyEvent(null, 0, 0, 0, ' ', 83);
        initialTankPower = currentPlayer.getTankPower();
        //Press down
        app.keyPressed(sKeyEvent);
        app.delay(200);
        //Release down
        app.keyReleased(sKeyEvent);
        app.delay(200);

        //Assert power has decreased
        assertTrue(currentPlayer.getTankPower() < initialTankPower);

        //----------------------------------------------------------------
        //Reset game to level 0. Done to ensure no error in other Tests
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());
    }

    
}
