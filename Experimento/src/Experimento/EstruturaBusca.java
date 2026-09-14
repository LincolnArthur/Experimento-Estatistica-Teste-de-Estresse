package Experimento;

// Contrato comum a todas as estruturas testadas no experimento.
public interface EstruturaBusca {
    void inserir(int valor);
    boolean buscar(int chave);
    String nome();

    // Complexidade teórica da busca (caso médio), só para mostrar na tabela final.
    default String complexidade() {
        return "?";
    }

    // Chamado uma vez, depois de todas as inserções e antes das buscas.
    // Só o ArrayBinario usa isso, para ordenar o array.
    default void finalizarInsercoes() {
        // nada a fazer por padrão
    }
}
