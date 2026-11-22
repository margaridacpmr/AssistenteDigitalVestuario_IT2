package org.modelo;

/** Dados comuns usados na autenticação das duas contas. */
public interface Conta {
    int getId();
    String getNome();
    String getEmail();
    String getSalPalavraPasse();
    String getHashPalavraPasse();
}
