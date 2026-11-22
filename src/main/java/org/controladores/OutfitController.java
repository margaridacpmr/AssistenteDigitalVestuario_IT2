package org.controladores;

import org.modelo.*;
import java.util.List;

/** Coordena UC6, UC13 e UC14. */
public class OutfitController {
    private final DadosAplicacao dados;
    private final GeradorOutfits gerador = new GeradorOutfits();

    public OutfitController(DadosAplicacao dados) { this.dados = dados; }

    public PedidoOutfit solicitar(Utilizador utilizador, Ocasiao ocasiao, Estacao estacao) {
        if (utilizador == null
                || !dados.getOcasioes().contains(ocasiao) || !dados.getEstacoes().contains(estacao)) {
            throw new IllegalArgumentException("pedidoInvalido");
        }
        GeradorOutfits.Selecao selecao = gerador.procurarOutfit(utilizador.getGuardaRoupa().getPecas(),
                dados.getCombinacoes(), ocasiao, estacao, dados.getCombinacoesCores());
        if (selecao == null) { return null; }

        Outfit outfit = new Outfit(dados.novoId(), selecao.pecas(), selecao.regra(), selecao.paleta());
        PedidoOutfit pedido = new PedidoOutfit(dados.novoId(), ocasiao, estacao, outfit);
        utilizador.adicionarPedido(pedido);
        return pedido;
    }

    /** Identifica até onde pelo menos uma regra ativa conseguiu ser satisfeita. */
    public String motivoFalha(Utilizador utilizador, Ocasiao ocasiao, Estacao estacao) {
        List<CombinacaoPossivel> regras = dados.getCombinacoes();
        regras.removeIf(regra -> !regra.isAtiva());
        if (regras.isEmpty()) { return "semRegrasOutfit"; }
        regras.removeIf(regra -> !regra.temTiposValidos());
        if (regras.isEmpty()) { return "semCombinacao"; }

        List<PecaVestuario> pecas = utilizador.getGuardaRoupa().getPecas();
        regras.removeIf(regra -> gerador.procurarPecas(pecas, regra, peca -> true) == null);
        if (regras.isEmpty()) { return "semTiposOutfit"; }

        pecas.removeIf(peca -> !peca.isDisponivel());
        regras.removeIf(regra -> gerador.procurarPecas(pecas, regra, peca -> true) == null);
        if (regras.isEmpty()) { return "semDisponibilidadeOutfit"; }

        pecas.removeIf(peca -> !peca.incluiOcasiao(ocasiao));
        regras.removeIf(regra -> gerador.procurarPecas(pecas, regra, peca -> true) == null);
        if (regras.isEmpty()) { return "semOcasiaoOutfit"; }

        pecas.removeIf(peca -> !peca.incluiEstacao(estacao));
        regras.removeIf(regra -> gerador.procurarPecas(pecas, regra, peca -> true) == null);
        return regras.isEmpty() ? "semEstacaoOutfit" : "semCoresOutfit";
    }

    public void avaliar(Utilizador utilizador, PedidoOutfit pedido, int valor) {
        if (utilizador == null || pedido == null || !utilizador.getPedidos().contains(pedido)) {
            throw new IllegalArgumentException("avaliacaoDeOutroUtilizador");
        }
        pedido.getOutfit().avaliar(valor);
    }

    public List<PedidoOutfit> consultarHistorico(Utilizador utilizador) {
        if (utilizador == null) {
            throw new IllegalArgumentException("sessaoUtilizadorObrigatoria");
        }
        return utilizador.getPedidos();
    }
}
