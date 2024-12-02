package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate jdbcTemplate = dbConnectionProvider.getConnection();

        // Sorteio de um número entre 1 e 8 usando Math.random
        int numeroSorteado = (int) (Math.random() * 8) + 1;

        System.out.println("Número sorteado: " + numeroSorteado);

        // Switch para diferentes números sorteados
        switch (numeroSorteado) {
            case 1, 2, 3, 4, 5, 6, 7, 8:
                executarSelect(jdbcTemplate, numeroSorteado);
                break;
            default:
                System.out.println("Número inválido (não deveria acontecer)");
        }
    }

    private static void executarSelect(JdbcTemplate jdbcTemplate, int id) {
        String sql = "SELECT * FROM prompt WHERE id = ?";
        try {
            List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql, id);

            if (resultados.isEmpty()) {
                System.out.println("Nenhum resultado encontrado para id: " + id);
            } else {
                for (Map<String, Object> linha : resultados) {
                    System.out.println(linha); // Exibe todas as colunas e valores da linha
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
