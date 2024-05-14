package Tanks;

/**
 * The Tank class represents a Tank on the terrain. 
 */
public class Tank implements Coordinates {
    private double xPositionVal;
    private double yPositionVal;

    private double tanksXLeftEdge;
    private double tanksXRightEdge;
    private double tanksYTopEdge;

    private int fuelLevel; 
    private int health;
    private double power; 
    private char player;
    private Terrain t;
    private int[] colour;
    private int speed = 2; //60 pixels per second equal (60/30) = 2 pixels per frame
    private int score = 0;
    private boolean deleted = false;
    private int parachuteNo;
    private int pixelsDropped = 0;
    private boolean largerProjectile = false;

    private boolean tankFalling = false;
    private double tankFallingSpeed;


    private int direction = 0;
    private int turrentPowerDirection = 0; //-1 Decrease power, +1 Increase power
    private double turrentPowerChange = 1.2;  // Change per frame (36/30)

    private double tanksTurrentXStart;
    private double tanksTurrentYStart;
    private double tanksTurrentXEnd;
    private double tanksTurrentYEnd;
    private double tanksTurrentWidth = 5;
    private double tanksTurrentLength = 15;

    public static final double tanksBottonWidth = 32;
    public static final double tanksHeight = 16;
    public static final double tanksTopWidth = 28;
    private double tanksSideCurves = 5;

    //Turrents angle
    private double turrentAngle = 0;
    private int turretDirection = 0;


    /**
     * The Tank constructor used to initiate a Tank object
     * @param initialXPosition The initial Tank position given in the config txt
     * @param t The Terrain on which the Tank is initiated
     * @param player The Tanks player character
     * @param colour The Tanks RGB colour object
     */
    public Tank(int initialXPosition, Terrain t, char player, Object colour){
        this.fuelLevel = 250;
        this.health = 100;
        this.power = 50;
        this.t = t;
        this.parachuteNo = 3;
        //this.xPositionVal = initialXPosition * 32;
        //System.out.println("Initial X position: "+initialXPosition);

        this.setXPosition(((initialXPosition * 32) + 16));
        this.setYPosition(t.terrainMovingAverageHeight[(int)xPositionVal]);

        this.player = player;

        // this.tanksXLeftEdge = this.xPositionVal - (Tank.tanksBottonWidth/2);
        // this.tanksXRightEdge = this.xPositionVal + (Tank.tanksBottonWidth/2);
        // this.tanksYTopEdge = this.yPositionVal - (Tank.tanksHeight);

        String[] colourVal =  String.valueOf(colour).split(",");
        this.colour = new int[]{Integer.valueOf(colourVal[0]), Integer.valueOf(colourVal[1]), Integer.valueOf(colourVal[2])};
        //System.out.println("Tank: Tank created position: ("+this.getXPosition()+","+this.getYPosition()+")");
    }

    // if(this.getTankFuelLevel() <= 0){
    //     this.move(0);
    // }

    // if(App.currentPlayer() != this){
    //     this.move(0);
    //     this.turrentMovement(0);
    // }

    /**
     * The setter method, used to set the Tanks X position on the screen
     * @param xValue The Tanks X-axis position
     */
    public void setXPosition(double xValue){
        //Only move if Tank has fuel
        if(xValue >= 16 && xValue <= 848){
            this.xPositionVal = xValue;

        }else{
            //Check it Tank is in Map
            if(xValue < 16){
                this.xPositionVal = 16;
            }else if(xValue > 848){
                this.xPositionVal = 848;
            }
        }

        //Set Turrets X-Position
        this.tanksXLeftEdge = this.xPositionVal - (Tank.tanksBottonWidth/2);
        this.tanksXRightEdge = this.xPositionVal + (Tank.tanksBottonWidth/2);
    }

    /**
     * The Tanks X-axis getter method. 
     * @return The Tanks X-axis position on the screen
     */
    public double getXPosition(){
        return xPositionVal;
    }

    /**
     * The Tanks Y position setter method. 
     * @param yValue The tanks Y-axis value
     */
    public void setYPosition(double yValue){
        this.yPositionVal = yValue;

        //Set Turret Y Position
        tanksYTopEdge = this.yPositionVal - (Tank.tanksHeight);

        //Check if Tank Below Map
        if(this.getYPosition() > App.HEIGHT){
            System.out.println("Tank: Tank is below map");
            tankBelowMap();
        }
    }

    /**
     * The Tanks Y-axis getter method. 
     * @return The Y-axis position of the Tank
     */
    public double getYPosition(){
        return yPositionVal;
    }

    /**
     * The Tanks Edges getter method. Used to get the co-ordinates of the tanks edges
     * @return It returns a 4*1 double array representing 0 - The Tanks X-axis left Edge, 1 - The Tanks X-axis right Edge, 2 - The Tanks Y-axis top Edge, 3- The Tanks Y-axis bottom Edge
     */
    public double[] getTanksEdges(){
        //XLeft, XRight, YTop
        return new double[]{this.tanksXLeftEdge,this.tanksXRightEdge, this.tanksYTopEdge, this.yPositionVal};
    }

    /**
     * Getter method to get a Tanks Score
     * @return The Tanks score value 
     * @see #tankDamage
     */
    public int getTankScore(){
        return this.score;
    }

    /**
     * Getter method to obatin the number of parachutes available for a Tank
     * @return The number of Tanks parachutes available
     */
    public int getParachuteNo(){
        return this.parachuteNo;
    }

    /**
     * A setter method to set the no of Parachutes
     * @param noOfParachutes  The number of Tanks parachutes to set
     */
    public void setParachuteNo(int noOfParachutes){
        if(noOfParachutes < 0){
            this.parachuteNo = 0;
        }else{
            this.parachuteNo = noOfParachutes;
        }
    }



    /**
     * Getter method to obtain a Tanks colour
     * @return An integer array containing the Tanks rgb colour
     */
    public int[] getTanksColour(){
        return colour;
    }
    
    /**
     * Getter method to obtain a Tanks character
     * @return A Tanks player character
     */
    public char playerCharacter(){
        return this.player;
    }

    /**
     * A getter method to obtain a Tanks health
     * @return A Tanks health
     */
    public int getTankHealth(){
        return this.health;
    }

    /**
     * A getter method to obtain a Tanks fuel level
     * @return A Tanks fuel level
     */
    public int getTankFuelLevel(){
        return this.fuelLevel;
    }

    /**
     * A setter method to set a Tanks fuel level
     * @param fuelLevelEntered The fuel level to set the Tank to
     */
    public void setTankFuelLevel(int fuelLevelEntered){

        if(fuelLevelEntered <= 0){
            fuelLevelEntered = 0;
            this.move(0);
        }
        this.fuelLevel = fuelLevelEntered;
    }

    /**
     * A Tanks getter method to obtain a Tanks power
     * @return A Tanks turret power
     */
    public double getTankPower(){
        return this.power;
    }

    /**
     * A setter method used to set a Tanks power
     * @param power The power of a Tanks projectile
     */
    public void setTankPower(double power ){
        if(power >= this.getTankHealth()){
            power = this.getTankHealth();
        }else if(power < 0){
            power = 0;
        }
        this.power = power;
    }

    /**
     * This function checks to ensure that a Tanks power does not exceed its health. It is executed everytime the health changes
     */
    public void checkTanksPower(){
        if(this.getTankPower() > this.getTankHealth()){
            this.setTankPower(this.getTankPower());
        }
    }

    /**
     * Returns a Tanks Turret angle
     * @return A double value of the Tanks turret angle 
     */
    public double getTurrentAngle(){
        return turrentAngle;
    }
    

    /**
     * Setter method to set the Tanks Turret angle 
     * @param angle The Turret angle to set to
     */
    public void setTurrentAngle(double angle){
        if(angle < -1.57){
            this.turrentAngle = -1.57;
            turretDirection = 0;
        }else if(angle > 1.57){
            this.turrentAngle = 1.57;
            turretDirection = 0;
        }else{
            this.turrentAngle = angle;
        }
    }

    /**
     * A getter method to obtain whether a tanks next projectile will be larger
     * @return A boolean which is true if the next projectile will be larger. 
     * This is a tanks powerup
     * @see #handlePowerUps
     */
    public boolean nextLargerProjectile(){
        return this.largerProjectile;
    }

    /**
     * Getter method to get if a Tank is alive
     * @return A boolean representing whether a Tank is deleted or Not
     */
    public boolean isNotActive(){
        return this.deleted;
    }

    // public double[] tanksSize(){
    //     tanksBottonWidth = 32;
    // public static double tanksHeight = 16;
    //     return new double[]{Tank.tanksBottonWidth, this.tanksTopWidth}
    // }

    /**
     * The reset Tank method. Used to reset the Tanks position after a level has ended
     * @param newXPosition The Tanks new position from the config text file. This value is multiplied by 32 and 16 added to obtain the Tanks exact X-axis position
     * The position value represents the Tanks X-positions in 32 pixels
     * @param t The new Terrain inwhich the Tank is being reset to 
     */
    public void resetTank(int newXPosition, Terrain t){
        //System.out.println("Resetting tank: "+player);
        // this.xPositionVal = (newXPosition * 32) + 16;
        // this.yPositionVal = t.terrainMovingAverageHeight[(int)xPositionVal];
        this.setXPosition((newXPosition * 32) + 16);
        this.setYPosition(t.terrainMovingAverageHeight[(int)xPositionVal]);
        this.t = t;
        this.fuelLevel = 250;
        this.power = 50;
        this.health = 100;
        this.deleted = false;
        this.turrentAngle = 0;

        //getXPosition()

        // this.tanksXLeftEdge = this.xPositionVal - (Tank.tanksBottonWidth/2);
        // this.tanksXRightEdge = this.xPositionVal + (Tank.tanksBottonWidth/2);
        //this.tanksYTopEdge = this.yPositionVal - (Tank.tanksHeight); 
        
        //Add to alive players
        App.addAlivePlayer(player);
    }

    /**
     * This method handles the games powerups such as repair and extra Parachute
     * @param code This is the Keyboard event code such as 82 for r
     */
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

    /**
     * This function handles cases when the Tank is below the Map. It resets the position, inititates an explossion and sets the Tank as deleted
     */
    public void tankBelowMap(){
        this.setYPosition(640);
        //yPositionVal = 640;
        tankFalling = false;
        tankFallingSpeed = 0;
        direction = 0;
        this.deleted = true;

        
        //Explosion tankExplosion = new Explosion(this.xPositionVal,640, 30);
        Explosion tankExplosion = new Explosion(this.getXPosition(),640, 30);
        App.addExplosion(tankExplosion);
    }


    /**
     * This is the setter method used to set the Tanks health
     * @param change This is the health difference a (decrease or increase) on the tanks health
     * @return The Tanks health difference that has been changes. This is used when calculating the score earned by an opponent 
     * @see Tank#tankDamage(int damage, Projectile projectile) 
     */
    public int setHealth(int change){
        int healthChange = 0;
        //Only set health for a none deleted Tank
        if(this.deleted == false){
            if((this.health + change) <= 0){
                healthChange = this.health;
                this.health = 0;
                this.deleted = true;
                this.tankFalling = false;
                this.deleted = true;
                //Explosion tankExplosion = new Explosion(this.xPositionVal,t.terrainMovingAverageHeight[(int)this.xPositionVal], 15);
                Explosion tankExplosion = new Explosion(this.getXPosition(),t.terrainMovingAverageHeight[(int)this.getXPosition()], 15);
                App.addExplosion(tankExplosion);
                
            }else if((this.health + change) >= 100){
                this.health = 100;
                healthChange = (100 - this.health);
            }else{
                this.health += change;
                healthChange = change;
            }
        }else{
            this.health = 0;
        }
        checkTanksPower();
        return healthChange;
    }

    /**
     * This function handles the damage inflicted on a Tank either by falling or by a projectile. It also handles the score earned 
     * @param damage This is the damage or health decrease. This is a positive integer value
     * @param projectile This is the projectile that has caused the damage or health decrease
     */
    public void tankDamage(int damage, Projectile projectile ){
        int scoreEarned = Math.abs(setHealth(-damage));
        //System.out.println("Tank damaged: "+player+" Damage amount: "+damage);

        if(projectile.getSourceTank() != this){
            projectile.getSourceTank().score += scoreEarned;
        }
    }

    /**
     * The getter method to get whether a tank is falling
     * @return A boolean representing wheather a Tank is falling or not
     */
    public boolean istankFalling(){
        return tankFalling;

    }


    /**
     * This function refreshes the Tanks position and checks for different cases such as fuels remaining.
     * It runs every frame
     */
    public void refresh(){


        //Check if health is below zero
        // if(this.health <= 0){
        //     System.out.println("Tank explodes");
        //     Explosion tankExplosion = new Explosion(this.xPositionVal,t.terrainMovingAverageHeight[(int)this.xPositionVal], 15);
        //     App.explossionQueue.add(tankExplosion);
        //     App.alivePlayers.remove(App.alivePlayers.indexOf(player)); //Look for a better way to remove this players
        //     System.out.println("Player removed");
        //     System.out.println("Player value:"+player);
        // }



        //Change direction
        //if(direction != 0 && this.getTankFuelLevel() > 0 && this.xPositionVal >= 16 && this.xPositionVal <= 848 && App.currentPlayer() == this && !istankFalling()){
        if(direction != 0 && this.getTankFuelLevel() > 0 && this.getXPosition() >= 16 && this.getXPosition() <= 848 && App.currentPlayer() == this && !istankFalling()){
            //System.out.println("Tank: Tank durection is mot 0");
        
            if(direction == 1){
                //xPositionVal = xPositionVal + speed; 
                this.setXPosition(this.getXPosition() + speed);
            }else if(direction == -1){
                //xPositionVal = xPositionVal - speed;
                this.setXPosition(this.getXPosition() - speed);
            }

            //fuelLevel = fuelLevel - speed;
            this.setTankFuelLevel(fuelLevel - speed);
            //yPositionVal = this.t.terrainMovingAverageHeight[(int)xPositionVal];

            //Set Y position
            setYPosition(this.t.terrainMovingAverageHeight[(int)xPositionVal]);

            // //Changing tanks Edges
            // tanksXLeftEdge = this.xPositionVal - (Tank.tanksBottonWidth/2);
            // tanksXRightEdge = this.xPositionVal + (Tank.tanksBottonWidth/2);
            // tanksYTopEdge = this.yPositionVal - (Tank.tanksHeight);

            // if(yPositionVal > 640){
            //     System.out.println("Tank: Tank is below map");
            //     tankBelowMap();
            // }
            // if(this.getYPosition() > App.HEIGHT){
            //     System.out.println("Tank: Tank is below map");
            //     tankBelowMap();
            // }
        }else{
            //System.out.println("Tank: Tank durection is  0");
            //Make sure player does not leave box
            // if(this.xPositionVal <= 16){
            //     this.xPositionVal = 16;
            // }

            // if(this.xPositionVal >= 848){
            //     this.xPositionVal = 848;
            // }

            //Check if fuel level is above 0 or if tank is out of frame
            if(this.getTankFuelLevel() <= 0){
                this.move(0);
            }

            if(App.currentPlayer() != this){
                this.move(0);
                this.turrentMovement(0);
            }
        }

        //Change turrets direction
        if(turretDirection != 0 &&  App.currentPlayer() == this){
            //turrentAngle = turrentAngle +  (float)((float)turretDirection / 10);
            this.setTurrentAngle(this.getTurrentAngle() + ((double)turretDirection / 10));
            //System.out.println("turret angle: "+turrentAngle);
        }



        // //Limit turretn rotation
        // if(turrentAngle < -1.57){
        //     turretDirection = 0;
        //     turrentAngle = (float)-1.57;
        // }else if(turrentAngle > 1.57){
        //     turretDirection = 0;
        //     turrentAngle = (float)1.57;
        // }

        //Change turrets power
        if(turrentPowerDirection != 0 &&  App.currentPlayer() == this){
            this.setTankPower(this.getTankPower() + (turrentPowerDirection * turrentPowerChange));
            //this.power = this.power + (turrentPowerDirection * turrentPowerChange);
        }

        //Check if tank falling 
        if(tankFalling){
            this.setYPosition(this.getYPosition() + tankFallingSpeed);
            //this.yPositionVal += tankFallingSpeed;
            //if (this.getYPosition() >= t.terrainMovingAverageHeight[(int)this.xPositionVal]){
            if (this.getYPosition() >= t.terrainMovingAverageHeight[(int)this.getXPosition()]){
                tankFalling = false;
                tankFallingSpeed = 0;
                //this.yPositionVal = t.terrainMovingAverageHeight[(int)this.xPositionVal];
                this.setYPosition(t.terrainMovingAverageHeight[(int)this.getXPosition()]);
            }
            // if(yPositionVal > 640){
            //     tankBelowMap();
            // }
            if(this.getYPosition() > 640){
                tankBelowMap();
            }

            if(this.direction != 0){
                //Set tank cant move
                this.move(0);
            }


            // tanksXLeftEdge = this.xPositionVal - (Tank.tanksBottonWidth/2);
            // tanksXRightEdge = this.xPositionVal + (Tank.tanksBottonWidth/2);
            // tanksYTopEdge = this.yPositionVal - (Tank.tanksHeight);
            // tanksXLeftEdge = this.getXPosition() - (Tank.tanksBottonWidth/2);
            // tanksXRightEdge = this.getXPosition() + (Tank.tanksBottonWidth/2);
            // tanksYTopEdge = this.getYPosition() - (Tank.tanksHeight);
        }



        // tanksTurrentXStart = (xPositionVal);
        // tanksTurrentYStart = yPositionVal - (Tank.tanksHeight);
        tanksTurrentXStart = (this.getXPosition());
        tanksTurrentYStart = this.getYPosition() - (Tank.tanksHeight);

        tanksTurrentXEnd = tanksTurrentXStart + tanksTurrentLength * (float) Math.sin(turrentAngle);
        tanksTurrentYEnd = tanksTurrentYStart - tanksTurrentLength * (float) Math.cos(turrentAngle);
    }

    /**
     * This function initiates a Tanks movement
     * @param direction -1 - For left, 0 - For stop and +1 - For right
     */
    public void move(int direction){

        if( this.fuelLevel > 0){
            //if(direction > 0 && this.xPositionVal < 848){
            if(direction > 0 && this.getXPosition() < 848){
                this.direction = +1;
            //else if(direction < 0 && this.xPositionVal > 16){
            }else if(direction < 0 && this.getXPosition() > 16){
                this.direction = -1;
            }else{
                this.direction = 0;
            }

        }else{
            this.direction = 0;
        }        
    }


    /**
     * This function checks if a Tank if falling
     * @param projectileHit The projectile that has caused the terrain to deform
     */
    public void checkTankFalling(Projectile projectileHit){
        //Check if tank is floating
        //if(this.t.terrainMovingAverageHeight[(int)this.xPositionVal] > yPositionVal && this.tankFalling == false){
        if(this.t.terrainMovingAverageHeight[(int)this.getXPosition()] > getYPosition() && this.tankFalling == false){
            //System.out.println("Tank is floating");
            this.move(0);
            this.tankFalling = true;

            if(parachuteNo >= 1){
                //Falling speed 120 pixels per second with no parachutes and 60 with parachutes
                // 120/30 == 4      60/30 = 2
                //No damage
                tankFallingSpeed = 2;
                parachuteNo -= 1;
            }else{
                tankFallingSpeed = 4;
                //getYPosition
                //pixelsDropped = Math.abs(t.terrainMovingAverageHeight[(int)this.xPositionVal] - (int)this.yPositionVal);
                pixelsDropped = Math.abs(t.terrainMovingAverageHeight[(int)this.getXPosition()] - (int)this.getYPosition());
                tankDamage(pixelsDropped, projectileHit);
            }
        }
    }


    /**
     * This function is used to change the Tanks power
     * @param value A 1 - to increase power and 0 - to decrease power
     */
    public void turrentPower(int value){
        //Increse turret power
        //System.out.println("Value: "+value+" Health: "+this.health);
        if (value > 0 && this.power < 100 && this.power < this.health){
            turrentPowerDirection = 1;
        }else if(value < 0 && this.power > 0){
            turrentPowerDirection = -1;
        }else{
            turrentPowerDirection = 0;
        }
    }

    /**
     * Used to change the direction of the Tanks turret
     * @param direction -1 - To move turret left, 0 - To stop turret movement and +1 - To move the turret right
     */
    public void turrentMovement(int direction){
        this.turretDirection = direction;
    }



    /**
     * This function is used to fire a projectile from the Tanks turret
     */
    public void fire(){
        Projectile firedProjectile = new Projectile(tanksTurrentXEnd, tanksTurrentYEnd, power, turrentAngle, t, this, this.largerProjectile);
        App.addProjectile(firedProjectile);
        if(this.largerProjectile){
            this.largerProjectile = false;
        }
    }

    /**
     * This function is run every frame and its used to draw the Tank on the screen
     * @param app The App function instance
     */
    public void draw(App app){
        //System.out.println("Tank: In draw method");
        this.refresh();

        //Drawing parachute
        if(tankFalling){
            //getYPosition
            //app.image(App.getParachuteImage(),(float)xPositionVal-48,(float)yPositionVal,96,-96); 
            app.image(App.getParachuteImage(),(float)getXPosition()-48,(float)getYPosition(),96,-96); 
        }

        //Drawing tank
        app.stroke(0,0,0);
        app.fill(colour[0],colour[1],colour[2]);
        app.rect((float)(xPositionVal - tanksBottonWidth/2),(float)yPositionVal, (float)tanksBottonWidth, (float)-(tanksHeight/2), (float)tanksSideCurves, (float)tanksSideCurves, (float)tanksSideCurves, (float)tanksSideCurves);
        app.rect((float)((xPositionVal - tanksBottonWidth/2) + (tanksBottonWidth - tanksTopWidth)/2),(float)(yPositionVal - (tanksHeight/2)), (float)tanksTopWidth, (float)-(tanksHeight/2), (float)tanksSideCurves, (float)tanksSideCurves, 0, 0);
        
        //Drawing turret
        app.stroke(0,0,0);
        app.strokeWeight((float)this.tanksTurrentWidth);
        app.line((float)tanksTurrentXStart,(float)tanksTurrentYStart, (float)tanksTurrentXEnd, (float)tanksTurrentYEnd );
        app.strokeWeight(2);

        //Drawing turret
        app.stroke(0,0,0);
        app.noFill();

        app.rect((float)(xPositionVal - tanksBottonWidth/2),(float)yPositionVal,(float)tanksBottonWidth,-16);
    }

    /**
     * This function overrides the default toString method and returns the Tanks (X,Y) - Axis and Fuel level
     */
    @Override
    public String toString(){
        return "Current position: ("+ this.xPositionVal+","+this.yPositionVal+") " + " Fuel: "+ fuelLevel;
    }
    
}
