package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import school.sptech.client.S3Provider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Iniciando execução do programa\n");
        //Instanciando o cliente S3 via S3Provider


        String varBucket = System.getenv("NAME_BUCKET");
        S3Client s3Client = new S3Provider().getS3Client();
        String nomeBucket = varBucket;

        try {
            s3Client.headBucket(HeadBucketRequest.builder().
                    bucket(nomeBucket).
                    build());
            System.out.println("O bucket : " + nomeBucket + ", já existe");
            Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Bucket já existe: " + nomeBucket);
        }catch (S3Exception e){
            Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Bucket já existe: " + nomeBucket);

            if (e.statusCode() == 400){
                try {

                    Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Bucket não encontrado, criando um novo: " + nomeBucket);

                    CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                            .bucket(nomeBucket)
                            .build();
                    s3Client.createBucket(createBucketRequest);
                    System.out.println("Bucket criado com sucesso: " + nomeBucket);
                    Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Bucket criado com sucesso: " + nomeBucket);

                }catch (S3Exception ex){
                    Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Erro ao criar o bucket: " + ex.getMessage());
                    System.err.println("Erro ao criar o bucket: " + ex.getMessage());
                }

            }else {
                Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Erro ao verrificar o bucket: " + e.getMessage());
                System.err.println("Erro ao verrificar o bucket: " + e.getMessage());
            }
        }

        //listando os "objetos"/arquvos do bucket

//        try {
//            ListObjectsRequest listObjects = ListObjectsRequest.builder()
//                    .bucket(nomeBucket)
//                    .build();
//
//            List<S3Object> objects = s3Client.listObjects(listObjects).contents();
//
//            System.out.println("Objetos no bucket " + nomeBucket + ":");
//
//            for (S3Object object : objects) {
//
////  System.out.println("- " + object.key());
//            }
//
//        } catch (Exception e) {
//            System.err.println("Erro ao listar objetos no bucket: " + e.getMessage());
//        }

        //fazendo downloads de arquivos


//        String nomeArquivo = "objetos-furtados.xlsx";
//        String nomeArquivoPopulacao = "populacao-es.xlsx";
//
//        Path caminho = Path.of(nomeArquivo);
//        Path caminhoPopulacao = Path.of(nomeArquivoPopulacao);

        // Verifica se os arquivos locais já existem

//        try {
//
//            Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Listando e fazendo download dos arquivos no bucket: " + nomeBucket);
//
//            List<S3Object> objects = s3Client.listObjects(ListObjectsRequest.builder()
//                    .bucket(nomeBucket)
//                    .build()).contents();
//
//            for (S3Object object : objects) {
//
//                if (object.key().endsWith(".xlsx")) {
//                    Path oupuPath = new File(object.key()).toPath();
//
//                    if(Files.exists(oupuPath)){
//                        Files.delete(oupuPath);
//                    }
//
//
//                    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                            .bucket(nomeBucket)
//                            .key(object.key())
//                            .build();
//
//                    try (InputStream inputStream = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream())) {
//                        Path outputPath = new File(object.key()).toPath();
//                        Files.copy(inputStream, outputPath);
//                        System.out.println("Arquivo baixado: " + object.key());
//                        Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Arquivo baixado com sucesso: " + object.key());
//                    } catch (IOException e) {
//
//                        Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Erro ao salvar o arquivo: " + e.getMessage());
//                        System.err.println("Erro ao salvar o arquivo: " + e.getMessage());
//                    }
//                }
//            }
//        } catch (S3Exception e) {
//
//            Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Erro ao fazer download dos arquivos: " + e.getMessage());
//            System.err.println("Erro ao fazer download dos arquivos: " + e.getMessage());
//        }


        //Lendo arquivos direto do BUcket

        List<S3Object> objects = s3Client.listObjects(ListObjectsRequest.builder()
                .bucket(nomeBucket)
                .build()).contents();


        LeitorExcel leitorExcel = new LeitorExcel();
        LeitorExcel leitorExcel1 = new LeitorExcel();

        List<Furto> dadosExtraidos = null;
        List<Populacao> populacaoList = null;

        Set <String> mensagensEnviadas = new HashSet<>();

        for (S3Object object : objects) {
            if (object.key().endsWith(".xlsx")){
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(nomeBucket)
                        .key(object.key())
                        .build();

                try (InputStream inputStream = s3Client.getObject(getObjectRequest)){
                    if (object.key().equals("objetos-furtados.xlsx")){
                        dadosExtraidos = leitorExcel.extrairDados(object.key(),inputStream);
                        String mensagem1 = "Arquivo objetos-furtados.xlsx lido com sucesso!" ;
                        if (mensagensEnviadas.add(mensagem1)){
                            System.out.println(mensagem1);
                            Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] " + mensagem1);
                            JSONObject jsonLeituraArquivos = new JSONObject();

                            jsonLeituraArquivos.put("text", String.format("""
                            %s 
                            """, mensagem1));

                            Slack.sendMessage(jsonLeituraArquivos);
                        }
                    }else if (object.key().equals("populacao-es.xlsx")){
                        populacaoList = leitorExcel1.extrairDadosPopulacao(object.key(), inputStream);
                        String mensagem2 = "Arquivo populacao-es.xlsx lido com sucesso! ";

                        if (mensagensEnviadas.add(mensagem2)){
                            System.out.println(mensagem2);
                            Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] " + mensagem2);

                            JSONObject jsonLeituraArquivos = new JSONObject();


                            jsonLeituraArquivos.put("text", String.format("""
                %s 
                """, mensagem2));

                            Slack.sendMessage(jsonLeituraArquivos);

                        }
                    }
                } catch (IOException e){
                    String mensagem3 = "Erro ao ler o arquivo de excel do bucket: " + e.getMessage();

                    if (mensagensEnviadas.add(mensagem3)){
                        Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "]"+ mensagem3 + e.getMessage());
                        System.err.println(mensagem3);

                        JSONObject jsonErro = new JSONObject();


                        jsonErro.put("text", String.format("""
                %s 
                """, mensagem3));

                        Slack.sendMessage(jsonErro);
                    }
                }
            }
        }

        // deletando um 'objeto'/arquivo do bucket

//        try{
//            String objectKeyToDelete = "identificador-do-arquivo";
//
//            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
//                    .bucket(nomeBucket)
//                    .key(objectKeyToDelete)
//                    .build();
//            s3Client.deleteObject(deleteObjectRequest);
//
//            System.out.println("Objeto deletado com sucesso: " + objectKeyToDelete);
//        } catch (S3Exception e) {
//            System.err.println("Erro ao deletar objeto: " + e.getMessage());
//        }

        // Conectando ao banco de dados

        Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Conexão com o banco o Bnaco de Dados");



        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();

        connection.execute("USE projetoHorizon");
        connection.execute("TRUNCATE TABLE furto");
        connection.execute("ALTER TABLE furto DROP FOREIGN KEY fk_furtos_populacao");
        connection.execute("TRUNCATE TABLE municipio_es");
        connection.execute("""
                                    ALTER TABLE furto ADD CONSTRAINT fk_furtos_populacao
                                    FOREIGN KEY (id_municipio_es)
                                    REFERENCES municipio_es(id)
                                    """);

//populacao
//        connection.execute("""
//                CREATE TABLE IF NOT EXISTS populacao (
//                    idMunicipio INT AUTO_INCREMENT PRIMARY KEY,
//                    municipio VARCHAR(255) NOT NULL,
//                    populacao INT NOT NULL
//                )
//            """);
//
//            connection.execute("""
//                CREATE TABLE IF NOT EXISTS furtos (
//                    idFurtos INT AUTO_INCREMENT PRIMARY KEY,
//                    dataFurto DATE NOT NULL,
//                    horario TIME NOT NULL,
//                    tipoObjeto VARCHAR(255) NOT NULL,
//                    idMunicipio INT,
//                    CONSTRAINT fkPopulacao FOREIGN KEY (idMunicipio) REFERENCES populacao(idMunicipio)
//                )
//            """);





        // Inserindo os dados no banco de dados
        for (Populacao populacao : populacaoList) {
            connection.update(
                    "INSERT INTO municipio_es  (nome,habitante) VALUES(?,?)",
                    populacao.getNomeMunicipio(),
                    populacao.getPopulacao()
            );
            //System.out.println(populacao);
        }
        String mensagem4 = "Dados de população do Espirito Santos inseridos com sucesso";
        System.out.println("Dados de população do Espirito Santos inseridos com sucesso");
        Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] " + mensagem4);

        for (Furto furto : dadosExtraidos) {

            Integer idMunicipio = connection.queryForObject(
                    "SELECT id FROM municipio_es where nome = ?",
                    Integer.class,
                    furto.getNomeMunicipio()
            );

            connection.update("INSERT INTO furto (data, horario, objeto_roubado,id_municipio_es) VALUES (?, ?, ?, ?)",
                    furto.getData(),
                    furto.getHorario(),
                    furto.getObjeto(),
                    idMunicipio);
//                connection.update("INSERT INTO futos (dataFurto, horario, tipoObjeto, idMunicipio) VALUES (?, ?, ?, ?)",
//                "2024-04-06", "00:00", "CELULAR", 78);
            //System.out.println(dados);
        }

        String mensagem5 = "Dados sobre furtos inseridos com sucesso no banco de dados!";
        System.out.println("Dados sobre furtos inseridos com sucesso no banco de dados!");
        Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] "+ mensagem5);

        JSONObject jsonInsercaoDados = new JSONObject();


        jsonInsercaoDados.put("text", String.format("""
                %s 
                %s
                """, mensagem4,mensagem5));

        Slack.sendMessage(jsonInsercaoDados);


        // fazendo upload de arqivos

        String logCaminhoArquivo = Log.colocarNaPasta();

        try {
            File file = new File(logCaminhoArquivo);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(nomeBucket)
                    .key(file.getName())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
            String mensagem6 = String.format("Arquivo '" + file.getName() + "' enviado com sucesso");
            System.out.println(mensagem6);

            JSONObject json = new JSONObject();


            json.put("text", String.format("""
                Para mais informações acesse seu log: %s
                %s 
                """, file.getName(),mensagem6));

            Slack.sendMessage(json);
        } catch (Exception e) {
            String mensagem6 = String.format("Erro ao fazer upload do arquivo: " + e.getMessage());
            System.err.println(mensagem6);

            JSONObject json = new JSONObject();


            json.put("text", String.format("""
                %s 
                """, mensagem6));

            Slack.sendMessage(json);
        }


        int numeroSorteado = (int) (Math.random() * 8) + 1;

        switch (numeroSorteado) {
            case 1, 2, 3, 4, 5, 6, 7, 8:
                executarSelect(connection, numeroSorteado);
                break;
            default:
                System.out.println("Número inválido (não deveria acontecer)");
        }
    }

    private static void executarSelect(JdbcTemplate connection, int id) {
        String sqlGetPergunta = "SELECT pergunta FROM prompt WHERE id = ?";

        try {
            List<Map<String, Object>> perguntas = connection.queryForList(sqlGetPergunta, id);

            if (perguntas.isEmpty()) {
                System.out.println("Nenhuma pergunta encontrada para id: " + id);
                return;
            }

            String perguntaOriginal = perguntas.get(0).get("pergunta").toString();

            String perguntaComSubstituicao;
            switch (id) {
                case 1 -> perguntaComSubstituicao = substituirParaLinha1(connection, perguntaOriginal);
                case 2 -> perguntaComSubstituicao = substituirParaLinha2(connection, perguntaOriginal);
                case 3 -> perguntaComSubstituicao = substituirParaLinha3(connection, perguntaOriginal);
                case 4 -> perguntaComSubstituicao = substituirParaLinha4(connection, perguntaOriginal);
                case 5 -> perguntaComSubstituicao = substituirParaLinha5(connection, perguntaOriginal);
                case 6 -> perguntaComSubstituicao = substituirParaLinha6(connection, perguntaOriginal);
                case 7 -> perguntaComSubstituicao = "Estratégia baseada no mapeamento regional."; // Caso 7 sem REPLACE
                case 8 -> perguntaComSubstituicao = substituirParaLinha8(connection, perguntaOriginal);
                default -> perguntaComSubstituicao = "Número inválido.";
            }

            System.out.println("Pergunta com substituições: " + perguntaComSubstituicao);

            String resposta = GeminiClient.getCompletion(perguntaComSubstituicao);

            connection.update(
                    "INSERT INTO recomendacao (data_hora, mensagem, id_prompt) VALUES (NOW(), ?, ?)",
                    resposta,
                    id
            );

            System.out.println(resposta);

            JSONObject json = new JSONObject();


            json.put("text", String.format("""
                Resposta gerada pela IA:
                %s 
                Para mais recomendações acesse o nosso site
                """, resposta));
            Slack.sendMessage(json);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Métodos de substituição por linha

    private static String substituirParaLinha1(JdbcTemplate connection, String perguntaOriginal) {
        String municipio = executarSelectSimples(connection, "SELECT \n" +
                "    m.nome AS municipio\n" +
                "FROM \n" +
                "    furto f\n" +
                "JOIN \n" +
                "    municipio_es m ON f.id_municipio_es = m.id\n" +
                "WHERE \n" +
                "    MONTH(f.data) = 7 AND YEAR(f.data) = YEAR(CURDATE())\n" +
                "GROUP BY \n" +
                "    m.id\n" +
                "ORDER BY \n" +
                "    COUNT(f.id) DESC\n" +
                "LIMIT 1;");
        String ocorrencias = executarSelectSimples(connection, "SELECT \n" +
                "    COUNT(f.id) AS total_furtos\n" +
                "FROM \n" +
                "    furto f\n" +
                "JOIN \n" +
                "    municipio_es m ON f.id_municipio_es = m.id\n" +
                "WHERE \n" +
                "    MONTH(f.data) = 7 AND YEAR(f.data) = YEAR(CURDATE())\n" +
                "GROUP BY \n" +
                "    m.id\n" +
                "ORDER BY \n" +
                "    total_furtos DESC\n" +
                "LIMIT 1");
        String objeto = executarSelectSimples(connection, "SELECT \n" +
                "    f.objeto_roubado\n" +
                "FROM \n" +
                "    furto f\n" +
                "WHERE \n" +
                "    MONTH(f.data) = 7 AND YEAR(f.data) = YEAR(CURDATE())\n" +
                "GROUP BY \n" +
                "    f.objeto_roubado\n" +
                "ORDER BY \n" +
                "    COUNT(f.id) DESC\n" +
                "LIMIT 1;\n");
        String porcentagem = executarSelectSimples(connection, "SELECT\n" +
                "    ROUND((COUNT(f.id) * 100.0 / (\n" +
                "        SELECT COUNT(f2.id)\n" +
                "        FROM furto f2\n" +
                "        JOIN municipio_es m2 ON f2.id_municipio_es = m2.id\n" +
                "        WHERE\n" +
                "            MONTH(f2.data) = 7 AND YEAR(f2.data) = YEAR(CURDATE())\n" +
                "            AND f2.objeto_roubado IN ('CELULAR', 'BICICLETA', 'VEICULO')\n" +
                "            AND m2.id = (\n" +
                "                SELECT m.id\n" +
                "                FROM municipio_es m\n" +
                "                JOIN furto f3 ON m.id = f3.id_municipio_es\n" +
                "                WHERE MONTH(f3.data) = 7 AND YEAR(f3.data) = YEAR(CURDATE())\n" +
                "                GROUP BY m.id\n" +
                "                ORDER BY COUNT(f3.id) DESC\n" +
                "                LIMIT 1\n" +
                "            )\n" +
                "    )), 2) AS percentual\n" +
                "FROM\n" +
                "    furto f\n" +
                "JOIN municipio_es m ON f.id_municipio_es = m.id\n" +
                "WHERE\n" +
                "    MONTH(f.data) = 7 AND YEAR(f.data) = YEAR(CURDATE())\n" +
                "    AND f.objeto_roubado = 'CELULAR'  -- Aqui filtra apenas os furtos de celular\n" +
                "    AND m.id = (\n" +
                "        SELECT m.id\n" +
                "        FROM municipio_es m\n" +
                "        JOIN furto f3 ON m.id = f3.id_municipio_es\n" +
                "        WHERE MONTH(f3.data) = 7 AND YEAR(f3.data) = YEAR(CURDATE())\n" +
                "        GROUP BY m.id\n" +
                "        ORDER BY COUNT(f3.id) DESC\n" +
                "        LIMIT 1\n" +
                "    );\n");

        return perguntaOriginal
                .replace("K", municipio)
                .replace("Y", ocorrencias)
                .replace("W", objeto)
                .replace("Z", porcentagem);
    }

    private static String substituirParaLinha2(JdbcTemplate connection, String perguntaOriginal) {
        String municipio = executarSelectSimples(connection, "SELECT \n" +
                "    m.nome AS municipio\n" +
                "FROM \n" +
                "    furto f\n" +
                "JOIN \n" +
                "    municipio_es m ON f.id_municipio_es = m.id\n" +
                "WHERE \n" +
                "    MONTH(f.data) = 7 AND YEAR(f.data) = YEAR(CURDATE())\n" +
                "GROUP BY \n" +
                "    m.id\n" +
                "ORDER BY \n" +
                "    (COUNT(f.id) * 100.0 / m.habitante) DESC\n" +
                "LIMIT 1;\n");
        String proporcao = executarSelectSimples(connection, "SELECT \n" +
                "    ROUND((COUNT(f.id) * 100.0 / m.habitante), 2) AS proporcao_furtos\n" +
                "FROM \n" +
                "    furto f\n" +
                "JOIN \n" +
                "    municipio_es m ON f.id_municipio_es = m.id\n" +
                "WHERE \n" +
                "    MONTH(f.data) = 7 AND YEAR(f.data) = YEAR(CURDATE())\n" +
                "GROUP BY \n" +
                "    m.id\n" +
                "ORDER BY \n" +
                "    proporcao_furtos DESC\n" +
                "LIMIT 1;\n");

        return perguntaOriginal
                .replace("K", municipio)
                .replace("Y", proporcao);
    }

    private static String substituirParaLinha3(JdbcTemplate connection, String perguntaOriginal) {
        String objeto = executarSelectSimples(connection, "SELECT \n" +
                "    f.objeto_roubado\n" +
                "FROM \n" +
                "    furto f\n" +
                "JOIN \n" +
                "    municipio_es m ON f.id_municipio_es = m.id\n" +
                "WHERE \n" +
                "    MONTH(f.data) = 7 AND YEAR(f.data) = YEAR(CURDATE())\n" +
                "GROUP BY \n" +
                "    f.objeto_roubado\n" +
                "ORDER BY \n" +
                "    COUNT(f.id) DESC\n" +
                "LIMIT 1;\n");
        String ocorrencias = executarSelectSimples(connection, "SELECT \n" +
                "    COUNT(f.id) AS total_furtos\n" +
                "FROM \n" +
                "    furto f\n" +
                "JOIN \n" +
                "    municipio_es m ON f.id_municipio_es = m.id\n" +
                "WHERE \n" +
                "    MONTH(f.data) = 7 AND YEAR(f.data) = YEAR(CURDATE())\n" +
                "GROUP BY \n" +
                "    f.objeto_roubado\n" +
                "ORDER BY \n" +
                "    total_furtos DESC\n" +
                "LIMIT 1;\n");

        return perguntaOriginal
                .replace("K", objeto)
                .replace("Y", ocorrencias);
    }

    private static String substituirParaLinha4(JdbcTemplate connection, String perguntaOriginal) {
        String municipio = executarSelectSimples(connection, "SELECT \n" +
                "    m.nome AS municipio\n" +
                "FROM \n" +
                "    furto f\n" +
                "JOIN \n" +
                "    municipio_es m ON f.id_municipio_es = m.id\n" +
                "WHERE \n" +
                "    MONTH(f.data) = 7 AND YEAR(f.data) = YEAR(CURDATE())\n" +
                "GROUP BY \n" +
                "    m.nome\n" +
                "ORDER BY \n" +
                "    COUNT(f.id) DESC\n" +
                "LIMIT 1;");
        String objeto = executarSelectSimples(connection, "SELECT \n" +
                "    f.objeto_roubado\n" +
                "FROM \n" +
                "    furto f\n" +
                "JOIN \n" +
                "    municipio_es m ON f.id_municipio_es = m.id\n" +
                "WHERE \n" +
                "    MONTH(f.data) = 7 AND YEAR(f.data) = YEAR(CURDATE())\n" +
                "    AND m.id = (\n" +
                "        SELECT \n" +
                "            m2.id\n" +
                "        FROM \n" +
                "            furto f2\n" +
                "        JOIN \n" +
                "            municipio_es m2 ON f2.id_municipio_es = m2.id\n" +
                "        WHERE \n" +
                "            MONTH(f2.data) = 7 AND YEAR(f2.data) = YEAR(CURDATE())\n" +
                "        GROUP BY \n" +
                "            m2.id\n" +
                "        ORDER BY \n" +
                "            COUNT(f2.id) DESC\n" +
                "        LIMIT 1\n" +
                "    )\n" +
                "GROUP BY \n" +
                "    f.objeto_roubado\n" +
                "ORDER BY \n" +
                "    COUNT(f.id) DESC\n" +
                "LIMIT 1;");
        String porcentagem = executarSelectSimples(connection, "SELECT \n" +
                "    ROUND((COUNT(f.id) * 100.0 / (\n" +
                "        SELECT COUNT(f2.id)\n" +
                "        FROM furto f2\n" +
                "        JOIN municipio_es m2 ON f2.id_municipio_es = m2.id\n" +
                "        WHERE \n" +
                "            MONTH(f2.data) = 7 AND YEAR(f2.data) = YEAR(CURDATE())\n" +
                "            AND m2.id = (\n" +
                "                SELECT \n" +
                "                    m3.id\n" +
                "                FROM \n" +
                "                    furto f3\n" +
                "                JOIN \n" +
                "                    municipio_es m3 ON f3.id_municipio_es = m3.id\n" +
                "                WHERE \n" +
                "                    MONTH(f3.data) = 7 AND YEAR(f3.data) = YEAR(CURDATE())\n" +
                "                GROUP BY \n" +
                "                    m3.id\n" +
                "                ORDER BY \n" +
                "                    COUNT(f3.id) DESC\n" +
                "                LIMIT 1\n" +
                "            )\n" +
                "    )), 2) AS percentual\n" +
                "FROM \n" +
                "    furto f\n" +
                "JOIN \n" +
                "    municipio_es m ON f.id_municipio_es = m.id\n" +
                "WHERE \n" +
                "    MONTH(f.data) = 7 AND YEAR(f.data) = YEAR(CURDATE())\n" +
                "    AND m.id = (\n" +
                "        SELECT \n" +
                "            m2.id\n" +
                "        FROM \n" +
                "            furto f2\n" +
                "        JOIN \n" +
                "            municipio_es m2 ON f2.id_municipio_es = m2.id\n" +
                "        WHERE \n" +
                "            MONTH(f2.data) = 7 AND YEAR(f2.data) = YEAR(CURDATE())\n" +
                "        GROUP BY \n" +
                "            m2.id\n" +
                "        ORDER BY \n" +
                "            COUNT(f2.id) DESC\n" +
                "        LIMIT 1\n" +
                "    )\n" +
                "    AND f.objeto_roubado = (\n" +
                "        SELECT \n" +
                "            f4.objeto_roubado\n" +
                "        FROM \n" +
                "            furto f4\n" +
                "        JOIN \n" +
                "            municipio_es m4 ON f4.id_municipio_es = m4.id\n" +
                "        WHERE \n" +
                "            MONTH(f4.data) = 7 AND YEAR(f4.data) = YEAR(CURDATE())\n" +
                "            AND m4.id = (\n" +
                "                SELECT \n" +
                "                    m5.id\n" +
                "                FROM \n" +
                "                    furto f5\n" +
                "                JOIN \n" +
                "                    municipio_es m5 ON f5.id_municipio_es = m5.id\n" +
                "                WHERE \n" +
                "                    MONTH(f5.data) = 7 AND YEAR(f5.data) = YEAR(CURDATE())\n" +
                "                GROUP BY \n" +
                "                    m5.id\n" +
                "                ORDER BY \n" +
                "                    COUNT(f5.id) DESC\n" +
                "                LIMIT 1\n" +
                "            )\n" +
                "        GROUP BY \n" +
                "            f4.objeto_roubado\n" +
                "        ORDER BY \n" +
                "            COUNT(f4.id) DESC\n" +
                "        LIMIT 1\n" +
                "    )\n" +
                "GROUP BY \n" +
                "    f.objeto_roubado;");

        return perguntaOriginal
                .replace("K", municipio)
                .replace("W", objeto)
                .replace("Y", porcentagem);
    }

    private static String substituirParaLinha5(JdbcTemplate connection, String perguntaOriginal) {
        String mes = executarSelectSimples(connection, "   SELECT \n" +
                "    CASE mes\n" +
                "        WHEN 1 THEN 'Janeiro'\n" +
                "        WHEN 2 THEN 'Fevereiro'\n" +
                "        WHEN 3 THEN 'Março'\n" +
                "        WHEN 4 THEN 'Abril'\n" +
                "        WHEN 5 THEN 'Maio'\n" +
                "        WHEN 6 THEN 'Junho'\n" +
                "        WHEN 7 THEN 'Julho'\n" +
                "        WHEN 8 THEN 'Agosto'\n" +
                "        WHEN 9 THEN 'Setembro'\n" +
                "        WHEN 10 THEN 'Outubro'\n" +
                "        WHEN 11 THEN 'Novembro'\n" +
                "        WHEN 12 THEN 'Dezembro'\n" +
                "    END AS mes\n" +
                "FROM (\n" +
                "    SELECT MONTH(data) AS mes\n" +
                "    FROM furto\n" +
                "    GROUP BY MONTH(data)\n" +
                "    ORDER BY COUNT(*) DESC\n" +
                "    LIMIT 1\n" +
                ") AS subquery;\n");

        return perguntaOriginal.replace("K", mes);
    }

    private static String substituirParaLinha6(JdbcTemplate connection, String perguntaOriginal) {
        String taxa = executarSelectSimples(connection, "SELECT\n" +
                "                    ROUND((COUNT(id)/46.184),0) AS furtos_por_km2\n" +
                "                    FROM furto;");

        return perguntaOriginal.replace("K", taxa);
    }

    private static String substituirParaLinha8(JdbcTemplate connection, String perguntaOriginal) {
        String dia = executarSelectSimples(connection, "SELECT \n" +
                "    CASE DAYOFWEEK(data)\n" +
                "        WHEN 1 THEN 'Domingo'\n" +
                "        WHEN 2 THEN 'Segunda-feira'\n" +
                "        WHEN 3 THEN 'Terça-feira'\n" +
                "        WHEN 4 THEN 'Quarta-feira'\n" +
                "        WHEN 5 THEN 'Quinta-feira'\n" +
                "        WHEN 6 THEN 'Sexta-feira'\n" +
                "        WHEN 7 THEN 'Sábado'\n" +
                "    END AS dia_da_semana\n" +
                "FROM \n" +
                "    furto\n" +
                "GROUP BY \n" +
                "    dia_da_semana\n" +
                "ORDER BY \n" +
                "    COUNT(*) DESC\n" +
                "LIMIT 1;");
        String ocorrencias = executarSelectSimples(connection, "SELECT COUNT(*) AS total_furtos\n" +
                "FROM furto\n" +
                "WHERE MONTH(data) = (\n" +
                "    SELECT MONTH(data)\n" +
                "    FROM furto\n" +
                "    GROUP BY MONTH(data)\n" +
                "    ORDER BY COUNT(*) DESC\n" +
                "    LIMIT 1\n" +
                ");");

        return perguntaOriginal
                .replace("K", dia)
                .replace("Y", ocorrencias);
    }

    // Método auxiliar para executar SELECTs simples
    private static String executarSelectSimples(JdbcTemplate connection, String sql) {
        try {
            Map<String, Object> resultado = connection.queryForMap(sql);
            return resultado.values().iterator().next().toString();
        } catch (Exception e) {
            System.out.println("Erro ao executar consulta: " + sql);
            return "N/A";
        }
    }
}

