package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();

    //Created
    private PImage background;
    public int[] foreGroundColor;
    public PImage tree;
    private char[][] terrain = new char[BOARD_HEIGHT][28];
    Terrain gameTerraine;
    boolean gameOver = false;

    public ArrayList<Integer> treePositions = new ArrayList<Integer>();
    public ArrayList<Tree> trees = new ArrayList<Tree>();
    //public static ArrayList<Tank> tanks = new ArrayList<Tank>();
    public static HashMap<Character,Tank> tanks = new HashMap<Character,Tank>();
    public static HashMap<Integer,Character> hPlayerPos = new HashMap<Integer,Character>();
    public static HashMap<Integer,Character> AIPlayerPos = new HashMap<Integer,Character>();
    public static ArrayList<Character> hPlayerSortedLetters;

    public static ArrayList<Projectile> projectileQueue = new ArrayList<Projectile>();
    public static ArrayList<Explosion> explossionQueue = new ArrayList<Explosion>();

    Text textObject;
    public static int currentPlayerNo = 0;
    public static Tank currentPlayer;
    

    public static JSONObject playerColours;
    public static String playersTurn;
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);

        //Set text size to 14
        this.textSize(14);
		//See PApplet javadoc:
		//loadJSONObject(configPath)
		//loadImage(this.getClass().getResource(filename).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));

        //Loading configPath
        JSONObject configJSON = loadJSONObject(configPath);
        int levelNo = 0;
        playersTurn = "A";

        String backgroundFileName = configJSON.getJSONArray("levels").getJSONObject(levelNo).get("background").toString();
        String terrainFileName = configJSON.getJSONArray("levels").getJSONObject(levelNo).get("layout").toString();
        String[] foreGroundColorName = configJSON.getJSONArray("levels").getJSONObject(levelNo).get("foreground-colour").toString().split(",");
        playerColours = (JSONObject)configJSON.get("player_colours");
        String treesFileName;

        try{
            treesFileName = configJSON.getJSONArray("levels").getJSONObject(levelNo).get("trees").toString();
        }catch(Exception e){
            treesFileName = null;
        }


        //Loading the snow background image
        this.background = this.loadImage(this.getClass().getResource(backgroundFileName).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        this.foreGroundColor  = new int[]{Integer.valueOf(foreGroundColorName[0]), Integer.valueOf(foreGroundColorName[0]), Integer.valueOf(foreGroundColorName[0])};

        //Load tree image
        if(treesFileName != null){
            this.tree = this.loadImage(this.getClass().getResource(treesFileName).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        }else{
            this.tree = null;
        }

        //Loading terraine
        //File level = this.
        File levelFile = new File(terrainFileName);

        try{

            Scanner next = new Scanner(levelFile);

            int row = 0;
            while(next.hasNext()){  
                String line = next.nextLine();
                terrain[row] = line.toCharArray();
                row++;
            }   
            next.close();

        } catch (FileNotFoundException e){

            System.out.println("File not found");

        }

        //Setup terraine
        gameTerraine = new Terrain(BOARD_HEIGHT,28,terrain,this);
        gameTerraine.setup();


        //Setup trees
        System.out.println("Printing terraine: "+gameTerraine);

        //Setup text
        textObject = new Text(this);



    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        System.out.println("Keyboard pressed: "+ event.getKeyCode());

        //Check for key press only if game is not Over
        if(!gameOver){

            if(event.getKeyCode() == 39){
                System.out.println("Forward pressed");
                currentPlayer.forward();
            }else if(event.getKeyCode() == 37){
                System.out.println("Back pressed");
                currentPlayer.backward();
            }else if(event.getKeyCode() == 38){
                System.out.println("Up button pressed");
                currentPlayer.turrentMovement(-1);
            }else if(event.getKeyCode() == 40){
                System.out.println("Down button pressed");
                currentPlayer.turrentMovement(1);
            }else if(event.getKeyCode() == 32){
                System.out.println("Spacebar pressed");
                currentPlayer.fire();
                currentPlayerNo = currentPlayerNo + 1;
                textObject.generateRandonWind();

                while(tanks.get(hPlayerSortedLetters.get(currentPlayerNo%hPlayerSortedLetters.size())).deleted == true){
                    currentPlayerNo = currentPlayerNo + 1;
                }
                System.out.println("Tanks size: "+tanks.size());
                System.out.println("Calculations value: "+ (currentPlayerNo % tanks.size()) );
                System.out.println("Player letter: "+hPlayerSortedLetters.get(currentPlayerNo%hPlayerSortedLetters.size()));
                currentPlayer = tanks.get(hPlayerSortedLetters.get(currentPlayerNo%hPlayerSortedLetters.size()));
                System.out.println("Current player selected");
                //Change player
            }else if(event.getKeyCode() == 87){
                //W has been pressed
                currentPlayer.turrentPower(+1);
                //System.out.println("Button 87 pressed");
            }else if(event.getKeyCode() == 83){
                //S pressed
                currentPlayer.turrentPower(-1);
                //System.out.println("Button 83 pressed");
            }
        }

        //39 forward
        //37 back
        //38 up
        //40 down
        //32 spacebar
        
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(KeyEvent event){
        if(event.getKeyCode() == 39 || event.getKeyCode() == 37){
            currentPlayer.stop();
        }else if(event.getKeyCode() == 38 || event.getKeyCode() == 40){
            currentPlayer.turrentMovementStop();
        }else if(event.getKeyCode() == 83 || event.getKeyCode() == 87){
            System.out.println("Button 83 or 87 released");
            currentPlayer.turrentPower(0);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //TODO - powerups, like repair and extra fuel and teleport


    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {

        //-------------------------------------------
        //--------Check no of players remaining------
        //-------------------------------------------

        int remainingPlayers = 0;
        for(Tank tank : tanks.values()){
            if(!tank.deleted){
                remainingPlayers += 1;
            }
        }
        if (remainingPlayers == 1){
            gameOver = true;
        }

        //-------------------------------------------
        //----------Stop game when game is over------
        //-------------------------------------------

        //-------------------------------------------
        //----------Ensure current player is alive------
        //-------------------------------------------
        if(tanks.get(hPlayerSortedLetters.get(currentPlayerNo%hPlayerSortedLetters.size())).deleted == true){
            while(tanks.get(hPlayerSortedLetters.get(currentPlayerNo % hPlayerSortedLetters.size())).deleted == true && gameOver == false){
                System.out.println("Current player is deleted");
                currentPlayerNo = currentPlayerNo + 1;
            }
            currentPlayer = tanks.get(hPlayerSortedLetters.get(currentPlayerNo%hPlayerSortedLetters.size()));
        }

        //Set image backgroud
        this.image(this.background, 0, 0);

        //Draw terrain
        this.gameTerraine.draw(this);

        //Draw trees
        for (Tree t : trees){
            t.draw(this);
        }

        //Draw tanks
        for (char c : App.tanks.keySet()){
            Tank tank = tanks.get(c);

            //Do not draw deleted tank
            if(!tank.deleted){
                tank.refresh();
                tank.draw(this);
            }

        }



        //-------------------------------------------
        //----------Draw projectiles from Queue------
        //-------------------------------------------

        for(int i = 0; i < projectileQueue.size(); i++){
            Projectile projectile = projectileQueue.get(i);

            //Only refresh if game is not over
            if (!gameOver)projectile.refresh();

            if(projectile.delete){
                projectileQueue.remove(i);
            }else{
                //System.out.println("Projectile to delete");
                projectile.draw(this);
            }   
        }              



        //-------------------------------------------
        //----------Draw explossion from Queue------
        //-------------------------------------------
        for(int i = 0; i < explossionQueue.size(); i++){
            Explosion explosonObject = explossionQueue.get(i);
            if(explosonObject.delete){
                explossionQueue.remove(i);
            }else{
                explosonObject.draw(this);
            }
        }

        //Refresh text
        textObject.refreshText(this);
        textObject.draw(this);

        













        

        



        //----------------------------------
        //display HUD:
        //----------------------------------
        //TODO

        //----------------------------------
        //display scoreboard:
        //----------------------------------
        //TODO
        
		//----------------------------------
        //----------------------------------

        //TODO: Check user action
    }


    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
