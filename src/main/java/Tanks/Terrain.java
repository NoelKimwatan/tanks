package Tanks;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;
import processing.core.PGraphics;
//import processing.core.createGraphics;

public class Terrain {

    public int[] terrainHeights;
    public int[] terrainMovingAverageHeight;
    private int movingAverageNo = 32;
    private ArrayList<Integer> treePositions ;
    private static HashMap<Integer,Character> hPlayerPos;
    private static HashMap<Integer,Character> AIPlayerPos;
    public ArrayList<Tree> trees = new ArrayList<Tree>();
    PGraphics terrainGraphics;
    public int windMagnitude;

    public App app;

    public Terrain(int height, int width, char[][] terrain,App app){
        terrainHeights = new int[width];
        this.app = app;

        hPlayerPos = new HashMap<Integer,Character>();
        AIPlayerPos = new HashMap<Integer,Character>();
        treePositions = new ArrayList<Integer>();

        //Setting terrain height
        for(int i = 0; i < terrain.length; i++ ){
            for(int j = 0; j < terrain[i].length; j++){
                if(terrain[i][j] == 'X'){
                    terrainHeights[j] = i * 32;
                }else if(terrain[i][j] == 'T'){
                    treePositions.add(j);
                }else if(Character.isLetter(terrain[i][j])){
                    hPlayerPos.put(j,terrain[i][j]);
                }else if(Character.isDigit(terrain[i][j])){
                    //Number of AI player stored as a string
                    AIPlayerPos.put(j,terrain[i][j]);
                }
            }
        }

        getHeightPartitions(terrainHeights);
        windMagnitude = (int) ((Math.random() * ((35.0 + 35.0 + 1.0)) - 35.0));
    }

    //Expand every height section into 32 sections
    private void getHeightPartitions(int[] heights){
        terrainMovingAverageHeight = new int[heights.length * 32];

        for ( int i = 0; i < terrainMovingAverageHeight.length; i++){
            terrainMovingAverageHeight[i] = heights[i / 32];
        }
    } 

    public void setup(){
        
        terrainMovingAverageHeight = this.movingAverage(terrainMovingAverageHeight);
        terrainMovingAverageHeight = this.movingAverage(terrainMovingAverageHeight);


        //Setup tree positions
        for(Integer i: treePositions){
            this.trees.add(new Tree(i, this, app.tree)) ;
        }



        if(App.newGame){
            System.out.println("New game");
            //Creating tanks and setting up position.  
            App.hPlayerSortedLetters = new ArrayList<Character>();
            App.alivePlayers = new ArrayList<Character>();
            for(int i: hPlayerPos.keySet()){
                Tank tank = new Tank(i, this, hPlayerPos.get(i), App.playerColours.get(String.valueOf(hPlayerPos.get(i))));
                App.tanks.put(hPlayerPos.get(i), tank);
                App.alivePlayers.add(tank.player);
                App.hPlayerSortedLetters.add(tank.player);
            }
            App.hPlayerSortedLetters.sort(Comparator.naturalOrder());
        }else{
            System.out.println("Next level");
            System.out.println("Number of alive players before: "+App.alivePlayers.size());
            App.alivePlayers.clear();
            System.out.println("Number of alive players after: "+App.alivePlayers.size());
            for(int i: hPlayerPos.keySet()){
                Tank tank = App.tanks.get(hPlayerPos.get(i));
                tank.resetTank(i,this);
                App.alivePlayers.add(tank.player);
            }
            System.out.println("Number of alive players after after: "+App.alivePlayers.size());

        }



        
        App.alivePlayers.sort(Comparator.naturalOrder());
        App.currentPlayer = App.tanks.get(App.hPlayerSortedLetters.get(0));
        System.out.println("Current player: "+App.currentPlayer.player);


        drawTerraingraphics();

    }

    public void changeWind(){
        windMagnitude += (int) ((Math.random() * ((5.0 + 5.0 + 1.0)) - 5.0));
    }    

    public void drawTerraingraphics(){
        //Draw graphics
        terrainGraphics = app.createGraphics(App.WIDTH,App.HEIGHT);

        //Begin drawing terrain graphics
        terrainGraphics.beginDraw();
        terrainGraphics.image(app.background, 0, 0);
        terrainGraphics.stroke(app.foreGroundColor[0], app.foreGroundColor[1], app.foreGroundColor[2]);

        for(int i = 0; i < terrainMovingAverageHeight.length; i++){
            terrainGraphics.line(i,terrainMovingAverageHeight[i],i,App.HEIGHT);
        }

        //Draw trees
        for (Tree tree : trees){
            tree.draw(terrainGraphics);
        }
        terrainGraphics.endDraw();
        //End drawing terrain graphics
    }


    //Obtain moving average of values
    public int[] movingAverage(int[] heights){
        for(int i = 0; i < (heights.length - movingAverageNo); i++){
            int sum = 0;
            for(int j = i; j < (i + movingAverageNo); j++){
                sum = sum + heights[j];
            }
            heights[i] = (sum / movingAverageNo );
        }
        return heights;
    }

    //Draw terrain
    public void draw(App app){
        app.image(this.terrainGraphics, 0, 0); 
    }

    //How to display object when printed
    public String toString(){
        return Arrays.toString(terrainHeights);
    }
    
}
