package org.controladores;

import org.modelo.DadosAplicacao;
import org.modelo.Utilizador;

/** UC11 + controlo de acesso às operações pessoais e de administração. */
public class AutenticacaoController {
    private final DadosAplicacao dados;

    public AutenticacaoController(DadosAplicacao dados) { this.dados = dados; }

    public Utilizador registar(String nome, String email, String palavraPasse, boolean admin) {
        if (nome == null || nome.isBlank() || email == null || email.isBlank()
                || !email.contains("@") || palavraPasse == null || palavraPasse.isBlank()) {
            throw new IllegalArgumentException("Preencha nome, email válido e palavra-passe.");
        }
        // O administrador só pode ser criado na configuração inicial.
        if (admin && dados.existeAdministrador()) {
            throw new IllegalArgumentException("Já existe um administrador.");
        }
        for (Utilizador u : dados.getUtilizadores()) {
            if (u.getEmail().equalsIgnoreCase(email.trim())) {
                throw new IllegalArgumentException("Já existe uma conta com esse email.");
            }
        }
        String sal = Seguranca.criarSal();
        String hash = Seguranca.calcularHash(palavraPasse, sal);
        Utilizador utilizador = new Utilizador(dados.novoId(), nome.trim(),
                email.trim().toLowerCase(), sal, hash, admin);
        dados.adicionarUtilizador(utilizador);
        return utilizador;
    }

    public Utilizador entrar(String email, String palavraPasse) {
        if (email == null || palavraPasse == null) { return null; }
        for (Utilizador u : dados.getUtilizadores()) {
            if (u.getEmail().equalsIgnoreCase(email.trim())
                    && u.getHashPalavraPasse().equals(
                        Seguranca.calcularHash(palavraPasse, u.getSalPalavraPasse()))) {
                return u;
            }
        }
        return null;
    }
}
