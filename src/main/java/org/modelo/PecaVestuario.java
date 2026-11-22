package org.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** UC1 a UC5. NÃO inclui tamanho, padrão ou características adicionais. */
public class PecaVestuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private String nome;
    private TipoRoupa tipoRoupa;
    private ArrayList<Cor> cores;
    private ArrayList<Estacao> estacoes;
    private ArrayList<Ocasiao> ocasioes;
    private boolean disponivel;

    public PecaVestuario(int id, String nome, TipoRoupa tipo, List<Cor> cores,
                          List<Estacao> estacoes, List<Ocasiao> ocasioes) {
        this.id = id;
        atualizar(nome, tipo, cores, estacoes, ocasioes);
        this.disponivel = true;
    }

    public void atualizar(String nome, TipoRoupa tipo, List<Cor> cores,
                          List<Estacao> estacoes, List<Ocasiao> ocasioes) {
        if (nome == null || nome.isBlank() || tipo == null || cores == null || cores.isEmpty()
                || estacoes == null || estacoes.isEmpty() || ocasioes == null || ocasioes.isEmpty()) {
            throw new IllegalArgumentException("Nome, tipo, cores, estações e ocasiões são obrigatórios.");
        }
        this.nome = nome.trim();
        this.tipoRoupa = tipo;
        this.cores = new ArrayList<>(cores);
        this.estacoes = new ArrayList<>(estacoes);
        this.ocasioes = new ArrayList<>(ocasioes);
    }

    /**
     * Uma peça é elegível apenas se estiver disponível e incluir, em simultâneo,
     * a ocasião E a estação escolhidas pelo utilizador (regra do UC6).
     */
    public boolean adequada(Ocasiao ocasiao, Estacao estacao) {
        return disponivel && incluiOcasiao(ocasiao) && incluiEstacao(estacao);
    }

    public boolean incluiOcasiao(Ocasiao ocasiao) { return ocasioes.contains(ocasiao); }
    public boolean incluiEstacao(Estacao estacao) { return estacoes.contains(estacao); }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public TipoRoupa getTipoRoupa() { return tipoRoupa; }
    public List<Cor> getCores() { return new ArrayList<>(cores); }
    public List<Estacao> getEstacoes() { return new ArrayList<>(estacoes); }
    public List<Ocasiao> getOcasioes() { return new ArrayList<>(ocasioes); }
    public boolean isDisponivel() { return disponivel; }
    public void marcarIndisponivel() { disponivel = false; }
    public void tornarDisponivel() { disponivel = true; }
    @Override
    public String toString() { return nome + " (" + tipoRoupa + ")"; }
}
