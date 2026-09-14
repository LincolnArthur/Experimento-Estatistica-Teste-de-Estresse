package Experimento;

import java.util.Arrays;

// Array ordenado, com busca binária.
public class ArrayBinario implements EstruturaBusca {
    private int[] dados;
    private int tamanho = 0;

    public ArrayBinario(int capacidade) {
        dados = new int[capacidade];
    }

    public void inserir(int valor) {
        // Só acrescenta no final (O(1)). Ordenar a cada inserção seria O(n)
        // por chamada, ou seja O(n²) no total, o que inviabilizaria o teste
        // com volumes grandes.
        dados[tamanho++] = valor;
    }

    public void finalizarInsercoes() {
        // Ordena uma única vez, depois de todas as inserções: O(n log n) no total.
        Arrays.sort(dados, 0, tamanho);
    }

    public boolean buscar(int chave) {
        int lo = 0, hi = tamanho - 1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            if (dados[mid] == chave) return true;
            else if (dados[mid] < chave) lo = mid + 1;
            else hi = mid - 1;
        }
        return false;
    }

    public String nome() { return "ArrayBinario"; }

    public String complexidade() { return "O(log n)"; }
}
