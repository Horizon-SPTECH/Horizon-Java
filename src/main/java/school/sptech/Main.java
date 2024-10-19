package school.sptech;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import school.sptech.client.S3Provider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.swing.plaf.synth.Region;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;


public class Main {

    public static void main(String[] args) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Iniciando execução do programa\n");
        //Instanciando o cliente S3 via S3Provider


        S3Client s3Client = new S3Provider().getS3Client();
        String nomeBucket = "bucket-horizon";

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

        try {

            Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Listando e fazendo download dos arquivos no bucket: " + nomeBucket);

            List<S3Object> objects = s3Client.listObjects(ListObjectsRequest.builder()
                    .bucket(nomeBucket)
                    .build()).contents();

            for (S3Object object : objects) {

                Path oupuPath = new File(object.key()).toPath();

                if(Files.exists(oupuPath)){
                    Files.delete(oupuPath);
                }

                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(nomeBucket)
                        .key(object.key())
                        .build();

                try (InputStream inputStream = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream())) {
                    Path outputPath = new File(object.key()).toPath();
                    Files.copy(inputStream, outputPath);
                    System.out.println("Arquivo baixado: " + object.key());
                    Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Arquivo baixado com sucesso: " + object.key());
                } catch (IOException e) {

                    Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Erro ao salvar o arquivo: " + e.getMessage());
                    System.err.println("Erro ao salvar o arquivo: " + e.getMessage());
                }

            }
        } catch (S3Exception e) {

            Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Erro ao fazer download dos arquivos: " + e.getMessage());
            System.err.println("Erro ao fazer download dos arquivos: " + e.getMessage());
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



        String nomeArquivo = "objetos-furtados.xlsx";
        String nomeArquivoPopulacao = "populacao-es.xlsx";

        Path caminho = Path.of(nomeArquivo);
        Path caminhoPopulacao = Path.of(nomeArquivoPopulacao);

            InputStream arquivo = Files.newInputStream(caminho);
            InputStream arquivoPopulacao = Files.newInputStream(caminhoPopulacao);

            LeitorExcel leitorExcel = new LeitorExcel();
            LeitorExcel leitorExcel1 = new LeitorExcel();

            List<Dados> dadosEstraidos = leitorExcel.extrairDados(nomeArquivo, arquivo);
            List<Populacao> populacaoList = leitorExcel1.extrairDadosPopulacao(nomeArquivoPopulacao, arquivoPopulacao);

            // Conectando ao banco de dados

        Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Conexão com o banco o Bnaco de Dados");

            DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
            JdbcTemplate connection = dbConnectionProvider.getConnection();

            connection.execute("DROP DATABASE IF EXISTS projetoHorizon");
            connection.execute("CREATE DATABASE projetoHorizon");
            connection.execute("USE projetoHorizon");

            connection.execute("""
                CREATE TABLE IF NOT EXISTS dados (
                    idDados INT AUTO_INCREMENT PRIMARY KEY,
                    dataFurto DATE NOT NULL,
                    horario TIME NOT NULL,
                    tipoObjeto VARCHAR(255) NOT NULL,
                    municipio VARCHAR(255) NOT NULL
                )
            """);

            connection.execute("""
                CREATE TABLE IF NOT EXISTS populacao (
                    idMunicipio INT AUTO_INCREMENT PRIMARY KEY,
                    municipio VARCHAR(255) NOT NULL,
                    populacao INT NOT NULL
                )
            """);



            // Inserindo os dados no banco de dados
            for (Populacao populacao : populacaoList) {
                connection.update(
                 "INSERT INTO populacao (municipio,populacao) VALUES(?,?)",
                        populacao.getMunicipio(),
                        populacao.getPopulacao()
                );
                //System.out.println(populacao);
            }
        System.out.println("Dados de população do Espirito Santos inseridos com sucesso");
        Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Dados de população do Espirito Santos inseridos com sucesso");

            //for (Dados dados : dadosEstraidos) {
//                connection.update("INSERT INTO dados (dataFurto, horario, tipoObjeto, municipio) VALUES (?, ?, ?, ?)",
//
//                        dados.getData(),
//                        dados.getHorario(),
//                        dados.getObjeto(),
//                        dados.getMunicipio());
                connection.update("INSERT INTO dados (dataFurto, horario, tipoObjeto, municipio) VALUES (?, ?, ?, ?)",
                "2024-04-06", "00:00", "CELULAR", "VITORIA");
                //System.out.println(dados);
            //}

            System.out.println("Dados sobre furtos inseridos com sucesso no banco de dados!");
            Log.inserirNoLog("["+ LocalDateTime.now() .format(formatter)+ "] Dados sobre furtos inseridos com sucesso no banco de dados!");


        // fazendo upload de arqivos

        String logCaminhoArquivo = Log.colocarNaPasta();

        try {
            File file = new File(logCaminhoArquivo);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(nomeBucket)
                    .key(file.getName())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
            System.out.println("Arquivo '" + file.getName() + "' enviado com sucesso");
        } catch (Exception e) {
            System.err.println("Erro ao fazer upload do arquivo: " + e.getMessage());
        }

    }
}