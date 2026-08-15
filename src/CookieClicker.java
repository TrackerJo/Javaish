import javax.swing.JOptionPane;
public class CookieClicker {
	public static int cookies = 0;
	public static int inc = 1;
	public static void main(String[] args) {
		displayCookie();
	}
	public static void displayCookie() {
		//Get choice
		int choice = Integer.parseInt(JOptionPane.showInputDialog("You have " + String.valueOf(cookies) + " cookies! Do you want to click the cookie (Enter 0) or Do you want to open the shop (Enter 1)"));
		if(choice == 0){
			cookies += inc;
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
		if(choice.equals("y")){
			if(cookies >= 5){
				cookies -= 5;
				inc *= 2;
				displayCookie();
			} else {
				JOptionPane.showMessageDialog(null, "You don't have enough cookies");
				displayCookie();
			}
		} else if(choice.equals("n")){
			displayCookie();
		} else {
			JOptionPane.showMessageDialog(null, "Thats not a valid choice!");
		}
	}
}
