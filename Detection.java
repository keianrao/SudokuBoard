
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**

Demonstrates detecting which component a mouse is hovered over
despite the presence of variable gaps between components.

*/
class DetectionI extends JPanel implements MouseMotionListener {
	/*
	It occurs to me that I can implement each section of the game board as
	a Swing GUI component, then fit them together through layout managers.
	It'd certainly work for simple geometric boards like for congkak & sudoku.
	
	Anyhow, I am going to do this myself to understand the principles.
	*/

	private int[] widths;
	private int commonHeight;
	private int totalWidth;
	
	private int hovering;
	private boolean isHovering = false;
	
	
	DetectionI(int[] widths, int commonHeight) {
		this.widths = widths;
		this.commonHeight = commonHeight;
		
		int totalWidth = 0;
		for (int width: widths) totalWidth += width;
		this.totalWidth = totalWidth;
		
		this.addMouseMotionListener(this);
	}
	
	public static void main(String... args) {
		JFrame mainframe = new JFrame("Detection I");
		mainframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainframe.add(new DetectionI(
			new int[] {
				48, 48, 10, 64, 5, 10, 20, 40
			},
			64
		));	
		mainframe.setSize(640, 480);
		mainframe.setVisible(true);
	}
	
	
	protected void paintComponent(Graphics g) {		
		int xOffset = (getWidth() - totalWidth) / 2;
		int yOffset = (getHeight() - commonHeight) / 2;
		// [1]
				
		for (int o = 0, x = 0; o < widths.length; ++o) {
			g.setColor(
				(isHovering && o == hovering)
					? Color.BLUE
					: ((o % 2) == 0)
						? getBackground().darker()
						: getForeground()
			);
		
			int width = widths[o];
			g.fillRect(
				xOffset + x,
				yOffset,
				width,
				commonHeight
			);
			x += width;
		}
	}
	
	
	public void mouseMoved(MouseEvent eM) {
		// Okay, fun part.
		
		// Right now mouse event's coordinates are relative to
		// the top-left of this panel. We want the coordinates
		// relative to the actual components.
			
		// First, get offset of component area from top-left of this panel.
		// This offset is of the top-left of the component area.
		int xOffset = (getWidth() - totalWidth) / 2;
		int yOffset = (getHeight() - commonHeight) / 2;		
			// [1] Alternatively, we can have a resize listener that 
			// updated and saved these variables inside this class..
				
		// Subtract that out from mouse event's coordinates.
		// This gets us mouse event's coordinates relative to
		// the component area.
		int cursorX = eM.getX() - xOffset;
		int cursorY = eM.getY() - yOffset;
		
		// We know that each component has a bounding area. The cursor is
		// hovering over some component if the cursor's coordinates is
		// within that component's bounding area.
		
		// We laid our components out in a row, so, we'll iterate through
		// the bounding *widths* of each component. We'll go left-to-right,
		// they're kind of sorted that way.
		for (int o = 0, x = 0; o < widths.length; ++o) {
			int left = x;
			int right = left + widths[o];
			
			// X no longer needed, increment it now.
			x = right;
			
			boolean cursorXWithin = 
				left <= cursorX && cursorX <= right;
			boolean cursorYWithin =
				cursorY <= commonHeight;
			
			if (cursorXWithin && cursorYWithin) {
				isHovering = true;
				hovering = o;
				repaint();
				//Toolkit.getDefaultToolkit().sync();
				return;
			};
		}
		
		// Didn't return from loop. Not hovering.
		isHovering = false;
		repaint();
	}
	
	public void mouseDragged(MouseEvent eM) { }
	
}
