package Tanks;
import java.util.Arrays;

public class Tank {
    int currentXPosition;
    int currentXPositionVal;
    int currentYPositionVal;


    int fuelLevel; 
    int health;
    double power; 
    char player;
    Terrain t;
    int[] colour;
    int speed = 2; //2 * 30 == 60 pixels per second
    int score = 0;
    boolean deleted = false;
    public int parachuteNo;
    private int pixelsDropped = 0;
    boolean largerProjectile = false;

    boolean tankFalling = false;
    int tankFallingSpeed;
    boolean tankCanMove = true;
    int initialPower = 100;
    int initialFuel = 250;

    

    int direction = 0;
    int turrentPowerDirection = 0; //-1 Decrease power, +1 Increase power
    double turrentPowerChange = 1.2;  // Change per frame (36/30)

    float tankDrawYAxis;
    float tanksDrawXCenter;
    float tanksTurrentXStart;
    float tanksTurrentYStart;
    float tanksTurrentXEnd;
    float tanksTurrentYEnd;
    float tanksTurrentWidth = 2;
    float tanksTurrentLength = 15;

    public static float tanksBottonWidth = 32;
    public static float tanksHeight = 16;
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
        this.power = initialPower;
        this.t = t;
        this.parachuteNo = 3;
        //this.currentXPositionVal = initialXPosition * 32;
        this.currentXPositionVal = (initialXPosition * 32) + 16;
        this.currentYPositionVal = t.terrainMovingAverageHeight[currentXPositionVal];
        this.player = player;

        String[] colourVal =  String.valueOf(colour).split(",");
        this.colour = new int[]{Integer.valueOf(colourVal[0]), Integer.valueOf(colourVal[1]), Integer.valueOf(colourVal[2])};
        //System.out.println("Tank created");

    }

    public void resetTank(int newXPosition, Terrain t){
        System.out.println("Resetting tank: "+player);
        this.currentXPositionVal = (newXPosition * 32) + 16;
        this.currentYPositionVal = t.terrainMovingAverageHeight[currentXPositionVal];
        this.t = t;
        this.fuelLevel = initialFuel;
        this.power = initialPower;
        this.parachuteNo = 3;
        this.health = 100;
        this.deleted = false;
        this.turrentAngle = 0;
    }

    public void handlePowerUps(int code){
        //Code 80 is for parachute. Cost 15. Letter P
        if(code == 80 && this.score >= 15){
            this.parachuteNo += 1;
            this.score -= 15;
        }else if (code == 82 && this.score >= 20){
            //Code 82 is for r. Repair cost 20
            setHealth(+20);
            this.score -= 20;
        }else if(code == 70 && this.score >= 10){
            //Code 70 for add fuel
            this.fuelLevel += 200;
            this.score -= 10;
        }else if(code == 88 && this.score >= 20 && largerProjectile == false){
            this.largerProjectile = true;
            this.score -= 20;
        }

    }

    public int setHealth(int change){
        int healthChange = 0;

        if((this.health + change) <= 0){
            healthChange = this.health;
            this.health = 0;

            try{
                App.alivePlayers.remove(App.alivePlayers.indexOf(this.player));
            }catch(Exception e){
                System.out.println("------------------Resolve this exception--------------------");
                System.out.print(e);
            }
            
            this.deleted = true;
            Explosion tankExplosion = new Explosion(this.currentXPositionVal,t.terrainMovingAverageHeight[(int)this.currentXPositionVal], 15);
            App.explossionQueue.add(tankExplosion);
            
        }else if((this.health + change) >= 100){
            this.health = 100;
            healthChange = (100 - this.health);
        }else{
            this.health += change;
            healthChange = change;

        }

        return healthChange;
    }

    //Process tank damage and score
    public void tankDamage(int damage, Projectile projectile ){
        int scoreEarned = Math.abs(setHealth(-damage));

        if(projectile.sourceTank != this){
            projectile.sourceTank.score += scoreEarned;
        }
    }

    public void setup(){
        // tanksTurrentXStart = (currentXPositionVal + 16) - tanksTurrentWidth/2;
        // tanksTurrentYStart = tankDrawYAxis - 16;



    }

    public void refresh(){
        //tankDrawYAxis = (t.terrainMovingAverageHeight[currentXPositionVal +  16]);

        //Check if health is below zero
        // if(this.health <= 0){
        //     System.out.println("Tank explodes");
        //     Explosion tankExplosion = new Explosion(this.currentXPositionVal,t.terrainMovingAverageHeight[(int)this.currentXPositionVal], 15);
        //     App.explossionQueue.add(tankExplosion);
        //     App.alivePlayers.remove(App.alivePlayers.indexOf(player)); //Look for a better way to remove this players
        //     System.out.println("Player removed");
        //     System.out.println("Player value:"+player);
        // }

        if(App.currentPlayer != this){
            this.move(0);
            this.turrentMovement(0);
        }

        //Check if fuel level is above 0 or if tank is out of frame
        if(fuelLevel <= 0){
            this.move(0);
        }

        //Check if power level is greater than health
        if(this.power > this.health){
            this.power = this.health;
        }

        //Make sure power does not go below zero
        if(this.power < 0){
            this.power = 0;
        }

        //Make sure player does not leave box
        if(this.currentXPositionVal <= 16){
            this.currentXPositionVal = 16;
        }

        if(this.currentXPositionVal >= 848){
            this.currentXPositionVal = 848;
        }




        //Change direction
        if(direction == 1){
            currentXPositionVal = currentXPositionVal + speed;
            currentYPositionVal = this.t.terrainMovingAverageHeight[currentXPositionVal];
            fuelLevel = fuelLevel - speed;
        }else if(direction == -1){
            currentXPositionVal = currentXPositionVal - speed;
            currentYPositionVal = this.t.terrainMovingAverageHeight[currentXPositionVal];
            fuelLevel = fuelLevel - speed;
        }

        //Change turrets direction
        if(turretDirection != 0){
            turrentAngle = turrentAngle +  (float)((float)turretDirection / 10);
        }

        //Change turrets power
        if(turrentPowerDirection != 0){
            this.power = this.power + (turrentPowerDirection * turrentPowerChange);
        }

        //Check if tank falling 
        if(tankFalling){
            this.currentYPositionVal += tankFallingSpeed;
            if (this.currentYPositionVal >= t.terrainMovingAverageHeight[(int)this.currentXPositionVal]){
                tankFalling = false;
                tankFallingSpeed = 0;
                tankCanMove = true;
                this.currentYPositionVal = t.terrainMovingAverageHeight[(int)this.currentXPositionVal];
            }
        }



        tanksTurrentXStart = (currentXPositionVal);
        tanksTurrentYStart = currentYPositionVal - 16;

        tanksTurrentXEnd = tanksTurrentXStart + tanksTurrentLength * (float) Math.sin(turrentAngle);
        tanksTurrentYEnd = tanksTurrentYStart - tanksTurrentLength * (float) Math.cos(turrentAngle);
    }


    public void move(int direction){

        if( this.fuelLevel > 0 ){

            if(direction > 0 && this.currentXPositionVal < 848){
                this.direction = +1;
            }else if(direction < 0 && this.currentXPositionVal > 16){
                this.direction = -1;
            }else{
                this.direction = 0;
            }

        }else{
            this.direction = 0;
        }        
    }



    public void checkTankFalling(Projectile projectileHit){
        //Check if tank is floating
        if(this.t.terrainMovingAverageHeight[(int)this.currentXPositionVal] > currentYPositionVal && this.tankFalling == false){
            this.move(0);
            tankCanMove = false;
            this.tankFalling = true;
            System.out.println("Tank is floating");

            if(parachuteNo >= 1){
                //Falling speed 120 pixels per second with no parachutes and 60 with parachutes
                // 120/30 == 4      60/30 = 2
                //No damage
                tankFallingSpeed = 2;
                parachuteNo -= 1;
            }else{
                tankFallingSpeed = 4;
                pixelsDropped = Math.abs(t.terrainMovingAverageHeight[(int)this.currentXPositionVal] - this.currentYPositionVal);
                tankDamage(pixelsDropped, projectileHit);
            }


        }
    }



    public void turrentPower(int value){
        //Increse turrent power
        //System.out.println("Value: "+value+" Health: "+this.health);
        if (value > 0 && this.power < 100 && this.power < this.health){
            turrentPowerDirection = 1;
        }else if(value < 0 && this.power > 0){
            turrentPowerDirection = -1;
        }else{
            turrentPowerDirection = 0;
        }
    }

    public void turrentMovement(int direction){
        this.turretDirection = direction;
    }




    public void fire(){
        Projectile firedProjectile = new Projectile(tanksTurrentXEnd, tanksTurrentYEnd, power, turrentAngle, t, this, this.largerProjectile);
        App.projectileQueue.add(firedProjectile);
        if(this.largerProjectile){
            this.largerProjectile = false;
        }
    }

    public void draw(App app){
        this.refresh();

        //Drawing parachute
        if(tankFalling){
            app.image(App.parachuteImage,currentXPositionVal-48,currentYPositionVal,96,-96); 
        }


        //Drawing turrent
        app.stroke(0,0,0);
        app.strokeWeight(5);
        app.line(tanksTurrentXStart,tanksTurrentYStart, tanksTurrentXEnd, tanksTurrentYEnd );
        app.strokeWeight(2);

        //Drawing tank
        app.stroke(0,0,0);
        app.fill(colour[0],colour[1],colour[2]);
        app.rect((currentXPositionVal - tanksBottonWidth/2),currentYPositionVal, tanksBottonWidth, -(tanksHeight/2), tanksSideCurves, tanksSideCurves, tanksSideCurves, tanksSideCurves);
        app.rect(((currentXPositionVal - tanksBottonWidth/2) + (tanksBottonWidth - tanksTopWidth)/2),(currentYPositionVal - (tanksHeight/2)), tanksTopWidth, -8, tanksSideCurves, tanksSideCurves, 0, 0);
        
        //Drawing turrent
        app.stroke(0,0,0);
        app.noFill();

        app.rect((currentXPositionVal - tanksBottonWidth/2),currentYPositionVal,tanksBottonWidth,-16);






    }

    public String toString(){
        return "Current position: "+ currentXPosition + " Fuel: "+ fuelLevel;
    }
    
}
