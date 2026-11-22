package org.controladores;

import org.modelo.*;
import java.util.List;

/** Coordena UC1, UC2, UC3, UC4 e UC5. */
public class GuardaRoupaController {
    private final DadosAplicacao dados;

    public GuardaRoupaController(DadosAplicacao dados) { this.dados = dados; }

    private void verificarDono(Utilizador utilizador, PecaVestuario peca) {
        if (utilizador == null || utilizador.isAdministrador()
                || !utilizador.getGuardaRoupa().getPecas().contains(peca)) {
            throw new IllegalArgumentException("Esta peça não pertence ao utilizador.");
        }
    }

    public PecaVestuario adicionar(Utilizador utilizador, String nome, TipoRoupa tipo,
                                    List<Cor> cores, List<Estacao> estacoes, List<Ocasiao> ocasioes) {
        if (utilizador == null || utilizador.isAdministrador()) {
            throw new IllegalArgumentException("Inicie sessão como utilizador.");
        }
        validarCatalogos(tipo, cores, estacoes, ocasioes);
        if (nome == null || nome.isBlank() || utilizador.getGuardaRoupa().temNome(nome, null)) {
            throw new IllegalArgumentException("O nome é obrigatório e deve ser único.");
        }
        PecaVestuario peca = new PecaVestuario(dados.novoId(), nome, tipo, cores, estacoes, ocasioes);
        utilizador.getGuardaRoupa().adicionar(peca);
        return peca;
    }

    public void editar(Utilizador u, PecaVestuario peca, String nome, TipoRoupa tipo,
                        List<Cor> cores, List<Estacao> estacoes, List<Ocasiao> ocasioes) {
        verificarDono(u, peca);
        validarCatalogos(tipo, cores, estacoes, ocasioes);
        if (nome == null || nome.isBlank() || u.getGuardaRoupa().temNome(nome, peca)) {
            throw new IllegalArgumentException("O nome é obrigatório e deve ser único.");
        }
        peca.atualizar(nome, tipo, cores, estacoes, ocasioes);
    }

    public boolean tornarDisponivel(Utilizador u, PecaVestuario peca, boolean confirmado) {
        verificarDono(u, peca);
        if (!confirmado || peca.isDisponivel()) { return false; }
        peca.tornarDisponivel();
        return true;
    }

    public void remover(Utilizador u, PecaVestuario peca) {
        verificarDono(u, peca);
        u.getGuardaRoupa().remover(peca);
    }

    public void marcarIndisponivel(Utilizador u, PecaVestuario peca) {
        verificarDono(u, peca);
        if (!peca.isDisponivel()) {
            throw new IllegalArgumentException("A peça já está indisponível.");
        }
        peca.marcarIndisponivel();
    }

    private void validarCatalogos(TipoRoupa tipo, List<Cor> cores,
                                  List<Estacao> estacoes, List<Ocasiao> ocasioes) {
        if (tipo == null || !dados.getTiposRoupa().contains(tipo) || cores == null || cores.isEmpty()
                || estacoes == null || estacoes.isEmpty() || ocasioes == null || ocasioes.isEmpty()
                || !dados.getCores().containsAll(cores) || !dados.getEstacoes().containsAll(estacoes)
                || !dados.getOcasioes().containsAll(ocasioes)) {
            throw new IllegalArgumentException("Selecione valores existentes em todos os catálogos.");
        }
    }
}
