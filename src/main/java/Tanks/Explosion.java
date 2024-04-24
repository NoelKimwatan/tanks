package Tanks;

public class Explosion{
    int xPosition;
    int yPosition;
    int explosionRadius = 0;
    int explosionMaxRadiud = 30;
    boolean delete = false;
    public Explosion(int x, int y){
        xPosition = x;
        yPosition = y;
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

        if(explosionRadius <= explosionMaxRadiud){
            System.out.println("Expanding explosion");
            explosionRadius += 5;
        }else{
            System.out.println("Explossion is deleted");
            delete = true;
        }
    }


}