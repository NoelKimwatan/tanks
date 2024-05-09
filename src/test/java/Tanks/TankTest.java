package Tanks;
import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import processing.event.KeyEvent;

public class TankTest {

    @Test
    public void testTankInitialisation(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);

        app.delay(3000);    

        Object color = "0,0,0";

        Tank newtank = new Tank(14,App.getGameterrain(),'e',color);
        assertNotNull(newtank);
        app.exit();
    }

    @Test
    public void testTankMovement(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(3000);    

        Object color = "0,0,0";

        Tank newtank = new Tank(14,App.getGameterrain(),'e',color);
        double current  = newtank.getXPosition();  
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        app.delay(1000); 

        assertNotSame(current,newtank.getXPosition() );
        app.exit();
    }

    @Test
    public void testTurretMovement(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(3000);    

        Object color = "0,0,0";

        Tank newtank = new Tank(14,App.getGameterrain(),'e',color);

        double turretangle = newtank.getTurrentAngle();

        //Pressing Up on the keyboard
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        app.delay(2000);
        assertNotSame(turretangle, newtank.getTurrentAngle());
        app.exit();
    }

    @Test
    public void testTankDamage(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(3000);    

        Object color = "0,0,0";

        Tank newtank = new Tank(14,App.getGameterrain(),'e',color);
        Projectile newProjectile = new Projectile(10.0, 10.0,100, 20,App.getGameterrain(),newtank,true);
        newtank.tankDamage(-50,newProjectile);

        double current  = newtank.getXPosition();  
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        app.delay(1000); 

        assertNotSame(current,newtank.getXPosition() );
        app.exit();
        //getTurrentAngle
    }

    @Test
    public void testTankDamageAndScores(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(3000);    

        Object color = "0,0,0";

        //Test adding score 
        Tank tankOne = new Tank(14,App.getGameterrain(),'e',color);
        int tankOneInitialScore = tankOne.getTankScore();
        assertEquals(tankOneInitialScore,0);
        Tank tankTwo = new Tank(16,App.getGameterrain(),'r',color);  
        int tankTwoInitialScore = tankTwo.getTankScore(); 
        assertEquals(tankTwoInitialScore,0);

        //Projectile fired from TankTwo 
        Projectile newProjectile = new Projectile(10.0, 10.0,100, 20,App.getGameterrain(),tankTwo,true);
        tankOne.tankDamage(60,newProjectile); //Projectile from Tank Two damages Tank One

        //Assert score added to Tank Two and not Tank One
        assertEquals(tankOne.getTankScore(),0);
        System.out.println("Tank two score: "+tankTwo.getTankScore()+" Tank two health: "+tankTwo.getTankHealth());
        assertEquals(tankTwo.getTankScore(),60);

        //Assert Health decrease on Tank One and not on tank two
        assertEquals(40,tankOne.getTankHealth());
        assertEquals(0,tankTwo.getTankHealth());
              
    }



    @Test
    public void testTurrentMove(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(3000); 
        
        Object color = "0,0,0";
        
        Tank newtank = new Tank(14,App.getGameterrain(),'e',color);
        assertNotNull(newtank);

        double turrentAngle = newtank.getTurrentAngle();

        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        app.delay(500);
        assertNotSame(turrentAngle,newtank.getTurrentAngle());
        app.exit();
    }

    
}
