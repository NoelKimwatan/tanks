package Tanks;
import java.util.ArrayList;


/**
 * The Projectile class represents a projectile fired from a Tank. It starts from the tanks turrents and travels across the screen being affected by both gravity and wind
 */
public class Projectile implements Coordinates {
    private double xPosition;
    private double yPosition;
    private final double projectileGravity = (float) (3.6/30.0); //Projectile downward gravity per frame
    private double projectileXVelocity;
    private double projectileYVelocity;
    private Terrain terrain;
    private Tank sourceTank;
    private boolean delete = false;
    private int craterRadius = 30;
    private int projectileRadius = 10;
    
    private int projectileEffectRadius = 30;
    private final int maxProjectileDamage = 60;

    private final double maxProjectileVelocity = 18;
    private final double minProjectileVelocity = 2;

    //private boolean largerProjectile;

    
    /**
     * The projectile constractor
     * @param xPos This is the projectile's X-coordinate. It is initially  Tanks turrent end
     * @param yPos This is the projectile's Y-coordinate. It is initially  Tanks turrent end
     * @param power This is the Tanks power when firing the projectile. The power determines the projectiles velocity
     * @param turrentAngle This is the turrents angle when firing the projectile. It is used to determine projectiles X and Y axis
     * @param terrain The terrain object used when the projectile is fired
     * @param tank The tank that fires the projectile
     * @param largerProjectile A boolean representing whether the Larger projectile powerup has been used for this projectile
     */
    public Projectile(double xPos, double yPos, double power, double turrentAngle, Terrain terrain, Tank tank, boolean largerProjectile){
        this.setXPosition(xPos);
        this.setYPosition(yPos);

        this.terrain = terrain;
        this.sourceTank = tank;
        //this.largerProjectile = largerProjectile;

        if(largerProjectile){
            projectileRadius *= 2;
            craterRadius *= 2;
            projectileEffectRadius *=2;
        }
        //System.out.println("Projectile created");

        float projectileVelocity = (float) ((float) (minProjectileVelocity + ((maxProjectileVelocity - minProjectileVelocity)/100.0 * (float)power)));

        projectileXVelocity = projectileVelocity * (float)Math.sin(turrentAngle);
        projectileYVelocity = projectileVelocity * (float)Math.cos(turrentAngle);
        System.out.println("Projectile velocity: " + projectileVelocity + " Projectile x velocity: "+ projectileXVelocity + " Projectile Y velocity: "+projectileYVelocity);
    }

    /**
     * The setter method used to set the projectiles X-axis
     * @param xValue This is the xValue to change to
     * When the projectile exceeds the screen on either side. It is set to deleted and later deleted
     */
    public void setXPosition(double xValue){
        if(xValue >= App.WIDTH){
            //If projectile goes outside the screen
            this.xPosition = App.WIDTH;
            this.delete = true;
            //System.out.println("Projectile deleted");
        }else if(xValue < 0){
            //If projectile goes outside the screen
            this.xPosition = 0;
            this.delete = true;
            //System.out.println("Projectile deleted");
        }else{
            this.xPosition = xValue;
        }
    }

    /**
     * This method determines whether a projectile is active or not. Inactive projectile objects are deleted (Removed from the projectile stack)
     * @return It reterns a boolean. False when the projecile is inactive and True when the projectile is active
     */
    public boolean isActive(){
        return this.delete;
    }

    /**
     * This method returns the source tank that fired the projectile. 
     * @return A Tank object representing the Tank that fired the projectile
     */
    public Tank getSourceTank(){
        return this.sourceTank;
    }



    /**
     * The setter method used to set the projectiles Y-axis
     * @param yValue This is the yValue to change to
     * When the projectile exceeds the screen the bottom side. It is set to deleted and later deleted
     */
    public void setYPosition(double yValue){
        if(yValue >= App.HEIGHT){
            //If projectile goes below the screen
            this.yPosition = App.HEIGHT;
            this.delete = true;
        }else{
            this.yPosition = yValue;
        }        
    }

    /**
     * A geter method for the Y-axis co-ordinate
     * @return The y-axis co-ordinate for the Projectile
     */
    public double getYPosition(){
        return this.yPosition;
    }

    /**
     * A geter method for the X-axis co-ordinate
     * @return The x-axis co-ordinate for the Projectile
     */
    public double getXPosition(){
        return this.xPosition;
    }

    /**
     * A method used to calculate the damaged of Tank given its proximity to the projectiles Impact point
     * @param xImpactPoint The X-axis Impact point
     * @param yImpactpoint The Y-axis Impact point
     */
    public void calculateTankDamage(float xImpactPoint, float yImpactpoint){
        for(Tank tank : App.getAliveTanks()){
            System.out.println("Checking tank hit: "+tank.playerCharacter());


            //getTanksEdges(){
            //XLeft, XRight, YTop

            if(tank.getTanksEdges()[0] < this.getXPosition() && tank.getTanksEdges()[1] > this.getXPosition() && tank.getYPosition() > this.getYPosition() && tank.getTanksEdges()[2] < this.getYPosition()){ 
                tank.tankDamage(this.maxProjectileDamage, this);
            }else{
                int explosionDamage;
                double minXDistance;
                double minYDistance;
                //System.out.println("Explossion point: X:"+xPosition+" Y:"+yPosition);

                //Find min  Y distance
                //Top Y Edge <= yImpactpoint
                //getTanksEdges(){
                //XLeft, XRight, YTop
                if(tank.getYPosition() >= yImpactpoint && tank.getTanksEdges()[2] <= yImpactpoint){
                    minYDistance = 0;
                }else{
                    double ydistanceBottom = Math.abs(tank.getYPosition() - yImpactpoint);
                    double ydistanceTop = Math.abs((tank.getYPosition() - (Tank.tanksHeight)) - yImpactpoint);
                    minYDistance = Math.min(ydistanceTop,ydistanceBottom);                
                }

                //getTanksEdges(){
                //XLeft, XRight, YTop

                //Find min X distance
                if(tank.getTanksEdges()[0] <= xImpactPoint && tank.getTanksEdges()[1] >= xImpactPoint){
                    minXDistance = 0;
                }else{
                    //Left edge - Impact poin
                    double xDistanceLeft = Math.abs(tank.getTanksEdges()[0] - xImpactPoint);
                    double xDistanceRight = Math.abs(tank.getTanksEdges()[1] - xImpactPoint);       
                    minXDistance = Math.min(xDistanceLeft,xDistanceRight);     
                }

                double distanceVector = Math.sqrt( ((minXDistance * minXDistance) + (minYDistance * minYDistance)) );

                if(distanceVector <= projectileEffectRadius ){
                    explosionDamage = (int) ((1 - distanceVector/projectileEffectRadius)*60);
                    //System.out.println("Explossion damage: "+explosionDamage);
                    tank.tankDamage(explosionDamage, this);
                }
    
                
            }
            tank.checkTankFalling(this);
        }
    }

    /**
     * This function handles the Terrain deformation, whenever a projectile explodes
     * @param xImpactPoint The projectiles x-axis impact point
     * @param yImpactPoint The projectiles y-axis impact point
     */
    public void deformTerrain(double xImpactPoint, double yImpactPoint){
            //Looping through the blast diameter
            int start = 0;
            int end = 864;

            if( ((int)xImpactPoint - craterRadius) > 0){
                start = (int)xImpactPoint - craterRadius;
            }

            if( ((int)xImpactPoint + craterRadius) < 864){
                end = (int)xImpactPoint + craterRadius;
            }

            for(int i = start; i <= end; i++){

                int xDifference = i - (int)xImpactPoint;

                //Blast radius ralative to yPosition or blast yPosition
                float yValue = (float) Math.sqrt((craterRadius * craterRadius) - (xDifference * xDifference));
                //System.out.println("yValue: : "+yValue+ "yValue * 2: "+(int)(yValue * 2));
                //System.out.println("Yvalue: "+yValue+" i value: "+i);

                //If there is ground above blast. In this case decrease ground by yValue * 2
                if((yImpactPoint - yValue ) >= terrain.terrainMovingAverageHeight[i]){
                    terrain.terrainMovingAverageHeight[i] = terrain.terrainMovingAverageHeight[i] + (int)(yValue) + (int)(yValue);
                    //System.out.println("The ground is above blast");
                }
                //If the ground is above mid blast but below top blast. Remove bottom blast radius and top blast radius
                else if(yImpactPoint > terrain.terrainMovingAverageHeight[i] && (this.getYPosition() - yValue ) < terrain.terrainMovingAverageHeight[i]){
                    terrain.terrainMovingAverageHeight[i] = terrain.terrainMovingAverageHeight[i] + (int)yValue + (int)(this.getYPosition() - terrain.terrainMovingAverageHeight[i]);
                    //System.out.println("The ground is between blast and mid blast");
                }
                //If ground is exact position as blast or below blast
                else if((yImpactPoint + yValue) >= terrain.terrainMovingAverageHeight[i]){
                    terrain.terrainMovingAverageHeight[i] = (int)terrain.terrainMovingAverageHeight[i] + (int)((yValue + this.getYPosition()) - terrain.terrainMovingAverageHeight[i]);
                    //System.out.println("Ground is below mid blast");
                }
            }  

            terrain.drawTerraingraphics();         
    }

    /**
     * This function handles a projectiles impact and creates an explossion object and sets the explodded projectile as inactive or deleted
     * @param xImpactPoint The X-axis impact point
     * @param yImpactPoint The Y-axis impact point
     */
    public void projectileImpact(double xImpactPoint, double yImpactPoint){
        System.out.println("Projectile Impact");
        this.delete = true;
        Explosion projectileExplossion = new Explosion( (double)this.getXPosition(), (double)this.getYPosition(), (double)projectileEffectRadius );
        App.addExplosion(projectileExplossion);
        
    }

    /**
     * Checks whether its a direct Tank hit. 
     * @return A boolean. True if its a direct Tank hit and False if it is not
     */
    public boolean checkDirectHit(){
        for(Tank tank : App.getAliveTanks()){
            //getTanksEdges(){
            //XLeft, XRight, YTop
            
            if(tank.getTanksEdges()[0] < this.getXPosition() && tank.getTanksEdges()[1] > this.getXPosition() && tank.getYPosition() > this.getYPosition() && tank.getTanksEdges()[2] < this.getYPosition()){ 
                System.out.println("Is Tanks Direct Hit. Direct hit Func Func Function");
                System.out.println("Current projectile position: ("+this.getXPosition()+","+this.getYPosition()+")");
                System.out.println("Current tank position: ("+tank.getXPosition()+","+tank.getYPosition()+")");
                System.out.println("Tank edges: Left:"+tank.getTanksEdges()[0]+" Right: "+tank.getTanksEdges()[1]+" Top: "+tank.getTanksEdges()[2]+" Bottom: "+tank.getYPosition());
                return true;
            }
        }   
        return false;    
    }

    /**
     * This function is called every frame to refresh the projectiles position on the screen
     */
    public void refresh(){
        //Check if projectile has gone beyond screen
        if(terrain.terrainMovingAverageHeight[(int)this.getXPosition()] <= (int)this.getYPosition()){

            this.setYPosition(terrain.terrainMovingAverageHeight[(int)getXPosition()]);
          

            projectileImpact(this.getXPosition(),this.getYPosition());
            deformTerrain(this.getXPosition(),this.getYPosition());
            calculateTankDamage((float)this.getXPosition(),(float)this.getYPosition());
            System.out.println("Projectile has hit the ground");
        //Projectile is still in screen
        }else{
            boolean directHit = checkDirectHit();

            if(directHit){
                System.out.println("Tank direct hit. Direct Hit function");
                projectileImpact((double)this.getXPosition(),(double)this.getYPosition());
                deformTerrain(this.getXPosition(),this.getYPosition());
                calculateTankDamage((float)this.getXPosition(),(float)this.getYPosition());
            }



            //Effects of wind W * 0.03 pixels per second == (w * 0.03)
            this.setXPosition((this.getXPosition() + projectileXVelocity + (terrain.windMagnitude * 0.03)));
            this.setYPosition((this.getYPosition() - projectileYVelocity));
            projectileYVelocity = projectileYVelocity - projectileGravity;
    
            //System.out.println("Projectile Y velocity: "+projectileYVelocity);
        }
    }

    /**
     * The draw method. This method is called every frame to draw the projectile on the screen
     * @param app The app object method. Passed from the APP class 
     */
    public void draw(App app) {
        app.fill(sourceTank.getTanksColour()[0],sourceTank.getTanksColour()[1],sourceTank.getTanksColour()[2]);
        app.stroke(sourceTank.getTanksColour()[0],sourceTank.getTanksColour()[1],sourceTank.getTanksColour()[2]);
        app.ellipse((float)this.getXPosition(), (float)this.getYPosition(), (float)projectileRadius,(float)-projectileRadius);
        app.stroke(0,0,0);
        app.noFill();
        //System.out.println("Projectile pos: "+ xPosition + " ypos: "+yPosition);

        
    }
    
}
