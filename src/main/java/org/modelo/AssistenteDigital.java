package org.modelo;

import org.controladores.AdministracaoController;
import org.controladores.AutenticacaoController;
import org.controladores.GuardaRoupaController;
import org.controladores.OutfitController;

/** Ponto de entrada do modelo: disponibiliza dados e controladores. */
public class AssistenteDigital {
    private final DadosAplicacao dados;
    private final AutenticacaoController autenticacao;
    private final AdministracaoController administracao;
    private final GuardaRoupaController guardaRoupa;
    private final OutfitController outfits;

    public AssistenteDigital(DadosAplicacao dados) {
        this.dados = dados;
        this.autenticacao = new AutenticacaoController(dados);
        this.administracao = new AdministracaoController(dados);
        this.guardaRoupa = new GuardaRoupaController(dados);
        this.outfits = new OutfitController(dados);
    }

    public DadosAplicacao getDados() { return dados; }
    public AutenticacaoController getAutenticacao() { return autenticacao; }
    public AdministracaoController getAdministracao() { return administracao; }
    public GuardaRoupaController getGuardaRoupa() { return guardaRoupa; }
    public OutfitController getOutfits() { return outfits; }
}
