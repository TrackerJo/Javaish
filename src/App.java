
import javaish.JavaishVal.JavaishType;
import javaish.Runner;
import javaish.State;
import javaish.Result;
import javaish.Return;
import javaish.Variables;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

// import org.json.*;


public class App {
    public static void main(String[] args) throws Exception {
        // Runner.runFile("/Users/nathanielkemmenash/Desktop/VS Code Projects/Javaish/src/code.javaish");
        String oldStateString = "false***true***5***null***false***===bill,STRING test?,2, (|||PRINT:::5:::ran line 5,6,STRING|||PRINT:::6:::ran line 6,6,STRING|||)___***===bill,STRING test?,2, (|||PRINT:::5:::ran line 5,6,STRING|||PRINT:::6:::ran line 6,6,STRING|||)___***";
        State oldState = Runner.convertStringToState(oldStateString);
        System.out.println(oldState.getCurrentLine());
        State newState = Runner.debugFile("src/debug.javaish", oldState);
       System.out.println(Runner.convertStateToString(newState));

    }

   
}
