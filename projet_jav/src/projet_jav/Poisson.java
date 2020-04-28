package projet_jav;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.PI;
import java.util.ArrayList;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class Poisson{
	//outils
	Random random = new Random();
	
	// variables globales
	protected int id;
	protected Vect Position;
	protected Vect Vitesse;
	protected static final int size = 3;
    protected static final Path2D shape = new Path2D.Double();
	
	public Poisson(int id, int L, int l) {
		this.id = id;
		this.Position = new Vect(random.nextInt(L),random.nextInt(l));
		double x = 2*random.nextDouble()-1;
		this.Vitesse = new Vect(x,sqrt(1-pow(x,2)));
	}
	
	static {
        shape.moveTo(0, -size * 2);
        shape.lineTo(-size, size * 2);
        shape.lineTo(size, size * 2);
        shape.closePath();
    }
	
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
	
	public Vect aligner(ArrayList<Poisson> voisins) {
		Vect moyenne = new Vect(0,0);
		for(int i = 0 ; i < voisins.size()  ; i++) {
			moyenne.add(voisins.get(i).Vitesse); 
		}
		moyenne.div(voisins.size());
		moyenne.limit(0.08);
		return moyenne;
	}
	
	public Vect eloigner(ArrayList<Poisson> voisins) {
		double trop_proche = 49;
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
	
	public static double mod (double a, int b) {
		int i = (int) a;
        int res = i % b;
        if (res<0 && b>0) {
            res += b;
        }
        return res + a - i;
    }
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
	
	public static Poisson[] creation_poisson(int nb , Zones_dangereuses[] zones , int L , int l) {
		Poisson[] poissons = new Poisson[nb];
		for(int i=0 ; i < nb ; i++) {
			poissons[i] = Poisson.apparition(zones,i,L,l);
		}
		return poissons;
	}

	public static void main(String[] args) {
		
		/*for(int i=1 ; i < 799; i++) {
			zones[9+i] = new Objet_physique(9+i,1,new Vect (i,0));
			zones[807+i] = new Objet_physique(809+i,1,new Vect (i,799));
		}
		for(int j=1 ; j < 799 ; j++) {
			zones[1605+j] = new Objet_physique(1609+j,1,new Vect(0,j));
			zones[2403+j] = new Objet_physique(2409+j,1,new Vect(799,j));
		}*/
		
		new Fenetre(800,800);
	}
}