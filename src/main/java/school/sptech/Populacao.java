package school.sptech;

public class Populacao {
    private Integer idMunicipio;
    private String municipio;
    private Integer populacao;

    public Populacao(Integer idMunicipio, String municipio, Integer populacao) {
        this.idMunicipio = idMunicipio;
        this.municipio = municipio;
        this.populacao = populacao;
    }

    public Populacao(){}

    public Integer getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public Integer getPopulacao() {
        return populacao;
    }

    public void setPopulacao(Integer populacao) {
        this.populacao = populacao;
    }

    @Override
    public String toString() {
        return "Populacao{" +
                "idMunicipio=" + idMunicipio +
                ", municipio='" + municipio + '\'' +
                ", populacao=" + populacao +
                '}';
    }
}
