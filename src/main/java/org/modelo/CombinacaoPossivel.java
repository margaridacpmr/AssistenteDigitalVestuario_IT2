package org.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/** UC15: cada regra contém pelo menos dois tipos de roupa distintos. */
public class CombinacaoPossivel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String designacao;
    private final ArrayList<TipoRoupa> tiposRoupa;
    private final boolean ativa = true; // Apenas regras ativas são usadas no UC6.

    public CombinacaoPossivel(int id, String designacao, List<TipoRoupa> tipos) {
        if (designacao == null || designacao.isBlank() || tipos == null || tipos.size() < 2) {
            throw new IllegalArgumentException("A combinação precisa de nome e pelo menos dois tipos.");
        }
        for (int i = 0; i < tipos.size(); i++) {
            for (int j = i + 1; j < tipos.size(); j++) {
                if (Objects.equals(tipos.get(i), tipos.get(j))) {
                    throw new IllegalArgumentException("Uma combinação não pode repetir tipos.");
                }
            }
        }
        this.id = id;
        this.designacao = designacao.trim();
        this.tiposRoupa = new ArrayList<>(tipos);
    }

    public boolean temTiposValidos() {
        return tiposRoupa.size() >= 2 && !tiposRoupa.contains(null)
                && new HashSet<>(tiposRoupa).size() == tiposRoupa.size();
    }

    public boolean isAtiva() { return ativa; }
    public int getId() { return id; }
    public String getDesignacao() { return designacao; }
    public List<TipoRoupa> getTiposRoupa() { return new ArrayList<>(tiposRoupa); }
    @Override
    public String toString() { return designacao + " " + tiposRoupa; }
}
