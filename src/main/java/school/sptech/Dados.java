package school.sptech;

import java.time.LocalDate;
import java.time.LocalTime;

public class Dados {
    private Integer idDados;
    private String nomeMunicipio;

    public Dados(Integer idDados, String nomeMunicipio) {
        this.idDados = idDados;
        this.nomeMunicipio = nomeMunicipio;
    }

    public Dados() {}

    public Integer getIdDados() {
        return idDados;
    }

    public void setIdDados(Integer idDados) {
        this.idDados = idDados;
    }

    public String getNomeMunicipio() {
        return nomeMunicipio;
    }

    public void setNomeMunicipio(String nomeMunicipio) {
        this.nomeMunicipio = nomeMunicipio;
    }
}
