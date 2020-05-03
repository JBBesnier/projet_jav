package projet_jav;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.util.ArrayList;

/**Classe utilisé pour dessiner les éléments de la fenêtre.
 * Toutes les informations sur paintComponant sont dans le 
 * Overdrive, paintComponant existant deja dans java.lang
 * @author Jean-Baptiste
 */

public class Canvas extends JPanel {

	private static final long serialVersionUID = 1L;
	// Utile uniquement lors de sérialisation : pas besoin ici mais laissé par acquis de conscience
	
	// Paramètres :
	protected Poisson[] poisson;
	protected Zones_dangereuses[] zones;
	protected int L;
	protected int l;
	
	public Canvas (Poisson[] poisson , Zones_dangereuses[] zones, int L , int l) {
		this.poisson = poisson;
		this.zones = zones;
		this.l = l;
		this.L = L;
	}
	@Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        for (int i=0 ; i < zones.length ; i++) {
        	this.zones[i].draw(g);	
        }
        
        ArrayList<Predateurs> predateurs = new ArrayList<Predateurs>();
		for (Zones_dangereuses zone : zones) {
			if (zone instanceof Predateurs) {
				predateurs.add((Predateurs)zone);
			}
		}
		
        this.poisson = Poisson.manger(this.poisson , predateurs);
        this.poisson = Poisson.reproduction(this.poisson, predateurs);
        Poisson.run(this.poisson,this.zones,g,L,l);
        g.fillRect(L-2, 0, 1, l);
        }
	}