package org.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Regista um pedido bem-sucedido e a recomendação associada (UC6 e UC14). */
public class PedidoOutfit implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final LocalDateTime data;
    private final Ocasiao ocasiao;
    private final Estacao estacao;
    private final Outfit outfit;
    private final String estado;

    public PedidoOutfit(int id, Ocasiao ocasiao, Estacao estacao, Outfit outfit) {
        this.id = id;
        this.data = LocalDateTime.now();
        this.ocasiao = ocasiao;
        this.estacao = estacao;
        this.outfit = outfit;
        this.estado = "Gerado";
    }
    public int getId() { return id; }
    public LocalDateTime getData() { return data; }
    public Ocasiao getOcasiao() { return ocasiao; }
    public Estacao getEstacao() { return estacao; }
    public Outfit getOutfit() { return outfit; }
    public String getEstado() { return estado; }
}
