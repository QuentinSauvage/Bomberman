package view;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The common informations to all Views
 * @author quentin sauvage
 */
public abstract class AbstractView {
	protected JFrame window;
	protected JPanel content;
	private Rectangle screenDimension = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	
	public AbstractView(String title, int width, int height) {
		window = new JFrame(title);
		window.setSize(new Dimension(width, height));
		window.setResizable(false);
		window.setVisible(false);
	}
	
	/**
	 * Resize the window and centers it.
	 * @param w the new width.
	 * @param h the new height.
	 */
	public void resize(int w, int h) {
		window.setSize(w, h);
		window.setLocation((int) (screenDimension.getWidth() - w) / 2, (int) (screenDimension.getHeight() - h) / 2);
	}
	
	/**
	 * Makes the window visible (or not visible).
	 */
	public void display() {
		window.setVisible(!window.isVisible());
	}
}
