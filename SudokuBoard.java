
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
	
	private static final int COLUMNS = 9;
	private static final int ROWS = 9;
	int[][] board = new int[COLUMNS][ROWS];


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
		
		int verticalLines = COLUMNS + 1;
		int horizontalLines = ROWS + 1;
		int verticalLineWidth = 2;
		int horizontalLineWidth = 2;
		
		// For simplicity's sake, let's have constant tile sizes.
		// This means we won't scale the tiles up to fit the window.
		final int tileWidth = 40;
		final int tileHeight = tileWidth;
		
		// Given the tile widths, number of columns, and vertical line widths,
		// we can determine board width. Similarly for height.
		int boardWidth =
			(verticalLines * 2)
			+ (COLUMNS * tileWidth);
		int boardHeight =
			(horizontalLines * 2)
			+ (ROWS * tileHeight);
			
		// Okay. Fill the board area..
		int startX = (getWidth() - boardWidth) / 2;
		int startY = (getHeight() - boardHeight) / 2;
		g.setColor(getBackground().darker());
		g.fillRect(startX, startY, boardWidth, boardHeight);
		
		g.setColor(getForeground());
		// ..then draw the lines.
		// There needs to be tileWidth/tileHeight between every line.
		// We have x as always the topleft of the current line -
		// so we advance rightwards first by line width, then by tilewidth.
		int xAdv = verticalLineWidth + tileWidth;
		for (int x = 0; x <= boardWidth; x += xAdv) {
			g.fillRect(startX + x, startY, verticalLineWidth, boardHeight);
		}
		int yAdv = horizontalLineWidth + tileHeight;
		for (int y = 0; y <= boardHeight; y += yAdv) {
			g.fillRect(startX, startY + y, boardWidth, horizontalLineWidth);
		}
	}

}
