package Tanks;

public class Explosion{
    int xPosition;
    int yPosition;
    int explosionRadius;
    int explosionMaxRadiud;
    boolean delete = false;

    public Explosion(int x, int y, int maxRadius){
        this.xPosition = x;
        this.yPosition = y;
        this.explosionRadius = 0;
        this.explosionMaxRadiud = maxRadius;
        //System.out.println("New explossion created");
    }

    public void tick(){

        //System.out.println("Explossion radius:"+explosionRadius);
        if(explosionRadius <= explosionMaxRadiud){
            //System.out.println("Expanding explosion. Radius: "+explosionRadius);
            explosionRadius += 5;
        }else{
            //System.out.println("Explossion is deleted");
            this.delete = true;
        }
    }


    public void draw(App app){
        app.stroke(255,0,0);
        app.fill(255,0,0);
        app.ellipse(xPosition, yPosition, explosionRadius,explosionRadius);

        app.stroke(255,165,0);
        app.fill(255,165,0);
        app.ellipse(xPosition, yPosition, (float) (explosionRadius*0.5),  (float) (explosionRadius*0.5));

        app.stroke(255,255,0);
        app.fill(255,255,0);
        app.ellipse(xPosition, yPosition, (float) (explosionRadius*0.2), (float) (explosionRadius*0.2));

    }
}