
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class SudokuBoard extends JPanel implements MouseListener {
	/*
	This class is for testing proper rendering and mouse controls while
	supporting and reacting to window size changes. We need to figure
	this out for Congkopi.
	
	Also, I'm going to write in canonical Sun style this time.
	*/

	int[][] board = new int[9][9];
	// We won't design for changing the number of columns and rows, because
	// it's programatically difficult to support multiple configurations.
	// And Congkopi certainly won't have varying configurations.
	
	private int[] widths;
	private int[] heights;
	private int boardWidth;
	private int boardHeight;
	// Secret weapons that will help us later

	SudokuBoard() {
		final int
			W_TILE = 40, // Tile width
			W_MAJOR = 4, // Major line width
			W_MINOR = 2; // Minor line width
		// For simplicity's sake, and also to match Congkopi, let's
		// have constant tile sizes. This means we won't scale the tiles 
		// up to fit the window.. which also means we don't have to
		// scale mouse coordinates to logical coordinates.
		
		widths = new int[] {
			W_MAJOR,
			W_TILE, W_MINOR, W_TILE, W_MINOR, W_TILE,
			W_MAJOR,
			W_TILE, W_MINOR, W_TILE, W_MINOR, W_TILE,
			W_MAJOR, 
			W_TILE, W_MINOR, W_TILE, W_MINOR, W_TILE,
			W_MAJOR
		};
		heights = widths; // (Same numbers, so be lazy)		
		
		boardWidth = 0; for (int width: widths) boardWidth += width;
		boardHeight = 0; for (int height: heights) boardHeight += height;
	}

	public static void main(String... args) {
		JFrame mainframe = new JFrame("Sudoku board");
		mainframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		mainframe.add(new SudokuBoard());
		
		mainframe.setSize(640, 480);
		mainframe.setVisible(true);
	}
	
	
	/*
	Okay, the general strategy is this. Divide the game board into the
	subcomponents with distinct layouts. Record the bounds of the
	subcomponents in whatever data structure you'd like.
	
	Then. For any mouse event, first find which subcomponent it is in.
	Then get the mouse event coordinates relative to the subcomponent.
	
	Then you can continue handling the mouse event, now with relative
	components and the subcomponent's distinct layout in mind.
	
	An alternative, costly method is to forgo subcomponents, just
	record the bounds of every component. Then check if the coordinates
	are within any of them. If the game area is not the whole window, 
	get the mouse event coordinates relative to the game area.
	
	For this sudoku board case, what I plan to do is have get relative to
	the sudoku board first. Then - note that the board has 
	a thick line, a tile, a thin line, a tile, so on.. 
	From the top-left of the sudoku board downwards then rightwards,
	I'll record those "things we should be crossing" into an array.
	Then, umm.. see the code.	
	*/
	
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());

		// So. Fill the board area first.		
		int xOffset = (getWidth() - boardWidth) / 2;
		int yOffset = (getHeight() - boardHeight) / 2;
		g.setColor(getBackground().darker());
		g.fillRect(xOffset, yOffset, boardWidth, boardHeight);
		
		// Draw lines. We'll just iterate through widths and assume
		// every odd one is meant to be a vertical line. Same for heights.
		g.setColor(getForeground());
		for (int o = 0, x = xOffset; o < widths.length; ++o) {
			if ((o % 2) == 0) {
				g.fillRect(x, yOffset, widths[o], boardHeight);
			}
			x += widths[o];
		}
		for (int o = 0, y = yOffset; o < heights.length; ++o) {			
			if ((o % 2) == 0) {
				g.fillRect(xOffset, y, boardWidth, heights[o]);
			}
			y += heights[o];
		}
	}
	
	
	public void mouseClicked(MouseEvent eM) {
		
	}
	
	public void mouseExited(MouseEvent eM) { }
	public void mouseEntered(MouseEvent eM) { }
	public void mousePressed(MouseEvent eM) { }
	public void mouseReleased(MouseEvent eM) { }

}
