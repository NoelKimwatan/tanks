package Tanks;
import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import processing.event.KeyEvent;

import java.util.*;

public class ProjectileTest {

    @Test
    public void testWhenProjectileGoesOutside(){
        //Test when a projectile goes outside screen
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);

        Object color = "0,0,0";
        Terrain terrain = app.getTerrain();
        Tank newtank = new Tank(14,terrain,'e',color);

        //---------------------------------------------
        //Test along X-axis-----------------------------
        //---------------------------------------------

        Projectile newProjectile = new Projectile(10.0, 10.0,100, 20,terrain,newtank,false);
        newProjectile.setXPosition(-10);

        //Assert position set to 0
        assertTrue(newProjectile.getXPosition() == 0);
        //assert Projectile deleted
        assertTrue(newProjectile.isNotActive());

        newProjectile = new Projectile(10.0, 10.0,100, 20,terrain,newtank,false);
        newProjectile.setXPosition(App.WIDTH+100);

        //Assert position set to App.WIDTH
        assertTrue(newProjectile.getXPosition() == App.WIDTH);
        //assert Projectile deleted
        assertTrue(newProjectile.isNotActive());

        //---------------------------------------------
        //Test along Y-axis-----------------------------
        //---------------------------------------------

        newProjectile = new Projectile(10.0, 10.0,100, 20,terrain,newtank,false);
        newProjectile.setYPosition(App.HEIGHT+100);

        //Assert position set to App.WIDTH
        assertTrue(newProjectile.getYPosition() == App.HEIGHT);
        //assert Projectile deleted
        assertTrue(newProjectile.isNotActive());

        //Reset game to level 0. Done to ensure no error in other Tests
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());
    }

    @Test
    public void testTankHit(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(4000);

        //Reset game to level 0. Done to ensure no error in other Tests
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());

        Terrain terrain = app.getTerrain();

        Tank firstPlayer = app.getTank(app.getSortedPlayerLetters().get(0));
        Tank secondPlayer = app.getTank(app.getSortedPlayerLetters().get(1));

        int firstPlayerInitialScore = firstPlayer.getTankScore();

        //Assert second player has full health
        assertEquals(100,secondPlayer.getTankHealth());

        System.out.println("Before Second player initial health: "+secondPlayer.getTankHealth()+" Nomber of Parachutes: "+secondPlayer.getParachuteNo());
        //Test First Tank hitting Second Tank. A direct hit by setting projectiles co-ordinates to that of the Tank
        //public Projectile(double xPos, double yPos, double power, double turrentAngle, Terrain terrain, Tank tank, boolean largerProjectile)
        Projectile newProjectile = new Projectile(secondPlayer.getXPosition(), (secondPlayer.getYPosition() - 5),100, 0,terrain,firstPlayer,false);

        //Refresh projectile to impact Tank
        newProjectile.refresh();
        app.delay(2000);

        System.out.println("After Second player initial health: "+secondPlayer.getTankHealth()+" Nomber of Parachutes: "+secondPlayer.getParachuteNo());

        //Assert Second tanks health has decreased 
        assertTrue(secondPlayer.getTankHealth()< 100);
        

        //Assert Score of First player has increased by secondPlayer health decrease
        assertEquals((firstPlayerInitialScore + (100 - secondPlayer.getTankHealth())),firstPlayer.getTankScore());

        //Assertain projectile has been deleted after explodding
        assertTrue(newProjectile.isNotActive());



        //Reset game to level 0. Done to ensure no error in other Tests
        app.resetGame();
        app.delay(4000);

        //Assert game has been reset
        assertTrue(app.isNewGame());
        assertFalse(app.isGameover());
        assertEquals(0,app.getLevel());
    }
}
