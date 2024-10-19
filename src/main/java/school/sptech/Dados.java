package school.sptech;

import java.time.LocalDate;
import java.time.LocalTime;

public class Dados {

    private Integer idDados;
    private LocalDate data;
    private LocalTime horario;
    private String objeto;
    private String municipio;


    public Dados(Integer idDados, LocalDate data, LocalTime horario, String objeto, String municipio) {
        this.idDados = idDados;
        this.data = data;
        this.horario = horario;
        this.objeto = objeto;
        this.municipio = municipio;
    }

    public Dados(){}

    public Integer getIdDados() {
        return idDados;
    }

    public void setIdDados(Integer idDados) {
        this.idDados = idDados;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    @Override
    public String toString() {
        return "Dados{" +
                "idDados=" + idDados +
                ", data=" + data +
                ", horario=" + horario +
                ", objeto='" + objeto + '\'' +
                ", municipio='" + municipio + '\'' +
                '}';
    }
}
