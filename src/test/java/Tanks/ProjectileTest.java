package Tanks;
import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import processing.event.KeyEvent;

public class ProjectileTest {

    @Test
    public void testWhenProjectileIsOutside(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);

        Object color = "0,0,0";

        Terrain terrain = app.getTerrain();
        Tank newtank = new Tank(14,terrain,'e',color);
        Projectile newProjectile = new Projectile(10.0, 10.0,100, 20,terrain,newtank,true);

        //Test when projectile goes out of screen X
        newProjectile.setXPosition(App.WIDTH+100);
        assertTrue(newProjectile.isNotActive());

        Projectile secondProjectile = new Projectile(10.0, 10.0,100, 20,terrain,newtank,true);

        //Test when projectile goes out of screen Y axis
        secondProjectile.setYPosition(App.HEIGHT+100);
        assertTrue(secondProjectile.isNotActive());
    }
    

    @Test
    public void testDirectHit(){
        App app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(5000);

        Terrain terrain = app.getTerrain();
        terrain.resetTanks();
        app.delay(3000);



        // Tank newtank = new Tank(14,terrain,'e',color);
        Tank newtank = app.getAliveTanks().get(0);
        Projectile newProjectile = new Projectile(10.0, 10.0,100, 20,terrain,newtank,false);


        //Set to direct hit
        newProjectile.setYPosition(newtank.getYPosition() - 2);
        newProjectile.setXPosition(newtank.getXPosition());


        assertTrue(newProjectile.checkDirectHit());
        newProjectile.refresh();

    }
}
