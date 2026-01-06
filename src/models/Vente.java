package models;
import java.time.LocalDateTime; // Pas LocalDate

public class Vente {
	private int id_vente;
	private String numeroFacture;
	private LocalDateTime date_vente;
	private double total;
	
	// Constructeur sans numéro de facture (ancien constructeur)
	public Vente(int id_vente, LocalDateTime date_vente, double total) {
		this.id_vente = id_vente;
		this.date_vente = date_vente;
		this.total = total;
	}
	
	public Vente(int id_vente, String numeroFacture, LocalDateTime date_vente, double total) {
		this.id_vente = id_vente;
		this.numeroFacture = numeroFacture;
		this.date_vente = date_vente;
		this.total = total;
	}
	
	public int getId_vente() {
		return id_vente;
	}
	
	public void setId_vente(int id_vente) {
		this.id_vente = id_vente;
	}
	
	public LocalDateTime getDate_vente() {
		return date_vente;
	}
	
	public void setDate_vente(LocalDateTime date_vente) {
		this.date_vente = date_vente;
	}
	
	public double getTotal() {
		return total;
	}
	
	public void setTotal(double total) {
		this.total = total;
	}
	
	public String getNumeroFacture() {
		return numeroFacture;
	}
	
	public void setNumeroFacture(String numeroFacture) {
		this.numeroFacture = numeroFacture;
	}
}