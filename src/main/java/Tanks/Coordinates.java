package Tanks;
/**
 * The co-ordinates Interface, Implemented by Classes that use X and Y co-ordinates. Representing the position on a screen
 */
public interface Coordinates {
    /**
     * Setter method used to set the X-axis position on the screen
     * @param xValue The X-axis value of the co-ordinate
     */
    public void setXPosition(double xValue);

    /**
     * Setter method used to set the Y-axis position on the screen
     * @param yValue The Y-axis value of the co-ordinate
     */
    public void setYPosition(double yValue);

    /**
     * A Getter method used return the X-axis position
     * @return The X position
     */
    public double getXPosition();

    /**
     * A Getter method used return the X-axis position
     * @return The Y position
     */
    public double getYPosition();
}
