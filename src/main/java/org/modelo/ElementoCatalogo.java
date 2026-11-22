package org.modelo;

import java.io.Serializable;

/** Superclasse simples dos quatro catálogos administrados. */
public abstract class ElementoCatalogo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String designacao;

    public ElementoCatalogo(int id, String designacao) {
        if (designacao == null || designacao.isBlank()) {
            throw new IllegalArgumentException("designacaoObrigatoria");
        }
        this.id = id;
        this.designacao = designacao.trim();
    }

    public int getId() { return id; }
    public String getDesignacao() { return designacao; }

    @Override
    public boolean equals(Object outro) {
        return outro != null && getClass() == outro.getClass()
                && id == ((ElementoCatalogo) outro).id;
    }

    @Override
    public int hashCode() { return 31 * getClass().hashCode() + id; }

    @Override
    public String toString() { return designacao; }
}
