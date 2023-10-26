import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Arrays;
public class Code {
	public static String hi = JOptionPane.showInputDialog("hello");
	public static ArrayList<String> list = new ArrayList<String>(Arrays.asList("5", "3"));
	public static int x = 5;
	public static void main(String[] args) {
		//Create hi string
	list.add("2");
	list.remove(2);
	list.remove("2");
	list.add("3");
	list.removeAll(Arrays.asList("3"));
	hi = String.valueOf(53);
	if(x == 2){
		hi = "now";
	} else if(x == 5){
		hi = "yay";
	} else {
		hi = "wow";
	}
	while(x < 3){
		x = 5;
	}
	for(String name : list){
		hi = name;
	}
	test("t");
	hi = test("4");
	JOptionPane.showMessageDialog(null, "hi");
	for(; x > 4; x += 3){
		System.out.println(x);
	}
	}
public static String test(String name) {
	System.out.println(name);
	return "2";
}
public static void done() {
	return;
}
}
