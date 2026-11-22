package org.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/** Um outfit guarda as peças selecionadas e a regra que as combinou. */
public class Outfit implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String nome;
    private final boolean favorito = false;
    private final LocalDateTime dataRecomendacao;
    private final ArrayList<PecaVestuario> pecas;
    private final CombinacaoPossivel combinacao;
    private final CombinacaoCores combinacaoCores;
    private Avaliacao avaliacao;

    public Outfit(int id, List<PecaVestuario> pecas, CombinacaoPossivel combinacao) {
        this(id, pecas, combinacao, null);
    }

    public Outfit(int id, List<PecaVestuario> pecas, CombinacaoPossivel combinacao,
                  CombinacaoCores combinacaoCores) {
        if (pecas == null || pecas.size() < 2 || combinacao == null) {
            throw new IllegalArgumentException("outfitCombinacaoObrigatoria");
        }
        this.id = id;
        this.nome = "Outfit " + id;
        this.dataRecomendacao = LocalDateTime.now();
        this.pecas = new ArrayList<>(pecas);
        this.combinacao = combinacao;
        this.combinacaoCores = combinacaoCores;
    }

    public void avaliar(int valor) {
        if (avaliacao == null) { avaliacao = new Avaliacao(valor); }
        else { avaliacao.atualizar(valor); }
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public boolean isFavorito() { return favorito; }
    public LocalDateTime getDataRecomendacao() { return dataRecomendacao; }
    public List<PecaVestuario> getPecas() { return new ArrayList<>(pecas); }
    public CombinacaoPossivel getCombinacao() { return combinacao; }
    public CombinacaoCores getCombinacaoCores() { return combinacaoCores; }
    public Avaliacao getAvaliacao() { return avaliacao; }
}
