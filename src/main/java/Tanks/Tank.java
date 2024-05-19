package Tanks;

/**
 * The Tank class represents a Tank on the terrain. 
 */
public class Tank implements Coordinates {
    public static final double TANKBOTTOMWIDTH = 32;
    public static final double TANKSHEIGHT = 16;
    public static final double TANKSTOPWIDTH = 28;
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
    private double tanksSideCurves = 5;
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
        this.health = 100;
        this.power = 50;
        this.t = t;
        this.parachuteNo = 3;
        this.setXPosition(((initialXPosition * 32) + 16));
        this.setYPosition(t.getTerrainHeight((int)xPositionVal));
        this.player = player;
        String[] colourVal =  String.valueOf(colour).split(",");
        this.colour = new int[]{Integer.valueOf(colourVal[0]), Integer.valueOf(colourVal[1]), Integer.valueOf(colourVal[2])};

        //Set fuel Last to ensure it doesnt change when Setting XPosition
        this.fuelLevel = 250;
    }

    /**
     * The setter method, used to set the Tanks X position on the screen
     * @param xValue The Tanks X-axis position
     */
    public void setXPosition(double xValue){
        double initialXPosition = getXPosition();
        //Only move if Tank has fuel
        if(xValue >= 16 && xValue <= 848){
            this.xPositionVal = xValue;

        }else{
            //Check it Tank is in Map
            if(xValue < (TANKBOTTOMWIDTH/2)){
                this.xPositionVal = (TANKBOTTOMWIDTH/2);
            }else if(xValue > (App.WIDTH - (TANKBOTTOMWIDTH/2))){
                this.xPositionVal = (App.WIDTH - (TANKBOTTOMWIDTH/2));
            }
        }


        //Set Tanks X edges everythime X-position is changed
        this.tanksXLeftEdge = this.xPositionVal - (Tank.TANKBOTTOMWIDTH/2);
        this.tanksXRightEdge = this.xPositionVal + (Tank.TANKBOTTOMWIDTH/2);

        //Change fuel by the absolute difference of before position and current position
        this.setTankFuelLevel((int)this.getTankFuelLevel() - (int)Math.abs(initialXPosition - getXPosition()));
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

        //Set Tanks Top Y endge everythime Y position is changed
        tanksYTopEdge = this.yPositionVal - (Tank.TANKSHEIGHT);

        //Check if Tank Below Map
        if(this.getYPosition() > App.HEIGHT){
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
        return new double[]{this.tanksXLeftEdge,this.tanksXRightEdge, this.tanksYTopEdge};
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
     * A setter method to set a Tanks score
     * @param newScore The new score to set the Tanks score to
     * @see #getTankScore
     */
    public void setTankScore(int newScore){
        if(newScore <= 0 ){
            this.score = 0;
        }else{
            this.score = newScore;
        }
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
    private void checkTanksPower(){
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

    /**
     * The reset Tank method. Used to reset the Tanks position after a level has ended
     * @param newXPosition The Tanks new position from the config text file. This value is multiplied by 32 and 16 added to obtain the Tanks exact X-axis position
     * The position value represents the Tanks X-positions in 32 pixels
     * @param t The new Terrain inwhich the Tank is being reset to 
     */
    public void resetTank(int newXPosition, Terrain t){
        this.setXPosition((newXPosition * 32) + 16);
        this.setYPosition(t.getTerrainHeight((int)xPositionVal));
        this.t = t;
        this.power = 50;
        this.health = 100;
        this.deleted = false;
        this.turrentAngle = 0;

        this.fuelLevel = 250;
        
        //Add player to alive players
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
        }else if (code == 82 && this.score >= 20 && this.health < 100){
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
        this.setYPosition(App.HEIGHT);
        this.setIsTankFalling(false);
    }

    /**
     * A getter method to obtain a Tanks health
     * @return A Tanks health
     */
    public int getTankHealth(){
        return this.health;
    }

    /**
     * This is the setter method used to set the Tanks health
     * @param change This is the health difference a (decrease or increase) on the tanks health
     * @return The Tanks health difference that has been changes. This is used when calculating the score earned by an opponent 
     * @see Tank#tankDamage(int damage, Projectile projectile) 
     */
    public int setHealth(int change){
        int healthChange = 0;
        //Only set health for a none deleted Tank isNotActive
        if(this.isNotActive() == false){
            if((this.health + change) <= 0){
                healthChange = this.health;
                this.health = 0;

                //If tank is NOT falling
                if(!this.istankFalling()){
                    this.createTankExplossion(15);
                }

                
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
     * This function creates a Tank explossion
     * @param explossionRadius The explossion radius 
     */
    public void createTankExplossion(int explossionRadius){
        double xExplossionSource = this.getXPosition();
        double yExplossionSource = t.getTerrainHeight((int)this.getXPosition()); //getTerrainHeight

        if(yExplossionSource < 0){
            yExplossionSource = 0;
        }else if (yExplossionSource > App.HEIGHT){
            yExplossionSource = App.HEIGHT;
        }

        this.deleted = true;
        Explosion tankExplosion = new Explosion(xExplossionSource,yExplossionSource,explossionRadius);
        App.addExplosion(tankExplosion);
    }

    /**
     * This function handles the damage inflicted on a Tank either by falling or by a projectile. It also handles the score earned 
     * @param damage This is the damage or health decrease. This is a positive integer value
     * @param projectile This is the projectile that has caused the damage or health decrease
     */
    public void tankDamage(int damage, Projectile projectile ){

        int scoreEarned = Math.abs(setHealth(-damage));
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
     * Setter method to set if the Tank is falling
     * @param isTankFalling A boolean to set whether a Tank is falling or not. This function is used when setting the value to false
     */
    public void setIsTankFalling(boolean isTankFalling){

        if(!isTankFalling && this.getTankHealth() == 0 && !this.isNotActive()){
            this.createTankExplossion(15);
        }else if(!isTankFalling && this.getTankHealth() > 0 && !this.isNotActive() && this.getYPosition() >= App.HEIGHT){
            //Tank bellow Terrain
            //Tank explossion to be 30 since Tank has gone below terrain
            this.createTankExplossion(30);
        }

        if(!isTankFalling){
            setTankFallingSpeed(0);
        }

        this.tankFalling = isTankFalling;
    }

    /**
     * Getter method to get Tank's falling speed
     * @return The Tanks falling speed
     */
    public double getTankFallingSpeed(){
        return tankFallingSpeed;
    }

    /**
     * A Setter method to set the Tank's falling speed
     * @param fallingSpeed The Tanks falling speed
     */
    public void setTankFallingSpeed(double fallingSpeed){
        this.tankFallingSpeed = fallingSpeed;
    }

    /**
     * This function refreshes the Tanks position and checks for different cases such as fuels remaining.
     * It runs every frame
     */
    public void refresh(){

        //Change direction
        if(direction != 0 && this.getTankFuelLevel() > 0 && this.getXPosition() >= 16 && this.getXPosition() <= 848 && App.currentPlayer() == this && !istankFalling()){
            //System.out.println("Tank: Tank durection is mot 0");
        
            if(direction == 1){
                this.setXPosition(this.getXPosition() + speed);
            }else if(direction == -1){
                this.setXPosition(this.getXPosition() - speed);
            }

            //Decrease fuel by change of direction if positive or negative
            //this.setTankFuelLevel(fuelLevel - speed);
 
            setYPosition(this.t.getTerrainHeight((int)xPositionVal));

        }else{
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
            //3 radian per second is equals to 0.1 radians per frame. So +-1/10 equals to +0.1 or -0.1
            this.setTurrentAngle(this.getTurrentAngle() + ((double)turretDirection / 10));
        }


        //Change turrets power
        if(turrentPowerDirection != 0 &&  App.currentPlayer() == this){
            this.setTankPower(this.getTankPower() + (turrentPowerDirection * turrentPowerChange));
        }

        //Check if tank falling 
        if(this.istankFalling()){
            this.setYPosition(this.getYPosition() + getTankFallingSpeed());
 
            if (this.getYPosition() >= t.getTerrainHeight((int)this.getXPosition())){
                this.setIsTankFalling(false);
                this.setYPosition(t.getTerrainHeight((int)this.getXPosition()));
            }

            if(this.direction != 0){
                //Set tank cant move
                this.move(0);
            }
        }

        tanksTurrentXStart = (this.getXPosition());
        tanksTurrentYStart = this.getYPosition() - (Tank.TANKSHEIGHT);

        tanksTurrentXEnd = tanksTurrentXStart + tanksTurrentLength * (float) Math.sin(turrentAngle);
        tanksTurrentYEnd = tanksTurrentYStart - tanksTurrentLength * (float) Math.cos(turrentAngle);
    }

    /**
     * This function initiates a Tanks movement
     * @param direction -1 - For left, 0 - For stop and +1 - For right
     */
    public void move(int direction){
        if( this.fuelLevel > 0){
            if(direction > 0 && this.getXPosition() < 848){
                this.direction = +1;
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
        if(this.t.getTerrainHeight((int)this.getXPosition()) > getYPosition() && this.istankFalling() == false){
            this.move(0);
            if(this.getParachuteNo() >= 1){
                //Falling speed 60 with parachutes. 60/30 = 2
                setTankFallingSpeed(2);
                this.setParachuteNo(this.getParachuteNo() - 1);
                this.setIsTankFalling(true);
            }else{
                //Falling speed 120 pixels per second with no parachutes. 120/30 = 4
                setTankFallingSpeed(4);
                pixelsDropped = Math.abs(t.getTerrainHeight((int)this.getXPosition()) - (int)this.getYPosition());
                this.setIsTankFalling(true);
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
        this.refresh();
        if(this.istankFalling() && this.getTankFallingSpeed() == 2){
            app.image(App.getParachuteImage(),(float)getXPosition()-48,(float)getYPosition(),96,-96); 
        }

        //Drawing tank
        app.stroke(0,0,0);
        app.fill(colour[0],colour[1],colour[2]);
        app.rect((float)(xPositionVal - TANKBOTTOMWIDTH/2),(float)yPositionVal, (float)TANKBOTTOMWIDTH, (float)-(TANKSHEIGHT/2), (float)tanksSideCurves, (float)tanksSideCurves, (float)tanksSideCurves, (float)tanksSideCurves);
        app.rect((float)((xPositionVal - TANKBOTTOMWIDTH/2) + (TANKBOTTOMWIDTH - TANKSTOPWIDTH)/2),(float)(yPositionVal - (TANKSHEIGHT/2)), (float)TANKSTOPWIDTH, (float)-(TANKSHEIGHT/2), (float)tanksSideCurves, (float)tanksSideCurves, 0, 0);
        
        //Drawing turret
        app.stroke(0,0,0);
        app.strokeWeight((float)this.tanksTurrentWidth);
        app.line((float)tanksTurrentXStart,(float)tanksTurrentYStart, (float)tanksTurrentXEnd, (float)tanksTurrentYEnd );
        app.strokeWeight(2);

        //Drawing turret
        app.stroke(0,0,0);
        app.noFill();
        app.rect((float)(xPositionVal - TANKBOTTOMWIDTH/2),(float)yPositionVal,(float)TANKBOTTOMWIDTH,-16);
    }

    /**
     * This function overrides the default toString method and returns the Tanks (X,Y) - Axis and Fuel level
     */
    @Override
    public String toString(){
        return "Tank: "+this.playerCharacter()+" ("+this.getXPosition()+","+this.getYPosition()+") LeftX Edge"+tanksXLeftEdge+" Right X Edge"+tanksXRightEdge+" Top Edge "+tanksYTopEdge;
    }

    
}
