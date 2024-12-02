package school.sptech;

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
import java.util.List;
import java.util.Set;

public class MainReserva {

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



        //listar os bucket da minha instancias

//        try{
//
//            List<Bucket> buckets = s3Client.listBuckets().buckets();
//            System.out.println("Lista de buckets");
//            for (Bucket bucket : buckets) {
//                System.out.println(bucket.name());
//            }
//
//        }catch (S3Exception e){
//            System.out.println("Erro ao listar buckets: " + e.getMessage());
//        }
//

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

//            connection.execute("DROP DATABASE IF EXISTS projetoHorizon");
//            connection.execute("CREATE DATABASE projetoHorizon");

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

    }
}

