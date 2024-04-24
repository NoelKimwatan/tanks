package Tanks;


import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SampleTest {
    

    @Test
    public void simpleTest() {
        App app = new App();
        app.setup();
        app.settings();

        assert(1 + 2 == 3);

        assert(App.FPS == 30);
    }

    // @Test
    // public void newSimpleTest(){
    //     App app = new App();
    //     assert(App.WIDTH == 864);
    // }
}

//gradle test jacocoTestReport