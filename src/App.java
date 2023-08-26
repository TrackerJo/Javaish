import TurtleGraphics.Turtle;
import TurtleGraphics.World;
import javaish.Runner;


public class App {
    public static void main(String[] args) throws Exception {
      
        Runner.runFile("src/javaish/code.javaish");
        World w = new World();
        Turtle t = new Turtle(w);
        t.forward(50);
    }
}
