package Experimento;

// Árvore binária de busca, sem balanceamento.
public class ArvoreBST implements EstruturaBusca {
    private class No {
        int valor;
        No esquerda, direita;
        No(int valor) { this.valor = valor; }
    }

    private No raiz;

    public void inserir(int valor) {
        raiz = inserirRec(raiz, valor);
    }

    private No inserirRec(No no, int valor) {
        if (no == null) return new No(valor);
        if (valor < no.valor) no.esquerda = inserirRec(no.esquerda, valor);
        else no.direita = inserirRec(no.direita, valor);
        return no;
    }

    public boolean buscar(int chave) {
        No atual = raiz;
        while (atual != null) {
            if (atual.valor == chave) return true;
            atual = (chave < atual.valor) ? atual.esquerda : atual.direita;
        }
        return false;
    }

    public String nome() { return "ArvoreBST"; }

    // Caso médio O(log n); no pior caso (dados já ordenados, árvore vira uma lista) é O(n).
    public String complexidade() { return "O(log n)*"; }
}
