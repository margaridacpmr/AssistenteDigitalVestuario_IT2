package org.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

/** UC13: a escala de 1 a 5 é uma opção de implementação. */
public class Avaliacao implements Serializable {
    private static final long serialVersionUID = 1L;
    private int valor;
    private LocalDateTime data;

    public Avaliacao(int valor) { atualizar(valor); }
    public void atualizar(int valor) {
        if (valor < 1 || valor > 5) {
            throw new IllegalArgumentException("A avaliação deve ser entre 1 e 5.");
        }
        this.valor = valor;
        this.data = LocalDateTime.now();
    }
    public int getValor() { return valor; }
    public LocalDateTime getData() { return data; }
}
