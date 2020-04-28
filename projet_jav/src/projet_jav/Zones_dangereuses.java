package projet_jav;

import static java.lang.Math.PI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Random;
import java.awt.geom.Path2D;

public class Zones_dangereuses {
	protected int id;
	protected Vect Position;
	static final Path2D shape = new Path2D.Double();
	Random random = new Random();
	
	public Zones_dangereuses(int id , int L , int l) {
		this.id=id;
		this.Position = new Vect (random.nextInt(L), random.nextInt(l));
	}
	
	public Zones_dangereuses(int id , Vect Position) {
		this.id=id;
		this.Position = Position;
	}
	
	public void draw(Graphics2D g) {
        AffineTransform save = g.getTransform();
        int x = (int) this.Position.toArray()[0];
        int y = (int) this.Position.toArray()[1];
        if(this instanceof Objet_physique) {
        	g.setColor(Color.blue);
            g.fillOval(x - ((Objet_physique) this).Rayon, y - ((Objet_physique) this).Rayon, 2*((Objet_physique) this).Rayon, 2*((Objet_physique) this).Rayon);
        }
        else {
        	g.translate(this.Position.x, this.Position.y);
	        g.rotate(((Predateurs) this).Vitesse.heading() + PI / 2);
	        g.setColor(Color.red);
        	g.fill(shape);
        	g.setColor(Color.black);
	        g.draw(shape);
        }
        g.setTransform(save);
    }
	
	public static Zones_dangereuses[] creation_zones (int nb_objets, int nb_predateurs , int L , int l) {
		Zones_dangereuses[] zones = new Zones_dangereuses[nb_objets+nb_predateurs];
		for(int i=0 ; i < nb_objets ; i++) {
			zones[i] = new Objet_physique(i,L,l);
		}
		
		for(int i=0 ; i < nb_predateurs ; i++) {
			zones[nb_objets+i] = Predateurs.apparition(zones,nb_objets+i,L,l);
		}
		return zones;
	}

}
