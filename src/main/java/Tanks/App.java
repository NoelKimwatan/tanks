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
 * @author Noel Kimwatan
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
    private String configPath;
    private static HashMap<Character,Tank> tanks;
    private static ArrayList<Character> hPlayerSortedLetters;
    private static ArrayList<Character> alivePlayers;
    private static ArrayList<Projectile> projectileQueue = new ArrayList<Projectile>();
    private static ArrayList<Explosion> explossionQueue = new ArrayList<Explosion>();
    private static Text textObject;
    private static int currentPlayerNo = 0;
    private static Tank currentPlayer;
    private static JSONObject playerColours;

	

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

        //Loading configPath
        JSONObject configJSON = loadJSONObject(configPath);
        

        String backgroundFileName = configJSON.getJSONArray("levels").getJSONObject(levelNo).get("background").toString();
        String terrainFileName = configJSON.getJSONArray("levels").getJSONObject(levelNo).get("layout").toString();
        String[] foreGroundColorName = configJSON.getJSONArray("levels").getJSONObject(levelNo).get("foreground-colour").toString().split(",");
        playerColours = (JSONObject)configJSON.get("player_colours");
        String treesFileName;

        //Loading the snow background image
        background = this.loadImage(this.getClass().getResource(backgroundFileName).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        foreGroundColor  = new int[]{Integer.valueOf(foreGroundColorName[0]), Integer.valueOf(foreGroundColorName[1]), Integer.valueOf(foreGroundColorName[2])};

        try{
            treesFileName = configJSON.getJSONArray("levels").getJSONObject(levelNo).get("trees").toString();
        }catch(Exception e){
            treesFileName = null;
        }

        //Load tree image
        if(treesFileName != null){
            tree = this.loadImage(this.getClass().getResource(treesFileName).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        }else{
            tree = null;
        }
        
        //Loading parachuteImage
        try{
            parachuteImage =loadImage(getClass().getResource("parachute.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        }catch(Exception e){
            parachuteImage = null;
        }

        File levelFile = new File(terrainFileName);

        //Loading terrain file
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

        //Reset variables if its a new game
        if(App.isNewGame()){
            hPlayerSortedLetters = new ArrayList<Character>();
            tanks = new HashMap<Character,Tank>();
            alivePlayers = new ArrayList<Character>();
        }

        //Setup terrain
        gameTerraine = new Terrain(BOARD_HEIGHT,28,terrain,this);
        gameTerraine.setup();
        gameTerraine.draw(this);

        //Sort all players once added
        alivePlayers.sort(Comparator.naturalOrder());
        

        //Sort players list if its a new game
        if(App.isNewGame()){
            hPlayerSortedLetters.sort(Comparator.naturalOrder());
        }

        currentPlayer = App.getTank(App.hPlayerSortedLetters.get(0));

        //Setup text
        textObject = new Text(this,gameTerraine);
    }

    /**
     * A getter method to get an Apps terrain
     * @return An Apps Terrain object
     */
    public Terrain getTerrain(){
        return gameTerraine;
    }

    /**
     * A getter method to obtain the player colours from the JSON file
     * @return A JSON object of all the player colours
     */
    public static JSONObject getPlayerColours(){
        return App.playerColours;
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
            if(!hPlayerSortedLetters.contains(c)){
                hPlayerSortedLetters.add(c);
            }
            
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

    /**
     * Setter method to get current player. Most used by test cases
     * @param currentToSet A Tank object of the current player
     * @see Tank
     */
    public static void setCurrentPlayer(Tank currentToSet){
        App.currentPlayer = currentToSet;
    }

    /**
     * Getter method to get an ArrayList of Alive Tank players
     * @return An array list of Alive Tank players
     */
    public static ArrayList<Tank> getAliveTanks(){
        ArrayList<Tank> aliveTanks = new ArrayList<Tank>();

        for(Tank tank: tanks.values()){
            if(!tank.isNotActive()){
                aliveTanks.add(tank);
            }
        }
        return aliveTanks;
    }


    /**
     * This method is used to reset a game to start afresh
     */
    public void resetGame(){
        levelNo = 0;
        App.newGame = true;
        gameOver = false;
        App.hPlayerSortedLetters = new ArrayList<Character>(); 
        setup();
    }


    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        int code = event.getKeyCode();

        //Check for key press only if game is not Over
        if(!gameOver){
            //Forward pressed
            if(code == 39){
                currentPlayer.move(1);
            //Back pressed
            }else if(code == 37){
                currentPlayer.move(-1);
            //Up button pressed
            }else if(code == 38){
                currentPlayer.turrentMovement(-1);
            //Down button pressed
            }else if(code == 40){
                currentPlayer.turrentMovement(1);
            //Space pressed
            }else if(code == 32){
                currentPlayer.fire();
                //Change wind when changing player
                gameTerraine.changeWind();
                this.changeCurrentPlayer();

                //If no players alive change level
                if(alivePlayers.size() <= 1){
                    changeLevel();
                }
            //W Pressed
            }else if(code == 87){
                currentPlayer.turrentPower(+1);
            //S Pressed
            }else if(code == 83){
                currentPlayer.turrentPower(-1);
            //When power up pressed
            }else if(code == 80 || code == 82 || code == 70 || code == 88){
                //Hand power ups
                currentPlayer.handlePowerUps(code);
            }
        }else{
            if(code == 82){
                //When game over and player pressed 'r'
                this.resetGame();
            }
        }        
    }

    /**
     * Receive key released signal from the keyboard.
     * @param event The Key event passed when a key is released
     */
	@Override
    public void keyReleased(KeyEvent event){
        if(event.getKeyCode() == 39 || event.getKeyCode() == 37){
            //Stop movement when back or forward button released
            currentPlayer.move(0);
        }else if(event.getKeyCode() == 38 || event.getKeyCode() == 40){
            //Stop Turrent movement when up or down button released
            currentPlayer.turrentMovement(0);
        }else if(event.getKeyCode() == 83 || event.getKeyCode() == 87){
            //Stop power increase or decrease when 'w' or 's' released
            currentPlayer.turrentPower(0);
        }
    }

    /**
     * Receives mouse press signals from the Mouse
     * @param e The mouse press event 
     */
    @Override
    public void mousePressed(MouseEvent e) {

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

    /**
     * This function changes the current player to the next consecutive player
     */
    public void changeCurrentPlayer(){
        currentPlayerNo += 1;

        if(getAliveTanks().size() > 1){
            while(tanks.get(hPlayerSortedLetters.get(currentPlayerNo%hPlayerSortedLetters.size())).isNotActive() == true &&  getAliveTanks().size() > 1){
                currentPlayerNo = currentPlayerNo + 1;
            }
            currentPlayer = tanks.get(hPlayerSortedLetters.get(currentPlayerNo%hPlayerSortedLetters.size()));
        }else if(getAliveTanks().size() == 1){
            currentPlayer = getAliveTanks().get(0);

        }else if(getAliveTanks().size() <= 0){
            changeLevel();
        }
    }


    /**
     * This method adds a projectile to the projectile Queue
     * @param projectile The projectile to add to the Queue
     */
    public static void addProjectile(Projectile projectile){
        App.projectileQueue.add(projectile);
    }

    /**
     * This method adds an Explossion to the explossion Queue
     * @param explossion The explossion to add to the Queue
     */
    public static void addExplosion(Explosion explossion){
        App.explossionQueue.add(explossion);
    }

    /**
     * Get projectile from Projectile Queue
     * @param index The index of the projectile
     * @return The projectile Object or Null
     */
    public static Projectile getProjectile(int index){
        if (index < projectileQueue.size()){
            return projectileQueue.get(index);
        }else{
            return null;
        }
    }


    /**
     * Get Explossion from Explossion Queue
     * @param index The index of the explossion
     * @return The explossion object
     */
    public static Explosion getExplossion(int index){
        if (index < explossionQueue.size()){
            return explossionQueue.get(index);
        }else{
            return null;
        }
    }    




    /**
     * Changes the game level once a game level is over. The function deletes all current projectiles and increaments the level number 
     * One a player gets to the last level the gema is over 
     */
    public void changeLevel(){
        //Clear all projectiles and explossions when changing levels
        explossionQueue.clear();
        projectileQueue.clear();

        //Restart current player counter
        currentPlayerNo = 0;
        //Restart change level counter
        changeLevelCounter = 30;

        //When level two is over the game os over
        if(levelNo == 2){
            gameOver = true;

            //Sort According to Score
            hPlayerSortedLetters.sort(new Comparator<Character>(){
                public int compare(Character c1, Character c2){
                    return Integer.compare(App.tanks.get(c2).getTankScore(), App.tanks.get(c1).getTankScore());
                }
            });
        }else{
            //Vhange level when level is not equal to 2
            levelNo += 1;
            App.newGame = false;
            setup();
        }
    }

    /**
     * Getter method to obtain a games level
     * @return The games current level
     */
    public int getLevel(){
        return levelNo;
    }

    /**
     * This method draws all elements in the game by current frame.
     */
	@Override
    public void draw() {

        //Start counter when game is over and only 1 player remaining
        if(alivePlayers.size() <= 1 && isGameover() == false){
            changeLevelCounter -= 1;

            if(changeLevelCounter <= 0){
                changeLevel();
            }
        }
        
        //Change player if current player is deleted
        if(currentPlayer.isNotActive() == true){
            this.changeCurrentPlayer();
        }

        //Draw terrain
        App.gameTerraine.draw(this);

        //Draw explossion in Queue
        if(explossionQueue.size() > 0){
            for(int i = 0; i < explossionQueue.size(); i++){
                Explosion explosonObject = explossionQueue.get(i);
                if(explosonObject.isDeleted()){
                    explossionQueue.remove(i);
                }else{
                    explosonObject.refresh();
                    explosonObject.draw(this);
                }
            }
        }

        //Remove dead players every frame
        for(Tank tank: tanks.values()){
            if(!tank.isNotActive()){
                tank.draw(this);
            }else{
                if(App.alivePlayers.contains(tank.playerCharacter())){
                    App.alivePlayers.remove(App.alivePlayers.indexOf(tank.playerCharacter())); 
                }            
            }
        }

        //Handle projectile if there is a projectile in Queue
        if(projectileQueue.size() > 0){
            for(int i = 0; i < projectileQueue.size(); i++){
                Projectile projectile = projectileQueue.get(i);

                //Only refresh if game is not over
                if (!gameOver)projectile.refresh();
                //Remove projectile from Queue if not active or deleted
                if(projectile.isNotActive()){
                    projectileQueue.remove(i);
                }else{
                    projectile.draw(this);
                }   
            }   
        }
           
        //Refresh text
        textObject.refreshText(this);
        textObject.draw(this);
    }

    /**
     * The App class main method. This serves as the entrypoint of the Application or game
     * @param args Aruments passed in 
     */
    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }
}
