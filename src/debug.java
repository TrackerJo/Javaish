import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
public class debug {
	public static int l = 20;
	public static ArrayList<Boolean> boollist = new ArrayList<Boolean>(Arrays.asList(false, true, false, 1 == 2));
	public static String name = "Michael Phelan";
	public static void main(String[] args) {
		//let int[] intList = [1, 3, 43, 4, (20 + 5)].
		//let int x = intList sub 4.
		System.out.println(l);
		if(boollist.size() > 3){
			JOptionPane.showMessageDialog(null, "he");
		}
		System.out.println("Hello " + name);
		for(; l < 3; l += 1){
			System.out.println(String.valueOf(l));
		}
		//print(boollist sub 3).
	}
	public static void test(String name) {
		int age = 5;
		System.out.println("Hello World " + age + " " + name);
	}
}
