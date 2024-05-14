package Tanks;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import processing.core.PGraphics;


/**
 * The Terrain class representing the terrain in the game. All other objects are built on the terrain 
 */
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


    /**
     * Constructor for terrain, requires board height, width, the terrain config file and app object
     * @param height The height of the board
     * @param width The width of the board
     * @param terrain A two dimensional character array of the terrain file
     * @param app The app object
     */
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

        terrainMovingAverageHeight = getHeightPartitions(terrainHeights);
        windMagnitude = (int) ((Math.random() * ((35.0 + 35.0 + 1.0)) - 35.0));
    }

    /**
     * Converts the X height charcters into terrain heights. Output is the height of the 864 pixels on the screen
     * @param heights The height values representing the height of 32 pixels
     * @return A one dimensional integer array. Representing the terrain height of each pixel on the X axis (864 pixels)
     */
    private int[] getHeightPartitions(int[] heights){
        int[] terrainHeights = new int[heights.length * 32];

        for ( int i = 0; i < terrainHeights.length; i++){
            terrainHeights[i] = heights[i / 32];
        }

        return terrainHeights;
    } 

    /**
     * The setup function sets up the terrain by performing the moving average function and placing the players and trees. 
     * This function is recalled whenever a new level restarts
     */
    public void setup(){
        
        terrainMovingAverageHeight = this.movingAverage(terrainMovingAverageHeight);
        terrainMovingAverageHeight = this.movingAverage(terrainMovingAverageHeight);

        //Setup tree positions
        for(Integer i: treePositions){
            this.trees.add(new Tree(i, this, App.getTreeImage())) ;
        }

        if(App.isNewGame()){
            System.out.println("New game");
            //Creating tanks and setting up position.  
            //App.hPlayerSortedLetters = new ArrayList<Character>();
            //App.alivePlayers = new ArrayList<Character>();
            for(int i: hPlayerPos.keySet()){
                Tank tank = new Tank(i, this, hPlayerPos.get(i), App.playerColours.get(String.valueOf(hPlayerPos.get(i))));
                App.addTank(hPlayerPos.get(i), tank);
                //App.alivePlayers.add(tank.playerCharacter());
                //App.hPlayerSortedLetters.add(tank.playerCharacter());
            }
            //App.hPlayerSortedLetters.sort(Comparator.naturalOrder());
        }else{
            System.out.println("Next level");
            //System.out.println("Number of alive players before: "+App.alivePlayers.size());
            //App.alivePlayers.clear();
            //System.out.println("Number of alive players after: "+App.alivePlayers.size());
            for(int i: hPlayerPos.keySet()){
                Tank tank = App.getTank(hPlayerPos.get(i));
                tank.resetTank(i,this);
                //App.alivePlayers.add(tank.playerCharacter());
            }
            //System.out.println("Number of alive players after after: "+App.alivePlayers.size());

        }

        drawTerraingraphics();
        
        

    }

    /**
     * The change wind function, is used to change the windMagnitude value by + or - 5 unit values
     */
    public void changeWind(){
        windMagnitude += (int) ((Math.random() * ((5.0 + 5.0 + 1.0)) - 5.0));
    }    

    /**
     * This function is used to draw the terrain graphics. Everytime the function is called a new terrain image is created
     * This function is only called whenever there is a terrain deformation. The terrain image created by this function is re-drawn everyframe
     * The purpose of this implmenetation is to improve the performance of the game as redrawing the terrain on every frame is very costly
     */
    public void drawTerraingraphics(){
        //Draw graphics  terrainGraphics
        terrainGraphics =  app.createGraphics(App.WIDTH,App.HEIGHT);

        //Begin drawing terrain graphics
        terrainGraphics.beginDraw();
        terrainGraphics.image(App.getBackgroundImage(), 0, 0);
        terrainGraphics.stroke(App.getForeGroundColor()[0], App.getForeGroundColor()[1], App.getForeGroundColor()[2]);


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


    /**
     * Obtaines the moving average values of the terrain
     * @param heights Takes in an integer array of the heaights. This heights represent the height of each pixel on the screens X axis
     * @return Returns the moving average of the height. The output is the same size as the input
     */
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

    /**
     * Draws the terrain grapgics generated by the {@link #drawTerraingraphics} function
     * @param app Takes in the app object as its parameter
     * @see #drawTerraingraphics
     */
    public void draw(App app){
        app.image(this.terrainGraphics, 0, 0); 
    }

    /**
     * A custom toString method to display the heaight of the terrain. 
     * @return The return height is the height of each 32 pixels. The value returned is the value before the moving average function is performed
     */
    @Override
    public String toString(){
        return Arrays.toString(terrainHeights);
    }
    
}
