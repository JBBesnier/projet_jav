package projet_jav;

import javax.swing.*;  // biblio des fenetre
import java.awt.*;
import java.awt.event.*;
import java.lang.*;

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
	protected JTextField nb_pois = new JTextField("100");
	protected JTextField nb_obj = new JTextField("10");
	protected JTextField nb_pred = new JTextField("5");
	protected JTextField taille_min = new JTextField("0");
	protected JTextField taille_max = new JTextField("60");
	
	
	public Fenetre(int L, int l) {
		this.L = L;
		this.l = l;
		this.setTitle("Poissons");
		this.setSize(L+285, l+15);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		JPanel panneau = creerPanel();
		panneau.setPreferredSize(new Dimension(270,l));
		this.getContentPane().add(panneau , BorderLayout.EAST);
		this.setVisible(true);
	}
	
	public Fenetre(int L , int l ,String texte) {
		this.setTitle("Message d'erreur");
		this.setSize(L,l);
		this.setLocationRelativeTo(null);
		JPanel panneau = new JPanel();
		this.setContentPane(panneau);
		JLabel erreur = new JLabel(texte);
		panneau.add(erreur);
		JButton bouton = new JButton("OK");
		ActionListener fermer = new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent action) {
				fermer();
			}
		};
		bouton.addActionListener(fermer);
		panneau.add(bouton);
		this.setVisible(true);
	}

	public JPanel creerPanel() {
		ActionListener commencer = new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent action) {
				commencer();
			}
		};
		JPanel panneau = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton bouton = new JButton("Commencer");
		bouton.setPreferredSize(new Dimension(120, 20));
		bouton.addActionListener(commencer);
		panneau.add(bouton);
		
		ActionListener fermer = new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent action) {
				fermer();
			}
		};
		JButton bouton2 = new JButton("Fermer");
		bouton2.setPreferredSize(new Dimension(120, 20));
		bouton2.addActionListener(fermer);
		panneau.add(bouton2);
		
		JLabel texte = new JLabel ("Nombre de poissons = ");
		panneau.add(texte);
		nb_pois.setPreferredSize(new Dimension (50 , 20));
		panneau.add(nb_pois);
		
		JLabel texte2 = new JLabel ("Nombre d'objets physiques = ");
		panneau.add(texte2);
		nb_obj.setPreferredSize(new Dimension (50 , 20));
		panneau.add(nb_obj);
		
		JLabel texte3 = new JLabel ("Nombre de prédateurs = ");
		panneau.add(texte3);
		nb_pred.setPreferredSize(new Dimension (50 , 20));
		panneau.add(nb_pred);
		
		JLabel texte4 = new JLabel ("Taille minimale des obstacles = ");
		panneau.add(texte4);
		taille_min.setPreferredSize(new Dimension (50 , 20));
		panneau.add(taille_min);
		
		JLabel texte5 = new JLabel ("Taille maximale des obstacles = ");
		panneau.add(texte5);
		taille_max.setPreferredSize(new Dimension (50 , 20));
		panneau.add(taille_max);
		
		return panneau;
	}
	
	public void commencer () {
		if (this.affichage.isAlive()) {
			this.affichage.interrupt();
			this.affichage = new Thread(this);
		}
		try {
			def_elements();
		} catch (NumberFormatException e) {
			new Fenetre (380 , 100 ,"Vous n'avez pas rentré un nombre entier dans un champ.");
			return;
		} catch (NegativeArraySizeException e) {
			new Fenetre (350 , 100 ,"Vous avez rentré un nombre négatif dans un champ.");
			return;
		} catch (ExceptionRayonMinimum e) {
			new Fenetre (480 , 100 ,"La valeur du rayon minimum doit être inférieur à la valeur du rayon maximum.");
			return;
		}
		this.affichage.start();
	}
	
	public void def_elements () throws ExceptionRayonMinimum {
		this.nb_poissons = Integer.parseInt(this.nb_pois.getText());
		this.nb_objets = Integer.parseInt(this.nb_obj.getText());
		this.nb_predateurs = Integer.parseInt(this.nb_pred.getText());
		Objet_physique.R_max = Integer.parseInt(this.taille_max.getText());
		Objet_physique.R_min = Integer.parseInt(this.taille_min.getText());
		if (Objet_physique.R_min > Objet_physique.R_max) {
			throw new ExceptionRayonMinimum();
		} else {
			this.zones = Zones_dangereuses.creation_zones(this.nb_objets, this.nb_predateurs, this.L, this.l);
			this.poissons = Poisson.creation_poisson(this.nb_poissons, this.zones, this.L, this.l);
		}
	}
	
	public void fermer() {
		if (this.affichage.isAlive()) {
			this.affichage.interrupt();
		}
		this.dispose();
	}
	
	public void run() {
		Canvas canvas = new Canvas(this.poissons, this.zones ,this.L,this.l);
		this.setContentPane(canvas);
		this.setLayout(new BorderLayout());
		JPanel panneau = creerPanel();
		panneau.setPreferredSize(new Dimension(270,l));
		this.getContentPane().add(panneau , BorderLayout.EAST);
		this.validate();
		Thread.currentThread();
		while(!Thread.interrupted()) {
			canvas.repaint();
		}	
	}
}

	

