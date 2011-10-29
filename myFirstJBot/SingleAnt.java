import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

public class SingleAnt {
	private Tile ant;
	private List<Impulse> impulses = new ArrayList<Impulse>();
	
	public SingleAnt(Tile ant) {
		MyBot.log("Creating Ant " + ant);
		this.ant = ant;
	}
	
	public Tile getLocation() {
		return ant;
	}

	public void addImpulse(Impulse impulse, int distance){
		this.impulses.add(impulse);
		this.modulate(impulse, distance);
	}
	
	protected void modulate(Impulse impulse, int distance){
		//basic ants will gather food above all
		switch(impulse.getImpulseType()){
		case DISCOVERY:
			//the more distant the more interesting
			impulse.setWill(50);
			break;
		case HUNGER:
			impulse.setWill(100);
			break;
		}
	}

	public void move(Ants ants, HashMap<Tile, Tile> orders) {
		StringBuilder sb = new StringBuilder();
		sb.append("moving ant (");
		sb.append(this.ant);
		sb.append(")");
		Aim aim = null;
		if (!this.impulses.isEmpty()) {
			sb.append(") withImpulse -");
			Collections.sort(this.impulses);
			while (!this.impulses.isEmpty() && aim == null) {
				Impulse imp = this.impulses.get(0);
				sb.append(imp);
				Aim desiredDirection = imp.getTowards();
				if (desiredDirection != null) {
					Tile destination = ants.getTile(this.ant, desiredDirection);
					if (ants.getIlk(destination).isUnoccupied()
							&& !orders.containsKey(destination)) {
						ants.issueOrder(this.ant, desiredDirection);
						orders.put(destination, this.ant);
						aim = desiredDirection;
					} else {
						this.impulses.remove(0);
					}
				}
			}
		}

		if (aim == null) {
			sb.append(" - randomly -");
			List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
			Collections.shuffle(directions);
			for (Aim direction : directions) {
				Tile destination = ants.getTile(this.ant, direction);
				if (ants.getIlk(destination).isUnoccupied()
						&& !orders.containsKey(destination)) {
					ants.issueOrder(this.ant, direction);
					orders.put(destination, this.ant);
					aim = direction;
					break;
				}
			}
		}
		sb.append("->");
		sb.append(aim);
		MyBot.log(sb.toString());
	}

	@Override
	public String toString() {
		return "ant@" + this.ant.toString();
	}
}
