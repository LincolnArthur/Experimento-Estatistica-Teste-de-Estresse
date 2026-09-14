package Experimento;

// Lista encadeada simples. Cada inserção vira o novo primeiro nó.
public class ListaEncadeada implements EstruturaBusca {
    private class No {
        int valor;
        No proximo;
        No(int valor) { this.valor = valor; }
    }

    private No cabeca;

    public void inserir(int valor) {
        No novo = new No(valor);
        novo.proximo = cabeca;
        cabeca = novo;
    }

    public boolean buscar(int chave) {
        No atual = cabeca;
        while (atual != null) {
            if (atual.valor == chave) return true;
            atual = atual.proximo;
        }
        return false;
    }

    public String nome() { return "ListaEncadeada"; }

    public String complexidade() { return "O(n)"; }
}
