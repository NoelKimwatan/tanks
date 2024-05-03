package Tanks;

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

    

    public Projectile(float xPos, float yPos, double power, float turrentAngle, Terrain terrain, Tank tank){
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.power = power;
        this.turrentAngle = turrentAngle;
        this.terrain = terrain;
        this.sourceTank = tank;
        //System.out.println("Projectile created");

        float projectileVelocity = (float) ((float) (1.0 + (8.0/100.0 * (float)power)));

        projectileXVelocity = projectileVelocity * (float)Math.sin(turrentAngle);
        projectileYVelocity = projectileVelocity * (float)Math.cos(turrentAngle);
        //System.out.println("Projectile velocity: " + projectileVelocity + " Projectile x velocity: "+ projectileXVelocity + " Projectile Y velocity: "+projectileYVelocity);
    }

    public void checkTankHit(){
        for(char tankChar : App.tanks.keySet()){
            Tank tank = App.tanks.get(tankChar);

            if (tank.deleted) continue;

            // System.out.println("Tanks position x: "+tank.currentXPositionVal+" y: "+tank.currentYPositionVal);
            // System.out.println("Projectile position: x: "+xPosition+" y: "+yPosition);
            // System.out.println("Terraine: y: "+terrain.terrainMovingAverageHeight[(int)xPosition]);

            double xdistance = tank.currentXPositionVal - xPosition;
            //For exact damage calc use level value
            //double ydistance = tank.currentYPositionVal - yPosition;
            double ydistance = tank.currentYPositionVal - terrain.terrainMovingAverageHeight[(int)xPosition];
            double distance = Math.sqrt( ((xdistance * xdistance) + (ydistance * ydistance)) );
            //System.out.println("Distance: "+distance+ " Int distance: "+(int)distance);

            //Check if tank is within blast radius
            if(distance <= 30 ){
                int explosionDamage = (int) ((1 - distance/30)*60);
                System.out.println("Explossion damage: "+explosionDamage);
                if( explosionDamage >= tank.health){
                    //Delete a player
                    System.out.println("Tank explodes");
                    tank.health = 0;
                    tank.deleted = true;
                    // System.out.println("Tank: "+tank.player);
                    // System.out.println("hPlayerSortedLetters array:"+App.hPlayerSortedLetters.toString());
                    // System.out.println("Remove player: "+App.hPlayerSortedLetters.remove(App.hPlayerSortedLetters.indexOf(tank.player)));
                    // //App.hPlayerSortedLetters.remove(tank.player);
                    Explosion tankExplosion = new Explosion(tank.currentXPositionVal,terrain.terrainMovingAverageHeight[(int)tank.currentXPositionVal], 15);
                    App.explossionQueue.add(tankExplosion);

                }else{
                    //tank.health = tank.health - (int) (1 - (Math.abs(tank.currentXPositionVal - xPosition))/30)*60;
                    tank.health = tank.health - (int) explosionDamage;
                }

                if(tank != this.sourceTank){
                    System.out.println("Damaged another player");
                    this.sourceTank.score += (int) explosionDamage;
                }else{
                    System.out.println("Damaged self");
                }
            }
        }
    }

    public void refresh(){
        //Check if projectile has gone beyond screen
        if(xPosition >= App.WIDTH || xPosition < 0){
            delete = true;
        }
        //Check if it has hit the ground
        else if(terrain.terrainMovingAverageHeight[(int)xPosition] <= (int)yPosition){
            //Remove terraine
            //System.out.println("Projectile has hit the ground");
            //System.out.println("Projectile has hit the ground at X: "+xPosition + " Y:"+ yPosition);

            checkTankHit();

            //Looping through the blast diameter
            int start = 0;
            int end = 864;

            if( ((int)xPosition - craterRadius) > 0){
                start = (int)xPosition - craterRadius;
            }

            if( ((int)xPosition + craterRadius) < 864){
                end = (int)xPosition + craterRadius;
            }

            for(int i = start; i <= end; i++){

                //Since we assume blast source is point (0,0)
                int xDifference = i - (int)xPosition;

                //Blast radius ralative to yPosition or blast yPosition
                float yValue = (float) Math.sqrt((craterRadius * craterRadius) - (xDifference * xDifference));
                //System.out.println("yValue: : "+yValue+ "yValue * 2: "+(int)(yValue * 2));
                //System.out.println("Yvalue: "+yValue+" i value: "+i);

                //If there is ground above blast. In this case decrease ground by yValue * 2
                if((yPosition - yValue ) >= terrain.terrainMovingAverageHeight[i]){
                    terrain.terrainMovingAverageHeight[i] = terrain.terrainMovingAverageHeight[i] + (int)(yValue) + (int)(yValue);
                    //System.out.println("The ground is above blast");
                }
                //If the ground is above mid blast but below top blast. Remove bottom blast radius and top blast radius
                else if(yPosition > terrain.terrainMovingAverageHeight[i] && (yPosition - yValue ) < terrain.terrainMovingAverageHeight[i]){
                    terrain.terrainMovingAverageHeight[i] = terrain.terrainMovingAverageHeight[i] + (int)yValue + (int)(yPosition - terrain.terrainMovingAverageHeight[i]);
                    //System.out.println("The ground is between blast and mid blast");
                }
                //If ground is exact position as blast or below blast
                else if((yPosition + yValue) >= terrain.terrainMovingAverageHeight[i]){
                    terrain.terrainMovingAverageHeight[i] = (int)terrain.terrainMovingAverageHeight[i] + (int)((yValue + yPosition) - terrain.terrainMovingAverageHeight[i]);
                    //System.out.println("Ground is below mid blast");
                }
            }
             
            System.out.println("Projectile has hit the ground");
            delete = true;
            Explosion projectileExplossion = new Explosion( (int)xPosition, (int)yPosition, 30 );
            App.explossionQueue.add(projectileExplossion);

        //Check if projectile has gone beyond the screen
        }else{
            xPosition = xPosition + projectileXVelocity;
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
