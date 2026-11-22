package org.controladores;

import org.modelo.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/** Responsabilidade única: encontrar peças que cumprem uma regra (UC6). */
public class GeradorOutfits {
    private final Random sorteio = new Random();

    record Selecao(List<PecaVestuario> pecas, CombinacaoPossivel regra, CombinacaoCores paleta) { }

    Selecao procurarOutfit(List<PecaVestuario> pecas, List<CombinacaoPossivel> regras,
                          Ocasiao ocasiao, Estacao estacao, List<CombinacaoCores> paletas) {
        Selecao alternativa = null;
        List<List<PecaVestuario>> candidatasAlternativa = null;
        for (CombinacaoPossivel regra : regras) {
            if (!regra.isAtiva() || !regra.temTiposValidos()) { continue; }
            ArrayList<List<PecaVestuario>> candidatas = new ArrayList<>();
            for (TipoRoupa tipo : regra.getTiposRoupa()) {
                ArrayList<PecaVestuario> doTipo = new ArrayList<>();
                for (PecaVestuario peca : pecas) {
                    if (peca.getTipoRoupa().equals(tipo) && peca.adequada(ocasiao, estacao)) {
                        doTipo.add(peca);
                    }
                }
                if (doTipo.isEmpty()) { break; }
                candidatas.add(doTipo);
            }
            if (candidatas.size() != regra.getTiposRoupa().size()) { continue; }

            for (CombinacaoCores paleta : paletas) {
                List<PecaVestuario> selecao = procurarPorCores(candidatas, paleta.getCores(), false);
                if (selecao != null) { return criarSelecao(candidatas, selecao, regra, paleta); }
            }
            if (alternativa == null) {
                for (CombinacaoCores paleta : paletas) {
                    List<PecaVestuario> selecao = procurarPorCores(candidatas, paleta.getCores(), true);
                    if (selecao != null) {
                        alternativa = new Selecao(selecao, regra, paleta);
                        candidatasAlternativa = candidatas;
                        break;
                    }
                }
            }
        }
        if (alternativa == null) { return null; }
        return criarSelecao(candidatasAlternativa, alternativa.pecas(), alternativa.regra(), alternativa.paleta());
    }

    private List<PecaVestuario> procurarPorCores(List<List<PecaVestuario>> candidatas,
                                                List<Cor> paleta, boolean permitirMaisCores) {
        if (!permitirMaisCores && paleta.size() > 3) { return null; }
        ArrayList<PecaVestuario> escolhidas = new ArrayList<>();
        if (completar(candidatas, paleta, permitirMaisCores, escolhidas, new ArrayList<>(), false)) {
            return escolhidas;
        }
        return null;
    }

    /** Experimenta uma peça de cada tipo e recua quando a escolha não completa a paleta. */
    private boolean completar(List<List<PecaVestuario>> candidatas, List<Cor> paleta,
                             boolean permitirMaisCores, ArrayList<PecaVestuario> escolhidas,
                             List<Cor> coresUsadas, boolean temMulticolorida) {
        if (escolhidas.size() == candidatas.size()) {
            return coresUsadas.containsAll(paleta)
                    && (!permitirMaisCores || (coresUsadas.size() > 3 && temMulticolorida));
        }
        ArrayList<List<Cor>> coresTentadas = new ArrayList<>();
        for (PecaVestuario peca : candidatas.get(escolhidas.size())) {
            ArrayList<Cor> coresPeca = new ArrayList<>();
            for (Cor cor : peca.getCores()) {
                if (!coresPeca.contains(cor)) { coresPeca.add(cor); }
            }
            boolean multicolorida = coresPeca.size() > 1;
            if (!paleta.containsAll(coresPeca) && (!permitirMaisCores || !multicolorida)) { continue; }

            boolean jaTentadas = false;
            for (List<Cor> anteriores : coresTentadas) {
                if (mesmasCores(anteriores, coresPeca)) {
                    jaTentadas = true;
                    break;
                }
            }
            if (jaTentadas) { continue; }
            coresTentadas.add(coresPeca);

            ArrayList<Cor> uniao = new ArrayList<>(coresUsadas);
            for (Cor cor : coresPeca) {
                if (!uniao.contains(cor)) { uniao.add(cor); }
            }
            escolhidas.add(peca);
            if (completar(candidatas, paleta, permitirMaisCores, escolhidas, uniao,
                    temMulticolorida || multicolorida)) { return true; }
            escolhidas.remove(escolhidas.size() - 1);
        }
        return false;
    }

    private Selecao criarSelecao(List<List<PecaVestuario>> candidatas, List<PecaVestuario> escolhidas,
                                CombinacaoPossivel regra, CombinacaoCores paleta) {
        ArrayList<PecaVestuario> resultado = new ArrayList<>();
        for (int i = 0; i < escolhidas.size(); i++) {
            List<Cor> cores = escolhidas.get(i).getCores();
            ArrayList<PecaVestuario> equivalentes = new ArrayList<>();
            for (PecaVestuario peca : candidatas.get(i)) {
                if (mesmasCores(cores, peca.getCores())) { equivalentes.add(peca); }
            }
            resultado.add(equivalentes.get(sorteio.nextInt(equivalentes.size())));
        }
        return new Selecao(resultado, regra, paleta);
    }

    private boolean mesmasCores(List<Cor> primeira, List<Cor> segunda) {
        return primeira.containsAll(segunda) && segunda.containsAll(primeira);
    }

    /**
     * Devolve uma escolha ou null se nenhuma combinação funcionar.
     * REGRA UC6: todas as peças do mesmo outfit têm de ser adequadas à MESMA
     * ocasião e à MESMA estação que o utilizador selecionou para este pedido.
     * Uma peça pode ter várias ocasiões/estações, mas deve incluir ambas as escolhidas.
     */
    public List<PecaVestuario> procurarPecas(List<PecaVestuario> pecas,
              CombinacaoPossivel regra, Ocasiao ocasiao, Estacao estacao) {
        return procurarPecas(pecas, regra, peca -> peca.adequada(ocasiao, estacao));
    }

    List<PecaVestuario> procurarPecas(List<PecaVestuario> pecas,
              CombinacaoPossivel regra, Predicate<PecaVestuario> elegivel) {
        if (!regra.temTiposValidos()) { return null; }
        ArrayList<PecaVestuario> selecionadas = new ArrayList<>();
        for (TipoRoupa tipoNecessario : regra.getTiposRoupa()) {
            PecaVestuario encontrada = null;
            for (PecaVestuario peca : pecas) {
                if (peca.getTipoRoupa().equals(tipoNecessario)
                        && elegivel.test(peca)
                        && !selecionadas.contains(peca)) {
                    encontrada = peca;
                    break;
                }
            }
            if (encontrada == null) { return null; }
            selecionadas.add(encontrada);
        }
        return selecionadas;
    }
}
