package flappyBird;

import java.util.Random;

// You should not be allowed to create an instance of this class (-> abstract)
public abstract class Preferences {

	// Frame settings
	protected static final int WIDTH = 800, HEIGHT = 800;

	/* Video settings */
	// Game settings
	protected static final double TARGET_FPS = 60, GAME_HERTZ = 30.0;

	// If we are able to get as high as this FPS, don't render again
	protected static final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / Preferences.TARGET_FPS;

	// Calculate how many ns each frame should take for our target game hertz
	protected static final double TIME_BETWEEN_UPDATES = 1000000000 / Preferences.GAME_HERTZ;

	// At the very most it will update the game this many times before a new render
	// If you're worried about visual hitches more than perfect timing, set this to 1
	protected static final int MAX_UPDATES_BEFORE_RENDER = 5;

	/* game mechanics */
	// The space between the top and bottom part of a column row
	protected static final int SPACE_COLUMNS = 275;

	/* Stuff n things */
	// Just a random
	protected static Random rand = new Random();

	// Bird size
	protected static final int birdSize = 34;

	// (c) 2016 Joshua Sonnet
}
