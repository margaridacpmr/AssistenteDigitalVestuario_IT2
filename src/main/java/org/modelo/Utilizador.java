package org.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/** Registado por UC11. A função de administrador é indicada por um boolean. */
public class Utilizador implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String nome;
    private final LocalDateTime dataRegisto;
    private final String email;
    private final String salPalavraPasse;
    private final String hashPalavraPasse;
    private final boolean administrador;
    private final GuardaRoupa guardaRoupa = new GuardaRoupa();
    private final ArrayList<PedidoOutfit> pedidos = new ArrayList<>();

    public Utilizador(int id, String nome, String email, String sal, String hash, boolean admin) {
        this.id = id;
        this.nome = nome;
        this.dataRegisto = LocalDateTime.now();
        this.email = email;
        this.salPalavraPasse = sal;
        this.hashPalavraPasse = hash;
        this.administrador = admin;
    }
    public int getId() { return id; }
    public String getNome() { return nome; }
    public LocalDateTime getDataRegisto() { return dataRegisto; }
    public String getEmail() { return email; }
    public String getSalPalavraPasse() { return salPalavraPasse; }
    public String getHashPalavraPasse() { return hashPalavraPasse; }
    public boolean isAdministrador() { return administrador; }
    public GuardaRoupa getGuardaRoupa() { return guardaRoupa; }
    public List<PedidoOutfit> getPedidos() { return new ArrayList<>(pedidos); }
    public void adicionarPedido(PedidoOutfit pedido) { pedidos.add(pedido); }
}
