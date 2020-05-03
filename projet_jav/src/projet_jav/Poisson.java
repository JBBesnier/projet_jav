package projet_jav;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.PI;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.AffineTransform;
import java.util.Random;
import java.util.ArrayList;
/**Classe gérant les poissons ainsi que tout leurs comportements
 * @author Jean-Baptiste
 */
public class Poisson{
	// Outils
	Random random = new Random();
	
	public static double mod (double a, int b) {
		int i = (int) a;
        int res = i % b;
        if (res<0 && b>0) {
            res += b;
        }
        return res + a - i;
    }
	
	// Paramètres :
	protected int id;
	protected Vect Position;
	protected Vect Vitesse;
	protected int Temps_repos = 0;
	protected static int Vitesse_repro;
	protected static final int size = 3;
    protected static final Path2D shape = new Path2D.Double();
	
	public Poisson(int id, int L, int l) {
		// Constructeur du poisson
		this.id = id;
		this.Position = new Vect(random.nextInt(L),random.nextInt(l));
		double x = 2*random.nextDouble()-1;
		this.Vitesse = new Vect(x,sqrt(1-pow(x,2)));
	}
	
	public Poisson(int id , Poisson poisson) {
		// Constructeur du poisson fils issu de reproduction()
		/* le poisson fils est décalé du poisson parent,
		 * vu qu'il n'y avait pas d'aléatoire dans la direction des poissons,
		 * ils avaient le même comportement et restaient superposés
		 */
		this.id = id;
		double x = poisson.Position.x;
		double y = poisson.Position.y;
		this.Position = new Vect(x+3,y+3);
		x = poisson.Vitesse.x;
		y = poisson.Vitesse.y;
		this.Vitesse = new Vect(x,y);
		this.Temps_repos = 0;
	}
	
	static { // Création de la forme du poisson
        shape.moveTo(0, -size * 2);
        shape.lineTo(-size, size * 2);
        shape.lineTo(size, size * 2);
        shape.closePath();
    }
	
	//Les méthodes aligner() eloigner() et cohesion() sont 3 comportements théorisés par Craig Reynolds en 1986
	
	/** Méthode permettant la tendance d'un poisson à s'aligner avec ses voisins
	 * @param voisins
	 * @return Vecteur de la direction moyenne de ses voisins
	 */
	public Vect aligner(ArrayList<Poisson> voisins) {
		Vect moyenne = new Vect(0,0);
		for(int i = 0 ; i < voisins.size()  ; i++) {
			moyenne.add(voisins.get(i).Vitesse); 
		}
		moyenne.div(voisins.size());
		moyenne.limit(0.08);
		return moyenne;
	}
	
	/** Méthode permettant la tendance d'un poisson à s'éloigner de ses voisins
	 * @param voisins
	 * @return Vecteur suivant la droite centre-poisson/barycentre des voisins trop proches visant à éloigner le poisson de ses congénères 
	 */
	public Vect eloigner(ArrayList<Poisson> voisins) {
		double trop_proche = 49; // Distance de "confort" des poissons 
		ArrayList<Poisson> collants = new ArrayList<Poisson>();
		for(Poisson autre : voisins) {
			double d = Vect.dist(this.Position,autre.Position);
			if (autre != this && d <= trop_proche) {
				collants.add(autre);
				}
		}
		Vect moyenne = new Vect(0,0);
		if (collants.size() != 0) {
			for(int i = 0 ; i < collants.size()  ; i++) {
				double distance = Vect.dist(this.Position,collants.get(i).Position);
				Vect diff = new Vect(0,0);
				diff = Vect.sub(this.Position, collants.get(i).Position);
				diff.div(distance);
				moyenne.add(diff);
			}
			moyenne.div(collants.size());
			moyenne.limit(0.08);
		}
		return moyenne;
	}
	
	/** Méthode permettant la tendance d'un poisson à se rapprocher du barycentre du banc
	 * @param voisins
	 * @return Vecteur pointant vers le barycentre des poissons voisins
	 */
	public Vect cohesion(ArrayList<Poisson> voisins) {
		double taille_groupe = 50;
		ArrayList<Poisson> groupe = new ArrayList<Poisson>();
		for(Poisson autre : voisins) {
			double d = Vect.dist(this.Position,autre.Position);
			if (autre != this && d <= taille_groupe) {
				groupe.add(autre);
				}
		}
		Vect moyenne = new Vect(0,0);
		for(int i = 0 ; i < voisins.size()  ; i++) {
			moyenne.add(voisins.get(i).Position); 
		}
		moyenne.div(voisins.size());
		moyenne.sub(this.Position);
		moyenne.limit(0.08);
		return moyenne;
	}
	
	/** Méthode empêchant la colision entre entités
	 * Moyenne des éléments de la liste des éléments dangeureux
	 * @param voisins
	 * @return moyenne des vecteurs de la liste
	 */
	public Vect eviter(Zones_dangereuses[] dangereux) {
		double perception;
		ArrayList<Zones_dangereuses> danger = new ArrayList<Zones_dangereuses>();
		for(Zones_dangereuses zone : dangereux) {
			if (zone instanceof Objet_physique) {
				perception = ((Objet_physique) zone).Rayon + 30;
			}else {perception=50;}
					double d = Vect.dist(this.Position,zone.Position);
					if (d <= perception) {
						danger.add(zone);			
			}
		}
		Vect moyenne = new Vect(0,0);
		if (danger.size() != 0) {
			for(int i = 0 ; i < danger.size()  ; i++) {
				Vect diff = new Vect(0,0);
				diff = Vect.sub(this.Position, danger.get(i).Position);
				moyenne.add(diff);
			}
			moyenne.div(danger.size());
			moyenne.limit(0.3);
		}
		return moyenne;
	}
	
	/** Méthode permettant la mise à jour des comportements primaires des poissons 
	 * à chaque itération
	 * @param poissons
	 * @param dangereux
	 */
	public void update(Poisson[] poissons , Zones_dangereuses[] dangereux) {
		double perception = 100;
		Vect modif = new Vect (0,0);
		ArrayList<Poisson> voisins = new ArrayList<Poisson>();
		
		for(Poisson autre : poissons) {
			double d = Vect.dist(this.Position,autre.Position);
			if (autre != this && d <= perception) {
				voisins.add(autre);
				}
			}
		
		if(voisins.size() != 0) {
			modif.add(this.aligner(voisins));
			modif.add(this.eloigner(voisins));
			modif.add(this.cohesion(voisins));
			this.Vitesse.add(modif);
			this.Vitesse.normalisation();
		}
		
		this.Vitesse.add(this.eviter(dangereux));
		this.Vitesse.normalisation();
		
		this.Position.add(this.Vitesse);
	}
	
	/** Méthode permettant la mise à jour de la liste des poissons 
	 * Si un poisson se retrouve trop prêt d'un poisson : il est considéré comme mangé
	 * et disparait de la simulation
	 * @param poissons
	 * @param predateurs
	 * @return Liste des poissons restant
	 */
	public static Poisson[] manger (Poisson[] poissons, ArrayList<Predateurs> predateurs ) {
		ArrayList<Poisson> survivants = new ArrayList<Poisson>();
		for (int i=0 ; i < poissons.length ; i++) {
			boolean manger = false;
			for (int j=0 ; j < predateurs.size(); j++) {
				double distance = Vect.dist(poissons[i].Position,predateurs.get(j).Position);
				if (distance < Predateurs.size) {
					manger = true;
					break;
				}
			}
			if (!manger) {
				survivants.add(poissons[i]);
			}
		}
		Poisson[] restants = new Poisson[survivants.size()];
		for (int i=0 ; i < survivants.size() ; i++) {
			restants[i] = survivants.get(i);
		}
		return restants;
	}
	
	/**Méthode permettant la multiplication des poissons
	 * Si un groupe de poisson conséquent se retrouve pendant un certain nombre d'itération
	 * sans prédateurs aux alentours, ils se multiplient
	 * @param poissons
	 * @param predateurs
	 * @return Liste des poissons avec potentiellement des poissons fils
	 */
	public static Poisson[] reproduction (Poisson[] poissons , ArrayList<Predateurs> predateurs) {
		ArrayList<Poisson> nouveau = new ArrayList<Poisson>();
		for (Poisson poisson : poissons) {
			nouveau.add(poisson);
			boolean danger = false;
			for (int i=0 ; i < predateurs.size(); i++) {
				double distance = Vect.dist(poisson.Position,predateurs.get(i).Position);
				if (distance < 100) {
					danger = true;
					break;
				}
			}
			if (!danger) {
				int nb_voisins = 0;
				for (Poisson autre : poissons) {
					double distance = Vect.dist(poisson.Position,autre.Position);
					if (autre != poisson && distance < 50) {
						nb_voisins += 1;
					}
				}
				if (nb_voisins > (1/5)*poissons.length) {
					poisson.Temps_repos += 1;
				} else {
					poisson.Temps_repos = 0;
				}
			} else {
				poisson.Temps_repos = 0;
			}
			
			if (poisson.Temps_repos == Vitesse_repro) {
				nouveau.add(new Poisson(poissons.length + nouveau.size(), poisson)); 
				poisson.Temps_repos = 0;
			}
		}
		
		Poisson[] resultat = new Poisson[nouveau.size()];
		for (int i=0 ; i < nouveau.size() ; i++) {
			resultat[i] = nouveau.get(i);
		}
		return resultat;
	}
	
	/** Méthode gérant l'apparition d'un poisson
	 * On s'assure à chaque fois que le poisson n'apparait pas
	 * dans un objet physique
	 * @param zones
	 * @param i
	 * @param L
	 * @param l
	 * @return Objet poisson avec des coordonnées aléatoires dans la fenêtre
	 */
	public static Poisson apparition(Zones_dangereuses[] zones , int i , int L , int l) {
		boolean etape = true;
		Poisson poisson = new Poisson(i,L,l);
		while(etape) {
			poisson = new Poisson(i,L,l);
			etape = false;
			for(int j=0 ; j < zones.length ; j++) {
				if (zones[j] instanceof Objet_physique) {
					double d = Vect.dist(poisson.Position,zones[j].Position);
					if (d < ((Objet_physique)zones[j]).Rayon) {
						etape = true;
						break;
					}
				}
				else {
					double d = Vect.dist(poisson.Position,zones[j].Position);
					if (d < Predateurs.size) {
						etape = true;
						break;
					}
				}
			}
		}
		return poisson;
	}
	
	/** Méthode permettant la création de tout les poissons
	 * @param nb
	 * @param zones
	 * @param L
	 * @param l
	 * @return Liste de Poissons
	 */
	public static Poisson[] creation_poisson(int nb , Zones_dangereuses[] zones , int L , int l) {
		Poisson[] poissons = new Poisson[nb];
		for(int i=0 ; i < nb ; i++) {
			poissons[i] = Poisson.apparition(zones,i,L,l);
		}
		return poissons;
	}
	
	/** Méthode permettant l'affichage des poissons
	 * @param g
	 */
	 public void draw(Graphics2D g) {
	        AffineTransform save = g.getTransform();
	        g.translate(this.Position.x, this.Position.y);
	        g.rotate(this.Vitesse.heading() + PI / 2);
	        g.setColor(Color.white);
	        g.fill(shape);
	        g.setColor(Color.black);
	        g.draw(shape);
	        g.setTransform(save);
	    }
	 
	/** Méthode principale de la classe poisson
	 * On fait 'vivre' les poissons en les dessinant et en appelant update()
	 * @param poissons
	 * @param zones
	 * @param g
	 * @param L
	 * @param l
	 */
	public static void run(Poisson[] poissons , Zones_dangereuses[] zones , Graphics2D g , int L , int l) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		for (int i =0 ; i < poissons.length ; i++) {
		poissons[i].draw(g);
		poissons[i].update(poissons , zones);
		poissons[i].Position = new Vect (mod(poissons[i].Position.toArray()[0],L) , mod(poissons[i].Position.toArray()[1],l));
		}
		for (int j=0 ; j < zones.length ; j++) {
			if (zones[j] instanceof Predateurs) {
				((Predateurs) zones[j]).deplacement(zones , poissons);
				zones[j].Position = new Vect (mod(zones[j].Position.toArray()[0],L) , mod(zones[j].Position.toArray()[1],l));
			}
		}
	}

	public static void main(String[] args) {
		new Fenetre(800,800);
	}
}