package Tanks;


import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.time.Clock;

//Unused Imports
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import processing.data.JSONArray;
import org.checkerframework.checker.units.qual.A;

/**
 * The App object representing the app or game
 * @Author Noel Kimwatan
 */
public class App extends PApplet {

    //Public and final variables
    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;
    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static final int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static final int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;
    public static final int INITIAL_PARACHUTES = 1;
    public static final int FPS = 30;


    //Private variables
    private String configPath;

    public static Random random = new Random();

    //Created
    private static PImage background;
    private static PImage parachuteImage;
    private static int[] foreGroundColor;
    private static PImage tree;
    private static char[][] terrain = new char[BOARD_HEIGHT][28];
    private static Terrain gameTerraine;
    private static boolean gameOver = false;
    private static int changeLevelCounter = 30;  //1 second == 30 frames
    private static int levelNo = 0;
    private static boolean newGame = true;

    //To remove
    public Clock clock = Clock.systemDefaultZone();

    
    
    //public static ArrayList<Tank> tanks = new ArrayList<Tank>();


    private static HashMap<Character,Tank> tanks = new HashMap<Character,Tank>();
    private static ArrayList<Character> hPlayerSortedLetters = new ArrayList<Character>();
    private static ArrayList<Character> alivePlayers = new ArrayList<Character>();
    private static ArrayList<Projectile> projectileQueue = new ArrayList<Projectile>();
    private static ArrayList<Explosion> explossionQueue = new ArrayList<Explosion>();

    private static Text textObject;
    private static  int currentPlayerNo = 0;
    private static  Tank currentPlayer;
    

    public static JSONObject playerColours;

	

    /**
     * The app constructor. It sets the config file path
     */
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
     * Setsup and initialises the App by loading all resources such as images. Initialise the elements such as the player and map elements. 
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
        background = this.loadImage(this.getClass().getResource(backgroundFileName).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        foreGroundColor  = new int[]{Integer.valueOf(foreGroundColorName[0]), Integer.valueOf(foreGroundColorName[0]), Integer.valueOf(foreGroundColorName[0])};

        //Load tree image
        if(treesFileName != null){
            tree = this.loadImage(this.getClass().getResource(treesFileName).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        }else{
            tree = null;
        }
        
        try{
            parachuteImage =loadImage(getClass().getResource("parachute.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        }catch(Exception e){
            parachuteImage = null;
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

        //Set image backgroud once
        //this.image(this.background, 0, 0);

        //Setup terrain
        gameTerraine = new Terrain(BOARD_HEIGHT,28,terrain,this);
        gameTerraine.setup();

        gameTerraine.draw(this);

        //Sort Alive players once added
        alivePlayers.sort(Comparator.naturalOrder());
        

        //Sort players according to Charcter
        hPlayerSortedLetters.sort(Comparator.naturalOrder());
        currentPlayer = App.getTank(App.hPlayerSortedLetters.get(0));
        System.out.println("Current player: "+currentPlayer.playerCharacter());


        //Setup trees
        System.out.println("Printing terraine: "+gameTerraine);

        //Setup text
        textObject = new Text(this,gameTerraine);
    }

    /**
     * Getter method to get the Background Image
     * @return The PImage object of the background
     */
    public static PImage getBackgroundImage(){
        return background;
    }

    /**
     * Getter method to get the Parachute Image
     * @return The PImage object of the parachute
     */
    public static PImage getParachuteImage(){
        return parachuteImage;
    }

    /**
     * Getter method to get the foreground colour 
     * @return The foreground rgb colour
     */
    public static int[] getForeGroundColor(){
        return foreGroundColor;
    }

    /**
     * Getter method to get the Tree Image
     * @return The PImage object of tree 
     */
    public static PImage getTreeImage(){
        return tree;
    }

    /**
     * Getter method to get if the game is over
     * @return A boolean object indicating whether the game if over or Not
     */
    public static boolean isGameover(){
        return gameOver;
    }

    /**
     * Getter method to get if its a new game
     * @return A boolean object indicating whether its a new game of not
     */    
    public static boolean isNewGame(){
        return newGame;
    }

    /**
     * Getter method to get a Tank
     * @param c The character of the Tank to get
     * @return A tank object of the character passed
     */
    public static Tank getTank(char c){
        return tanks.get(c);
    }

    /**
     * Getter method to get a sorted list of player letters
     * @return Asorted list of player letters
     */
    public static ArrayList<Character> getSortedPlayerLetters(){
        return hPlayerSortedLetters;
    }

    /**
     * Setter method to add a Tank to the game
     * @param c The caharacter of the Tank
     * @param tank The Tank object of the Tank
     * @see Tank
     */
    public static void addTank(char c, Tank tank){
        tanks.put(c,tank);
        addAlivePlayer(c);
        if(App.isNewGame()){
            //System.out.println("Added player: "+c+" To new game");
            hPlayerSortedLetters.add(c);
        }
    }

    /**
     * Setter method to add a Tank to the list of alive tanks
     * @param c The character of the Tank
     */
    public static void addAlivePlayer(char c){
        if(!alivePlayers.contains(c)){
            alivePlayers.add(c);
        }
    }

    /**
     * Getter method to get current player
     * @return A Tank object of the current player
     * @see Tank
     */
    public static Tank currentPlayer(){
        return currentPlayer;
    }

    public static Terrain getGameterrain(){
        return gameTerraine;
    }

    /**
     * Getter method to get an ArrayList of Alive Tank players
     * @return An array list of Alive Tank players
     */
    public static ArrayList<Tank> getAliveTanks(){
        ArrayList<Tank> aliveTanks = new ArrayList<Tank>();
        tanks.forEach((character,tank) -> {
            if(!tank.isNotActive()){
                aliveTanks.add(tank);
            }else{
                if(App.alivePlayers.contains(tank.playerCharacter())){
                    App.alivePlayers.remove(tank.playerCharacter()); 
                    System.out.println("Tank deleted");
                }    
            }
        });

        return aliveTanks;
    }


    // public static ArrayList<Character> alivePlayersChar(){
    //     return alivePlayers;
    // }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        //System.out.println("Keyboard pressed: "+ event.getKeyCode());
        int code = event.getKeyCode();

        //Check for key press only if game is not Over
        if(!gameOver){

            if(code == 39){
                //System.out.println("Forward pressed");
                currentPlayer.move(1);
            }else if(code == 37){
                //System.out.println("Back pressed");
                currentPlayer.move(-1);
            }else if(code == 38){
                //System.out.println("Up button pressed");
                currentPlayer.turrentMovement(-1);
            }else if(code == 40){
                //System.out.println("Down button pressed");
                currentPlayer.turrentMovement(1);
            }else if(code == 32){
                //System.out.println("Spacebar pressed");
                currentPlayer.fire();
                currentPlayerNo = currentPlayerNo + 1;
                gameTerraine.changeWind();

                if(alivePlayers.size() <= 1){
                    changeLevel();
                }

                while(tanks.get(hPlayerSortedLetters.get(currentPlayerNo%hPlayerSortedLetters.size())).isNotActive() == true &&  alivePlayers.size() > 1){
                    currentPlayerNo = currentPlayerNo + 1;
                }
                //System.out.println("Tanks size: "+tanks.size());
                //System.out.println("Calculations value: "+ (currentPlayerNo % tanks.size()) );
                //System.out.println("Player letter: "+hPlayerSortedLetters.get(currentPlayerNo%hPlayerSortedLetters.size()));
                currentPlayer = tanks.get(hPlayerSortedLetters.get(currentPlayerNo%hPlayerSortedLetters.size()));
                //System.out.println("Current player selected");
                //Change player


            }else if(code == 87){
                //W has been pressed
                currentPlayer.turrentPower(+1);
                //System.out.println("Button 87 pressed");
            }else if(code == 83){
                //S pressed
                currentPlayer.turrentPower(-1);
                //System.out.println("Button 83 pressed");
            }else if(code == 80 || code == 82 || code == 70 || code == 88){
                //Check if player has pressed p to purchase parachute
                // 80, 82, 70
                currentPlayer.handlePowerUps(code);

            }
        }else{
            if(code == 82){
                System.out.println("Game reset");
                levelNo = 0;
                App.newGame = true;
                gameOver = false;
                hPlayerSortedLetters = new ArrayList<Character>(); 
                setup();
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
     * @param event The Key event passed when a key is released
     */
	@Override
    public void keyReleased(KeyEvent event){
        if(event.getKeyCode() == 39 || event.getKeyCode() == 37){
            currentPlayer.move(0);
        }else if(event.getKeyCode() == 38 || event.getKeyCode() == 40){
            currentPlayer.turrentMovement(0);
        }else if(event.getKeyCode() == 83 || event.getKeyCode() == 87){
            System.out.println("Button 83 or 87 released");
            currentPlayer.turrentPower(0);
        }
    }

    /**
     * Receives mouse press signals from the Mouse
     * @param e The mouse press event 
     */
    @Override
    public void mousePressed(MouseEvent e) {
        //TODO - powerups, like repair and extra fuel and teleport


    }

    /**
     * Receives mouse release signals from the Mouse
     * @param e The mouse release event 
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Redraws the terrain. This function is only when the terrain is deformed by a projectile {@link Projectile} to increase efficienty
     */
    public void drawTerrain(){
        //Draw terrain after deformation
        App.gameTerraine.draw(this);
    }



    public static void addProjectile(Projectile projectile){
        App.projectileQueue.add(projectile);
    }

    public static void addExplosion(Explosion explossion){
        App.explossionQueue.add(explossion);
    }



    /**
     * Changes the game level once a game level is over. The function deletes all current projectiles and increaments the level number 
     * One a player gets to the last level the gema is over 
     */
    private void changeLevel(){
        explossionQueue.clear();
        projectileQueue.clear();

        if(levelNo == 2){
            gameOver = true;
            System.out.println("Game over");

            //Sort According to Score
            hPlayerSortedLetters.sort(new Comparator<Character>(){
                public int compare(Character c1, Character c2){
                    return Integer.compare(App.tanks.get(c2).getTankScore(), App.tanks.get(c1).getTankScore());
                }
            });
        }else{
            System.out.println("Level over");
            currentPlayerNo = 0;
            levelNo += 1;
            App.newGame = false;
            setup();
        }
    }

    public int getLevel(){
        return levelNo;
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        //System.out.println(clock.millis());

        //-------------------------------------------
        //--------Check no of players remaining------
        //-------------------------------------------
        if(alivePlayers.size() <= 1){
            System.out.println("Level over");
            changeLevelCounter -= 1;

            if(changeLevelCounter <= 0){
                changeLevelCounter = 60;
                changeLevel();
            }
        }
        
        
        //----------------------------------------------
        //------Check is current player is deleted------
        //----------------------------------------------
        if(currentPlayer.isNotActive() == true){
            while(tanks.get(hPlayerSortedLetters.get(currentPlayerNo%hPlayerSortedLetters.size())).isNotActive() == true && alivePlayers.size() > 1){
                System.out.println("Current player is deleted");
                currentPlayerNo = currentPlayerNo + 1;
            }
            currentPlayer = tanks.get(hPlayerSortedLetters.get(currentPlayerNo%hPlayerSortedLetters.size()));
        }




        //-------------------------------------------
        //----------Stop game when game is over------
        //-------------------------------------------




        App.gameTerraine.draw(this);

        // -------------------------------------------
        // ----------Draw explossion from Queue------
        // -------------------------------------------
        if(explossionQueue.size() > 0){
            for(int i = 0; i < explossionQueue.size(); i++){
                Explosion explosonObject = explossionQueue.get(i);
                if(explosonObject.delete){
                    explossionQueue.remove(i);
                }else{
                    explosonObject.refresh();
                    explosonObject.draw(this);
                }
            }
        }

        //Remove dead players
        for(Tank tank: tanks.values()){
            if(!tank.isNotActive()){
                tank.draw(this);
            }else{
                if(App.alivePlayers.contains(tank.playerCharacter())){
                    App.alivePlayers.remove(App.alivePlayers.indexOf(tank.playerCharacter())); 
                    System.out.println("Tank deleted");
                }            
            }
        }




        //-------------------------------------------
        //----------Draw projectiles from Queue------
        //-------------------------------------------

        if(projectileQueue.size() > 0){
            System.out.println("Alive players: "+alivePlayers.size());
            for(int i = 0; i < projectileQueue.size(); i++){
                Projectile projectile = projectileQueue.get(i);
    
                //Only refresh if game is not over
                if (!gameOver)projectile.refresh();
    
                if(projectile.isActive()){
                    projectileQueue.remove(i);
                }else{
                    //System.out.println("Projectile to delete");
                    projectile.draw(this);
                }   
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


    /**
     * The App class main method. This serves as the entrypoint of the Application or game
     * @param args Aruments passed in 
     */
    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
