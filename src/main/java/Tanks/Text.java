package Tanks;

import java.util.Locale;

import processing.core.PImage;

/**
 * The text class. This represents all texts rendered on the screen
 */
public class Text {

    private static PImage fuelImage;
    private static PImage windImage1;
    private static PImage windImage2;
    private static PImage windImage;
    private static double healthLineXCoordinates;
    private static int displayArrowCounter = 60;
    private static int gameOverDisplayCounter;
    private static Terrain terrain;
    

    /**
     * The texts constructor
     * @param app The App object {@link App}
     * @param terrain The terrain object {@link Terrain}
     */
    public Text(App app, Terrain terrain){
        Text.terrain = terrain;
        gameOverDisplayCounter = 0;
        try{
            fuelImage = app.loadImage(app.getClass().getResource("fuel.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        }catch(Exception e){
            fuelImage = null;
        }

        try{
            windImage1 = app.loadImage(app.getClass().getResource("wind.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
            windImage2 = app.loadImage(app.getClass().getResource("wind-1.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        }catch(Exception e){
            windImage1 = null;
        }
    }



    /**
     * This methods refreshes the texts and shapes on the screen. The method runs every frame
     * @param app The App object {@link App}
     */
    public static void refreshText(App app){
        displayArrowCounter = 60;
        
        if(terrain.getWindMagnitude() < 0){
            windImage = windImage2;
        }else if (terrain.getWindMagnitude() > 0){
            windImage = windImage1;
        }else{
            windImage = null;
        }
    }

    /**
     * The draw method draws the the texts on the screen. The method runs every frame
     * @param app The app object {@link App}
     */
    public void draw(App app){
        app.fill(0, 0, 0); 

        //Displayes players turn
        app.text("Players "+ App.currentPlayer().playerCharacter() +"'s turn",(1 * 32),(1 * 32));

        //Fuel indicator
        app.image(fuelImage,(10 * 16),(2 * 16) + 4,32,-32); 
        app.text(App.currentPlayer().getTankFuelLevel(),(11 * 16) + 20,(2 * 16));
        
        if (App.getParachuteImage() != null) app.image(App.getParachuteImage(),(10 * 16),(5 * 16) + 4,32,-32); 
        app.text(App.currentPlayer().getParachuteNo(),(11 * 16) + 20,(5 * 16) - 5);

        if(App.currentPlayer().nextLargerProjectile()){
            app.text("Larger Projectile",(11 * 16)+20,(7 * 16) +6);
            app.fill(App.currentPlayer().getTanksColour()[0], App.currentPlayer().getTanksColour()[1], App.currentPlayer().getTanksColour()[2]);
            app.stroke(App.currentPlayer().getTanksColour()[0], App.currentPlayer().getTanksColour()[1], App.currentPlayer().getTanksColour()[2]);
            app.ellipse((11 * 16),(7 * 16),20,20);
        }
        

        //Health indicator
        app.fill(0, 0, 0); 
        app.text("Health: ",(22 * 16),(2 * 16));
        app.fill(App.currentPlayer().getTanksColour()[0], App.currentPlayer().getTanksColour()[1], App.currentPlayer().getTanksColour()[2]);
        app.rect((float)(25.5 * 16.0), (float)(2.125 * 16.0) , 150, -16);
        app.fill(0, 0, 0); 
        app.stroke(255, 0, 0);
        app.strokeWeight(4);
        healthLineXCoordinates = ((25.5 * 16.0) + 150.0 * ((double)App.currentPlayer().getTankHealth()/100.0));
        app.line( (float)healthLineXCoordinates, (float)(2.135 * 16.0), (float)healthLineXCoordinates, (float)(2.125 * 16.0) - 18 );
        app.strokeWeight(2);
        app.text(" "+App.currentPlayer().getTankHealth() ,(float)((25.5 * 16.0) + 155.0),(float)(2 * 16.0));

        //Power indicator
        app.text("Power: "+String.format("%.1f",App.currentPlayer().getTankPower()) ,(float)(22.0 * 16.0),(float)(3.5 * 16.0));

        if (windImage != null) app.image(windImage,(48 * 16),(3 * 16) + 4,40,-40);
        app.text(" "+terrain.getWindMagnitude() ,(float)((48 * 16) + 45.0),(float)(2.25 * 16.0));

        //Draw current player
        if(displayArrowCounter > 0 && !App.isGameover()){
            app.stroke(0, 0, 0);
            app.line((float)(App.currentPlayer().getXPosition()),(float)(App.currentPlayer().getYPosition() -80), (float)(App.currentPlayer().getXPosition()), (float)(App.currentPlayer().getYPosition() -180));
            app.line((float)(App.currentPlayer().getXPosition()),(float)(App.currentPlayer().getYPosition() -80), (float)(App.currentPlayer().getXPosition() + 20), (float)(App.currentPlayer().getYPosition() -100));
            app.line((float)(App.currentPlayer().getXPosition()),(float)(App.currentPlayer().getYPosition() -80), (float)(App.currentPlayer().getXPosition() - 20), (float)(App.currentPlayer().getYPosition() -100));
            displayArrowCounter -= 1;
        }

        //If game over
        int displacement = 0;
        if (App.isGameover()){
            app.textSize(25);
            Tank winnerTank = App.getTank(App.getSortedPlayerLetters().get(0));
            app.text("Player "+winnerTank.playerCharacter() + " wins!",(App.WIDTH/2 - 150),(App.HEIGHT/2 - 130));
            app.fill(winnerTank.getTanksColour()[0],winnerTank.getTanksColour()[1],winnerTank.getTanksColour()[2],20);
            app.rect((App.WIDTH/2-150), (App.HEIGHT/2 - 60), 300,-30);
            
            app.rect((App.WIDTH/2-150), (App.HEIGHT/2 - 60), 300,120);

            app.fill(0,0,0);
            app.text("Final Scores",(App.WIDTH/2 - 140),(App.HEIGHT/2 - 65));

            displacement = 0;
            for (char c : App.getSortedPlayerLetters()){
                if(((App.getSortedPlayerLetters().indexOf(c) + 1) * (0.7 * App.FPS)) <=  gameOverDisplayCounter){
                    Tank loopTank = App.getTank(c);
                    app.fill(loopTank.getTanksColour()[0],loopTank.getTanksColour()[1],loopTank.getTanksColour()[2]);
                    app.text("Player "+loopTank.playerCharacter(),(App.WIDTH/2 - 140),((App.HEIGHT/2 - 35) + displacement));
                    app.text(loopTank.getTankScore(),(App.WIDTH/2 +100),((App.HEIGHT/2 - 35) + displacement));
                    displacement += 30;
                }else{
                    gameOverDisplayCounter += 1;
                }
            }
            app.textSize(14);
            
        }else{
            app.stroke(0, 0, 0);
            app.noFill();
            app.strokeWeight(3);
            app.rect((float)((46 * 16)),(float)(3.5 * 16.0), 120, 20);
            app.text("Scores", (float)((46 * 16) + 4),(float)(4.5 * 16.06));
            app.rect((float)((46 * 16)),(float)(3.5 * 16.0) + 20, 120, 80);
            app.strokeWeight(2);
    
            displacement = 0;
    
            for (char c : App.getSortedPlayerLetters()){
                Tank loopTank = App.getTank(c);
                app.fill(loopTank.getTanksColour()[0], loopTank.getTanksColour()[1], loopTank.getTanksColour()[2]);
                app.text("Player "+ loopTank.playerCharacter(), (float)((46 * 16) + 4),(float)((5.7 * 16.00) + displacement));
                app.text(loopTank.getTankScore(), (float)((51 * 16) + 4),(float)((5.7 * 16.00) + displacement));
                displacement += 20;
            }
            app.strokeWeight(2);
            app.noFill();
        }
    }   
}
