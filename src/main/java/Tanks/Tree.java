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
        this.xPositionVal = (xPosition * 32);
    }
    

    /**
     * The draw function draws the tree on the terrain
     * @param app The App function object
     */
    public void draw(App app){
        //System.out.println("You should see a tree");
        int treeheight = (t.terrainMovingAverageHeight[xPositionVal + 16] - 30);
        int treeXPosition = xPositionVal;
        if(treeImage != null)app.image(treeImage,treeXPosition,treeheight, 32,32);
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
        int treeheight = (t.terrainMovingAverageHeight[xPositionVal + 16] - 30);
        int treeXPosition = xPositionVal;
        if(treeImage != null)app.image(treeImage,treeXPosition,treeheight, 32,32);
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
