package org.controladores;

import org.modelo.*;
import java.time.LocalDateTime;
import java.util.Locale;

/** UC11, configuração da conta administrativa e autenticação. */
public class AutenticacaoController {
    private final DadosAplicacao dados;

    public AutenticacaoController(DadosAplicacao dados) { this.dados = dados; }

    public Utilizador registar(String nome, String email, String palavraPasse) {
        validarRegisto(nome, email, palavraPasse);
        String sal = Seguranca.criarSal();
        Utilizador utilizador = new Utilizador(dados.novoId(), nome.trim(), normalizarEmail(email),
                sal, Seguranca.calcularHash(palavraPasse, sal));
        dados.adicionarUtilizador(utilizador);
        return utilizador;
    }

    public Administrador registarAdministrador(String nome, String email, String palavraPasse) {
        if (dados.existeAdministrador()) {
            throw new IllegalArgumentException("administradorExistente");
        }
        validarRegisto(nome, email, palavraPasse);
        String sal = Seguranca.criarSal();
        Administrador admin = new Administrador(dados.novoId(), nome.trim(), LocalDateTime.now(),
                normalizarEmail(email), sal, Seguranca.calcularHash(palavraPasse, sal));
        dados.adicionarAdministrador(admin);
        return admin;
    }

    public Conta entrar(String email, String palavraPasse) {
        if (email == null || palavraPasse == null) { return null; }
        Conta conta = procurarConta(email);
        if (conta == null || !Seguranca.verificarPalavraPasse(palavraPasse,
                conta.getSalPalavraPasse(), conta.getHashPalavraPasse())) { return null; }
        return conta;
    }

    private void validarRegisto(String nome, String email, String palavraPasse) {
        if (nome == null || nome.isBlank() || email == null || email.isBlank()
                || !email.contains("@") || palavraPasse == null || palavraPasse.isBlank()) {
            throw new IllegalArgumentException("dadosRegistoInvalidos");
        }
        if (procurarConta(email) != null) {
            throw new IllegalArgumentException("emailExistente");
        }
    }

    private Conta procurarConta(String email) {
        String procurado = email.trim();
        for (Administrador admin : dados.getAdministradores()) {
            if (admin.getEmail().equalsIgnoreCase(procurado)) { return admin; }
        }
        for (Utilizador utilizador : dados.getUtilizadores()) {
            if (utilizador.getEmail().equalsIgnoreCase(procurado)) { return utilizador; }
        }
        return null;
    }

    private String normalizarEmail(String email) { return email.trim().toLowerCase(Locale.ROOT); }
}
