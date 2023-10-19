import javax.swing.JOptionPane;
public class Code {
	public static int cookies = 0;
	public static int inc = 1;
	public static String t = JOptionPane.showInputDialog("You have " + String.valueOf(cookies) + " cookies! Do you want to click the cookie (Enter 0) or Do you want to open the shop (Enter 1)");
	public static void main(String[] args) {
		displayCookie();
	}
	public static void displayCookie() {
		int choice = Integer.parseInt(JOptionPane.showInputDialog("You have " + String.valueOf(cookies) + " cookies! Do you want to click the cookie (Enter 0) or Do you want to open the shop (Enter 1)"));
		if(choice == 0){
			displayCookie();
		} else if(choice == 1){
			showShop();
		} else {
			System.out.println(choice);
			JOptionPane.showMessageDialog(null, "Thats not a valid choice!");
			displayCookie();
		}
	}
	public static void showShop() {
		String choice = JOptionPane.showInputDialog("Do you want to buy click upgrade for 5 cookies? (y/n)");
		if(choice == "y"){
			if(cookies >= 5){
				displayCookie();
			} else {
				JOptionPane.showMessageDialog(null, "You don't have enough cookies");
				displayCookie();
			}
		} else if(choice == "n"){
			displayCookie();
		} else {
			JOptionPane.showMessageDialog(null, "Thats not a valid choice!");
		}
	}
}
