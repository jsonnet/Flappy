package flappyBird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameLoop extends JPanel {

	// private static final long serialVersionUID = 1L;

	/* RENDER */
	private boolean running = false;
	private boolean paused = false;
	private int actualFps = (int) Preferences.TARGET_FPS;
	// Current FPS Count
	private int frameCount = 0;
	// Delta of gameLoop and fps difference
	private float interpolation;
	private int lastDrawX, lastDrawY;

	/* PHYSICS */
	// Variables of the game and physics
	private int ticks, yMotion, score;
	private boolean gameOver, started;

	/* OBJECTS */
	// Copysave objects
	private JFrame jframe;
	private GameObjects gameObjects;

	public GameLoop(JFrame jframe, GameObjects gameObjects) {
		this.jframe = jframe;
		this.gameObjects = gameObjects;

		// Start the main game
		this.running = true;
		this.runGameLoop();
	}

	// Starts a new thread and runs the game loop in it
	private void runGameLoop() {
		Thread loop = new Thread() {
			@Override
			public void run() {
				GameLoop.this.gameLoop();
			}
		};
		loop.start();
	}

	// Only run this in another Thread!
	private void gameLoop() {
		// We will need the last update time
		double lastUpdateTime = System.nanoTime();
		// Store the last time we rendered
		double lastRenderTime = System.nanoTime();

		// Simple way of finding FPS
		int lastSecondTime = (int) (lastUpdateTime / 1000000000);

		while (this.running) {
			double now = System.nanoTime();
			int updateCount = 0;

			if (!this.paused) {
				// Do as many game updates as we need to, potentially playing catchup
				while (now - lastUpdateTime > Preferences.TIME_BETWEEN_UPDATES && updateCount < Preferences.MAX_UPDATES_BEFORE_RENDER) {
					this.updateGame();
					lastUpdateTime += Preferences.TIME_BETWEEN_UPDATES;
					updateCount++;
				}

				// If for some reason an update takes forever, we don't want to do an insane number of catchups
				if (now - lastUpdateTime > Preferences.TIME_BETWEEN_UPDATES) lastUpdateTime = now - Preferences.TIME_BETWEEN_UPDATES;

				// Render. To do so, we need to calculate interpolation for a smooth render
				float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / Preferences.TIME_BETWEEN_UPDATES));
				this.drawGame(interpolation);
				lastRenderTime = now;

				// Update the frames we got
				int thisSecond = (int) (lastUpdateTime / 1000000000);
				if (thisSecond > lastSecondTime) {
					// LogHelper.log("NEW SECOND " + thisSecond + " " + this.frameCount);
					this.actualFps = this.frameCount;
					this.frameCount = 0;
					lastSecondTime = thisSecond;
				}

				// Yield until it has been at least the target time between renders. This saves the CPU from hogging
				while (now - lastRenderTime < Preferences.TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < Preferences.TIME_BETWEEN_UPDATES) {
					Thread.yield();

					// This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it
					// You can remove this line and it will still work (better), your CPU just climbs on certain OSes
					// FYI on some OS's this can cause pretty bad stuttering
					try {
						Thread.sleep(1);
					} catch (Exception e) {
					}

					now = System.nanoTime();
				}
			}
		}
	}

	// Game ticks with physics
	private void updateGame() {
		// Don't even think about changing that!
		final int runningSpeed = 10;

		this.ticks++;

		if (this.started) {
			// Movement of the game (aka Movement of the columns)
			for (int i = 0; i < this.gameObjects.columns.size(); i++) {
				Rectangle column = this.gameObjects.columns.get(i);

				int helper = runningSpeed;
				// Should result in smoother movement of the columns
				while (helper > 0) {
					column.x -= 2;
					helper -= 2;
					this.jframe.repaint();
				}
			}

			// The bird obviously needs to fall down after a jump
			if (this.ticks % 2 == 0 && this.yMotion < 15) this.yMotion += 2;

			// Remove column after being out of vision (for performance and memory reason; no one needs them anymore)
			for (int i = 0; i < this.gameObjects.columns.size(); i++) {
				Rectangle column = this.gameObjects.columns.get(i);

				// When column lefts the screen to the left
				if (column.x + column.width < 0) {
					this.gameObjects.columns.remove(column);

					if (column.y == 0) this.gameObjects.addColumn(false);
				}
			}

			// Constant falling of the bird
			this.gameObjects.bird.y += this.yMotion;

			// For each column check
			for (Rectangle column : this.gameObjects.columns) {
				// Add to score when passing the column
				if (column.y == 0 && this.gameObjects.bird.x + this.gameObjects.bird.width / 2 > column.x + column.width / 2 - 5 && this.gameObjects.bird.x + this.gameObjects.bird.width / 2 < column.x + column.width / 2 + 5) this.score++;

				// Does the bird hit the column
				if (column.intersects(this.gameObjects.bird)) {
					this.gameOver = true;

					/* PHYSICS */
					// Checks for the bird to not fly through instead collide with it
					if (this.gameObjects.bird.x <= column.x)
						this.gameObjects.bird.x = column.x - this.gameObjects.bird.width;
					else if (column.y != 0)
						this.gameObjects.bird.y = column.y - this.gameObjects.bird.height;
					else if (this.gameObjects.bird.y < column.height) this.gameObjects.bird.y = column.height;
				}
			}
			// If too high or to low (aka hit ground)
			if (this.gameObjects.bird.y > Preferences.HEIGHT - 120 - Preferences.birdSize || this.gameObjects.bird.y < 0) this.gameOver = true;

			// Slide on ground and don't go deeper
			if (this.gameObjects.bird.y + this.yMotion >= Preferences.HEIGHT - 130) {
				this.gameObjects.bird.y = Preferences.HEIGHT - 120 - this.gameObjects.bird.height;
				this.gameOver = true;
			}
		}
		this.jframe.repaint();
	}

	private void setInterpolation(float interp) {
		this.interpolation = interp;
	}

	private void drawGame(float interpolation) {
		this.setInterpolation(interpolation);
		this.jframe.repaint();
	}

	protected void jump() {
		if (this.gameOver) {
			// Reset position after game over on jump
			this.gameObjects.bird = new Rectangle(Preferences.WIDTH / 2 - 10, Preferences.HEIGHT / 2 - 10, Preferences.birdSize, Preferences.birdSize);
			this.gameObjects.columns.clear();
			this.yMotion = 0;
			this.score = 0;
			this.lastDrawX = this.lastDrawY = 0;

			// Render columns again (-> new game start)
			this.gameObjects.addColumn(true);
			this.gameObjects.addColumn(true);
			this.gameObjects.addColumn(true);
			this.gameObjects.addColumn(true);

			this.gameOver = false;
		}

		// Actual jump
		if (!this.started)
			this.started = true;
		else if (!this.gameOver) {
			if (this.yMotion > 0) this.yMotion = 0;
			this.yMotion -= 10;
		}
	}

	private void render(Graphics g) {
		// Make and color the background
		g.setColor(Color.cyan);
		g.fillRect(0, 0, Preferences.WIDTH, Preferences.HEIGHT);

		// Make and color the ground
		g.setColor(Color.orange);
		g.fillRect(0, Preferences.HEIGHT - 120, Preferences.WIDTH, 120);

		// Make and color the grass on top
		g.setColor(Color.green);
		g.fillRect(0, Preferences.HEIGHT - 120, Preferences.WIDTH, 20);

		// Handle the bird
		g.setColor(Color.red);

		// Handle interpolation
		int drawX = (int) ((this.gameObjects.bird.x - this.lastDrawX) * this.interpolation + this.gameObjects.bird.x);
		int drawY = (int) ((this.gameObjects.bird.y - this.lastDrawY) * this.interpolation + this.gameObjects.bird.y);

		try {
			// Set the bird texture
			Image img = ImageIO.read(this.getClass().getResource("/flappy.png"));
			g.drawImage(img, drawX, drawY, this.gameObjects.bird.width, this.gameObjects.bird.height, null);
		} catch (IOException e) {
			// If texture couldn't be loaded fall back to making a red rectangle
			g.fillRect(drawX, drawY, this.gameObjects.bird.width, this.gameObjects.bird.height);
		}

		// Interpolation variable for calculation
		this.lastDrawX = drawX;
		this.lastDrawY = drawY;

		// Color each column
		for (int i = 0; i < this.gameObjects.columns.size(); i++)
			this.gameObjects.paintColumn(g, this.gameObjects.columns.get(i));

		// Font handling
		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, 100));

		// Start screen
		if (!this.started) g.drawString("Click to start!", 75, Preferences.HEIGHT / 2 - 50);

		// Game over
		if (this.gameOver) g.drawString("Game Over!", 100, Preferences.HEIGHT / 2 - 50);

		// Score
		if (!this.gameOver && this.started) g.drawString(String.valueOf(this.score), Preferences.WIDTH / 2 - 25, 100);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.render(g);

		// Render some text to the screen
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", 1, 10));
		g.drawString("FPS: " + this.actualFps, 5, 10);
		g.drawString("© 2016 Joshua Sonnet", Preferences.HEIGHT - 21 - 100, Preferences.WIDTH - 35);

		// Count fps
		this.frameCount++;
	}

	// FIXME performance issues
	// TODO ? add new class for renderer
	// TODO ? interpolation for game movement (aka columns x pos)
	// TODO menu and configs (soon)

	// (c) 2016 Joshua Sonnet
}
