package school.sptech;

import java.time.LocalDate;
import java.time.LocalTime;

public class Furto extends Dados {
    private LocalDate data;
    private LocalTime horario;
    private String objeto;

    public Furto() {}

    public Furto(Integer idDados, String nomeMunicipio, LocalDate data, LocalTime horario, String objeto) {
        super(idDados, nomeMunicipio);
        this.data = data;
        this.horario = horario;
        this.objeto = objeto;
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
}
