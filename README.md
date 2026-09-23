# Experimento-Estatistica-Teste-de-Estresse

Análise de teste de estresse em diferentes estruturas de dados ao se tratar de um alto número de dados.

Trabalho da disciplina de Estatística e Probabilidade para Computação (Univasf, atividade EPC::T1,
Prof. Brauliro Gonçalves Leal). Cinco estruturas de dados implementadas em Java (array com busca
linear, array ordenado com busca binária, lista encadeada, árvore binária de busca e tabela hash)
são submetidas a volumes crescentes de dados (1.000 a 500.000 elementos), medindo tempo de
inserção, tempo de busca e memória utilizada. Os resultados são analisados com estatística
descritiva (média, mediana, desvio padrão, coeficiente de variação, distribuição de frequência).

## Estrutura da pasta

- `Experimento/` — projeto Java (NetBeans/Ant). Código-fonte em `src/Experimento/`.
- `dados/` — arquivos CSV com os dados brutos:
  - `resultados_estresse.csv` — dataset atual (o programa grava/acrescenta aqui).
  - `resultados.csv` — dataset histórico (medições anteriores, só tempo de busca).
- `docs/` — relatório técnico final (`relatorio_tecnico.docx`).

## Como rodar

A partir da pasta `Experimento/` (importante: o programa grava o CSV em `../dados/`, relativo a
essa pasta):

```bash
cd Experimento
javac -d build/classes src/Experimento/*.java
java -cp build/classes Experimento.Experimento
```

No menu, escolha a opção `2` para rodar a bateria completa do teste de estresse. Os resultados
são acrescentados a `dados/resultados_estresse.csv` (nunca sobrescreve o que já tem).

Também dá pra rodar direto pelo NetBeans, ou pelo comando `ant run` de dentro de `Experimento/`.
