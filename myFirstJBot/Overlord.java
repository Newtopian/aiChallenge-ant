import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Overlord {

	private Topology[][] topologyOverlay;
	private int row;
	private int col;
	private int[][] scentTrails;
	private int viewRadius;
	private int longestDistance;
	private Ants ants;
	private int scentRadius = 0;
	private int saturationLimit = 255;

	private Set<Tile> activeFoodTiles = new HashSet<Tile>();
	private List<Tile> knownWorld = new ArrayList<Tile>();
	private Map<Tile, Overlord.Owner> knownHills = new  HashMap<Tile, Overlord.Owner>();
	
	private enum Owner{
		MINE("Q"),
		ENEMY("@");
		String value;
		Owner(String value){
			this.value = value;
		}
		@Override
		public String toString() {
			return value;
		}
		
	}
	
	private enum Topology {
		UNKOWN("-"), LAND("."), WATER("#");
		String topo;

		Topology(String topo) {
			this.topo = topo;
		}

		public static Topology fromIlk(Ilk ilk) {
			if (ilk == null)
				return UNKOWN;
			switch (ilk) {
			case DEAD:
			case ENEMY_ANT:
			case FOOD:
			case LAND:
			case MY_ANT:
				return LAND;
			case WATER:
				return WATER;
			}
			return UNKOWN;
		}

		@Override
		public String toString() {
			return this.topo;
		}

	};

	public Overlord(Ants ants) {
		this.row = ants.getRows();
		this.col = ants.getCols();
		this.longestDistance = (int) Math.round(Math
				.sqrt(row * row + col * col));
		this.viewRadius = (int) Math.round(Math.sqrt((double) ants
				.getViewRadius2()));
		this.topologyOverlay = new Topology[col][row];
		for (Topology[] rs : topologyOverlay) {
			Arrays.fill(rs, Topology.UNKOWN);
		}
		this.scentTrails = new int[col][row];
		for (int[] rs : scentTrails) {
			Arrays.fill(rs, 0);
		}
		this.ants = ants;
		this.scentRadius = this.viewRadius;
	}

	public List<SingleAnt> assessSituation() {
		List<SingleAnt> thisTurnsAnts = new ArrayList<SingleAnt>();
		
		//checkout hills
		this.surveyHills();
		
		// get the food first
		this.surveyFood();

		// now if we find food it is because it is no longer there
		Deque<Tile> remainToEvaluate = new LinkedList<Tile>(ants.getMyAnts());
		Set<Tile> evaluated = new HashSet<Tile>();

		while (remainToEvaluate.peek() != null) {
			Tile subject = remainToEvaluate.pop();
			thisTurnsAnts.add(new SingleAnt(subject));

			for (int r = -this.viewRadius; r <= this.viewRadius; r++) {
				for (int c = -this.viewRadius; c<= this.viewRadius; c++) {
					int effectiveCol = (subject.getCol() + c) % this.col;
					effectiveCol = effectiveCol < 0 ? effectiveCol += this.col : effectiveCol;
					int effectiveRow = (subject.getRow() + r) % this.row;
					effectiveRow = effectiveRow < 0 ? effectiveRow += this.row : effectiveRow;
					Tile around = new Tile(effectiveRow, effectiveCol);
					this.evaluateScent(around, subject);
					if (!evaluated.contains(around)) {
						this.surveyTile(around, subject);
						evaluated.add(around);
					}
				}
			}
		}

		this.imprintImpulses(thisTurnsAnts);

		return thisTurnsAnts;
	}

	private void imprintImpulses(List<SingleAnt> thisTurnsAnts) {
		StringBuilder sb = new StringBuilder();

		sb.append("Imprinting ants\n");
		for (SingleAnt singleAnt : thisTurnsAnts) {
			sb.append("  **------ ");
			sb.append(singleAnt);
			sb.append("\n");
			for (Tile food : this.activeFoodTiles) {
				Impulse impulse = new Impulse(ImpulseTypes.HUNGER, food,
						ants.getDirections(singleAnt.getLocation(), food),
						(1.0 - 1.0
								* ants.getDistance(singleAnt.getLocation(),
										food) / longestDistance));
				singleAnt.addImpulse(impulse,
						ants.getDistance(singleAnt.getLocation(), food));
				sb.append(" -");
				sb.append(impulse);
				sb.append("\n");
			}
			for (Tile tile : this.knownWorld) {
				if (this.scentTrails[tile.getCol()][tile.getRow()] <= 0) {
					double instinct = (1.0 - 1.0
							* ants.getDistance(singleAnt.getLocation(), tile)
							/ longestDistance);
					if (instinct > 0 && this.topologyOverlay[tile.getCol()][tile.getRow()] != Topology.WATER) {
						Impulse impulse = new Impulse(ImpulseTypes.DISCOVERY,
								tile, ants.getDirections(
										singleAnt.getLocation(), tile),
								instinct);
						singleAnt
								.addImpulse(
										impulse,
										ants.getDistance(
												singleAnt.getLocation(), tile));
						sb.append(" -");
						sb.append(impulse);
						sb.append("\n");
					}
				}
			}
		}

		MyBot.log(sb.toString());
	}

	private void surveyFood() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n--Surveying Food\n");
		for (Tile food : ants.getFoodTiles()) {
			if (!this.activeFoodTiles.contains(food)) {
				sb.append(" [A]");
				this.activeFoodTiles.add(food);
			} else {
				sb.append(" [-]");
			}
			sb.append(food);
			sb.append("\n");
		}
		MyBot.log(sb.toString());
	}

	private void surveyTile(Tile eval, Tile pivot) {
		if (this.topologyOverlay[eval.getCol()][eval.getRow()] == Topology.UNKOWN) {
			if(ants.isVisible(eval)){
				this.topologyOverlay[eval.getCol()][eval.getRow()] = Topology
						.fromIlk(ants.getIlk(eval));
				this.knownWorld.add(eval);
			}
		}
		if (this.activeFoodTiles.contains(eval)
				&& ants.getIlk(eval) != Ilk.FOOD) {
			this.activeFoodTiles.remove(eval);
		}
	}
	
	private void surveyHills(){
		for (Tile hill : ants.getMyHills()) {
			if(!this.knownHills.containsKey(hill)){
				this.knownHills.put(hill, Owner.MINE);
			}
		}
		for (Tile hill : ants.getEnemyHills()) {
			if(!this.knownHills.containsKey(hill)){
				this.knownHills.put(hill, Owner.ENEMY);
			}
		}
	}
	/*
	 * not ideal, only allows basic scent trail modelization.
	 * 
	 * the scent power should be an attribute of the ant itself.
	 * 
	 * this would allow some ants to act as trail setters that others could
	 * follow thereby reducing the need for pathfinding to only these ants
	 */
	private void evaluateScent(Tile eval, Tile pivot) {
		int distance = this.ants.getDistance(eval, pivot);
		if(distance > this.scentRadius) return;
		int value = (int) Math.round(((2.0*this.scentRadius - 2.0*distance)) + 1
				+ this.scentTrails[eval.getCol()][eval.getRow()]);
		this.scentTrails[eval.getCol()][eval.getRow()] = value >= this.saturationLimit ? this.saturationLimit
				: value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String format = "[%s%02X]";
		sb.append("-----------------------\n");
		sb.append("-----OVERLORD----------\n");
		sb.append(" ");
		sb.append(row);
		sb.append("x");
		sb.append(col);
		sb.append(" - ");
		sb.append(longestDistance);
		sb.append(" v: ");
		sb.append(viewRadius);
		sb.append("\n");

		String[][] mapStatus = new String[this.col][this.row];
		for (int c = 0; c < this.col; c++) {
			Arrays.fill(mapStatus[c], String.format(format, Topology.UNKOWN, 0));
		}

		for (Tile tile : this.knownWorld) {
			mapStatus[tile.getCol()][tile.getRow()] = String.format(format,
					this.topologyOverlay[tile.getCol()][tile.getRow()],
					this.scentTrails[tile.getCol()][tile.getRow()]);
		}
		for (Tile food : this.activeFoodTiles) {
			mapStatus[food.getCol()][food.getRow()] = String.format(format,
					"*",
					this.scentTrails[food.getCol()][food.getRow()]);
		}
		for (Tile hill : this.knownHills.keySet()) {
			mapStatus[hill.getCol()][hill.getRow()] = String.format(format,
					this.knownHills.get(hill),
					this.scentTrails[hill.getCol()][hill.getRow()]);
		}

		for (int j = 0; j < this.row; j++) {
			sb.append("\n");
			for (int i = 0; i < this.col; i++) {
				sb.append(mapStatus[i][j]);
			}
		}
		sb.append("\n");

		return sb.toString();
	}

}
