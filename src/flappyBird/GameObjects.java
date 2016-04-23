package flappyBird;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public class GameObjects {

	// Rendered Objects
	protected Rectangle bird;
	protected ArrayList<Rectangle> columns;

	protected GameObjects() {
		// Create bird as rectangle
		this.bird = new Rectangle(Preferences.WIDTH / 2 - 17, Preferences.HEIGHT / 2 - 17, Preferences.birdSize, Preferences.birdSize);
		// Save every column in an arrayList
		this.columns = new ArrayList<Rectangle>();

		// Render enough columns (maybe needs to change later!)
		this.addColumn(true);
		this.addColumn(true);
		this.addColumn(true);
		this.addColumn(true);

	}

	protected void addColumn(boolean start) {
		// Space between top and bottom column
		final int space = Preferences.SPACE_COLUMNS;
		// Thickness of each column
		final int width = 100;
		// Random y position for the gap
		final int height = 50 + Preferences.rand.nextInt(400);

		if (start) {
			// If start create initial column
			// Bottom part of the column
			this.columns.add(new Rectangle(Preferences.WIDTH + width + this.columns.size() * 300, Preferences.HEIGHT - height - 120, width, height));
			// Top part of the column
			this.columns.add(new Rectangle(Preferences.WIDTH + width + (this.columns.size() - 1) * 300, 0, width, Preferences.HEIGHT - height - space));
		} else {
			// Create column 600px moved from the previous
			// Bottom part of the column
			this.columns.add(new Rectangle(this.columns.get(this.columns.size() - 1).x + 600, Preferences.HEIGHT - height - 120, width, height));
			// Top part of the column
			this.columns.add(new Rectangle(this.columns.get(this.columns.size() - 1).x, 0, width, Preferences.HEIGHT - height - space));
		}
	}

	protected void paintColumn(Graphics g, Rectangle column) {
		// Color handed over column
		g.setColor(Color.green.darker().darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}

	// (c) 2016 Joshua Sonnet
}
