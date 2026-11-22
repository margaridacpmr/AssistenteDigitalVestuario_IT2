package org.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

public final class Administrador implements Conta, Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String nome;
    private final LocalDateTime dataRegisto;
    private final String email;
    private final String salPalavraPasse;
    private final String hashPalavraPasse;

    public Administrador(int id, String nome, LocalDateTime dataRegisto,
                         String email, String salPalavraPasse, String hashPalavraPasse) {
        this.id = id;
        this.nome = nome;
        this.dataRegisto = dataRegisto;
        this.email = email;
        this.salPalavraPasse = salPalavraPasse;
        this.hashPalavraPasse = hashPalavraPasse;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public LocalDateTime getDataRegisto() { return dataRegisto; }
    public String getEmail() { return email; }
    public String getSalPalavraPasse() { return salPalavraPasse; }
    public String getHashPalavraPasse() { return hashPalavraPasse; }
}
