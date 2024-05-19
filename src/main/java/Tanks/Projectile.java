package Tanks;
import java.util.ArrayList;


/**
 * The Projectile class represents a projectile fired from a Tank. It starts from the tanks turrents and travels across the screen being affected by both gravity and wind
 */
public class Projectile implements Coordinates {
    private final int MAXPROJECTILEDAMAGE = 60;
    private final double MAXPROJECTILEVELOCITY = 18;
    private final double MINPROJECTILEVELOCITY = 2;
    private final double PROJECTILEGRAVITY = (float) (3.6/30.0); //Projectile downward gravity per frame
    private double xPosition;
    private double yPosition;
    private double projectileXVelocity;
    private double projectileYVelocity;
    private Terrain terrain;
    private Tank sourceTank;
    private boolean delete = false;
    private int projectileRadius = 10;
    private boolean isLargerProjectile;
    private int projectileEffectRadius = 30;


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
        this.isLargerProjectile = largerProjectile;

        if(largerProjectile){
            projectileRadius *= 2;
            projectileEffectRadius *=2;
        }


        float projectileVelocity = (float) ((float) (MINPROJECTILEVELOCITY + ((MAXPROJECTILEVELOCITY - MINPROJECTILEVELOCITY)/100.0 * (float)power)));
        projectileXVelocity = projectileVelocity * (float)Math.sin(turrentAngle);
        projectileYVelocity = projectileVelocity * (float)Math.cos(turrentAngle);
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
        }else if(xValue < 0){
            //If projectile goes outside the screen
            this.xPosition = 0;
            this.delete = true;
        }else{
            this.xPosition = xValue;
        }
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
            //If projectile is in screen or above
            this.yPosition = yValue;
        }        
    }

    /**
     * A geter method for the X-axis co-ordinate
     * @return The x-axis co-ordinate for the Projectile
     */
    public double getXPosition(){
        return this.xPosition;
    }
    
    /**
     * A geter method for the Y-axis co-ordinate
     * @return The y-axis co-ordinate for the Projectile
     */
    public double getYPosition(){
        return this.yPosition;
    }

    /**
     * This method determines whether a projectile is active or not. Inactive projectile objects are deleted (Removed from the projectile stack)
     * @return It reterns a boolean. False when the projecile is inactive and True when the projectile is active
     */
    public boolean isNotActive(){
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
     * A getter method to get whether the projectile is a largerProjectile
     * @return A boolean to represent 
     */
    public boolean isLargerProjectile(){
        return this.isLargerProjectile;
    }

    /**
     * A method used to calculate the damaged of Tank given its proximity to the projectiles Impact point
     * @param xImpactPoint The X-axis Impact point
     * @param yImpactpoint The Y-axis Impact point
     */
    public void calculateTankDamage(double xImpactPoint, double yImpactpoint){
        for(Tank tank : App.getAliveTanks()){

            if(tank.getTanksEdges()[0] <= this.getXPosition() && tank.getTanksEdges()[1] >= this.getXPosition() && tank.getYPosition() >= this.getYPosition() && tank.getTanksEdges()[2] <= this.getYPosition()){ 
                tank.tankDamage(this.MAXPROJECTILEDAMAGE, this);
            }else{
                int explosionDamage;
                double minXDistance;
                double minYDistance;

                if(tank.getYPosition() >= yImpactpoint && tank.getTanksEdges()[2] <= yImpactpoint){
                    minYDistance = 0;
                }else{
                    double ydistanceBottom = Math.abs(tank.getYPosition() - yImpactpoint);
                    double ydistanceTop = Math.abs((tank.getYPosition() - (Tank.TANKSHEIGHT)) - yImpactpoint);
                    minYDistance = Math.min(ydistanceTop,ydistanceBottom);                
                }

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

            if( ((int)xImpactPoint - projectileEffectRadius) > 0){
                start = (int)xImpactPoint - projectileEffectRadius;
            }

            if( ((int)xImpactPoint + projectileEffectRadius) < 864){
                end = (int)xImpactPoint + projectileEffectRadius;
            }

            for(int i = start; i <= end; i++){

                int xDifference = i - (int)xImpactPoint;

                //Blast radius ralative to yPosition or blast yPosition
                float yValue = (float) Math.sqrt((projectileEffectRadius * projectileEffectRadius) - (xDifference * xDifference));

                //If there is ground above blast. In this case decrease ground by yValue * 2 getTerrainHeight
                if((yImpactPoint - yValue ) >= terrain.getTerrainHeight(i)){
                    terrain.setTerrainHeight(i,terrain.getTerrainHeight(i) + (int)(yValue) + (int)(yValue));
                }
                //If the ground is above mid blast but below top blast. Remove bottom blast radius and top blast radius
                else if(yImpactPoint > terrain.getTerrainHeight(i) && (this.getYPosition() - yValue ) < terrain.getTerrainHeight(i)){
                    terrain.setTerrainHeight(i, (terrain.getTerrainHeight(i) + (int)yValue + (int)(this.getYPosition() - terrain.getTerrainHeight(i))));
                }
                //If ground is exact position as blast or below blast
                else if((yImpactPoint + yValue) >= terrain.getTerrainHeight(i)){
                    terrain.setTerrainHeight(i, ((int)terrain.getTerrainHeight(i) + (int)((yValue + this.getYPosition()) - terrain.getTerrainHeight(i))));
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

            //Check if its a direct Tanks projectile hit         
            if(tank.getTanksEdges()[0] < this.getXPosition() && tank.getTanksEdges()[1] > this.getXPosition() && tank.getYPosition() > this.getYPosition() && tank.getTanksEdges()[2] < this.getYPosition()){ 
                return true;
            }
        }   
        return false;    
    }

    /**
     * This function is called every frame to refresh the projectiles position on the screen
     */
    public void refresh(){
        //Check if projectile has gone beyond screen getTerrainHeight
        if(terrain.getTerrainHeight((int)this.getXPosition()) <= (int)this.getYPosition()){

            this.setYPosition(terrain.getTerrainHeight((int)getXPosition()));
        
            projectileImpact(this.getXPosition(),this.getYPosition());
            deformTerrain(this.getXPosition(),this.getYPosition());
            calculateTankDamage((double)this.getXPosition(),(double)this.getYPosition());
        //Projectile is still in screen
        }else{
            boolean directHit = checkDirectHit();

            if(directHit){
                projectileImpact((double)this.getXPosition(),(double)this.getYPosition());
                deformTerrain(this.getXPosition(),this.getYPosition());
                calculateTankDamage((double)this.getXPosition(),(double)this.getYPosition());
            }

            //Effects of wind W * 0.03 pixels per second == (w * 0.03)
            this.setXPosition((this.getXPosition() + projectileXVelocity));
            this.setYPosition((this.getYPosition() - projectileYVelocity));
            projectileYVelocity = projectileYVelocity - PROJECTILEGRAVITY;
            projectileXVelocity = projectileXVelocity + ((terrain.getWindMagnitude() * 0.03)/30);
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
    }
    
}
