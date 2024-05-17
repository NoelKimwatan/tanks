package Tanks;

/**
 * The Explosion class represents the explossion created whenever a projectile explodes or whenever a tank is damaged
 */
public class Explosion implements Coordinates{

    //Private variables
    private double xPosition;
    private double yPosition;
    private double explosionRadius;
    private double explosionMaxRadiud;
    private boolean delete = false;
    

    /**
     * Explossion constructor
     * @param x The X co-ordinates of the explossion
     * @param y The Y co-ordinates of the explossion
     * @param maxRadius The maximum radius of the explossion
     * The default or starting explossion radius is 0 and this expands to the maxRadius with every frame
     */
    public Explosion(double x, double y, double maxRadius){
        this.setXPosition(x);
        this.setYPosition(y);
        this.explosionRadius = 0;
        this.explosionMaxRadiud = maxRadius;
        //System.out.println("New explossion created. Radius"+maxRadius);
    }

    /**
     * A X position setter method
     * @param xValue The X co-ordinate of the explossion. When the X coordinate is below zero the value is set to zero and when the value is greater than APP.WIDTH, the value is set to APP.WIDTH
     */
    public void setXPosition(double xValue){
        if(xValue > App.WIDTH){
            this.xPosition = App.WIDTH;
        }else if(xValue < 0){
            this.xPosition = 0;
        }else{
            this.xPosition = xValue;
        }
    }

    /**
     * A Y position setter method
     * @param yValue The Y co-ordinate of the explossion. When the Y coordinate is below zero the value is set to zero and when the value is greater than APP.HEIGHT, the value is set to APP.HEIGHT
     */    
    public void setYPosition(double yValue){
        if(yValue > App.HEIGHT){
            this.yPosition = App.HEIGHT;
        }else if(yValue < 0){
            this.yPosition = 0;
        }else{
            this.yPosition = yValue;
        }
    }

    /**
     * A getter method for the X-axis value of explossion source
     * @return The X-axis source of the explossion
     */
    public double getXPosition(){
        return this.xPosition;
    }

    /**
     * A getter method for the Y-axis value of explossion source
     * @return The Y-axis source of the explossion
     */
    public double getYPosition(){
        return this.yPosition;
    }

    /**
     * The refresh method. This method changes the radius of the explossion with every frame. Starting with 0 to explosionMaxRadiud.
     * Once the explossion radius is equal to the explosionMaxRadiud. The explossion is deleeted
     */
    public void refresh(){
        if(explosionRadius <= explosionMaxRadiud){
            explosionRadius += 5;
        }else{
            //System.out.println("Explossion is deleted");
            this.delete = true;
        }
    }

    /**
     * A getter method to obtain if an Explossion has been deleted
     * @return A boolean representing whether an explossion has been deleted ot not
     */
    public boolean isDeleted(){
        return this.delete;
    }

    /**
     * The draw method. This method is called every frame
     * @param app The app object method. Passed from the APP class 
     */
    public void draw(App app){
        app.stroke(255,0,0);
        app.fill(255,0,0);
        app.ellipse((float)xPosition, (float)yPosition, (float)(explosionRadius * 2),(float)(explosionRadius * 2));


        app.stroke(255,165,0);
        app.fill(255,165,0);
        app.ellipse((float)xPosition, (float)yPosition, (float) ((explosionRadius*2)*0.5),  (float) ((explosionRadius * 2)*0.5));

        app.stroke(255,255,0);
        app.fill(255,255,0);
        app.ellipse((float)xPosition, (float)yPosition, (float) ((explosionRadius*2)*0.2), (float) ((explosionRadius * 2)*0.2));

    }
}