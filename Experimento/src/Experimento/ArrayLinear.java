package Experimento;

// Array simples, sem ordenação. Busca percorre item por item.
public class ArrayLinear implements EstruturaBusca {
    private int[] dados;
    private int tamanho = 0;

    public ArrayLinear(int capacidade) {
        dados = new int[capacidade];
    }

    public void inserir(int valor) {
        dados[tamanho++] = valor;
    }

    public boolean buscar(int chave) {
        for (int i = 0; i < tamanho; i++) {
            if (dados[i] == chave) return true;
        }
        return false;
    }

    public String nome() { return "ArrayLinear"; }

    public String complexidade() { return "O(n)"; }
}
