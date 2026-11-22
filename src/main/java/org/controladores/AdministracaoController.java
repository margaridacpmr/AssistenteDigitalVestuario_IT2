package org.controladores;

import org.modelo.*;
import java.util.ArrayList;
import java.util.List;

/** UC7, UC8, UC9, UC12, UC15 e UC16. */
public class AdministracaoController {
    private final DadosAplicacao dados;

    public AdministracaoController(DadosAplicacao dados) { this.dados = dados; }

    private void confirmarAdministrador(Utilizador u) {
        if (u == null || !u.isAdministrador()) {
            throw new IllegalArgumentException("Operação reservada ao administrador.");
        }
    }

    /** Valida toda a lista antes de guardar qualquer elemento. */
    public void adicionarCatalogo(Utilizador admin, String tipo, List<String> designacoes) {
        confirmarAdministrador(admin);
        if (designacoes == null || designacoes.isEmpty()) {
            throw new IllegalArgumentException("A lista não pode estar vazia.");
        }
        List<? extends ElementoCatalogo> existentes;
        switch (tipo) {
            case "cor": existentes = dados.getCores(); break;
            case "estacao": existentes = dados.getEstacoes(); break;
            case "ocasiao": existentes = dados.getOcasioes(); break;
            case "tipo": existentes = dados.getTiposRoupa(); break;
            default: throw new IllegalArgumentException("Catálogo desconhecido.");
        }
        for (int i = 0; i < designacoes.size(); i++) {
            String nome = designacoes.get(i);
            if (nome == null || nome.isBlank()) {
                throw new IllegalArgumentException("Nenhuma designação pode estar vazia.");
            }
            for (ElementoCatalogo existente : existentes) {
                if (existente.getDesignacao().equalsIgnoreCase(nome.trim())) {
                    throw new IllegalArgumentException("A designação já existe: " + nome);
                }
            }
            for (int j = 0; j < i; j++) {
                if (designacoes.get(j).trim().equalsIgnoreCase(nome.trim())) {
                    throw new IllegalArgumentException("Designação repetida no lote: " + nome);
                }
            }
        }
        // Só aqui, depois da validação do lote inteiro, se altera o estado.
        for (String nome : designacoes) {
            int id = dados.novoId();
            switch (tipo) {
                case "cor": dados.adicionarCor(new Cor(id, nome)); break;
                case "estacao": dados.adicionarEstacao(new Estacao(id, nome)); break;
                case "ocasiao": dados.adicionarOcasiao(new Ocasiao(id, nome)); break;
                case "tipo": dados.adicionarTipoRoupa(new TipoRoupa(id, nome)); break;
                default: break;
            }
        }
    }

    public CombinacaoPossivel definirCombinacao(Utilizador admin, String nome,
                                                 List<TipoRoupa> tipos) {
        confirmarAdministrador(admin);
        if (nome == null || nome.isBlank() || tipos == null || tipos.size() < 2) {
            throw new IllegalArgumentException("Indique o nome e pelo menos dois tipos distintos.");
        }
        for (CombinacaoPossivel c : dados.getCombinacoes()) {
            if (c.getDesignacao().equalsIgnoreCase(nome.trim())) {
                throw new IllegalArgumentException("Já existe uma combinação com esse nome.");
            }
            if (mesmosTipos(c.getTiposRoupa(), tipos)) {
                throw new IllegalArgumentException("Já existe uma combinação com esses tipos.");
            }
        }
        for (TipoRoupa tipo : tipos) {
            if (tipo == null || !dados.getTiposRoupa().contains(tipo)) {
                throw new IllegalArgumentException("Tipo de roupa inexistente.");
            }
        }
        // O construtor também valida tipos repetidos.
        CombinacaoPossivel combinacao = new CombinacaoPossivel(dados.novoId(), nome, tipos);
        dados.adicionarCombinacao(combinacao);
        return combinacao;
    }

    public CombinacaoCores definirCombinacaoCores(Utilizador admin, String nome, List<Cor> cores) {
        confirmarAdministrador(admin);
        return dados.adicionarCombinacaoCores(nome, cores);
    }

    private boolean mesmosTipos(List<TipoRoupa> a, List<TipoRoupa> b) {
        return a.size() == b.size() && a.containsAll(b) && b.containsAll(a);
    }
}
