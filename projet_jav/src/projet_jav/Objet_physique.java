package projet_jav;

/**Classe hérité de Zones_dangeureuses
 * permettant la création d'obstacles shériques dans le milieu
 * @author Jean-Baptiste
 */

public class Objet_physique extends Zones_dangereuses {
	
	// Paramètres :
	protected int Rayon;
	protected static int R_max;
	public static int R_min;
	
	public Objet_physique (int id , int L, int l) {
		super(id,L,l);
				this.Rayon = random.nextInt(R_max - R_min) + R_min;
	}
	
	public Objet_physique (int id , int Rayon , Vect Position) {
		super(id , Position);
		this.Rayon = Rayon;
	}
}