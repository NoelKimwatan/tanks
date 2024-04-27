package Tanks;
import java.util.Arrays;

public class Tank {
    int currentXPosition;
    int currentXPositionVal;
    int fuelLevel; 
    int health;
    int power; 
    char player;
    Terrain t;
    int[] colour;
    int speed = 2; //2 * 30 == 60 pixels per second
    int score = 0;
    boolean deleted = false;

    

    int direction = 0;

    float tankDrawYAxis;
    float tanksDrawXCenter;
    float tanksTurrentXStart;
    float tanksTurrentYStart;
    float tanksTurrentXEnd;
    float tanksTurrentYEnd;
    float tanksTurrentWidth = 2;
    float tanksTurrentLength = 15;

    float tanksBottonWidth = 32;
    float tanksTopWidth = 26;
    float tanksSideCurves = 5;



    //Turrents angle
    float turrentAngle = 0;
    int turretDirection = 0;
    Explosion tankExplosion;


    public Tank(int initialXPosition, Terrain t, char player, Object colour){
        this.currentXPosition = initialXPosition;
        this.fuelLevel = 250;
        this.health = 100;
        this.power = 100;
        this.t = t;
        this.currentXPositionVal = initialXPosition * 32;
        this.player = player;

        String[] colourVal =  String.valueOf(colour).split(",");
        this.colour = new int[]{Integer.valueOf(colourVal[0]), Integer.valueOf(colourVal[1]), Integer.valueOf(colourVal[2])};
        System.out.println("Tank created");

    }

    public void setup(){
        // tanksTurrentXStart = (currentXPositionVal + 16) - tanksTurrentWidth/2;
        // tanksTurrentYStart = tankDrawYAxis - 16;



    }

    public void refresh(){
        tankDrawYAxis = (t.terrainMovingAverageHeight[currentXPositionVal +  16]);

        tanksTurrentXStart = (currentXPositionVal + 16);
        tanksTurrentYStart = tankDrawYAxis - 16;

        tanksTurrentXEnd = tanksTurrentXStart + tanksTurrentLength * (float) Math.sin(turrentAngle);
        tanksTurrentYEnd = tanksTurrentYStart - tanksTurrentLength * (float) Math.cos(turrentAngle);
    }


    public void forward(){
        //Moving forward
        if(this.fuelLevel > 0){
            direction = 1;
        }else{
            this.stop();
        }
    }

    public void backward(){
        //Moving forward
        if(this.fuelLevel > 0){
            direction = -1;
        }else{
            this.stop();
        }
        
    }

    public void turrentMovement(int direction){
        this.turretDirection = direction;
    }

    public void turrentMovementStop(){
       this.turretDirection = 0;
    }

    public void stop(){
        //Moving forward
        direction = 0;
    }

    public void fire(){
        Projectile firedProjectile = new Projectile(tanksTurrentXEnd, tanksTurrentYEnd, power, turrentAngle, t, this);
        App.projectileQueue.add(firedProjectile);
    }

    public void draw(App app){
        //Drawing turrent
        app.stroke(0,0,0);
        app.strokeWeight(5);
        app.line(tanksTurrentXStart,tanksTurrentYStart, tanksTurrentXEnd, tanksTurrentYEnd );
        app.strokeWeight(2);

        //Drawing tank
        app.stroke(0,0,0);
        app.fill(colour[0],colour[1],colour[2]);
        app.rect(currentXPositionVal,tankDrawYAxis, tanksBottonWidth, -8, tanksSideCurves, tanksSideCurves, tanksSideCurves, tanksSideCurves);
        app.rect((currentXPositionVal + (tanksBottonWidth - tanksTopWidth)/2),(tankDrawYAxis - 8), tanksTopWidth, -8, tanksSideCurves, tanksSideCurves, 0, 0);

        //Drawing turrent
        app.stroke(0,0,0);
        app.fill(0,0,0);
        //app.rect(tanksTurrentXStart,tanksTurrentYStart,tanksTurrentWidth, -tanksTurrentLength);

        



        // app.stroke(0,0,0);
        // app.fill(255,255,255, 100);
        // app.rect(currentXPositionVal, tankDrawYAxis, 32,-32);

        if(fuelLevel <= 0 ){
            this.stop();
        }


        if(direction == 1){
            currentXPositionVal = currentXPositionVal + speed;
            fuelLevel = fuelLevel - speed;
        }else if(direction == -1){
            currentXPositionVal = currentXPositionVal - speed;
            fuelLevel = fuelLevel - speed;
        }

        if(turretDirection != 0){
            turrentAngle = turrentAngle +  (float)((float)turretDirection / 10);
            
        }



    }

    public String toString(){
        return "Current position: "+ currentXPosition + " Fuel: "+ fuelLevel;
    }
    
}
