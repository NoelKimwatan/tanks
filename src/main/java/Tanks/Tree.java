package Tanks;

import processing.core.PImage;
import processing.core.PGraphics;

public class Tree {
    private int xPosition;
    private Terrain t;
    private PImage treeImage;
    private int xPositionVal;


    public Tree(int xPosition, Terrain t, PImage image){
        this.xPosition = xPosition;
        this.t = t;
        this.treeImage = image;
        this.xPositionVal = (xPosition * 32);
    }
    

    //Draw terrain
    public void draw(App app){
        //System.out.println("You should see a tree");
        int treeheight = (t.terrainMovingAverageHeight[xPositionVal + 16] - 30);
        int treeXPosition = xPositionVal;
        if(treeImage != null)app.image(treeImage,treeXPosition,treeheight, 32,32);
        // app.stroke(0,0,0);
        // app.fill(0,0,0);
        // app.rect(treeXPosition,treeheight, 31, 32);
    }

    //Overloading to allow drawing from app or PGraphics
    public void draw(PGraphics app){
        int treeheight = (t.terrainMovingAverageHeight[xPositionVal + 16] - 30);
        int treeXPosition = xPositionVal;
        if(treeImage != null)app.image(treeImage,treeXPosition,treeheight, 32,32);
    }

    //How to display object when printed
    public String toString(){
        return "Position:" + xPosition;
    }
}
