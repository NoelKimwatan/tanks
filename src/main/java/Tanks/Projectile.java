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
    private int craterRadius = 30;

    

    public Projectile(float xPos, float yPos, int power, float turrentAngle, Terrain terrain, Tank tank){
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

    public void checkTankHit(int xPosition){
        for(char tankChar : App.tanks.keySet()){
            Tank tank = App.tanks.get(tankChar);

            if (tank.deleted) continue;

            //Check if tank is within blast radius
            if(Math.abs(tank.currentXPositionVal - xPosition) <= 30 ){
                int explosionDamage = (int) (1 - (Math.abs(tank.currentXPositionVal - xPosition))/30)*60;
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
                    tank.health = tank.health - (int) (1 - (Math.abs(tank.currentXPositionVal - xPosition))/30)*60;
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

            checkTankHit((int)xPosition);

            //Looping through the blast diameter
            int start = 0;
            int end = 864;

            if((int)xPosition - craterRadius > 0){
                start = (int)xPosition - craterRadius;
            }

            if((int)xPosition + craterRadius < 864){
                end = (int)xPosition + craterRadius;
            }

            for(int i = start; i <= end; i++){

                //Since we assume blast source is point (0,0)
                int xDifference = i - (int)xPosition;

                //Blast radius ralative to yPosition or blast yPosition
                float yValue = (float) Math.sqrt((craterRadius * craterRadius) - (xDifference * xDifference));
                //System.out.println("yValue: : "+yValue+ "yValue * 2: "+(int)(yValue * 2));

                //If there is ground above blast. In this case decrease ground by yValue * 2
                if((yPosition - yValue ) >= terrain.terrainMovingAverageHeight[i]){
                    terrain.terrainMovingAverageHeight[i] = terrain.terrainMovingAverageHeight[i] + (int)(yValue) + (int)(yValue);
                }
                //If the ground is above mid blast but below top blast. Remove bottom blast radius and top blast radius
                else if(yPosition > terrain.terrainMovingAverageHeight[i] && (yPosition - yValue ) < terrain.terrainMovingAverageHeight[i]){
                    terrain.terrainMovingAverageHeight[i] = terrain.terrainMovingAverageHeight[i] + (int)yValue + (int)(yPosition - terrain.terrainMovingAverageHeight[i]);
                }
                //If ground is exact position as blast or below blast
                else if((yPosition + yValue) >= terrain.terrainMovingAverageHeight[i]){
                    terrain.terrainMovingAverageHeight[i] = (int)terrain.terrainMovingAverageHeight[i] + (int)((yValue + yPosition) - terrain.terrainMovingAverageHeight[i]);
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
        app.fill(0,0,0);
        app.stroke(0,0,0);
        app.ellipse(xPosition, yPosition, 5,-5);
        //System.out.println("Projectile pos: "+ xPosition + " ypos: "+yPosition);

        
    }
    
}
