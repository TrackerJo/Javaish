import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
public class TicTacToe {
	public static ArrayList<String> board = new ArrayList<String>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8"));
	public static boolean hasWon = false;
	public static boolean XTurn = true;
	public static void main(String[] args) {
		while( !hasWon){
			printBoard();
			if(XTurn){
				int move = Integer.parseInt(JOptionPane.showInputDialog("Where do you want to move as X?"));
				while(board.get(move) != String.valueOf(move)){
					JOptionPane.showMessageDialog(null, "Invalid Move");
					move = Integer.parseInt(JOptionPane.showInputDialog("Where do you want to move as X?"));
				}
				board.set(move, "X");
			} else {
				int move = Integer.parseInt(JOptionPane.showInputDialog("Where do you want to move as O?"));
				while(board.get(move) != String.valueOf(move)){
					JOptionPane.showMessageDialog(null, "Invalid Move");
					move = Integer.parseInt(JOptionPane.showInputDialog("Where do you want to move as O?"));
				}
				board.set(move, "O");
			}
			XTurn =  !XTurn;
			int eval = evaluate();
			if(eval == 10){
				hasWon = true;
				JOptionPane.showMessageDialog(null, "X WON!");
				printBoard();
			} else if(eval == -10){
				hasWon = true;
				JOptionPane.showMessageDialog(null, "O WON!");
				printBoard();
			}
		}
	}
	public static boolean isMovesLeft() {
		for(String cell : board){
			if(cell == "_"){
				return true;
			}
		}
		return false;
	}
	public static int evaluate() {
		//Check for Horizontal Then Vertical Then Diagonal Wins
		if(board.get(0) == board.get(1) && board.get(1) == board.get(2) && board.get(0) != "_"){
			if(board.get(3) == "X"){
				return 10;
			} else {
				return -10;
			}
		} else if(board.get(3) == board.get(4) && board.get(4) == board.get(5) && board.get(3) != "_"){
			if(board.get(3) == "X"){
				return 10;
			} else {
				return -10;
			}
		} else if(board.get(6) == board.get(7) && board.get(7) == board.get(8) && board.get(6) != "_"){
			if(board.get(6) == "X"){
				return 10;
			} else {
				return -10;
			}
		} else if(board.get(0) == board.get(3) && board.get(3) == board.get(6) && board.get(0) != "_"){
			if(board.get(0) == "X"){
				return 10;
			} else {
				return -10;
			}
		} else if(board.get(1) == board.get(4) && board.get(4) == board.get(7) && board.get(1) != "_"){
			if(board.get(1) == "X"){
				return 10;
			} else {
				return -10;
			}
		} else if(board.get(2) == board.get(5) && board.get(5) == board.get(8) && board.get(2) != "_"){
			if(board.get(2) == "X"){
				return 10;
			} else {
				return -10;
			}
		} else if(board.get(0) == board.get(4) && board.get(4) == board.get(8) && board.get(0) != "_"){
			if(board.get(0) == "X"){
				return 10;
			} else {
				return -10;
			}
		} else if(board.get(2) == board.get(4) && board.get(4) == board.get(6) && board.get(2) != "_"){
			if(board.get(2) == "X"){
				return 10;
			} else {
				return -10;
			}
		}
		return 0;
	}
	public static void printBoard() {
		String firstRow = board.get(0) + " | " + board.get(1) + " | " + board.get(2);
		String secondRow = board.get(3) + " | " + board.get(4) + " | " + board.get(5);
		String thirdRow = board.get(6) + " | " + board.get(7) + " | " + board.get(8);
		System.out.println(firstRow);
		System.out.println(secondRow);
		System.out.println(thirdRow);
		System.out.println("----------");
	}
}
