package org.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** O guarda-roupa pertence a um único utilizador. */
public class GuardaRoupa implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ArrayList<PecaVestuario> pecas = new ArrayList<>();

    public List<PecaVestuario> getPecas() { return new ArrayList<>(pecas); }
    public void adicionar(PecaVestuario peca) { pecas.add(peca); }
    public void remover(PecaVestuario peca) { pecas.remove(peca); }

    /** O nome deve ser único dentro do guarda-roupa. */
    public boolean temNome(String nome, PecaVestuario pecaIgnorada) {
        for (PecaVestuario peca : pecas) {
            if (peca != pecaIgnorada && peca.getNome().equalsIgnoreCase(nome.trim())) {
                return true;
            }
        }
        return false;
    }
}
