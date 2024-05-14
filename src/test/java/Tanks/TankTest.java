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
        app.delay(5000);

        Object color = "0,0,0";

        Tank newtank = new Tank(14,App.getGameterrain(),'e',color);
        assertNotNull(newtank);
        //app.exit();
    }

    @Test
    public void testTankMovement(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);  

        Object color = "0,0,0";

        Tank newtank = new Tank(14,app.getGameterrain(),'e',color);
        double current  = newtank.getXPosition();  
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        app.delay(1000); 

        assertNotSame(current,newtank.getXPosition() );
        //app.exit();
    }

    @Test
    public void testTurretMovement(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);  

        Object color = "0,0,0";

        Tank newtank = new Tank(14,app.getGameterrain(),'e',color);

        double turretangle = newtank.getTurrentAngle();

        //Pressing Up on the keyboard
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        app.delay(2000);
        assertNotSame(turretangle, newtank.getTurrentAngle());
        //app.exit();
    }

    @Test
    public void testTankDamage(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000); 

        Object color = "0,0,0";

        Tank newtank = new Tank(14,App.getGameterrain(),'e',color);
        Projectile newProjectile = new Projectile(10.0, 10.0,100, 20,app.getGameterrain(),newtank,true);
        newtank.tankDamage(-50,newProjectile);

        double current  = newtank.getXPosition();  
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        app.delay(1000); 

        assertNotSame(current,newtank.getXPosition() );
        //app.exit();
        //getTurrentAngle
    }

    @Test
    public void testTankDamageAndScores(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000); 

        Object color = "0,0,0";

        //Initialise Tank one
        Tank tankOne = new Tank(14,app.getGameterrain(),'e',color);
        int tankOneInitialScore = tankOne.getTankScore();
        assertEquals(tankOneInitialScore,0);

        //Initialise Tank two
        Tank tankTwo = new Tank(16,app.getGameterrain(),'r',color);  
        int tankTwoInitialScore = tankTwo.getTankScore(); 
        assertEquals(tankTwoInitialScore,0);

        //Projectile fired from TankTwo 
        Projectile newProjectile = new Projectile(10.0, 10.0,100, 20,app.getGameterrain(),tankTwo,true);
        tankOne.tankDamage(60,newProjectile); //Projectile from Tank Two damages Tank One

        //Assert score added to Tank Two and not Tank One
        assertEquals(tankOne.getTankScore(),0);
        assertEquals(tankTwo.getTankScore(),60);

        //Assert Health decrease on Tank One and not on tank two
        assertEquals(40,tankOne.getTankHealth());
        assertEquals(100,tankTwo.getTankHealth());
    }



    @Test
    public void testTurrentMove(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);
        
        Object color = "0,0,0";
        
        Tank newtank = new Tank(14,app.getGameterrain(),'e',color);
        assertNotNull(newtank);

        double turrentAngle = newtank.getTurrentAngle();

        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        app.delay(500);
        assertNotSame(turrentAngle,newtank.getTurrentAngle());
        //app.exit();
    }

    @Test
    public void testTankFire(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);   

        Object color = "0,0,0";
        
        Tank newtank = new Tank(14,app.getGameterrain(),'e',color);
        
        assertNotNull(newtank);

        //Set current player to Tank
        app.setCurrentPlayer(newtank);

        //Check current player is set
        assertSame(app.currentPlayer(),newtank);

        //Fire a projectile
        newtank.setTurrentAngle(0);
        newtank.fire();

        Projectile firstProjectileInQueue = app.getProjectile(0);
        

        //Test a projectile was fired
        assertNotNull(firstProjectileInQueue);

        //Test Projectile was from initialised tank
        assertSame(firstProjectileInQueue.getSourceTank(),newtank);
    }


    @Test
    public void testTankFalling(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);
        
        Object color = "0,0,0";
        Tank newtank = new Tank(14,app.getGameterrain(),'e',color);
        int initialParachuteNo = newtank.getParachuteNo();
        double initialYPosition = newtank.getYPosition();
        System.out.println("Initial Position"+initialYPosition);


        //assert Iniatl Parachute no is 3
        assertEquals(initialParachuteNo,3);

        //System.out.println("Before raise Tank position: ("+newtank.getXPosition()+","+newtank.getYPosition()+")");

        //Set Y position to above and check if falling
        newtank.setYPosition(newtank.getYPosition() - 200);
        assertEquals(newtank.getYPosition(),(initialYPosition -200));
        
        Projectile newProjectile = new Projectile(newtank.getXPosition(), newtank.getYPosition(),100, 0,App.getGameterrain(),newtank,true);
        newtank.checkTankFalling(newProjectile);
        assertTrue(newtank.istankFalling());


        //Check Parachute no has decreased
        assertEquals(newtank.getParachuteNo(),(initialParachuteNo - 1));
        app.delay(2000);

        //Test Tank falling without Parachutes
        newtank.setParachuteNo(0);
        newtank.setYPosition(newtank.getYPosition() - 200);
        Projectile newSecondProjectile = new Projectile(newtank.getXPosition(), newtank.getYPosition(),100, 0,App.getGameterrain(),newtank,true);
        newtank.checkTankFalling(newSecondProjectile);
        assertTrue(newtank.istankFalling());
        app.delay(5000);
    }

    @Test
    public void testPowerups(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000); 

        Tank firstTank = app.getAliveTanks().get(1);
        Tank secondTank = app.getAliveTanks().get(2);

        //Earn posints
        Projectile newProjectile = new Projectile(secondTank.getXPosition(), secondTank.getYPosition(),100, 0,app.getGameterrain(),firstTank,true);
        secondTank.tankDamage(100,newProjectile);

        //Check Tank has earned score and has destroyed the other Tank
        assertEquals(firstTank.getTankScore(),100);
        assertTrue(secondTank.isNotActive());

        app.setCurrentPlayer(firstTank);

        //----------------------Parachute--------------------
        //Test parachute powerup
        int initailParachute = 	firstTank.getParachuteNo();
        int initialScore = firstTank.getTankScore();
        firstTank.handlePowerUps(80);
        app.delay(100);

        //Check Parachute have incresed by 1 and score decreased by 15
        assertEquals(firstTank.getParachuteNo(),(initailParachute+1));
        assertEquals(firstTank.getTankScore(),(initialScore-15));

        //----------------------Health--------------------
        //Decrease health to below 100
        firstTank.setHealth(-30);
        int initailHealth = firstTank.getTankHealth();
        initialScore = firstTank.getTankScore();
        firstTank.handlePowerUps(82);
        app.delay(100);

        //Check Health have incresed by 20
        
        assertEquals(firstTank.getTankHealth(),(initailHealth+20));
        assertEquals(firstTank.getTankScore(),(initialScore-20));

        //----------------------Fuel Level--------------------
        int initialFuelLevel = firstTank.getTankFuelLevel();
        initialScore = firstTank.getTankScore();
        firstTank.handlePowerUps(70);
        app.delay(100);

        //Check Health have incresed by 20
        assertEquals(firstTank.getTankFuelLevel(),(initialFuelLevel+200));
        assertEquals(firstTank.getTankScore(),(initialScore-10));

        //----------------------Larger Projectile--------------------
        initialScore = firstTank.getTankScore();
        firstTank.handlePowerUps(88);
        app.delay(100);

        //Check Health have incresed by 20
        assertTrue(firstTank.nextLargerProjectile());
        assertEquals(firstTank.getTankScore(),(initialScore-20));



        //----------------------Fire Larger Projectile--------------------
        firstTank.fire();
        app.delay(60);
        Projectile firstProjectile = app.getProjectile(0);

        assertSame(firstProjectile.getSourceTank(),firstTank);
        assertTrue(firstProjectile.isLargerProjectile());
    }

    @Test
    public void checkIfTankBelowMap(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000); 

        Tank firstTank = app.getAliveTanks().get(0);
        firstTank.setYPosition(App.HEIGHT+50);
        firstTank.tankBelowMap();

        //Check if tank has been set as not active
        assertTrue(firstTank.isNotActive());
    }

    @Test
    public void testSetPower(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000); 

        Tank firstTank = App.getAliveTanks().get(0);  

        //Check if power is below 0
        firstTank.setTankPower(-100);   
        
        //Power should be 0 not less than zero
        assertEquals(firstTank.getTankPower(),0);

        //Check if power is set to over 100
        firstTank.setTankPower(150);  
        assertEquals(firstTank.getTankPower(),100); //Power should be taken to 100
    }

    @Test
    public void checkTankToString(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);

        Tank firstTank = app.getAliveTanks().get(0);  
        assertNotNull(firstTank.toString());
    }

    @Test
    public void testSetPosition(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000); 

        Tank firstTank = app.getAliveTanks().get(0);  

        //Set outside screen

        firstTank.setXPosition(0);
        assertEquals(firstTank.getXPosition(),16);

        firstTank.setXPosition(850);
        assertEquals(firstTank.getXPosition(),848);

        //Test turret set
        firstTank.setTurrentAngle(+2);
        assertEquals(firstTank.getTurrentAngle(),1.57);

        //Test turret power set
        firstTank.turrentPower(+1);
        app.delay(500);  
        firstTank.turrentPower(-1);

        firstTank.move(-1);
        app.delay(500); 
        firstTank.move(1);
        app.delay(500); 

        System.out.println("Tank test - End");

    }
    
}
