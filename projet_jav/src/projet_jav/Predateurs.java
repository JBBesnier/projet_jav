package projet_jav;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.ArrayList;

/**Classe hérité de Zones_dangeureuses
 * permettant la création des prédateurs
 * @author Jean-Baptiste
 */
public class Predateurs extends Zones_dangereuses {
	
	// Paramètres :
	protected Vect Vitesse;
	protected static int size=5;
	protected static double vitesse_predateurs;
	
	static { // Création de la forme du prédateur
        shape.moveTo(0, -size * 2);
        shape.lineTo(-size, size * 2);
        shape.lineTo(size, size * 2);
        shape.closePath();
    }
	
	public Predateurs(int id , int L , int l) {
		// Constructeur
		super(id,L,l);
		double x = 2*random.nextDouble()-1;
		this.Vitesse = new Vect(x,sqrt(1-pow(x,2)));
	}
	
	/**Méthode permettant le déplacement des prédateurs
	 * On calcule la liste des voisins du prédateur contenant les autres prédateurs et les obstacles
	 * Pour ensuite savoir qui / quoi éviter
	 * @param zones
	 * @param poissons
	 */
	public void deplacement(Zones_dangereuses[] zones , Poisson[] poissons) {
		double perception;
		ArrayList<Zones_dangereuses> voisins = new ArrayList<Zones_dangereuses>();
		
		for(Zones_dangereuses autre : zones) {
			if (autre instanceof Objet_physique) { // on différencie les autres prédateurs des obstacles physiques
				perception = ((Objet_physique) autre).Rayon + 30;
			}else {perception=80;}
			double d = Vect.dist(this.Position,autre.Position);
			if (autre != this && d <= perception) { 
				voisins.add(autre); // On créé la liste des voisins de notre prédateur (lui non inclu)
				}
			}
		if(voisins.size() != 0) {
			this.Vitesse.add(eviter(voisins)); // On évite uniquement / on ne prend en compte que les voisins
			this.Vitesse.normalisation(); // Contrainte : vitesse constante
		}
		
		this.Vitesse.add(attaquer(poissons));
		this.Vitesse.normalisation();
		this.Vitesse.mult(vitesse_predateurs);
		
		this.Position.add(this.Vitesse);
	}
	
	/** Méthode empêchant la colision entre entités
	 * Moyenne des éléments de la liste voisins
	 * @param voisins
	 * @return moyenne des vecteurs de la liste des voisins
	 */
	public Vect eviter(ArrayList<Zones_dangereuses> voisins) {
		Vect moyenne = new Vect(0,0);
		if (voisins.size() != 0) {
			for(int i = 0 ; i < voisins.size()  ; i++) {
				Vect diff = new Vect(0,0);
				diff = Vect.sub(this.Position, voisins.get(i).Position);
				moyenne.add(diff);
			}
			moyenne.div(voisins.size());
			moyenne.limit(0.3);
		}
		return moyenne;
	}
	
	/**Méthode permettant a un prédateur d'attaquer
	 * Le prédateur va se diriger vers le barycentre du groupe des voisins qu'il voit
	 * @param poissons
	 * @return vecteur directeur du prédateur modifié par le barycentre des voisins
	 */
	public Vect attaquer(Poisson[] poissons) {
		double perception = 80;
		ArrayList<Poisson> nourriture = new ArrayList<Poisson>();
		
		for(Poisson autre : poissons) {
			double d = Vect.dist(this.Position,autre.Position);
			if (d <= perception) {
				nourriture.add(autre);
				}
			}
		Vect moyenne = new Vect(0,0);
		
		if(nourriture.size() != 0) {
			for(int i = 0 ; i < nourriture.size()  ; i++) {
				moyenne.add(nourriture.get(i).Position); 
			}
			moyenne.div(nourriture.size());
			moyenne.sub(this.Position);
			moyenne.limit(0.05);
		}
		return moyenne;
	}
	/** Méthode gérant l'apparition d'un prédateur
	 * On s'assure à chaque fois que le prédateur n'apparait pas
	 * dans un objet physique
	 * @param zones
	 * @param i
	 * @param L
	 * @param l
	 * @return objet prédateur avec des coordonnées aléatoires dans la fenêtre
	 */
	public static Predateurs apparition(Zones_dangereuses[] zones, int i , int L , int l) {
		boolean etape = true;
		Predateurs predateur = new Predateurs(i,L,l);
		while(etape) {
			predateur = new Predateurs(i,L,l);
			etape = false;
			for(int j=0 ; j < zones.length ; j++) {
				if (zones[j] instanceof Objet_physique) {
					double d = Vect.dist(predateur.Position,zones[j].Position);
					if (d < ((Objet_physique)zones[j]).Rayon) {
						etape = true;
						break;
					}
				}
			}
		}
		return predateur;
	}
}
