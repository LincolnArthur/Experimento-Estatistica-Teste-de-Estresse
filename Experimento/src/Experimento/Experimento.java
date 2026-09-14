package Experimento;

import java.util.*;
import java.util.function.Supplier;
import java.io.*;

public class Experimento {

    // Nome do arquivo onde os resultados do teste de estresse são gravados.
    private static final String ARQUIVO_CSV = "resultados_estresse.csv";
    private static final String CABECALHO_CSV =
            "estrutura,tamanho,repeticao,tempoInsercaoNs,tempoBuscaNs,memoriaBytes\n";

    // Parâmetros de uma rodada de teste (um ou mais tamanhos, repetido N vezes cada).
    private static class Configuracao {
        int[] tamanhos;
        int repeticoes;
        int buscasPorRepeticao;
        int warmupBuscas;

        Configuracao(int[] tamanhos, int repeticoes, int buscasPorRepeticao, int warmupBuscas) {
            this.tamanhos = tamanhos;
            this.repeticoes = repeticoes;
            this.buscasPorRepeticao = buscasPorRepeticao;
            this.warmupBuscas = warmupBuscas;
        }
    }

    // Uma medição (inserção + busca + memória) de uma única repetição.
    private static class Medicao {
        long tempoInsercaoNs;
        long tempoBuscaNs;
        long memoriaBytes;

        Medicao(long tempoInsercaoNs, long tempoBuscaNs, long memoriaBytes) {
            this.tempoInsercaoNs = tempoInsercaoNs;
            this.tempoBuscaNs = tempoBuscaNs;
            this.memoriaBytes = memoriaBytes;
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Teste de estresse: estruturas de dados sob alto volume ===");
        System.out.println("Metricas coletadas por repeticao: tempo de insercao, tempo de busca e memoria utilizada.\n");

        boolean continuar = true;
        while (continuar) {
            exibirMenu();
            int opcao = lerInteiro(sc, "Escolha uma opcao: ", -1);

            switch (opcao) {
                case 1:
                    Configuracao cfg = lerConfiguracaoManual(sc);
                    executarConfiguracoes(List.of(cfg));
                    break;
                case 2:
                    executarConfiguracoes(configuracoesDaBateria());
                    break;
                case 0:
                    continuar = false;
                    System.out.println("Encerrando...");
                    break;
                default:
                    System.out.println("Opcao invalida. Tente novamente.\n");
            }
        }

        sc.close();
    }

    private static void exibirMenu() {
        System.out.println("\n===================== MENU =====================");
        System.out.println("1 - Rodar um experimento personalizado");
        System.out.println("2 - Rodar TODOS os testes da bateria (teste de estresse), um apos o outro");
        System.out.println("0 - Sair");
        System.out.println("==================================================");
    }

    // Bateria completa de testes (opção 2 do menu): sobe o volume de dados até
    // 500 mil, para ver como cada estrutura se comporta sob carga alta.
    // Para testar outros tamanhos, é só editar a lista aqui embaixo.
    private static List<Configuracao> configuracoesDaBateria() {
        return List.of(
            new Configuracao(new int[]{1_000, 10_000, 100_000, 500_000}, 30, 1000, 200)
        );
    }

    private static Configuracao lerConfiguracaoManual(Scanner sc) {
        int[] tamanhos = lerTamanhos(sc);
        int repeticoes = lerInteiro(sc, "Numero de repeticoes por combinacao [padrao 30]: ", 30);
        int buscasPorRepeticao = lerInteiro(sc, "Numero de buscas medidas por repeticao [padrao 1000]: ", 1000);
        int warmupBuscas = lerInteiro(sc, "Numero de buscas de aquecimento (warm-up) [padrao 200]: ", 200);
        return new Configuracao(tamanhos, repeticoes, buscasPorRepeticao, warmupBuscas);
    }

    // Roda uma ou mais configurações seguidas, sempre acrescentando ao mesmo
    // CSV (nunca sobrescrevendo o que já foi salvo antes).
    private static void executarConfiguracoes(List<Configuracao> configuracoes) throws IOException {
        File arquivoCsv = new File(ARQUIVO_CSV);
        boolean arquivoJaExistia = arquivoCsv.exists();

        FileWriter csv = new FileWriter(arquivoCsv, true); // true = modo append
        try {
            if (!arquivoJaExistia) {
                csv.write(CABECALHO_CSV);
            } else {
                System.out.println("Arquivo " + ARQUIVO_CSV + " ja existe - novos resultados serao ACRESCENTADOS a ele.\n");
            }

            int total = configuracoes.size();
            int indice = 1;
            for (Configuracao cfg : configuracoes) {
                if (total > 1) {
                    System.out.println("\n>>> Rodando teste " + indice + " de " + total + " da bateria <<<");
                }
                rodarExperimento(cfg, csv);
                indice++;
            }
        } finally {
            csv.close();
        }

        System.out.println("\nTodos os resultados foram salvos (acrescentados) em " + ARQUIVO_CSV + "\n");
    }

    // Roda um experimento completo: todas as estruturas, em todos os tamanhos da configuração.
    private static void rodarExperimento(Configuracao cfg, FileWriter csv) throws IOException {
        Random rand = new Random();

        System.out.println("Configuracao:");
        System.out.println("  Tamanhos:    " + Arrays.toString(cfg.tamanhos));
        System.out.println("  Repeticoes:  " + cfg.repeticoes);
        System.out.println("  Buscas/rep:  " + cfg.buscasPorRepeticao);
        System.out.println("  Warm-up:     " + cfg.warmupBuscas);
        System.out.println();

        // resultados[estrutura][tamanho] -> uma medição por repetição
        Map<String, Map<Integer, List<Medicao>>> resultados = new LinkedHashMap<>();
        Map<String, String> complexidades = new LinkedHashMap<>();

        for (int n : cfg.tamanhos) {
            // valores a inserir, com alguma dispersão (0 a n*10)
            int[] valores = new int[n];
            for (int i = 0; i < n; i++) {
                valores[i] = rand.nextInt(n * 10);
            }

            // uma fábrica por estrutura, para recriar cada uma do zero a cada repetição
            List<Supplier<EstruturaBusca>> fabricas = List.of(
                () -> new ArrayLinear(n),
                () -> new ArrayBinario(n),
                () -> new ListaEncadeada(),
                () -> new ArvoreBST(),
                () -> new HashTableWrapper()
            );

            for (Supplier<EstruturaBusca> fabrica : fabricas) {
                String nomeEstrutura = fabrica.get().nome();
                String complexidade = fabrica.get().complexidade();
                complexidades.put(nomeEstrutura, complexidade);

                for (int rep = 0; rep < cfg.repeticoes; rep++) {
                    EstruturaBusca estrutura = fabrica.get();

                    // memória antes de popular a estrutura (pede uma coleta de lixo pra reduzir ruído)
                    System.gc();
                    long memAntes = memoriaUsadaBytes();

                    // tempo de inserção: o "estresse" de popular a estrutura de uma vez
                    long inicioInsercao = System.nanoTime();
                    for (int v : valores) {
                        estrutura.inserir(v);
                    }
                    estrutura.finalizarInsercoes(); // só faz algo no ArrayBinario
                    long fimInsercao = System.nanoTime();
                    long tempoInsercaoNs = fimInsercao - inicioInsercao;

                    // memória depois da inserção completa
                    System.gc();
                    long memDepois = memoriaUsadaBytes();
                    long memoriaBytes = Math.max(0, memDepois - memAntes);

                    // aquecimento: descarta as primeiras buscas (JIT ainda "esquentando")
                    for (int i = 0; i < cfg.warmupBuscas; i++) {
                        estrutura.buscar(valores[rand.nextInt(n)]);
                    }

                    // tempo de busca de verdade
                    long inicioBusca = System.nanoTime();
                    for (int i = 0; i < cfg.buscasPorRepeticao; i++) {
                        estrutura.buscar(valores[rand.nextInt(n)]);
                    }
                    long fimBusca = System.nanoTime();
                    long tempoBuscaNs = fimBusca - inicioBusca;

                    csv.write(nomeEstrutura + "," + n + "," + rep + "," +
                            tempoInsercaoNs + "," + tempoBuscaNs + "," + memoriaBytes + "\n");

                    resultados
                        .computeIfAbsent(nomeEstrutura, k -> new LinkedHashMap<>())
                        .computeIfAbsent(n, k -> new ArrayList<>())
                        .add(new Medicao(tempoInsercaoNs, tempoBuscaNs, memoriaBytes));
                }

                System.out.println("Concluido: " + nomeEstrutura + " | n=" + n);
            }
        }

        csv.flush();

        imprimirTabelaFinal(resultados, complexidades, cfg.tamanhos);
    }

    // Estimativa de memória em uso pela JVM. System.gc() é só uma sugestão ao
    // coletor de lixo, sem garantia de rodar na hora, então isso serve para
    // comparar a ordem de grandeza entre estruturas, não como valor exato.
    private static long memoriaUsadaBytes() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    private static int[] lerTamanhos(Scanner sc) {
        System.out.print("Tamanhos a testar, separados por virgula [padrao 1000,10000,100000,500000]: ");
        String linha = sc.nextLine().trim();
        if (linha.isEmpty()) {
            return new int[]{1_000, 10_000, 100_000, 500_000};
        }
        String[] partes = linha.split(",");
        int[] tamanhos = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            tamanhos[i] = Integer.parseInt(partes[i].trim());
        }
        return tamanhos;
    }

    private static int lerInteiro(Scanner sc, String prompt, int padrao) {
        System.out.print(prompt);
        String linha = sc.nextLine().trim();
        if (linha.isEmpty()) return padrao;
        try {
            return Integer.parseInt(linha);
        } catch (NumberFormatException e) {
            System.out.println("Valor invalido, usando padrao (" + padrao + ").");
            return padrao;
        }
    }

    // Tabela final: para cada estrutura e tamanho, mostra complexidade teórica,
    // tempo médio de inserção (ms), tempo médio de busca (ns) e memória média (KB),
    // cada um com seu desvio padrão.
    private static void imprimirTabelaFinal(
            Map<String, Map<Integer, List<Medicao>>> resultados,
            Map<String, String> complexidades,
            int[] tamanhos) {

        String linhaSep = "-".repeat(126);
        System.out.println("=== Tabela final: teste de estresse por estrutura e tamanho ===\n");
        System.out.println(linhaSep);
        System.out.printf("%-16s %-9s %-11s %16s %16s %16s %16s %14s %14s%n",
                "Estrutura", "Complex.", "Tamanho (n)",
                "Insercao med.(ms)", "Insercao dp(ms)",
                "Busca med.(ns)", "Busca dp(ns)",
                "Mem. med.(KB)", "Mem. dp(KB)");
        System.out.println(linhaSep);

        for (String estrutura : resultados.keySet()) {
            String complexidade = complexidades.get(estrutura);
            for (int n : tamanhos) {
                List<Medicao> medicoes = resultados.get(estrutura).get(n);

                List<Long> temposInsercao = new ArrayList<>();
                List<Long> temposBusca = new ArrayList<>();
                List<Long> memorias = new ArrayList<>();
                for (Medicao m : medicoes) {
                    temposInsercao.add(m.tempoInsercaoNs);
                    temposBusca.add(m.tempoBuscaNs);
                    memorias.add(m.memoriaBytes);
                }

                double mediaInsercaoMs = media(temposInsercao) / 1_000_000.0;
                double desvioInsercaoMs = desvioPadrao(temposInsercao, media(temposInsercao)) / 1_000_000.0;
                double mediaBusca = media(temposBusca);
                double desvioBusca = desvioPadrao(temposBusca, mediaBusca);
                double mediaMemKb = media(memorias) / 1024.0;
                double desvioMemKb = desvioPadrao(memorias, media(memorias)) / 1024.0;

                System.out.printf("%-16s %-9s %-11d %16.3f %16.3f %16.1f %16.1f %14.1f %14.1f%n",
                        estrutura, complexidade, n,
                        mediaInsercaoMs, desvioInsercaoMs,
                        mediaBusca, desvioBusca,
                        mediaMemKb, desvioMemKb);
            }
            System.out.println(linhaSep);
        }

        System.out.println("\n* O(log n) para ArvoreBST assume insercao em ordem aleatoria " +
                "(caso pior, com dados ja ordenados, degenera para O(n)).");
        System.out.println("* O(1) para HashTable e amortizado (caso medio); pode degradar " +
                "para O(n) em caso de muitas colisoes.");
        System.out.println("* Memoria medida via Runtime.totalMemory()-freeMemory() apos System.gc() " +
                "(estimativa; util para comparacao relativa, nao como valor absoluto exato).");
    }

    private static double media(List<Long> valores) {
        double soma = 0;
        for (long v : valores) soma += v;
        return soma / valores.size();
    }

    private static double desvioPadrao(List<Long> valores, double media) {
        double somaQuadrados = 0;
        for (long v : valores) somaQuadrados += Math.pow(v - media, 2);
        return Math.sqrt(somaQuadrados / valores.size());
    }
}
