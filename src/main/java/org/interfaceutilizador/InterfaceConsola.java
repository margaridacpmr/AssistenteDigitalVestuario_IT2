package org.interfaceutilizador;

import org.modelo.*;
import org.persistencia.FicheiroDados;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Interface simples de consola: apenas menus, entradas e apresentações. */
public class InterfaceConsola {
    private final AssistenteDigital sistema;
    private final FicheiroDados ficheiro;
    private final Consola consola;
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public InterfaceConsola(AssistenteDigital sistema, FicheiroDados ficheiro, Scanner scanner, String idioma) {
        this.sistema = sistema;
        this.ficheiro = ficheiro;
        this.consola = new Consola(scanner, new Textos(idioma));
    }

    public void executar() throws IOException {
        System.out.println("\n=== " + consola.t("titulo") + " ===");
        System.out.println(consola.t("catalogosIniciais"));
        if (!sistema.getDados().existeAdministrador()) { configurarAdministrador(); }
        while (true) {
            System.out.println("\n" + consola.t("menuInicial"));
            System.out.println("1 - " + consola.t("registar"));
            System.out.println("2 - " + consola.t("entrar"));
            System.out.println("3 - " + consola.t("mudarIdioma"));
            System.out.println("0 - " + consola.t("sair"));
            int escolha = consola.numero(consola.t("opcao"), 0, 3);
            if (escolha == 0) { return; }
            if (escolha == 3) {
                selecionarIdioma();
                continue;
            }
            try {
                if (escolha == 1) { registarUtilizador(false); }
                else {
                    Utilizador utilizador = iniciarSessao();
                    if (utilizador != null) {
                        if (utilizador.isAdministrador()) { menuAdministrador(utilizador); }
                        else { menuUtilizador(utilizador); }
                    }
                }
            } catch (IllegalArgumentException e) {
                System.out.println(consola.t("erro") + e.getMessage());
            }
        }
    }

    private void selecionarIdioma() {
        System.out.println("\n" + consola.t("selecionarIdioma"));
        System.out.println("1 - Português");
        System.out.println("2 - English");
        System.out.println("3 - Français");
        System.out.println("0 - " + consola.t("voltarMenuPrincipal"));
        switch (consola.numero(consola.t("opcao"), 0, 3)) {
            case 1: consola.mudarIdioma("pt"); break;
            case 2: consola.mudarIdioma("en"); break;
            case 3: consola.mudarIdioma("fr"); break;
            default: break;
        }
    }

    private void configurarAdministrador() throws IOException {
        System.out.println(consola.t("primeiroAdmin"));
        while (!sistema.getDados().existeAdministrador()) {
            try { registarUtilizador(true); }
            catch (IllegalArgumentException e) { System.out.println(consola.t("erro") + e.getMessage()); }
        }
    }

    private void registarUtilizador(boolean admin) throws IOException {
        String nome = consola.ler(consola.t("nome"));
        String email = consola.ler(consola.t("email"));
        String palavraPasse = consola.ler(consola.t("palavraPasse"));
        // A palavra-passe não aparece no resumo da confirmação.
        System.out.println(consola.t("nome") + ": " + nome + " | " + consola.t("email") + ": " + email);
        if (!consola.confirmar()) { System.out.println(consola.t("cancelado")); return; }
        sistema.getAutenticacao().registar(nome, email, palavraPasse, admin);
        guardar();
        System.out.println(consola.t("registoOk"));
    }

    private Utilizador iniciarSessao() {
        String email = consola.ler(consola.t("email"));
        String palavraPasse = consola.ler(consola.t("palavraPasse"));
        Utilizador utilizador = sistema.getAutenticacao().entrar(email, palavraPasse);
        if (utilizador == null) { System.out.println(consola.t("loginErro")); }
        else { System.out.println(consola.t("ola") + " " + utilizador.getNome()); }
        return utilizador;
    }

    private void menuAdministrador(Utilizador admin) throws IOException {
        while (true) {
            System.out.println("\n== " + consola.t("menuAdmin") + " ==");
            System.out.println("1 - " + consola.t("cores"));
            System.out.println("2 - " + consola.t("ocasioes"));
            System.out.println("3 - " + consola.t("estacoes"));
            System.out.println("4 - " + consola.t("tipos"));
            System.out.println("5 - " + consola.t("combinacoes"));
            System.out.println("6 - " + consola.t("mudarIdioma"));
            System.out.println("7 - " + consola.t("definirCombinacaoCores"));
            System.out.println("0 - " + consola.t("terminarSessao"));
            int escolha = consola.numero(consola.t("opcao"), 0, 7);
            if (escolha == 0) { return; }
            try {
                switch (escolha) {
                    case 1: definirCatalogo(admin, "cor", consola.t("cores")); break;
                    case 2: definirCatalogo(admin, "ocasiao", consola.t("ocasioes")); break;
                    case 3: definirCatalogo(admin, "estacao", consola.t("estacoes")); break;
                    case 4: definirCatalogo(admin, "tipo", consola.t("tipos")); break;
                    case 5: definirCombinacao(admin); break;
                    case 6: selecionarIdioma(); break;
                    case 7: definirCombinacaoCores(admin); break;
                    default: break;
                }
            } catch (IllegalArgumentException e) { System.out.println(consola.t("erro") + e.getMessage()); }
        }
    }

    private void definirCatalogo(Utilizador admin, String tipo, String titulo) throws IOException {
        List<? extends ElementoCatalogo> existentes;
        switch (tipo) {
            case "cor": existentes = sistema.getDados().getCores(); break;
            case "ocasiao": existentes = sistema.getDados().getOcasioes(); break;
            case "estacao": existentes = sistema.getDados().getEstacoes(); break;
            default: existentes = sistema.getDados().getTiposRoupa(); break;
        }
        System.out.println("\n" + titulo + ": " + existentes);
        ArrayList<String> novas = new ArrayList<>();
        System.out.println(consola.t("introduzirAteVazio"));
        while (true) {
            String nome = consola.ler(consola.t("designacao"));
            if (nome.isBlank()) { break; }
            novas.add(nome);
        }
        if (novas.isEmpty()) { System.out.println(consola.t("cancelado")); return; }
        System.out.println(consola.t("listaNova") + ": " + novas);
        if (!consola.confirmar()) { System.out.println(consola.t("cancelado")); return; }
        sistema.getAdministracao().adicionarCatalogo(admin, tipo, novas);
        guardar();
        System.out.println(consola.t("sucesso"));
    }

    private void definirCombinacao(Utilizador admin) throws IOException {
        System.out.println("\n" + consola.t("combinacoes") + ": " + sistema.getDados().getCombinacoes());
        List<TipoRoupa> tipos = sistema.getDados().getTiposRoupa();
        if (tipos.size() < 2) { System.out.println(consola.t("minimoDoisTipos")); return; }
        String nome = consola.ler(consola.t("designacao"));
        List<TipoRoupa> selecionados = consola.escolherVarios(consola.t("selecionarTipos"), tipos);
        if (selecionados.isEmpty()) { return; }
        if (selecionados.size() < 2) { System.out.println(consola.t("minimoDoisTipos")); return; }
        System.out.println(nome + ": " + selecionados);
        if (!consola.confirmar()) { System.out.println(consola.t("cancelado")); return; }
        sistema.getAdministracao().definirCombinacao(admin, nome, selecionados);
        guardar();
        System.out.println(consola.t("sucesso"));
    }

    private void definirCombinacaoCores(Utilizador admin) throws IOException {
        DadosAplicacao dados = sistema.getDados();
        System.out.println("\n" + consola.t("combinacaoCores") + ": " + dados.getCombinacoesCores());
        String nome = consola.ler(consola.t("designacao"));
        List<Cor> cores = consola.escolherVarios(consola.t("selecionarCoresCombinacao"), dados.getCores());
        if (cores.isEmpty()) { return; }
        if (cores.size() < 2) { System.out.println(consola.t("minimoDuasCores")); return; }
        System.out.println(nome + ": " + cores);
        if (!consola.confirmar()) { System.out.println(consola.t("cancelado")); return; }
        try {
            sistema.getAdministracao().definirCombinacaoCores(admin, nome, cores);
        } catch (IllegalArgumentException e) {
            System.out.println(consola.t("erro") + consola.t(e.getMessage()));
            return;
        }
        guardar();
        System.out.println(consola.t("sucesso"));
    }

    private void menuUtilizador(Utilizador utilizador) throws IOException {
        while (true) {
            System.out.println("\n== " + consola.t("menuUtilizador") + " ==");
            System.out.println("1 - " + consola.t("adicionarPeca"));
            System.out.println("2 - " + consola.t("removerPeca"));
            System.out.println("3 - " + consola.t("editarPeca"));
            System.out.println("4 - " + consola.t("indisponivel"));
            System.out.println("5 - " + consola.t("consultarGuarda"));
            System.out.println("6 - " + consola.t("solicitarOutfit"));
            System.out.println("7 - " + consola.t("avaliarOutfit"));
            System.out.println("8 - " + consola.t("consultarHistorico"));
            System.out.println("9 - " + consola.t("mudarIdioma"));
            System.out.println("0 - " + consola.t("terminarSessao"));
            int escolha = consola.numero(consola.t("opcao"), 0, 9);
            if (escolha == 0) { return; }
            try {
                switch (escolha) {
                    case 1: adicionarPeca(utilizador); break;
                    case 2: removerPeca(utilizador); break;
                    case 3: editarPeca(utilizador); break;
                    case 4: marcarIndisponivel(utilizador); break;
                    case 5: consultarGuardaRoupa(utilizador); break;
                    case 6: solicitarOutfit(utilizador); break;
                    case 7: avaliarOutfit(utilizador); break;
                    case 8: consultarHistorico(utilizador); break;
                    case 9: selecionarIdioma(); break;
                    default: break;
                }
            } catch (IllegalArgumentException e) { System.out.println(consola.t("erro") + e.getMessage()); }
        }
    }

    private boolean catalogosProntos() {
        boolean pronto = !sistema.getDados().getTiposRoupa().isEmpty()
                && !sistema.getDados().getCores().isEmpty()
                && !sistema.getDados().getEstacoes().isEmpty()
                && !sistema.getDados().getOcasioes().isEmpty();
        if (!pronto) { System.out.println(consola.t("faltamCatalogos")); }
        return pronto;
    }

    private void adicionarPeca(Utilizador utilizador) throws IOException {
        if (!catalogosProntos()) { return; }
        TipoRoupa tipo = consola.escolher(consola.t("tipos"), sistema.getDados().getTiposRoupa());
        if (tipo == null) { return; }
        String nome = consola.ler(consola.t("nomePeca"));
        List<Cor> cores = consola.escolherVarios(consola.t("cores"), sistema.getDados().getCores());
        if (cores.isEmpty()) { return; }
        List<Estacao> estacoes = consola.escolherVarios(consola.t("estacoes"), sistema.getDados().getEstacoes());
        if (estacoes.isEmpty()) { return; }
        List<Ocasiao> ocasioes = consola.escolherVarios(consola.t("ocasioes"), sistema.getDados().getOcasioes());
        if (ocasioes.isEmpty()) { return; }
        mostrarDadosPeca(nome, tipo, cores, estacoes, ocasioes);
        if (!consola.confirmar()) { System.out.println(consola.t("cancelado")); return; }
        sistema.getGuardaRoupa().adicionar(utilizador, nome, tipo, cores, estacoes, ocasioes);
        guardar();
        System.out.println(consola.t("sucesso"));
    }

    private void editarPeca(Utilizador utilizador) throws IOException {
        PecaVestuario peca = consola.escolher(consola.t("editarPeca"), utilizador.getGuardaRoupa().getPecas());
        if (peca == null) { return; }
        mostrarPeca(peca);
        System.out.println("1 - " + consola.t("alterarNome"));
        System.out.println("2 - " + consola.t("alterarTipo"));
        System.out.println("3 - " + consola.t("alterarCores"));
        System.out.println("4 - " + consola.t("alterarEstacoes"));
        System.out.println("5 - " + consola.t("alterarOcasioes"));
        boolean indisponivel = !peca.isDisponivel();
        if (indisponivel) {
            System.out.println("6 - " + consola.t("tornarDisponivel"));
        }
        System.out.println("0 - " + consola.t("voltar"));
        int escolha = consola.numero(consola.t("opcao"), 0, indisponivel ? 6 : 5);
        if (escolha == 0) { return; }

        if (escolha == 6) {
            if (!sistema.getGuardaRoupa().tornarDisponivel(utilizador, peca, consola.confirmar())) {
                System.out.println(consola.t("cancelado"));
                return;
            }
        } else {
            String nome = peca.getNome();
            TipoRoupa tipo = peca.getTipoRoupa();
            List<Cor> cores = peca.getCores();
            List<Estacao> estacoes = peca.getEstacoes();
            List<Ocasiao> ocasioes = peca.getOcasioes();
            DadosAplicacao dados = sistema.getDados();
            switch (escolha) {
                case 1:
                    nome = consola.ler(consola.t("novoNome"));
                    break;
                case 2:
                    tipo = consola.escolher(consola.t("tipos"), dados.getTiposRoupa());
                    if (tipo == null) { return; }
                    break;
                case 3:
                    cores = consola.escolherVarios(consola.t("cores"), dados.getCores());
                    if (cores.isEmpty()) { return; }
                    break;
                case 4:
                    estacoes = consola.escolherVarios(consola.t("estacoes"), dados.getEstacoes());
                    if (estacoes.isEmpty()) { return; }
                    break;
                case 5:
                    ocasioes = consola.escolherVarios(consola.t("ocasioes"), dados.getOcasioes());
                    if (ocasioes.isEmpty()) { return; }
                    break;
            }
            mostrarDadosPeca(nome, tipo, cores, estacoes, ocasioes);
            if (!consola.confirmar()) { System.out.println(consola.t("cancelado")); return; }
            sistema.getGuardaRoupa().editar(utilizador, peca, nome, tipo, cores, estacoes, ocasioes);
        }
        guardar();
        System.out.println(consola.t("sucesso"));
    }

    private void removerPeca(Utilizador utilizador) throws IOException {
        PecaVestuario peca = consola.escolher(consola.t("removerPeca"), utilizador.getGuardaRoupa().getPecas());
        if (peca == null) { return; }
        mostrarPeca(peca);
        if (!consola.confirmar()) { System.out.println(consola.t("cancelado")); return; }
        sistema.getGuardaRoupa().remover(utilizador, peca);
        guardar();
        System.out.println(consola.t("sucesso"));
    }

    private void marcarIndisponivel(Utilizador utilizador) throws IOException {
        ArrayList<PecaVestuario> disponiveis = new ArrayList<>();
        for (PecaVestuario peca : utilizador.getGuardaRoupa().getPecas()) {
            if (peca.isDisponivel()) { disponiveis.add(peca); }
        }
        PecaVestuario peca = consola.escolher(consola.t("indisponivel"), disponiveis);
        if (peca == null) { return; }
        mostrarPeca(peca);
        if (!consola.confirmar()) { System.out.println(consola.t("cancelado")); return; }
        sistema.getGuardaRoupa().marcarIndisponivel(utilizador, peca);
        guardar();
        System.out.println(consola.t("sucesso"));
    }

    private void consultarGuardaRoupa(Utilizador utilizador) {
        PecaVestuario peca = consola.escolher(consola.t("consultarGuarda"), utilizador.getGuardaRoupa().getPecas());
        if (peca != null) { mostrarPeca(peca); }
    }

    private void mostrarDadosPeca(String nome, TipoRoupa tipo, List<Cor> cores,
                                  List<Estacao> estacoes, List<Ocasiao> ocasioes) {
        System.out.println(consola.t("nome") + ": " + nome + " | " + consola.t("tipos") + ": " + tipo);
        System.out.println(consola.t("cores") + ": " + cores + " | " + consola.t("estacoes") + ": " + estacoes);
        System.out.println(consola.t("ocasioes") + ": " + ocasioes);
    }
    private void mostrarPeca(PecaVestuario peca) {
        mostrarDadosPeca(peca.getNome(), peca.getTipoRoupa(), peca.getCores(), peca.getEstacoes(), peca.getOcasioes());
        System.out.println(consola.t("disponibilidade") + ": "
                + (peca.isDisponivel() ? consola.t("sim") : consola.t("nao")));
    }

    private void solicitarOutfit(Utilizador utilizador) throws IOException {
        Ocasiao ocasiao = consola.escolher(consola.t("ocasioes"), sistema.getDados().getOcasioes());
        if (ocasiao == null) { return; }
        Estacao estacao = consola.escolher(consola.t("estacoes"), sistema.getDados().getEstacoes());
        if (estacao == null) { return; }
        System.out.println(consola.t("ocasioes") + ": " + ocasiao + " | " + consola.t("estacoes") + ": " + estacao);
        System.out.println(consola.t("regraMesmoContexto"));
        if (!consola.confirmar()) { System.out.println(consola.t("cancelado")); return; }
        PedidoOutfit pedido = sistema.getOutfits().solicitar(utilizador, ocasiao, estacao);
        if (pedido == null) {
            System.out.println(consola.t(sistema.getOutfits().motivoFalha(utilizador, ocasiao, estacao)));
            return;
        }
        guardar();
        System.out.println(consola.t("outfitGerado"));
        mostrarPedido(pedido);
    }

    private void avaliarOutfit(Utilizador utilizador) throws IOException {
        PedidoOutfit pedido = escolherPedido(utilizador);
        if (pedido == null) { return; }
        mostrarPedido(pedido);
        int valor = consola.numero(consola.t("valorAvaliacao"), 1, 5);
        if (!consola.confirmar()) { System.out.println(consola.t("cancelado")); return; }
        sistema.getOutfits().avaliar(utilizador, pedido, valor);
        guardar();
        System.out.println(consola.t("sucesso"));
    }

    private void consultarHistorico(Utilizador utilizador) {
        PedidoOutfit pedido = escolherPedido(utilizador);
        if (pedido != null) { mostrarPedido(pedido); }
    }

    private PedidoOutfit escolherPedido(Utilizador utilizador) {
        List<PedidoOutfit> pedidos = sistema.getOutfits().consultarHistorico(utilizador);
        if (pedidos.isEmpty()) { System.out.println(consola.t("semHistorico")); return null; }
        System.out.println("\n" + consola.t("consultarHistorico"));
        for (int i = 0; i < pedidos.size(); i++) {
            PedidoOutfit pedido = pedidos.get(i);
            System.out.println((i + 1) + " - " + pedido.getData().format(formatoData)
                    + " | " + pedido.getOcasiao() + " | " + pedido.getEstacao()
                    + " | " + textoAvaliacao(pedido.getOutfit()));
        }
        int escolha = consola.numero(consola.t("selecionarNumero"), 0, pedidos.size());
        return escolha == 0 ? null : pedidos.get(escolha - 1);
    }

    private String textoAvaliacao(Outfit outfit) {
        Avaliacao avaliacao = outfit.getAvaliacao();
        return avaliacao == null ? consola.t("semAvaliacao") : avaliacao.getValor() + "/5";
    }

    private void mostrarPedido(PedidoOutfit pedido) {
        Outfit outfit = pedido.getOutfit();
        System.out.println("\n" + outfit.getNome() + " | ID " + pedido.getId() + " | " + pedido.getData().format(formatoData));
        System.out.println(consola.t("ocasioes") + ": " + pedido.getOcasiao()
                + " | " + consola.t("estacoes") + ": " + pedido.getEstacao());
        System.out.println(consola.t("regra") + ": " + outfit.getCombinacao().getDesignacao());
        CombinacaoCores paleta = outfit.getCombinacaoCores();
        System.out.println(consola.t("combinacaoCores") + ": "
                + (paleta == null ? consola.t("combinacaoCoresNaoRegistada") : paleta));
        System.out.println(consola.t("pecas") + ": " + outfit.getPecas());
        System.out.println(consola.t("avaliacao") + ": " + textoAvaliacao(outfit));
        if (outfit.getAvaliacao() != null) {
            System.out.println(consola.t("dataAvaliacao") + ": " + outfit.getAvaliacao().getData().format(formatoData));
        }
    }

    private void guardar() throws IOException { ficheiro.guardar(sistema.getDados()); }
}
