package org.modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/** Conta pessoal registada no UC11. */
public class Utilizador implements Conta, Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nome;
    private LocalDateTime dataRegisto;
    private String email;
    private String salPalavraPasse;
    private String hashPalavraPasse;
    private GuardaRoupa guardaRoupa = new GuardaRoupa();
    private ArrayList<PedidoOutfit> pedidos = new ArrayList<>();

    private transient Administrador administradorLegado;

    public Utilizador(int id, String nome, String email, String sal, String hash) {
        this.id = id;
        this.nome = nome;
        this.dataRegisto = LocalDateTime.now();
        this.email = email;
        this.salPalavraPasse = sal;
        this.hashPalavraPasse = hash;
    }
    public int getId() { return id; }
    public String getNome() { return nome; }
    public LocalDateTime getDataRegisto() { return dataRegisto; }
    public String getEmail() { return email; }
    public String getSalPalavraPasse() { return salPalavraPasse; }
    public String getHashPalavraPasse() { return hashPalavraPasse; }
    public GuardaRoupa getGuardaRoupa() { return guardaRoupa; }
    public List<PedidoOutfit> getPedidos() { return new ArrayList<>(pedidos); }
    public void adicionarPedido(PedidoOutfit pedido) { pedidos.add(pedido); }

    Administrador getAdministradorLegado() { return administradorLegado; }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream entrada) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField campos = entrada.readFields();
        id = campos.get("id", 0);
        nome = (String) campos.get("nome", null);
        dataRegisto = (LocalDateTime) campos.get("dataRegisto", null);
        email = (String) campos.get("email", null);
        salPalavraPasse = (String) campos.get("salPalavraPasse", null);
        hashPalavraPasse = (String) campos.get("hashPalavraPasse", null);
        guardaRoupa = (GuardaRoupa) campos.get("guardaRoupa", null);
        pedidos = (ArrayList<PedidoOutfit>) campos.get("pedidos", null);

        // O campo antigo é usado apenas durante a leitura do formato legado.
        if (campos.getObjectStreamClass().getField("administrador") != null
                && campos.get("administrador", false)) {
            administradorLegado = new Administrador(id, nome, dataRegisto, email, salPalavraPasse, hashPalavraPasse);
        }
    }
}
