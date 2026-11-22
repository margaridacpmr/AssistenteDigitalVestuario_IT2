package org.modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Tudo o que é persistido num único ficheiro de dados. */
public class DadosAplicacao implements Serializable {
    private static final long serialVersionUID = 1L;
    private int proximoId = 1;
    private ArrayList<Administrador> administradores = new ArrayList<>();
    private final ArrayList<Utilizador> utilizadores = new ArrayList<>();
    private final ArrayList<TipoRoupa> tiposRoupa = new ArrayList<>();
    private final ArrayList<Cor> cores = new ArrayList<>();
    private final ArrayList<Estacao> estacoes = new ArrayList<>();
    private final ArrayList<Ocasiao> ocasioes = new ArrayList<>();
    private final ArrayList<CombinacaoPossivel> combinacoes = new ArrayList<>();
    private ArrayList<CombinacaoCores> combinacoesCores = new ArrayList<>();


    /**
     * Os catálogos já começam com valores úteis, mesmo sem o administrador
     * acrescentar nada. Não existem características extra (UC10).
     */
    public DadosAplicacao() {
        garantirCatalogosPredefinidos();
        garantirCombinacoesCoresPredefinidas();
    }

    /**
     * Adiciona só os valores que ainda não existem.
     * Também é chamado ao carregar um ficheiro guardado numa versão anterior.
     * Devolve true se foi acrescentado algum valor.
     */
    public boolean garantirCatalogosPredefinidos() {
        boolean alterou = false;
        for (String nome : new String[]{"Preto", "Branco", "Azul", "Vermelho", "Verde", "Amarelo", "Cinzento"}) {
            if (adicionarSeFaltar("cor", nome)) { alterou = true; }
        }
        for (String nome : new String[]{"Trabalho", "Aulas", "Jantar", "Casamento", "Dia a dia"}) {
            if (adicionarSeFaltar("ocasiao", nome)) { alterou = true; }
        }
        for (String nome : new String[]{"Primavera", "Verão", "Outono", "Inverno"}) {
            if (adicionarSeFaltar("estacao", nome)) { alterou = true; }
        }
        for (String nome : new String[]{"Calças", "Camisola", "Casaco", "T-shirt", "Vestido"}) {
            if (adicionarSeFaltar("tipo", nome)) { alterou = true; }
        }
        return alterou;
    }

    /** Completa as regras do UC6 depois de carregar os catálogos. */
    public boolean garantirCombinacoesPredefinidas() {
        boolean adicionouCalcas = adicionarCombinacaoSeFaltar("Calças", "Camisola");
        boolean adicionouVestido = adicionarCombinacaoSeFaltar("Vestido", "Casaco");
        return adicionouCalcas || adicionouVestido;
    }

    public boolean garantirCombinacoesCoresPredefinidas() {
        // Os ficheiros anteriores ao UC16 não têm este campo.
        if (combinacoesCores == null) { combinacoesCores = new ArrayList<>(); }
        String[][] predefinidas = {
            {"Preto", "Branco"}, {"Azul", "Branco"}, {"Azul", "Cinzento"},
            {"Preto", "Cinzento"}, {"Vermelho", "Preto"},
            {"Verde", "Branco", "Cinzento"}, {"Preto", "Branco", "Amarelo"}
        };
        boolean alterou = false;
        for (String[] nomes : predefinidas) {
            ArrayList<Cor> paleta = new ArrayList<>();
            for (String nome : nomes) {
                for (Cor cor : cores) {
                    if (cor.getDesignacao().equalsIgnoreCase(nome)) {
                        paleta.add(cor);
                        break;
                    }
                }
            }
            if (!existeCombinacaoCores(paleta)) {
                combinacoesCores.add(new CombinacaoCores(novoId(), String.join(" + ", nomes), paleta));
                alterou = true;
            }
        }
        return alterou;
    }

    private boolean existeCombinacaoCores(List<Cor> paleta) {
        for (CombinacaoCores combinacao : combinacoesCores) {
            if (combinacao.temMesmasCores(paleta)) { return true; }
        }
        return false;
    }

    public CombinacaoCores adicionarCombinacaoCores(String designacao, List<Cor> selecionadas) {
        ArrayList<Cor> paleta = new ArrayList<>();
        if (selecionadas != null) {
            for (Cor cor : selecionadas) {
                int indice = cores.indexOf(cor);
                if (indice < 0) { throw new IllegalArgumentException("corInexistente"); }
                paleta.add(cores.get(indice));
            }
        }
        CombinacaoCores nova = new CombinacaoCores(proximoId, designacao, paleta);
        for (CombinacaoCores existente : combinacoesCores) {
            if (existente.getDesignacao().equalsIgnoreCase(nova.getDesignacao())) {
                throw new IllegalArgumentException("nomeCombinacaoCoresDuplicado");
            }
        }
        if (existeCombinacaoCores(paleta)) {
            throw new IllegalArgumentException("combinacaoCoresDuplicada");
        }
        novoId();
        combinacoesCores.add(nova);
        return nova;
    }

    public List<CombinacaoCores> getCombinacoesCores() {
        return new ArrayList<>(combinacoesCores);
    }

    private boolean adicionarCombinacaoSeFaltar(String primeira, String segunda) {
        List<TipoRoupa> tipos = List.of(procurarTipo(primeira), procurarTipo(segunda));
        for (CombinacaoPossivel combinacao : combinacoes) {
            List<TipoRoupa> existentes = combinacao.getTiposRoupa();
            if (combinacao.isAtiva() && existentes.size() == tipos.size() && existentes.containsAll(tipos)) {
                return false;
            }
        }
        adicionarCombinacao(new CombinacaoPossivel(novoId(), primeira + " + " + segunda, tipos));
        return true;
    }

    private TipoRoupa procurarTipo(String designacao) {
        for (TipoRoupa tipo : tiposRoupa) {
            if (tipo.getDesignacao().equalsIgnoreCase(designacao)) { return tipo; }
        }
        throw new IllegalStateException("Tipo predefinido em falta: " + designacao);
    }

    private boolean adicionarSeFaltar(String categoria, String nome) {
        List<? extends ElementoCatalogo> lista;
        switch (categoria) {
            case "cor": lista = cores; break;
            case "ocasiao": lista = ocasioes; break;
            case "estacao": lista = estacoes; break;
            default: lista = tiposRoupa; break;
        }
        for (ElementoCatalogo elemento : lista) {
            if (elemento.getDesignacao().equalsIgnoreCase(nome)) { return false; }
        }
        int id = novoId();
        switch (categoria) {
            case "cor": adicionarCor(new Cor(id, nome)); break;
            case "ocasiao": adicionarOcasiao(new Ocasiao(id, nome)); break;
            case "estacao": adicionarEstacao(new Estacao(id, nome)); break;
            default: adicionarTipoRoupa(new TipoRoupa(id, nome)); break;
        }
        return true;
    }

    public int novoId() { return proximoId++; }
    public List<Utilizador> getUtilizadores() { return new ArrayList<>(utilizadores); }
    public void adicionarUtilizador(Utilizador u) { utilizadores.add(u); }
    public List<TipoRoupa> getTiposRoupa() { return new ArrayList<>(tiposRoupa); }
    public List<Cor> getCores() { return new ArrayList<>(cores); }
    public List<Estacao> getEstacoes() { return new ArrayList<>(estacoes); }
    public List<Ocasiao> getOcasioes() { return new ArrayList<>(ocasioes); }
    public List<CombinacaoPossivel> getCombinacoes() { return new ArrayList<>(combinacoes); }
    public void adicionarTipoRoupa(TipoRoupa t) { tiposRoupa.add(t); }
    public void adicionarCor(Cor c) { cores.add(c); }
    public void adicionarEstacao(Estacao e) { estacoes.add(e); }
    public void adicionarOcasiao(Ocasiao o) { ocasioes.add(o); }
    public void adicionarCombinacao(CombinacaoPossivel c) { combinacoes.add(c); }

    public boolean existeAdministrador() { return !administradores.isEmpty(); }
    public List<Administrador> getAdministradores() { return new ArrayList<>(administradores); }
    public void adicionarAdministrador(Administrador admin) { administradores.add(admin); }

    private void readObject(ObjectInputStream entrada) throws IOException, ClassNotFoundException {
        entrada.defaultReadObject();
        if (administradores == null) { administradores = new ArrayList<>(); }
        for (int i = utilizadores.size() - 1; i >= 0; i--) {
            Administrador admin = utilizadores.get(i).getAdministradorLegado();
            if (admin != null) {
                administradores.add(admin);
                utilizadores.remove(i);
            }
        }
    }
}
