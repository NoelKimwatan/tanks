package Tanks;

public class Projectile {
    public float xPosition;
    public float yPosition;
    public int power;

    public float projectileGravity = (float) (3.6/30.0); //Projectile downward gravity per frame
    public float projectileXVelocity;
    public float projectileYVelocity;
    public float turrentAngle;
    public Terrain terrain;
    public Tank sourceTank;
    public boolean delete = false;

    

    public Projectile(float xPos, float yPos, int power, float turrentAngle, Terrain terrain, Tank tank){
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.power = power;
        this.turrentAngle = turrentAngle;
        this.terrain = terrain;
        this.sourceTank = tank;

        float projectileVelocity = (float) ((float) (1.0 + (8.0/100.0 * (float)power)));

        projectileXVelocity = projectileVelocity * (float)Math.sin(turrentAngle);
        projectileYVelocity = projectileVelocity * (float)Math.cos(turrentAngle);
        //System.out.println("Projectile velocity: " + projectileVelocity + " Projectile x velocity: "+ projectileXVelocity + " Projectile Y velocity: "+projectileYVelocity);
    }

    public void refresh(){
        //Check if projectile has gone beyond screen
        if(xPosition >= App.WIDTH || xPosition < 0){
            delete = true;
        }
        //Check if it has hit the ground
        else if(terrain.terrainMovingAverageHeight[(int)xPosition] <= (int)yPosition){
            System.out.println("Projectile has hit the ground");
            delete = true;
            sourceTank.explosion = new Explosion( (int)xPosition, (int)yPosition );

        //Check if projectile has gone beyond the screen
        }else{
            xPosition = xPosition + projectileXVelocity;
            yPosition = yPosition - projectileYVelocity;
            projectileYVelocity = projectileYVelocity - projectileGravity;
    
            System.out.println("Projectile Y velocity: "+projectileYVelocity);
        }

    }


    public void draw(App app) {
        app.stroke(0,0,0);
        app.ellipse(xPosition, yPosition, 5,-5);


        
    }
    
}
