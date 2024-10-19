package school.sptech;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    private static String nomeArquivo = nomeArquivoLog();
    private static final String PASTA_LOGS = "log";

    public static String nomeArquivoLog() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String dataHora = LocalDateTime.now().format(formatter);
        return "aplicacao_" + dataHora + ".log";
    }

    public static String colocarNaPasta() {
        // Cria a pasta "log" se ela ainda não existir
        File pastaLogs = new File(PASTA_LOGS);
        if (!pastaLogs.exists()) {
            pastaLogs.mkdirs();
        }

        return PASTA_LOGS + File.separator + nomeArquivo;
    }

    public static void inserirNoLog(String mensagem) {
        try (FileWriter writer = new FileWriter(colocarNaPasta(), true)) {
            writer.write(mensagem + "\n");
        } catch (IOException e) {
            System.err.println("Erro ao escrever no log: " + e.getMessage());
        }
    }
}
