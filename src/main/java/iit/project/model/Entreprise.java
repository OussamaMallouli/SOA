package iit.project.model;

import java.util.Date;
import java.util.List;

public class Entreprise {
    
    private int id;
    private String nomEntreprise;
    private String email;
    private String numTel;
    private String description;
    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNumTel() {
		return numTel;
	}

	public void setNumTel(String numTel) {
		this.numTel = numTel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDateDeCreation() {
		return dateDeCreation;
	}

	public void setDateDeCreation(Date dateDeCreation) {
		this.dateDeCreation = dateDeCreation;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public List<Technologie> getListTech() {
		return listTech;
	}

	public void setListTech(List<Technologie> listTech) {
		this.listTech = listTech;
	}

	private Date dateDeCreation;
    private String logo;
    private List<Technologie> listTech;

    public Entreprise() {
        
    }

    public Entreprise(int id, String nomEntreprise, String email, String numTel, String description,
			Date dateDeCreation, String logo, List<Technologie> listTech) {
		super();
		this.id = id;
		this.nomEntreprise = nomEntreprise;
		this.email = email;
		this.numTel = numTel;
		this.description = description;
		this.dateDeCreation = dateDeCreation;
		this.logo = logo;
		this.listTech = listTech;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    @Override
    public String toString() {
        return "Entreprise{id=" + id + ", nomEntreprise='" + nomEntreprise + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entreprise entreprise = (Entreprise) obj;
        return id == entreprise.id && nomEntreprise.equals(entreprise.nomEntreprise);
    }

}
