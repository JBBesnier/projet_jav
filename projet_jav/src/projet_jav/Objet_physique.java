package projet_jav;


public class Objet_physique extends Zones_dangereuses {
	
	protected int Rayon;
	
	public Objet_physique (int id , int L, int l) {
		super(id,L,l);
		this.Rayon = random.nextInt(60);
	}
	
	public Objet_physique (int id , int Rayon , Vect Position) {
		super(id , Position);
		this.Rayon = Rayon;
	}

}
