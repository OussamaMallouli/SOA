package iit.project.model;

import java.util.Date;

public class Technologie {
    
    private int id;
    private String nomTech;
    private String version;
    private String description;
    private String plateformSupporte;
    private String siteWeb;
    private Date dateDerniereMAJ;

    public Technologie() {
    }

    public Technologie(int id, String nomTech, String version, String description, String plateformSupporte,
			String siteWeb, Date dateDerniereMAJ) {
		super();
		this.id = id;
		this.nomTech = nomTech;
		this.version = version;
		this.description = description;
		this.plateformSupporte = plateformSupporte;
		this.siteWeb = siteWeb;
		this.dateDerniereMAJ = dateDerniereMAJ;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPlateformSupporte() {
		return plateformSupporte;
	}

	public void setPlateformSupporte(String plateformSupporte) {
		this.plateformSupporte = plateformSupporte;
	}

	public String getSiteWeb() {
		return siteWeb;
	}

	public void setSiteWeb(String siteWeb) {
		this.siteWeb = siteWeb;
	}

	public Date getDateDerniereMAJ() {
		return dateDerniereMAJ;
	}

	public void setDateDerniereMAJ(Date dateDerniereMAJ) {
		this.dateDerniereMAJ = dateDerniereMAJ;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomTech() {
        return nomTech;
    }

    public void setNomTech(String nomTech) {
        this.nomTech = nomTech;
    }

    @Override
    public String toString() {
        return "Technologie{id=" + id + ", nomTech='" + nomTech + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Technologie technologie = (Technologie) obj;
        return id == technologie.id && nomTech.equals(technologie.nomTech);
    }

}
