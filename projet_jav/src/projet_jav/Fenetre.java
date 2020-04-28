package projet_jav;

import javax.swing.JFrame; // biblio des fenetres
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.Runnable;

public class Fenetre extends JFrame implements Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// variables globales
	protected int L;
	protected int l;
	protected Poisson[] poissons;
	protected Zones_dangereuses[] zones;
	protected int nb_objets;
	protected int nb_predateurs;
	protected int nb_poissons;
	protected Thread affichage = new Thread(this);
	protected Canvas canvas;
	
	public Fenetre(int L, int l, int nb_objets , int nb_predateurs , int nb_poissons) {
		this.zones = Zones_dangereuses.creation_zones(10, 5, 800, 800);
		this.poissons = Poisson.creation_poisson(100, zones, 800, 800);
		this.L = L;
		this.l = l;
		this.setTitle("Poissons");
		this.setSize(L+135, l+15);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		JPanel panneau = creerPanel();
		panneau.setPreferredSize(new Dimension(120,l));
		this.getContentPane().add(panneau , BorderLayout.EAST);
		this.setVisible(true);
	}

	public JPanel creerPanel() {
		ActionListener commencer = new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent action) {
				commencer();
			}
		};
		JPanel panneau = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JButton bouton = new JButton("Commencer");
		bouton.setPreferredSize(new Dimension(120, 20));
		bouton.addActionListener(commencer);
		panneau.add(bouton);
		JButton bouton3 = new JButton("Fermer");
		bouton3.setPreferredSize(new Dimension(120, 20));
		panneau.add(bouton3);
		return panneau;
		
	}
	
	public void commencer () {
		if (affichage.isAlive()) {
			affichage.interrupt();
			this.zones = Zones_dangereuses.creation_zones(10, 5, 800, 800);
			this.poissons = Poisson.creation_poisson(100, zones, 800, 800);
			affichage = new Thread(this);
		}
		affichage.start();
	}
	
	public void run() {
		Canvas canvas = new Canvas(this.poissons, this.zones ,L,l);
		this.setContentPane(canvas);
		this.setLayout(new BorderLayout());
		JPanel panneau = creerPanel();
		panneau.setPreferredSize(new Dimension(120,l));
		this.getContentPane().add(panneau , BorderLayout.EAST);
		this.validate();
		Thread.currentThread();
		while(!Thread.interrupted()) {
			canvas.repaint();
		}	
	}
}

	

