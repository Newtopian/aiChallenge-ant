import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Starter bot implementation.
 */
public class MyBot extends Bot {
	private static boolean enableLog = false;
	protected Overlord overlord;
	private int turnNumber = 1;
	/**
	 * Main method executed by the game engine for starting the bot.
	 * 
	 * @param args
	 *            command line arguments
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static void main(String[] args) throws IOException {
		new MyBot().readSystemInput();
	}

	
	@Override
	public void doTurn() {
		System.err.print("-----------------------TURN#" + this.turnNumber++);
		HashMap<Tile, Tile> orders = new HashMap<Tile, Tile>();

		List<SingleAnt> myAnts = this.overlord.assessSituation();
		
		//issue orders to all ants
		for (SingleAnt a : myAnts) {
			a.move(getAnts(), orders);
		}
		log(overlord.toString());
	}

	@Override
	public void setup(int loadTime, int turnTime, int rows, int cols,
			int turns, int viewRadius2, int attackRadius2, int spawnRadius2) {
		super.setup(loadTime, turnTime, rows, cols, turns, viewRadius2, attackRadius2,
				spawnRadius2);
		this.overlord = new Overlord(getAnts());
	}
	
	public static void log(String log){
		if(enableLog){
			System.err.print(log);
		}
	}
}
