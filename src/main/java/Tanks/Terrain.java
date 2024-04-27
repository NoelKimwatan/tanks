package Tanks;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashMap;

public class Terrain {

    public int[] terrainHeights;
    public int[] terrainMovingAverageHeight;
    private int movingAverageNo = 32;
    
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
                    app.treePositions.add(j);
                }else if(Character.isLetter(terrain[i][j])){
                    app.hPlayerPos.put(j,terrain[i][j]);
                }else if(Character.isDigit(terrain[i][j])){
                    //Number of AI player stored as a string
                    app.AIPlayerPos.put(j,terrain[i][j]);
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
        for(Integer i: app.treePositions){
            app.trees.add(new Tree(i, this, app.tree)) ;
        }

        //Setup tank positions hPlayerPos
        for(int i: App.hPlayerPos.keySet()){
            App.tanks.put(App.hPlayerPos.get(i), new Tank(i, this, App.hPlayerPos.get(i), App.playerColours.get(String.valueOf(App.hPlayerPos.get(i)))));
            //App.tanks.add(new Tank(i, this, App.hPlayerPos.get(i), App.playerColours.get(String.valueOf(App.hPlayerPos.get(i)))));
        }

        //Setting up tanks
        for (char c : App.tanks.keySet()){
            App.tanks.get(c).setup();
            //tank.setup();
        }


        //Get tank Keys and sort alphabetically
        App.hPlayerSortedLetters = new Character[App.tanks.keySet().size()];
        App.hPlayerSortedLetters = App.tanks.keySet().toArray(new Character[0]);
        Arrays.sort(App.hPlayerSortedLetters);
        

        App.currentPlayer = App.tanks.get(App.hPlayerSortedLetters[0]);
        //(char) App.tanks.keySet().toArray()[0];
        //App.currentPlayer = App.tanks.get(0);

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
        app.stroke(app.foreGroundColor[0], app.foreGroundColor[1], app.foreGroundColor[2]);


        for(int i = 0; i < terrainMovingAverageHeight.length; i++){
            app.line(i,terrainMovingAverageHeight[i],i,App.HEIGHT);
        }
    }

    //How to display object when printed
    public String toString(){
        return Arrays.toString(terrainHeights);
    }
    
}
