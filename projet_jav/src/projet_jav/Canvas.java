package projet_jav;

import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.Graphics2D;

public class Canvas extends JPanel {
	/*
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Utile uniquement lors de sérialisation : pas besoin ici mais laissé par acquis de conscience

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
	//
	@Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg); //
        Graphics2D g = (Graphics2D) gg;
        for (int i=0 ; i < zones.length ; i++) {
        	this.zones[i].draw(g);	
        }
        Poisson.run(this.poisson,this.zones,g,L,l);
        g.fillRect(L-2, 0, 1, l);
        }
	}