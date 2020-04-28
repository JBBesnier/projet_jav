package projet_jav;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.ArrayList;

public class Predateurs extends Zones_dangereuses {
	
	protected Vect Vitesse;
	protected static int size=5;
	
	static {
        shape.moveTo(0, -size * 2);
        shape.lineTo(-size, size * 2);
        shape.lineTo(size, size * 2);
        shape.closePath();
    }
	
	public Predateurs(int id , int L , int l) {
		super(id,L,l);
		double x = 2*random.nextDouble()-1;
		this.Vitesse = new Vect(x,sqrt(1-pow(x,2)));
	}
	
	public void deplacement(Zones_dangereuses[] zones , Poisson[] poissons) {
		double perception = 100;
		ArrayList<Zones_dangereuses> voisins = new ArrayList<Zones_dangereuses>();
		
		for(Zones_dangereuses autre : zones) {
			double d = Vect.dist(this.Position,autre.Position);
			if (autre != this && d <= perception) {
				voisins.add(autre);
				}
			}
		if(voisins.size() != 0) {
			this.Vitesse.add(eviter(voisins));
			this.Vitesse.normalisation();
		}
		
		this.Vitesse.add(attaquer(poissons));
		this.Vitesse.normalisation();
		
		this.Position.add(this.Vitesse);
	}
	
	public Vect eviter(ArrayList<Zones_dangereuses> voisins) {
		Vect moyenne = new Vect(0,0);
		if (voisins.size() != 0) {
			for(int i = 0 ; i < voisins.size()  ; i++) {
				double distance = Vect.dist(this.Position,voisins.get(i).Position);
				Vect diff = new Vect(0,0);
				diff = Vect.sub(this.Position, voisins.get(i).Position);
				diff.div(distance);
				moyenne.add(diff);
			}
			moyenne.div(voisins.size());
			moyenne.limit(0.1);
		}
		return moyenne;
	}
	
	public Vect attaquer(Poisson[] poissons) {
		double perception = 100;
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
