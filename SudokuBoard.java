
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class SudokuBoard extends JPanel {
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

	SudokuBoard() {
		
	}

	public static void main(String... args) {
		JFrame mainframe = new JFrame("Sudoku board");
		mainframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		mainframe.add(new SudokuBoard());
		
		mainframe.setSize(640, 480);
		mainframe.setVisible(true);
	}
	
	
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		
		// For simplicity's sake, and also to match Congkopi, let's
		// have constant tile sizes. This means we won't scale the tiles 
		// up to fit the window.. which also means we don't have to
		// scale mouse coordinates to logical coordinates.
		final int tileWidth = 40 + 1;
		final int tileHeight = tileWidth + 1;		
		// Okay, here's our strategy regarding the grid lines.
		// Game-logic-wise, we'll actually have no boundaries between lines.
		// So we draw the tiles first as a simple grid, and then we paint
		// grid lines on top of where they meet. Only for rendering.
		// Therefore, we increase the tile size a bit so that the
		// exposed area after painting the grid lines will come back to.
		
		// So. Fill the board area first.
		int boardWidth = tileWidth * 9;
		int boardHeight = tileHeight * 9;
		int startX = (getWidth() - boardWidth) / 2;
		int startY = (getHeight() - boardHeight) / 2;
		g.setColor(getBackground().darker());
		g.fillRect(startX, startY, boardWidth, boardHeight);
		
		// Later on, we should iterate across the board.
		// Selected tile, paint a different colour.
		// Otherwise, paint the filled number if present.
		
		g.setColor(getForeground());
		// Now we'll iterate over the equally-spaced tile boundaries.
		for (int column = 1; column <= 9 + 1; ++column) {
			int left = (column - 1) * tileWidth;
			
			boolean leftIsMajorLine = ( (column - 1) % 3 ) == 0;
			int lineWidth = leftIsMajorLine ? 4 : 2;
			
			// Okay, so. The line we're about to paint,
			// we'll horizontally center it on the boundary.
			int lineLeft = left - (lineWidth / 2);
			g.fillRect(startX + lineLeft, startY, lineWidth, boardHeight);
		}
		
		// Now do the same for rows.
		for (int row = 1; row <= 9 + 1; ++row) {
			int top = (row - 1) * tileHeight;
			boolean topIsMajorLine = ( (row - 1) % 3 ) == 0;
			int lineWidth = topIsMajorLine ? 4 : 2;			
			int lineTop = top - (lineWidth / 2);
			g.fillRect(startX, startY + lineTop, boardWidth, lineWidth);
		}
		// (We can actually combine the two loops, but let's
		// keep them separate for readability)
	}

}
