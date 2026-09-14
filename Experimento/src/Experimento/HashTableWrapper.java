package Experimento;

import java.util.HashSet;

// Tabela hash, usando o HashSet pronto do Java.
public class HashTableWrapper implements EstruturaBusca {
    private HashSet<Integer> dados = new HashSet<>();

    public void inserir(int valor) {
        dados.add(valor);
    }

    public boolean buscar(int chave) {
        return dados.contains(chave);
    }

    public String nome() { return "HashTable"; }

    // Caso médio O(1); pode piorar para O(n) se houver muitas colisões.
    public String complexidade() { return "O(1)*"; }
}
