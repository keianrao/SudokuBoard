
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
	private int[] xElements;
	private int[] yElements;
	private int boardWidth;
	private int boardHeight;
	// Secret weapons that will help us later
	
	private boolean hasSelected = false;
	private int xSelected;
	private int ySelected;

	SudokuBoard() {
		this.addMouseListener(this);
	
		// Element widths we'll be dealing with.
		final int
			W_TILE = 40, // Tile width
			W_MAJOR = 4, // Major line width
			W_MINOR = 2; // Minor line width
		
		// Save the sequence of widths when going left to right.
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
		
		
		// So.. A sudoku board consists of tiles and lines - which is what I
		// mean by "elements". Each has an array offset representing them.
		// We saved the widths and heights of each above - now we can
		// calculate the X and Y for all of them, relative to (0,0) of
		// the board.
				
		// For simplicity's sake, and also to match Congkopi, we assume
		// the board is not going to be scaled larger or smaller.
		// Everything will have constant sizes. This is somewhat important
		// for mouse controls, we don't have to scale the coordinates.
				
		xElements = new int[widths.length];
		xElements[0] = 0;
		for (int o = 1; o < xElements.length; ++o) {
			xElements[o] = xElements[o - 1] + widths[o - 1];
		}
		yElements = new int[heights.length];
		yElements[0] = 0;
		for (int o = 1; o < yElements.length; ++o) {
			yElements[o] = yElements[o - 1] + heights[o - 1];
		}
		boardWidth = 
			xElements[xElements.length - 1] 
			+ widths[xElements.length - 1];
		boardHeight = 
			yElements[yElements.length - 1] 
			+ heights[yElements.length - 1];
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
		
		// And, fill the selected tile with a special colour.
		if (hasSelected) {
			g.setColor(getForeground().brighter());
			g.fillRect(
				xOffset + xElements[xSelected], yOffset + yElements[ySelected], 
				widths[xSelected], heights[ySelected]
			);
			// I would fire myself over this variable naming.
			// But really, it's quite hard to name things in this situation,
			// we're using a bunch of abstract quantities..
		}
		
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
		// First, get eM.xy relative to board.		
		int boardX = (getWidth() - boardWidth) / 2;
		int boardY = (getHeight() - boardHeight) / 2;
		int x = eM.getX() - boardX;
		int y = eM.getY() - boardY;
		
		if (x < 0 || y < 0 || x > boardWidth || y > boardHeight) {
			hasSelected = false;
			repaint();
			return;
		}
		
		// Now. Figure out which horizontal line / tile row we are in.
		// We saved the X/Y positions earlier, they're sorted ascendingly.
		// So let's iterate the offsets.
		
		// We'll leave this loop only if the selection is not a tile,
		// in which case we'll deselect.
		for (int oy = 0; oy < yElements.length - 1; ++oy) {
			if (y >= yElements[oy + 1]) continue;
			
			// If we stopped continuing and we didn't exit the loop,
			// our y is in between yElements[o] and yElements[o + 1].
			
			// Okay. Is this a horizontal line or a row of tiles?
			// If the former, deselect.
			if ((oy % 2) == 0) break;
			// (Alternatively, change '++oy' to 'oy += 2')
			
			// We're in a row of tiles.. so, let's check the column we're in.
			for (int ox = 0; ox < xElements.length - 1; ++ox) {
				if (x >= xElements[ox + 1]) continue;
				
				// And we stopped at a certain ox here too.
				// We've selected (ox, oy). But is this a tile?
				if ((ox % 2) == 0) break;
				
				// It is a tile.
				hasSelected = true;
				xSelected = ox;
				ySelected = oy;
				repaint();
				return;
			}
			
			// We weren't in any column for this row.
			// For this sudoku case, we assume all rows have the same
			// column count, so, we aren't in any column for any row.
			break;
		}
		
		// We've exited the loop without selecting anything. Deselect.
		hasSelected = false;
		repaint();
	}
	
	public void mouseExited(MouseEvent eM) { }
	public void mouseEntered(MouseEvent eM) { }
	public void mousePressed(MouseEvent eM) { }
	public void mouseReleased(MouseEvent eM) { }

}
