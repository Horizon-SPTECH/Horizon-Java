package school.sptech;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


public class LeitorExcel {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public List<Furto> extrairDados(String nomeArquivo, InputStream arquivo){



        try {
            System.out.printf("""
                    Iniciando leitura do arquivo %s
                    """,nomeArquivo);

            Workbook workbook;

            if (nomeArquivo.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(arquivo);
            } else {
                workbook = new HSSFWorkbook(arquivo);
            }

            Sheet sheet = workbook.getSheetAt(0);

            List<Furto> dadosExtraidos = new ArrayList<>();

            Integer idCont = 1;


            Integer contadorLinha = 0;
            Integer linhasNaoInseridas = 0;
            Integer linhasInseridas = 0;

            Integer contadorErroData = 0;
            Integer contadorErroHorario = 0;
            Integer contadorErroNomeObjeto = 0;
            Integer contadorErroNomeMunicipio = 0;


            for (Row row : sheet) {
                contadorLinha++;
                if (row.getRowNum() == 0){
                    System.out.printf("""
                            Lendo
                            """);


                        System.out.println("----------------------------------");
                        continue;
                    }


                Furto furto = new Furto();


                //validando se o tipo de crime é Furto
                if(row.getCell(4).getStringCellValue().equals("FURTADO")){

                    Boolean verificarLinha = true;



                    //Validando se existe uma string na coluna de data
                    if (row.getCell(1).getCellType() == CellType.NUMERIC){
                        furto.setData(row.getCell(1).getLocalDateTimeCellValue().toLocalDate());
                    }else {
                        contadorErroData++;
                        verificarLinha = false;
                    }

                    //Validando se existe uma string na coluna de Horarios
                    if (row.getCell(2).getCellType() == CellType.NUMERIC) {
                        furto.setHorario(row.getCell(2).getLocalDateTimeCellValue().toLocalTime());
                    } else {
                        contadorErroHorario++;
                        verificarLinha = false;
                    }

                    if(row.getCell(3).getCellType() == CellType.STRING){
                        if (row.getCell(3).getStringCellValue().equals("VEICULO") ||
                                row.getCell(3).getStringCellValue().equals("BICICLETA") ||
                                row.getCell(3).getStringCellValue().equals("APARELHOS TELEFONICOS")
                        ){
                            if (row.getCell(3).getStringCellValue().equals("APARELHOS TELEFONICOS")) {
                                furto.setObjeto("CELULAR");
                            } else {
                                furto.setObjeto(row.getCell(3).getStringCellValue());
                            }
                        }else {
                            verificarLinha = false;
                            contadorErroNomeObjeto++;
                        }
                    }else {
                        contadorErroNomeObjeto++;
                        verificarLinha = false;
                    }


                    // verificando string e acentuação

                    if (row.getCell(8).getCellType() == CellType.STRING){

                        String textoCorrigido = row.getCell(8).getStringCellValue();

                        switch (textoCorrigido) {
                            case "AFONSO CLAUDIO":
                                textoCorrigido = "AFONSO CLÁUDIO";
                                break;
                            case "AGUA DOCE DO NORTE":
                                textoCorrigido = "ÁGUA DOCE DO NORTE";
                                break;
                            case "AGUIA BRANCA":
                                textoCorrigido = "ÁGUIA BRANCA";
                                break;
                            case "APIACA":
                                textoCorrigido = "APIACÁ";
                                break;
                            case "ATILIO VIVACQUA":
                                textoCorrigido = "ATÍLIO VIVÁCQUA";
                                break;
                            case "BARRA DE SAO FRANCISCO":
                                textoCorrigido = "BARRA DE SÃO FRANCISCO";
                                break;
                            case "BOA ESPERANCA":
                                textoCorrigido = "BOA ESPERANÇA";
                                break;
                            case "CONCEICAO DA BARRA":
                                textoCorrigido = "CONCEIÇÃO DA BARRA";
                                break;
                            case "CONCEICAO DO CASTELO":
                                textoCorrigido = "CONCEIÇÃO DO CASTELO";
                                break;
                            case "DIVINO DE SAO LOURENCO":
                                textoCorrigido = "DIVINO DE SÃO LOURENÇO";
                                break;
                            case "FUNDAO":
                                textoCorrigido = "FUNDÃO";
                                break;
                            case "GUACUI":
                                textoCorrigido = "GUAÇUÍ";
                                break;
                            case "IBIRACU":
                                textoCorrigido = "IBIRAÇU";
                                break;
                            case "ITAGUACU":
                                textoCorrigido = "ITAGUAÇU";
                                break;

                            case "ITARANA":
                                textoCorrigido = "ITARANA";
                                break;
                            case "IUNA":
                                textoCorrigido = "IÚNA";
                                break;
                            case "JAGUARE":
                                textoCorrigido = "JAGUARÉ";
                                break;
                            case "JERONIMO MONTEIRO":
                                textoCorrigido = "JERÔNIMO MONTEIRO";
                                break;
                            case "JOAO NEIVA":
                                textoCorrigido = "JOÃO NEIVA";
                                break;
                            case "MANTENOPOLIS":
                                textoCorrigido = "MANTENÓPOLIS";
                                break;
                            case "MARATAIZES":
                                textoCorrigido = "MARATAÍZES";
                                break;
                            case "MARILANDIA":
                                textoCorrigido = "MARILÂNDIA";
                                break;
                            case "NOVA VENECIA":
                                textoCorrigido = "NOVA VENÉCIA";
                                break;
                            case "PEDRO CANARIO":
                                textoCorrigido = "PEDRO CANÁRIO";
                                break;
                            case "PIUMA":
                                textoCorrigido = "PIÚMA";
                                break;
                            case "SANTA MARIA DE JETIBA":
                                textoCorrigido = "SANTA MARIA DE JETIBÁ";
                                break;
                            case "SAO DOMINGOS DO NORTE":
                                textoCorrigido = "SÃO DOMINGOS DO NORTE";
                                break;
                            case "SAO GABRIEL DA PALHA":
                                textoCorrigido = "SÃO GABRIEL DA PALHA";
                                break;
                            case "SAO JOSE DO CALCADO":
                                textoCorrigido = "SÃO JOSÉ DO CALÇADO";
                                break;
                            case "SAO MATEUS":
                                textoCorrigido = "SÃO MATEUS";
                                break;
                            case "VILA PAVAO":
                                textoCorrigido = "VILA PAVÃO";
                                break;
                            case "VILA VALERIO":
                                textoCorrigido = "VILA VALÉRIO";
                                break;
                            case "VITORIA":
                                textoCorrigido = "VITÓRIA";
                                break;
                                default:
                                break;
                        }

                        furto.setNomeMunicipio(textoCorrigido);
                    }else {
                        verificarLinha = false;
                        contadorErroNomeMunicipio++;
                    }

                    if (verificarLinha){
                        dadosExtraidos.add(furto);
                        linhasInseridas++;
                    }else {
                        linhasNaoInseridas++;
                    }


                }else {
                    linhasNaoInseridas++;
                }

            }

            Log.inserirNoLog("---------------------------------------");

            if (linhasNaoInseridas <=1){
                System.out.println(linhasNaoInseridas +" Linha não foi inserida na tabela de Furtos");
                Log.inserirNoLog( "["+ LocalDateTime.now() .format(formatter)  + "] "+ linhasNaoInseridas +" Linha não foi inseridas na tabela de Furtos");

            }else {
                System.out.println(linhasNaoInseridas +" Linhas não foram inseridas na tabela de Furtos");
                Log.inserirNoLog( "["+ LocalDateTime.now() .format(formatter)  + "] "+ linhasNaoInseridas +" Linhas não foram inseridas na tabela de Furtos");

            }

            if (contadorErroData > 0){
                System.out.println( contadorErroData +" Linhas não foram inseridas por data indeterminada!");
                Log.inserirNoLog( "["+ LocalDateTime.now() .format(formatter)  + "] "+ contadorErroData +" Linhas não foram inseridas por data indeterminada!");
            }

            if (contadorErroHorario > 0){
                System.out.println( contadorErroHorario +" Linhas não foram inseridas por Horário indeterminada! ");
                Log.inserirNoLog( "["+ LocalDateTime.now() .format(formatter)  + "] "+ contadorErroHorario +" Linhas não foram inseridas por Horário indeterminada!");
            }

            if (contadorErroNomeObjeto > 0) {
                System.out.println( contadorErroNomeObjeto +" Linhas não foram inseridas por Objeto não registrado");
                Log.inserirNoLog( "["+ LocalDateTime.now() .format(formatter)  + "] "+ contadorErroNomeObjeto +" Linhas não foram inseridas por Objeto não registrado");
            }

            if (contadorErroNomeMunicipio > 0){
                System.out.println( contadorErroNomeMunicipio +" Linhas não foram  inseridas por Município não identificada");
                Log.inserirNoLog( "["+ LocalDateTime.now() .format(formatter)  + "] "+ contadorErroNomeMunicipio +" Linhas não foram  inseridas por Município não identificada");
            }


            if (linhasInseridas <= 1 ){
                System.out.println( linhasInseridas +" Linha foi ser inserida na Tabela de Furtos");
                Log.inserirNoLog( "["+ LocalDateTime.now() .format(formatter)  + "] "+ linhasInseridas +" Linha foi ser inserida na Tabela de Furtos");

            }else {
                System.out.println( linhasInseridas +" Linhas vão ser inseridas na Tabela de Furtos");
                Log.inserirNoLog( "["+ LocalDateTime.now() .format(formatter)  + "] "+ linhasInseridas +" Linha(s) vão ser inseridas na Tabela de Furtos");
            }


            Log.inserirNoLog("---------------------------------------");




            workbook.close();

            System.out.printf("""
                    Leitura do arquivo finalizada
                    """);
            System.out.println("-----------------------------------");

            return  dadosExtraidos;


        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public List<Populacao> extrairDadosPopulacao(String nomeArquivoPopulacao,InputStream arquivoPopulacao){
        Integer contagem = 0;
        Integer contadorErroMunicipio = 0;
        Integer contadorErroTotalPopulacao = 0;

        try {
            System.out.printf("""
                    Iniciando leitura do arquivo %s
                    """,nomeArquivoPopulacao);

            Workbook workbook;

            if (nomeArquivoPopulacao.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(arquivoPopulacao);
            } else {
                workbook = new HSSFWorkbook(arquivoPopulacao);
            }

            Sheet sheet1 = workbook.getSheetAt(0);

            List<Populacao> extrairDadosPopulacao = new ArrayList<>();

            Integer linhasInseridasMunicipios = 0;
            Integer linhasMunicipiosNaoInseridas = 0;


            for (Row row : sheet1) {
                if (row.getRowNum() == 0){
                    System.out.printf("""
                            Lendo
                            """);

                    System.out.println("----------------------------------");
                    continue;
                }


                Populacao populacao = new Populacao();

                Boolean verificarLinha = true;
                if (row != null) {
                    contagem++;


                    if (row.getCell(0).getCellType() == CellType.STRING){

                        String maiusculo = row.getCell(0).getStringCellValue().toUpperCase();

                        populacao.setNomeMunicipio(maiusculo);
                    }else {
                        verificarLinha = false;
                        contadorErroMunicipio++;
                    }

                    if (row.getCell(2).getCellType() == CellType.NUMERIC){
                        populacao.setPopulacao((int) row.getCell(2).getNumericCellValue());
                    }else {

                        String valorCelula = row.getCell(2).getStringCellValue();

                        try {
                            // Remove a palavra "pessoas" e quaisquer espaços
                            String valorNumerico = valorCelula.replaceAll("[^0-9]", "");

                            // Converte a string numérica para um valor numérico
                            Integer populacaoNumerica = Integer.parseInt(valorNumerico);
                            populacao.setPopulacao(populacaoNumerica);

                        } catch (NumberFormatException e) {
                            verificarLinha = false;
                            contadorErroTotalPopulacao++;
                        }
                    }
                }
                    if (verificarLinha){
                        extrairDadosPopulacao.add(populacao);
                        linhasInseridasMunicipios++;
                    }else {
                        linhasMunicipiosNaoInseridas++;
                    }
            }
            Log.inserirNoLog("---------------------------------------");




            if (contadorErroMunicipio > 0){
                System.out.println( contadorErroMunicipio +" Linhas não foram inseridas por Município ser indeterminada!");
                Log.inserirNoLog( "["+ LocalDateTime.now() .format(formatter) + "] " + contadorErroMunicipio +" Linhas não foram inseridas por Município ser indeterminada!");
            }

            if (contadorErroTotalPopulacao > 0) {
                System.out.println(contadorErroTotalPopulacao + " Linhas não foram inseridas por Número de população ser texto!");
                Log.inserirNoLog("[" + LocalDateTime.now().format(formatter) + "] " + contadorErroMunicipio + " Linhas não foram inseridas por Número de população ser texto!");
            }



            if (linhasInseridasMunicipios <= 1){
                System.out.println( linhasInseridasMunicipios +" Linha vai ser inserida");
                Log.inserirNoLog( "["+ LocalDateTime.now() .format(formatter) + "] " + linhasInseridasMunicipios +" Linha vai ser inseridas na Tabela do Municipio de Espírito Santo");
            }else{
                System.out.println( linhasInseridasMunicipios +" Linhas vão ser inserida");
                Log.inserirNoLog( "["+ LocalDateTime.now() .format(formatter) + "] " + linhasInseridasMunicipios +" Linhas vão ser inseridas na Tabela do Municipio de Espírito Santo");
            }


            System.out.println("-------------------------------");
            Log.inserirNoLog("---------------------------------------");

            workbook.close();

            System.out.printf("""
                    Leitura do arquivo finalizada
                    """);

            return extrairDadosPopulacao;
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }


}
