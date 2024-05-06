package Tanks;

import java.util.Locale;

import processing.core.PImage;

//Handles Text on Screen
public class Text {

    public static PImage fuelImage;
    public static PImage windImage1;
    public static PImage windImage2;
    public static PImage windImage;
    public Tank currentPlayer;
    public int playersFuelAmount; 
    float healthLineXCoordinates;
    private int displayArrowCounter = 60;
    private int gameOverDisplayCounter = 0;
    private Terrain terrain;
    

    public Text(App app, Terrain terrain){
        this.terrain = terrain;
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


        
        currentPlayer = App.currentPlayer;
    }



    public void refreshText(App app){

        currentPlayer = App.currentPlayer;
        displayArrowCounter = 60;
        

        if(terrain.windMagnitude < 0){
            windImage = windImage1;
        }else if (terrain.windMagnitude > 0){
            windImage = windImage2;
        }else{
            //System.out.println("Null wind image selected. Wind speed: "+windMagnitude);
            windImage = null;
        }



    }


    public void draw(App app){
        app.fill(0, 0, 0); 

        //Displayes players turn
        app.text("Players "+ App.currentPlayer.player +"'s turn",(1 * 32),(1 * 32));

        //Fuel indicator
        app.image(fuelImage,(10 * 16),(2 * 16) + 4,32,-32); 
        app.text(App.currentPlayer.fuelLevel,(11 * 16) + 20,(2 * 16));
        
        if (App.parachuteImage != null) app.image(App.parachuteImage,(10 * 16),(5 * 16) + 4,32,-32); 
        app.text(App.currentPlayer.parachuteNo,(11 * 16) + 20,(5 * 16) - 5);



        if(currentPlayer.largerProjectile){
            app.text("Larger Projectile",(11 * 16)+20,(7 * 16) +6);
            app.fill(currentPlayer.colour[0], currentPlayer.colour[1], currentPlayer.colour[2]);
            app.stroke(currentPlayer.colour[0], currentPlayer.colour[1], currentPlayer.colour[2]);
            
            app.ellipse((11 * 16),(7 * 16),20,20);
        }
        

        //Health indicator
        app.fill(0, 0, 0); 
        app.text("Health: ",(22 * 16),(2 * 16));
        app.fill(currentPlayer.colour[0], currentPlayer.colour[1], currentPlayer.colour[2]);
        app.rect((float)(25.5 * 16.0), (float)(2.125 * 16.0) , 150, -16);
        app.fill(0, 0, 0); 
        app.stroke(255, 0, 0);
        app.strokeWeight(4);
        healthLineXCoordinates = (float)((25.5 * 16.0) + 150.0 * ((float)App.currentPlayer.health/100.0));
        app.line( healthLineXCoordinates, (float)(2.135 * 16.0), healthLineXCoordinates, (float)(2.125 * 16.0) - 18 );
        app.strokeWeight(2);
        app.text(" "+currentPlayer.health ,(float)((25.5 * 16.0) + 155.0),(float)(2 * 16.0));

        //Power indicator
        app.text("Power: "+String.format("%.1f",App.currentPlayer.power) ,(float)(22.0 * 16.0),(float)(3.5 * 16.0));



        if (windImage != null) app.image(windImage,(48 * 16),(3 * 16) + 4,40,-40);
        app.text(" "+terrain.windMagnitude ,(float)((48 * 16) + 45.0),(float)(2.25 * 16.0));


        //Draw current player
        if(displayArrowCounter > 0){
            //System.out.println("Display arrow");
            app.stroke(0, 0, 0);
            app.line((this.currentPlayer.currentXPositionVal),(this.currentPlayer.currentYPositionVal -80), (this.currentPlayer.currentXPositionVal), (this.currentPlayer.currentYPositionVal -180));
            app.line((this.currentPlayer.currentXPositionVal),(this.currentPlayer.currentYPositionVal -80), (this.currentPlayer.currentXPositionVal + 20), (this.currentPlayer.currentYPositionVal -100));
            app.line((this.currentPlayer.currentXPositionVal),(this.currentPlayer.currentYPositionVal -80), (this.currentPlayer.currentXPositionVal - 20), (this.currentPlayer.currentYPositionVal -100));
            displayArrowCounter -= 1;
        }




        //If game over
        int displacement = 0;
        if (app.gameOver){
            //Width 864
            //Height 640
            app.textSize(25);
            Tank winnerTank = App.tanks.get(App.hPlayerSortedLetters.get(0));
            app.text("Player "+winnerTank.player + " wins!",(App.WIDTH/2 - 150),(App.HEIGHT/2 - 130));
            app.fill(winnerTank.colour[0],winnerTank.colour[1],winnerTank.colour[2],20);
            app.rect((App.WIDTH/2-150), (App.HEIGHT/2 - 60), 300,-30);
            
            app.rect((App.WIDTH/2-150), (App.HEIGHT/2 - 60), 300,120);

            app.fill(0,0,0);
            app.text("Final Scores",(App.WIDTH/2 - 140),(App.HEIGHT/2 - 65));

            displacement = 0;
            for (char c : App.hPlayerSortedLetters){
                //Every 0.7 seconds is 30 * 0.7 = 21 frames
                if(((App.hPlayerSortedLetters.indexOf(c) + 1) * (0.7 * App.FPS)) <=  gameOverDisplayCounter){
                    Tank loopTank = App.tanks.get(c);
                    app.fill(loopTank.colour[0],loopTank.colour[1],loopTank.colour[2]);
                    app.text("Player "+loopTank.player,(App.WIDTH/2 - 140),((App.HEIGHT/2 - 35) + displacement));
                    app.text(loopTank.score,(App.WIDTH/2 +100),((App.HEIGHT/2 - 35) + displacement));
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
    
            
            for (char c : App.hPlayerSortedLetters){
                Tank loopTank = App.tanks.get(c);
                //System.out.println("Character: "+c);
                app.fill(loopTank.colour[0], loopTank.colour[1], loopTank.colour[2]);
                app.text("Player "+ loopTank.player, (float)((46 * 16) + 4),(float)((5.7 * 16.00) + displacement));
                app.text(loopTank.score, (float)((51 * 16) + 4),(float)((5.7 * 16.00) + displacement));
                displacement += 20;
            }
            app.strokeWeight(2);
            app.noFill();
        }



    }
    
}
