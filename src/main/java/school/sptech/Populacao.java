package school.sptech;

public class Populacao extends Dados {
    private Integer populacao;

    public Populacao(Integer idDados, String nomeMunicipio, Integer populacao) {
        super(idDados, nomeMunicipio);
        this.populacao = populacao;
    }

    public Populacao() {};

    public Integer getPopulacao() {
        return populacao;
    }

    public void setPopulacao(Integer populacao) {
        this.populacao = populacao;
    }
}
