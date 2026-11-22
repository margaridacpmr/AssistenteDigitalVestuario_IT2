package org.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** UC16: uma combinação aprovada de cores do catálogo. */
public class CombinacaoCores implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String designacao;
    private final ArrayList<Cor> cores;

    public CombinacaoCores(int id, String designacao, List<Cor> cores) {
        if (designacao == null || designacao.isBlank()) {
            throw new IllegalArgumentException("designacaoCoresObrigatoria");
        }
        if (cores == null || cores.size() < 2) {
            throw new IllegalArgumentException("minimoDuasCores");
        }
        this.cores = new ArrayList<>();
        for (Cor cor : cores) {
            if (cor == null) {
                throw new IllegalArgumentException("corInexistente");
            }
            if (this.cores.contains(cor)) {
                throw new IllegalArgumentException("corRepetida");
            }
            this.cores.add(cor);
        }
        this.id = id;
        this.designacao = designacao.trim();
    }

    public int getId() { return id; }
    public String getDesignacao() { return designacao; }
    public List<Cor> getCores() { return new ArrayList<>(cores); }

    public boolean temMesmasCores(List<Cor> outras) {
        return cores.containsAll(outras) && outras.containsAll(cores);
    }

    @Override
    public String toString() { return designacao + " " + cores; }
}
