package Tanks;

import processing.core.PImage;
import processing.core.PGraphics;
/**
 * The Tree class represents the trees placed on the terrain.
 */
public class Tree {
    private int xPosition;
    private Terrain t;
    private PImage treeImage;
    private int xPositionVal;
    private int treeDisplayDimensions = 32;

    /**
     * The tree constructor
     * @param xPosition The trees x position on the Terrain
     * @param t The Terrain object
     * @param image The Tree Image object
     */
    public Tree(int xPosition, Terrain t, PImage image){
        this.xPosition = xPosition;
        this.t = t;
        this.treeImage = image;

        this.xPositionVal = randomizeTreeXPosition(xPosition*32);
    }
    
    /**
     * A tree X-position randomizer. The function uses the random function to randomises the X-position of the tree to +-15 px around starting point which is 30px around starting point.
     * The function ensures that the tree position does not go beyond the App screen
     * @param treeXPosition The trees initial starting point
     * @return The randomised tree position
     */
    public int randomizeTreeXPosition(int treeXPosition){
        //Randomise +-15 hence 30px around starting point
        int randomisedValue = (int) ((Math.random() * ((15.0 + 15.0 + 1.0)) - 15.0));
        int randomisedTreePosition = treeXPosition + randomisedValue;

        //Make sure value is withing range
        if(randomisedTreePosition <= 0){
            randomisedTreePosition = 0;
        }else if(randomisedTreePosition >= App.WIDTH){
            randomisedTreePosition = App.WIDTH - treeDisplayDimensions;
        }

        //System.out.println("Initial tree position: "+treeXPosition+" Randomised tree value: "+randomisedValue+" Final randomised value: "+randomisedTreePosition);

        return randomisedTreePosition;
    }

    /**
     * The draw function draws the tree on the terrain
     * @param app The App function object
     */
    public void draw(App app){
        //System.out.println("You should see a tree");
        //int treeheight = (t.terrainMovingAverageHeight[xPositionVal + 16] - 30);
        int treeheight = (t.getTerrainHeight((xPositionVal + 16)) - 30);
        int treeXPosition = xPositionVal;
        if(treeImage != null)app.image(treeImage,treeXPosition,treeheight, treeDisplayDimensions,treeDisplayDimensions);

        // app.stroke(0,0,0);
        // app.fill(0,0,0);
        // app.rect(treeXPosition,treeheight, 31, 32);
    }

    /**
     * The draw function draws the tree on the terrain
     * @param app A PGraphics object of the terrain. The trees are drawn on the PGraphics object. 
     * This is used to increase the efficiency of the code by creating a PGraphics image and re-rendering it as compared to redrawing(terrain, trees e.t.c) in every frame 
     * @see Terrain#drawTerraingraphics 
     */
    public void draw(PGraphics app){
        //int treeheight = (t.terrainMovingAverageHeight[xPositionVal + 16] - 30); getTerrainHeight
        //int treeheight = (t.terrainMovingAverageHeight[xPositionVal + 16]-30);
        int treeheight = (t.getTerrainHeight((xPositionVal + 16)) -30);
        if(treeImage != null)app.image(treeImage,this.xPositionVal,treeheight, 32,32);

        // app.stroke(0,0,0);
        // app.fill(0,0,0);
        // app.line(this.xPositionVal,treeheight,(this.xPositionVal),(treeheight-30));
        // app.rect(treeXPosition,treeheight, 31, 32);

    }

    /**
     * The overridden toString method. 
     * @return The Trees position on the X-axis
     */
    @Override
    public String toString(){
        return "Position:" + xPosition;
    }
}
