package flappyBird;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class Window implements MouseListener, KeyListener {

	// Copy of the gameLoop instance
	private GameLoop gameLoop;

	protected JFrame createWindow(JFrame jframe, GameLoop gameLoop) {
		this.gameLoop = gameLoop;

		// Should be self explanatory
		jframe.add(gameLoop);
		jframe.setTitle("Flappy Bird");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(Preferences.WIDTH, Preferences.HEIGHT);
		jframe.addMouseListener(this);
		jframe.addKeyListener(this);
		jframe.setResizable(false);
		jframe.setVisible(true);

		return jframe;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.gameLoop.jump();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			this.gameLoop.jump();
		if (e.getKeyCode() == KeyEvent.VK_Q)
			System.exit(0);
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	// (c) 2016 Joshua Sonnet
}
