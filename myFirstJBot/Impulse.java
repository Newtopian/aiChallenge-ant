import java.util.Collections;
import java.util.List;

public class Impulse implements Comparable<Impulse> {
	@Override
	public int compareTo(Impulse o) {
		if (o == null)
			return 1;
		return Double.valueOf(o.getCompoundImpulse() - this.getCompoundImpulse()).intValue();
	}

	private Tile desire;
	private double compoundImpulse;
	private List<Aim> towards;
	private ImpulseTypes impulseType;


	public Impulse(ImpulseTypes impulseType, Tile desire, List<Aim> list, double instinct) {
		super();
		this.desire = desire;
		this.towards = list;
		Collections.shuffle(this.towards);
		this.impulseType = impulseType;
		this.compoundImpulse = instinct;
	}
	public ImpulseTypes getImpulseType() {
		return impulseType;
	}

	public Aim getTowards() {
		Aim dir = null;
		if (this.towards != null && !this.towards.isEmpty()) {
			dir = this.towards.get(0);
			this.towards.remove(0);
		}
		return dir;
	}

	public Tile getDesire() {
		return desire;
	}

	public double getCompoundImpulse() {
		return compoundImpulse;
	}

	public void setWill(double will){
		this.compoundImpulse *= will;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(impulseType);
		sb.append("@[");
		sb.append(this.desire);
		sb.append("] $:");
		sb.append(this.compoundImpulse);
		sb.append(" t:");
		sb.append(this.towards);
		return sb.toString();
	}
	
}
