import java.util.Arrays;
import java.util.ArrayList;
import javax.swing.JOptionPane;
public class Code {
	public static String test(String name) {
		System.out.println(name);
		return "2";
	}
	public static void done() {
		return;
	}
	public static void main(String[] args) {
		String hi = JOptionPane.showInputDialog("hello");;
		ArrayList<String> list = new ArrayList<String>(Arrays.asList("5", "3"));
		hi = String.valueOf(53);
		int x = 5;
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
}
