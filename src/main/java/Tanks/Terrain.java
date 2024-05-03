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
    private ArrayList<Integer> treePositions = new ArrayList<Integer>();
    private static HashMap<Integer,Character> hPlayerPos = new HashMap<Integer,Character>();
    private static HashMap<Integer,Character> AIPlayerPos = new HashMap<Integer,Character>();
    public ArrayList<Tree> trees = new ArrayList<Tree>();
    PGraphics terrainGraphics;

    public App app;

    public Terrain(int height, int width, char[][] terrain,App app){
        terrainHeights = new int[width];
        this.app = app;

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

        //Setup tank positions hPlayerPos
        App.hPlayerSortedLetters = new ArrayList<Character>();
        App.alivePlayers = new ArrayList<Character>();
        for(int i: hPlayerPos.keySet()){
            Tank tank = new Tank(i, this, hPlayerPos.get(i), App.playerColours.get(String.valueOf(hPlayerPos.get(i))));
            App.tanks.put(hPlayerPos.get(i), tank);
            App.alivePlayers.add(tank.player);
            App.hPlayerSortedLetters.add(tank.player);

        }



        App.hPlayerSortedLetters.sort(Comparator.naturalOrder());
        App.alivePlayers.sort(Comparator.naturalOrder());
        App.currentPlayer = App.tanks.get(App.hPlayerSortedLetters.get(0));
        //System.out.println("Current player: "+App.currentPlayer);

        //Get tank Keys and sort alphabetically
        
        // App.hPlayerSortedLetters = new Character[App.tanks.keySet().size()];
        // App.hPlayerSortedLetters = App.tanks.keySet().toArray(new Character[0]);
        // Arrays.sort(App.hPlayerSortedLetters);
        

        
        //(char) App.tanks.keySet().toArray()[0];
        //App.currentPlayer = App.tanks.get(0);

        drawTerraingraphics();

    }

    public void drawTerraingraphics(){
        //Draw graphics
        terrainGraphics = app.createGraphics(App.WIDTH,App.HEIGHT);
        terrainGraphics.beginDraw();

        terrainGraphics.image(app.background, 0, 0);
        terrainGraphics.stroke(app.foreGroundColor[0], app.foreGroundColor[1], app.foreGroundColor[2]);


        for(int i = 0; i < terrainMovingAverageHeight.length; i++){
            terrainGraphics.line(i,terrainMovingAverageHeight[i],i,App.HEIGHT);
            //app.point(i, terrainMovingAverageHeight[i]);
        }
        //System.out.println(app.clock.millis());

        //Draw trees
        for (Tree tree : trees){
            tree.draw(terrainGraphics);
        }
        terrainGraphics.endDraw();
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
        // app.stroke(app.foreGroundColor[0], app.foreGroundColor[1], app.foreGroundColor[2]);


        // for(int i = 0; i < terrainMovingAverageHeight.length; i++){
        //     //app.line(i,terrainMovingAverageHeight[i],i,terrainMovingAverageHeight[i]+2);
        //     app.point(i, terrainMovingAverageHeight[i]);
        // }
        // //System.out.println(app.clock.millis());

        // //Draw trees
        // for (Tree tree : trees){
        //     tree.draw(app);
        // }

        //app.image(this.terrainGraphics, App.HEIGHT, App.WIDTH); 
        app.image(this.terrainGraphics, 0, 0); 
        //System.out.println("Drawing image");
    }

    //How to display object when printed
    public String toString(){
        return Arrays.toString(terrainHeights);
    }
    
}
