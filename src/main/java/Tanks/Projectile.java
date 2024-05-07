package Tanks;
import java.util.ArrayList;

public class Projectile {
    public float xPosition;
    public float yPosition;
    public double power;

    public float projectileGravity = (float) (3.6/30.0); //Projectile downward gravity per frame
    public float projectileXVelocity;
    public float projectileYVelocity;
    public float turrentAngle;
    public Terrain terrain;
    public Tank sourceTank;
    public boolean delete = false;
    private int craterRadius = 30;
    private int projectileRadius = 10;
    public boolean largerProjectile;
    private int projectileEffectRadius = 30;
    private int maxProjectileDamage = 60;

    

    public Projectile(float xPos, float yPos, double power, float turrentAngle, Terrain terrain, Tank tank, boolean largerProjectile){
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.power = power;
        this.turrentAngle = turrentAngle;
        this.terrain = terrain;
        this.sourceTank = tank;
        this.largerProjectile = largerProjectile;

        if(largerProjectile){
            projectileRadius *= 2;
            craterRadius *= 2;
            projectileEffectRadius *=2;
        }
        //System.out.println("Projectile created");

        float projectileVelocity = (float) ((float) (1.0 + (8.0/100.0 * (float)power)));

        projectileXVelocity = projectileVelocity * (float)Math.sin(turrentAngle);
        projectileYVelocity = projectileVelocity * (float)Math.cos(turrentAngle);
        //System.out.println("Projectile velocity: " + projectileVelocity + " Projectile x velocity: "+ projectileXVelocity + " Projectile Y velocity: "+projectileYVelocity);
    }

    public void checkTankHit(float xImpactPoint, float yImpactpoint){
        for(Tank tank : App.tanks.values()){
            
        
            if (tank.deleted) continue;

            int explosionDamage;
            double minXDistance;
            double minYDistance;
            System.out.println("Explossion point: X:"+xPosition+" Y:"+yPosition);

            //Find min  Y distance
            if(tank.currentYPositionVal >= yImpactpoint && tank.tanksYTopEdge <= yImpactpoint){
                minYDistance = 0;
            }else{
                double ydistanceBottom = Math.abs(tank.currentYPositionVal - yImpactpoint);
                double ydistanceTop = Math.abs((tank.currentYPositionVal - (Tank.tanksHeight)) - yImpactpoint);
                minYDistance = Math.min(ydistanceTop,ydistanceBottom);                
            }

            //Find min X distance
            if(tank.tanksXLeftEdge <= xImpactPoint && tank.tanksXRightEdge >= xImpactPoint){
                minXDistance = 0;
            }else{
                double xDistanceLeft = Math.abs(tank.tanksXLeftEdge - xImpactPoint);
                double xDistanceRight = Math.abs(tank.tanksXRightEdge - xImpactPoint);       
                minXDistance = Math.min(xDistanceLeft,xDistanceRight);     
            }

            double distanceVector = Math.sqrt( ((minXDistance * minXDistance) + (minYDistance * minYDistance)) );

            if(distanceVector <= projectileEffectRadius ){
                explosionDamage = (int) ((1 - distanceVector/projectileEffectRadius)*60);
                System.out.println("Explossion damage: "+explosionDamage);
                tank.tankDamage(explosionDamage, this);
            }

            tank.checkTankFalling(this);
            
        }
    }

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
                else if(yImpactPoint > terrain.terrainMovingAverageHeight[i] && (yPosition - yValue ) < terrain.terrainMovingAverageHeight[i]){
                    terrain.terrainMovingAverageHeight[i] = terrain.terrainMovingAverageHeight[i] + (int)yValue + (int)(yPosition - terrain.terrainMovingAverageHeight[i]);
                    //System.out.println("The ground is between blast and mid blast");
                }
                //If ground is exact position as blast or below blast
                else if((yImpactPoint + yValue) >= terrain.terrainMovingAverageHeight[i]){
                    terrain.terrainMovingAverageHeight[i] = (int)terrain.terrainMovingAverageHeight[i] + (int)((yValue + yPosition) - terrain.terrainMovingAverageHeight[i]);
                    //System.out.println("Ground is below mid blast");
                }
            }  

            terrain.drawTerraingraphics();         
    }

    public void projectileImpact(double xImpactPoint, double yImpactPoint){
        System.out.println("Projectile Impact");
        this.delete = true;
        Explosion projectileExplossion = new Explosion( (int)xPosition, (int)yPosition, projectileEffectRadius );
        App.explossionQueue.add(projectileExplossion);
        
    }

    public void refresh(){
        //Check if projectile has gone beyond screen
        if(xPosition >= App.WIDTH || xPosition < 0 || yPosition > App.HEIGHT){
            this.delete = true;
        }
        //Check if it has hit the ground
        else if(terrain.terrainMovingAverageHeight[(int)xPosition] <= (int)yPosition){
            yPosition = terrain.terrainMovingAverageHeight[(int)xPosition];

            projectileImpact(xPosition,yPosition);
            deformTerrain(xPosition,yPosition);
            checkTankHit((float)xPosition,(float)yPosition);
            System.out.println("Projectile has hit the ground");
        //Projectile is still in screen
        }else{

            for(Tank tank : App.tanks.values()){
                if(tank.tanksXLeftEdge < this.xPosition && tank.tanksXRightEdge > this.xPosition && tank.currentYPositionVal > this.yPosition && tank.tanksYTopEdge < this.yPosition){ 
                    System.out.println("Tank direct hit");
                    projectileImpact((double)xPosition,(double)xPosition);
                    deformTerrain(xPosition,yPosition);
                    tank.tankDamage(this.maxProjectileDamage, this);
                    tank.checkTankFalling(this);
                }
            }

            //checkTankHit((float)xPosition,(float)yPosition);
            //checkTankHit((float)xPosition,(float)terrain.terrainMovingAverageHeight[(int)xPosition]);

            //Effects of wind W * 0.03 pixels per second == (w * 0.03)
            xPosition = xPosition + projectileXVelocity + (float)(terrain.windMagnitude * 0.03);
            yPosition = yPosition - projectileYVelocity;
            projectileYVelocity = projectileYVelocity - projectileGravity;
    
            //System.out.println("Projectile Y velocity: "+projectileYVelocity);
        }
    }


    public void draw(App app) {
        app.fill(sourceTank.colour[0],sourceTank.colour[1],sourceTank.colour[2]);
        app.stroke(sourceTank.colour[0],sourceTank.colour[1],sourceTank.colour[2]);
        app.ellipse(xPosition, yPosition, projectileRadius,-projectileRadius);
        app.stroke(0,0,0);
        app.noFill();
        //System.out.println("Projectile pos: "+ xPosition + " ypos: "+yPosition);

        
    }
    
}
