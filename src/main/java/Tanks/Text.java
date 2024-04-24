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
    public int windMagnitude;

    public Text(App app){
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

        windMagnitude = (int) (Math.random() * (35.0 + 35.0 + 1.0) - 35.0);

        
        currentPlayer = App.currentPlayer;
    }

    public void generateRandonWind(){
        windMagnitude = (int) (Math.random() * (35.0 + 35.0 + 1.0) - 35.0);
    }

    public void refreshText(App app){

        currentPlayer = App.currentPlayer;
        

        if(windMagnitude < 0){
            windImage = windImage1;
        }else if (windMagnitude > 0){
            windImage = windImage2;
        }else{
            windImage = null;
        }

    }


    public void draw(App app){
        app.fill(0, 0, 0); 
        app.text("Players "+ App.currentPlayer.player +"'s turn",(1 * 32),(1 * 32));

        app.image(fuelImage,(10 * 16),(2 * 16) + 4,20,-20);
        app.text(App.currentPlayer.fuelLevel,(10 * 16) + 25,(2 * 16));

        //app.text("Health: "+App.currentPlayer.health,(22 * 16),(2 * 16));
        app.text("Health: ",(22 * 16),(2 * 16));
        
        app.fill(currentPlayer.colour[0], currentPlayer.colour[1], currentPlayer.colour[2]);
        app.rect((25 * 16), (float)(2.125 * 16.0) , 150, -16);
        app.fill(0, 0, 0); 
        app.text("Power: "+App.currentPlayer.power ,(float)(22.0 * 16.0),(float)(3.5 * 16.0));

        app.stroke(255, 0, 0);
        app.strokeWeight(4);
        healthLineXCoordinates = (float)((25.0 * 16.0) + 150.0 * ((float)App.currentPlayer.health/100.0));
        app.line( healthLineXCoordinates, (float)(2.135 * 16.0), healthLineXCoordinates, (float)(2.125 * 16.0) - 18 );
        app.strokeWeight(2);
        app.text(" "+currentPlayer.power ,(float)((25.0 * 16.0) + 155.0),(float)(2 * 16.0));

        app.image(windImage,(48 * 16),(3 * 16) + 4,40,-40);
        app.text(" "+windMagnitude ,(float)((48 * 16) + 45.0),(float)(2.25 * 16.0));

        app.stroke(0, 0, 0);
        app.noFill();
        app.strokeWeight(3);
        app.rect((float)((46 * 16)),(float)(3.5 * 16.0), 120, 20);
        app.text("Scores", (float)((46 * 16) + 4),(float)(4.5 * 16.06));
        app.rect((float)((46 * 16)),(float)(3.5 * 16.0) + 20, 120, 80);
        app.strokeWeight(2);

        int displacement = 0;
        for (Tank tank : App.tanks){
            app.text("Player "+tank.player, (float)((46 * 16) + 4),(float)((5.7 * 16.00) + displacement));
            displacement += 20;
        }

    }
    
}
