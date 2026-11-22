package org;

import org.controladores.*;
import org.modelo.*;
import org.interfaceutilizador.Textos;
import org.persistencia.FicheiroDados;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/** Testes sem JUnit: executar como uma classe Java normal. */
public class TestesSimples {
    private static int verificacoes = 0;

    private static void verificar(boolean condicao, String nome) {
        if (!condicao) { throw new AssertionError("Falhou: " + nome); }
        verificacoes++;
    }

    private static void esperarErro(Runnable operacao, String nome) {
        boolean houveErro = false;
        try { operacao.run(); } catch (IllegalArgumentException e) { houveErro = true; }
        verificar(houveErro, nome);
    }

    /** Procura um elemento do catálogo pela designação, sem depender da ordem. */
    private static <T extends ElementoCatalogo> T procurar(List<T> lista, String nome) {
        for (T elemento : lista) {
            if (elemento.getDesignacao().equalsIgnoreCase(nome)) { return elemento; }
        }
        throw new AssertionError("Não existe no catálogo: " + nome);
    }

    public static void main(String[] args) throws Exception {
        DadosAplicacao dados = new DadosAplicacao();
        // Os catálogos já têm valores antes de qualquer administrador entrar.
        verificar(dados.getCores().size() == 7, "sete cores iniciais");
        verificar(dados.getOcasioes().size() == 5, "cinco ocasiões iniciais");
        verificar(dados.getEstacoes().size() == 4, "quatro estações iniciais");
        verificar(dados.getTiposRoupa().size() == 5, "cinco tipos iniciais");
        verificar(!dados.garantirCatalogosPredefinidos(), "não duplica valores iniciais");
        verificar(procurar(dados.getCores(), "Preto") != null, "preto já disponível");
        verificar(procurar(dados.getOcasioes(), "Trabalho") != null, "trabalho já disponível");
        verificar(procurar(dados.getEstacoes(), "Inverno") != null, "inverno já disponível");
        verificar(procurar(dados.getTiposRoupa(), "Calças") != null, "calças já disponíveis");
        AssistenteDigital app = new AssistenteDigital(dados);
        Utilizador admin = app.getAutenticacao().registar("Admin", "admin@teste.pt", "senha1", true);
        Utilizador ana = app.getAutenticacao().registar("Ana", "ana@teste.pt", "abc123", false);
        Utilizador bruno = app.getAutenticacao().registar("Bruno", "bruno@teste.pt", "xyz123", false);
        verificar(app.getAutenticacao().entrar("ana@teste.pt", "abc123") == ana, "login correto");
        verificar(app.getAutenticacao().entrar("ana@teste.pt", "errada") == null, "login incorreto");
        esperarErro(() -> app.getAutenticacao().registar("A", "ana@teste.pt", "q", false), "email único");
        esperarErro(() -> app.getAdministracao().adicionarCatalogo(ana, "cor", Arrays.asList("Azul")), "só admin altera catálogos");

        // O administrador pode acrescentar valores, mas não repetir os predefinidos.
        app.getAdministracao().adicionarCatalogo(admin, "tipo", Arrays.asList("Sapatos"));
        app.getAdministracao().adicionarCatalogo(admin, "cor", Arrays.asList("Turquesa"));
        app.getAdministracao().adicionarCatalogo(admin, "ocasiao", Arrays.asList("Passeio"));
        app.getAdministracao().adicionarCatalogo(admin, "estacao", Arrays.asList("Meia-estação"));
        verificar(dados.getTiposRoupa().size() == 6, "administrador acrescenta tipo de roupa");
        verificar(dados.getCores().size() == 8, "administrador acrescenta cor");
        verificar(dados.getOcasioes().size() == 6, "administrador acrescenta ocasião");
        verificar(dados.getEstacoes().size() == 5, "administrador acrescenta estação");
        esperarErro(() -> app.getAdministracao().adicionarCatalogo(admin, "cor", Arrays.asList("Rosa", "Preto")), "lote inválido rejeitado");
        verificar(dados.getCores().size() == 8, "sem registo parcial do lote");
        esperarErro(() -> app.getAdministracao().adicionarCatalogo(admin, "tipo", Arrays.asList("Calças")), "não repete tipo predefinido");

        TipoRoupa calcas = procurar(dados.getTiposRoupa(), "Calças");
        TipoRoupa camisola = procurar(dados.getTiposRoupa(), "Camisola");
        Cor azul = procurar(dados.getCores(), "Azul");
        Cor branco = procurar(dados.getCores(), "Branco");
        Ocasiao trabalho = procurar(dados.getOcasioes(), "Trabalho");
        Ocasiao jantar = procurar(dados.getOcasioes(), "Jantar");
        Estacao verao = procurar(dados.getEstacoes(), "Verão");
        Estacao inverno = procurar(dados.getEstacoes(), "Inverno");
        OutfitController outfits = app.getOutfits();
        verificar(outfits.motivoFalha(ana, trabalho, inverno).equals("semRegrasOutfit"), "diagnóstico sem regras");
        DadosAplicacao dadosInvalidos = new DadosAplicacao();
        dadosInvalidos.adicionarCombinacao(new CombinacaoPossivel(1, "Regra incompleta", Arrays.asList(calcas, null)));
        verificar(new OutfitController(dadosInvalidos).motivoFalha(ana, trabalho, inverno).equals("semCombinacao"),
                "diagnóstico de regra inválida");
        CombinacaoPossivel regraAdmin = app.getAdministracao().definirCombinacao(admin, "Dia de trabalho",
                Arrays.asList(camisola, calcas));
        esperarErro(() -> app.getAdministracao().definirCombinacao(admin, "Cópia", Arrays.asList(calcas, camisola)), "combinação duplicada");

        PecaVestuario p1 = app.getGuardaRoupa().adicionar(ana, "Calças pretas", calcas,
                Arrays.asList(azul, branco), Arrays.asList(inverno), Arrays.asList(trabalho));
        verificar(p1.getCores().size() == 2, "uma peça pode ter várias cores");
        esperarErro(() -> app.getGuardaRoupa().adicionar(ana, "calças pretas", calcas,
                Arrays.asList(azul), Arrays.asList(inverno), Arrays.asList(trabalho)), "peça com nome único");
        verificar(app.getOutfits().solicitar(ana, trabalho, inverno) == null, "sem camisola não gera outfit");
        verificar(ana.getPedidos().isEmpty(), "falha não cria pedido");
        app.getGuardaRoupa().adicionar(bruno, "Camisola do Bruno", camisola,
                List.of(branco), List.of(inverno), List.of(trabalho));
        verificar(outfits.solicitar(ana, trabalho, inverno) == null, "uma peça de outro utilizador não completa o outfit");
        verificar(outfits.motivoFalha(ana, trabalho, inverno).equals("semTiposOutfit"), "diagnóstico dos tipos");
        // Primeiro, há tipos suficientes, mas a camisola é de outra ocasião.
        PecaVestuario p2 = app.getGuardaRoupa().adicionar(ana, "Camisola clara", camisola,
                Arrays.asList(branco), Arrays.asList(inverno), Arrays.asList(jantar));
        verificar(app.getOutfits().solicitar(ana, trabalho, inverno) == null,
                "não combina peças de ocasiões diferentes");
        verificar(ana.getPedidos().isEmpty(), "ocasião incompatível não cria pedido");
        verificar(outfits.motivoFalha(ana, trabalho, inverno).equals("semOcasiaoOutfit"), "diagnóstico da ocasião");

        // Depois, a ocasião coincide, mas a estação é diferente.
        app.getGuardaRoupa().editar(ana, p2, "Camisola clara", camisola,
                Arrays.asList(branco), Arrays.asList(verao), Arrays.asList(trabalho));
        verificar(app.getOutfits().solicitar(ana, trabalho, inverno) == null,
                "não combina peças de estações diferentes");
        verificar(ana.getPedidos().isEmpty(), "estação incompatível não cria pedido");
        verificar(outfits.motivoFalha(ana, trabalho, inverno).equals("semEstacaoOutfit"), "diagnóstico da estação");

        // Por fim, ambas as peças incluem Trabalho e Inverno: o outfit pode ser gerado.
        app.getGuardaRoupa().editar(ana, p2, "Camisola clara", camisola,
                Arrays.asList(branco), Arrays.asList(inverno), Arrays.asList(trabalho));
        PedidoOutfit pedido = app.getOutfits().solicitar(ana, trabalho, inverno);
        verificar(pedido != null, "com duas peças pode gerar outfit");
        verificar(pedido.getOutfit().getPecas().size() == 2, "outfit com dois tipos");
        verificar(pedido.getOutfit().getCombinacao() == regraAdmin, "outfit usa a regra do administrador");
        esperarErro(() -> outfits.solicitar(admin, trabalho, inverno), "administrador não solicita outfits");
        verificar(!app.getOutfits().consultarHistorico(ana).isEmpty(), "histórico do próprio");
        verificar(app.getOutfits().consultarHistorico(bruno).isEmpty(), "outro utilizador não vê histórico");
        esperarErro(() -> app.getOutfits().avaliar(bruno, pedido, 4), "outro utilizador não avalia");
        esperarErro(() -> app.getOutfits().avaliar(ana, pedido, 6), "valor fora da escala");
        app.getOutfits().avaliar(ana, pedido, 4);
        app.getOutfits().avaliar(ana, pedido, 5);
        verificar(pedido.getOutfit().getAvaliacao().getValor() == 5, "a nova avaliação substitui anterior");
        app.getGuardaRoupa().marcarIndisponivel(ana, p2);
        verificar(app.getOutfits().solicitar(ana, trabalho, inverno) == null, "peça indisponível não é usada");
        verificar(outfits.motivoFalha(ana, trabalho, inverno).equals("semDisponibilidadeOutfit"),
                "diagnóstico da disponibilidade");
        app.getGuardaRoupa().tornarDisponivel(ana, p2, true);
        verificar(outfits.solicitar(ana, trabalho, inverno).getOutfit().getPecas().contains(p2),
                "peça que voltou a ficar disponível pode ser usada");
        app.getGuardaRoupa().remover(ana, p1);
        verificar(ana.getGuardaRoupa().getPecas().size() == 1, "remoção de peça");
        verificar(pedido.getOutfit().getPecas().size() == 2, "histórico preserva outfit anterior");

        File temporario = File.createTempFile("vestuario-testes-", ".dat");
        temporario.deleteOnExit();
        FicheiroDados ficheiro = new FicheiroDados(temporario.getAbsolutePath());
        CombinacaoPossivel outraRegra = app.getAdministracao().definirCombinacao(admin, "Com sapatos",
                List.of(calcas, procurar(dados.getTiposRoupa(), "Sapatos")));
        ficheiro.guardar(dados);
        DadosAplicacao carregados = ficheiro.carregar();
        AssistenteDigital recarregada = new AssistenteDigital(carregados);
        Utilizador anaRecarregada = recarregada.getAutenticacao().entrar("ana@teste.pt", "abc123");
        verificar(anaRecarregada != null, "login após carregar ficheiro");
        verificar(anaRecarregada.getPedidos().size() == 2, "histórico sobrevive ao reinício");
        verificar(carregados.getCores().size() == 8, "cores predefinidas e novas ficam guardadas");
        verificar(carregados.getEstacoes().size() == 5, "estações predefinidas e novas ficam guardadas");
        verificar(carregados.getOcasioes().size() == 6, "ocasiões predefinidas e novas ficam guardadas");
        verificar(carregados.getTiposRoupa().size() == 6, "tipos predefinidos e novos ficam guardados");
        verificar(!carregados.garantirCatalogosPredefinidos(), "não duplica ao reiniciar");
        verificar(anaRecarregada.getPedidos().get(0).getOutfit().getAvaliacao().getValor() == 5,
                "avaliação persiste");
        verificar(carregados.getCombinacoes().size() == 3, "acrescenta só a regra em falta num ficheiro com regras");
        verificar(carregados.getCombinacoes().get(0).getId() == regraAdmin.getId(),
                "preserva a regra equivalente com os tipos na ordem inversa");
        verificar(carregados.getCombinacoes().get(1).getId() == outraRegra.getId(),
                "preserva a regra adicional do administrador");
        verificar(ficheiro.carregar().getCombinacoes().size() == 3, "reiniciar não duplica combinações");

        // Um ficheiro ainda inexistente passa a incluir os catálogos iniciais ao carregar.
        File novoFicheiro = File.createTempFile("vestuario-novo-", ".dat");
        novoFicheiro.delete();
        novoFicheiro.deleteOnExit();
        FicheiroDados ficheiroNovo = new FicheiroDados(novoFicheiro.getAbsolutePath());
        DadosAplicacao dadosNovos = ficheiroNovo.carregar();
        verificar(novoFicheiro.exists(), "cria ficheiro inicial automaticamente");
        verificar(dadosNovos.getCores().size() == 7, "novo ficheiro com sete cores");
        verificar(dadosNovos.getTiposRoupa().size() == 5, "novo ficheiro com cinco tipos");
        verificarPredefinidas(ficheiroNovo, dadosNovos);
        verificarCombinacoesCores();
        for (String idioma : List.of("pt", "en", "fr")) {
            Textos textos = new Textos(idioma);
            for (String chave : List.of("semRegrasOutfit", "semTiposOutfit", "semDisponibilidadeOutfit",
                    "semOcasiaoOutfit", "semEstacaoOutfit", "semCombinacao", "semCoresOutfit",
                    "definirCombinacaoCores", "combinacaoCores", "selecionarCoresCombinacao",
                    "minimoDuasCores", "designacaoCoresObrigatoria", "corInexistente", "corRepetida",
                    "nomeCombinacaoCoresDuplicado", "combinacaoCoresDuplicada", "combinacaoCoresNaoRegistada")) {
                verificar(!textos.get(chave).isBlank(), "tradução " + idioma + " de " + chave);
            }
        }
        System.out.println("OK: " + verificacoes + " verificações efetuadas com sucesso.");
    }

    private static void verificarPredefinidas(FicheiroDados ficheiro, DadosAplicacao dados) throws Exception {
        verificar(dados.getCombinacoes().size() == 2, "novo ficheiro tem as duas combinações");
        TipoRoupa calcas = procurar(dados.getTiposRoupa(), "Calças");
        TipoRoupa camisola = procurar(dados.getTiposRoupa(), "Camisola");
        TipoRoupa vestido = procurar(dados.getTiposRoupa(), "Vestido");
        TipoRoupa casaco = procurar(dados.getTiposRoupa(), "Casaco");
        verificar(dados.getCombinacoes().stream().anyMatch(regra -> regra.getTiposRoupa().equals(List.of(vestido, casaco))),
                "Vestido + Casaco é predefinida");
        Cor preto = procurar(dados.getCores(), "Preto");
        Cor branco = procurar(dados.getCores(), "Branco");
        Ocasiao aulas = procurar(dados.getOcasioes(), "Aulas");
        Ocasiao trabalho = procurar(dados.getOcasioes(), "Trabalho");
        Ocasiao jantar = procurar(dados.getOcasioes(), "Jantar");
        Estacao inverno = procurar(dados.getEstacoes(), "Inverno");
        Estacao outono = procurar(dados.getEstacoes(), "Outono");
        Estacao verao = procurar(dados.getEstacoes(), "Verão");
        AssistenteDigital app = new AssistenteDigital(dados);
        Utilizador utilizador = app.getAutenticacao().registar("Lia", "lia@teste.pt", "senha1", false);
        // Instâncias distintas com o mesmo ID também têm de corresponder após o carregamento.
        PecaVestuario p1 = app.getGuardaRoupa().adicionar(utilizador, "Calças",
                new TipoRoupa(calcas.getId(), calcas.getDesignacao()), List.of(preto),
                List.of(outono, new Estacao(inverno.getId(), inverno.getDesignacao())),
                List.of(trabalho, new Ocasiao(aulas.getId(), aulas.getDesignacao())));
        PecaVestuario p2 = app.getGuardaRoupa().adicionar(utilizador, "Camisola", camisola,
                List.of(branco), List.of(outono, inverno), List.of(aulas, jantar));

        PedidoOutfit pedido = app.getOutfits().solicitar(utilizador, aulas, inverno);
        verificar(pedido != null && pedido.getOutfit().getPecas().equals(List.of(p1, p2)),
                "Calças + Camisola gera outfit com listas de ocasiões diferentes");
        app.getGuardaRoupa().editar(utilizador, p2, "Camisola", camisola,
                List.of(branco), List.of(verao, inverno), List.of(trabalho, aulas));
        verificar(app.getOutfits().solicitar(utilizador, aulas, inverno) != null,
                "Calças + Camisola gera outfit com listas de estações diferentes");
        app.getGuardaRoupa().editar(utilizador, p2, "Camisola", camisola,
                List.of(branco), List.of(verao, inverno), List.of(aulas, jantar));
        ficheiro.guardar(dados);

        DadosAplicacao carregados = ficheiro.carregar();
        verificar(carregados.getCombinacoes().size() == 2, "as duas predefinidas persistem sem duplicados");
        AssistenteDigital recarregada = new AssistenteDigital(carregados);
        Utilizador lia = recarregada.getAutenticacao().entrar("lia@teste.pt", "senha1");
        PedidoOutfit recarregado = recarregada.getOutfits().solicitar(lia, aulas, inverno);
        verificar(recarregado != null && recarregado.getOutfit().getPecas().equals(lia.getGuardaRoupa().getPecas()),
                "gera outfit depois de Serializable com instâncias distintas e listas diferentes");
    }

    private static void verificarCombinacoesCores() throws Exception {
        DadosAplicacao dados = new DadosAplicacao();
        dados.garantirCombinacoesPredefinidas();
        List<String> nomes = List.of("Preto + Branco", "Azul + Branco", "Azul + Cinzento", "Preto + Cinzento",
                "Vermelho + Preto", "Verde + Branco + Cinzento", "Preto + Branco + Amarelo");
        verificar(dados.getCombinacoesCores().size() == nomes.size(), "sete paletas predefinidas");
        for (int i = 0; i < nomes.size(); i++) {
            CombinacaoCores paleta = dados.getCombinacoesCores().get(i);
            verificar(paleta.getDesignacao().equals(nomes.get(i))
                            && paleta.getCores().toString().equals("[" + nomes.get(i).replace(" + ", ", ") + "]"),
                    "paleta inicial " + nomes.get(i));
        }

        AssistenteDigital app = new AssistenteDigital(dados);
        Utilizador admin = app.getAutenticacao().registar("Admin", "admin@teste.pt", "senha1", true);
        Utilizador utilizador = app.getAutenticacao().registar("Lia", "lia@teste.pt", "senha1", false);
        AdministracaoController administracao = app.getAdministracao();
        Cor preto = procurar(dados.getCores(), "Preto");
        Cor branco = procurar(dados.getCores(), "Branco");
        Cor azul = procurar(dados.getCores(), "Azul");
        Cor cinzento = procurar(dados.getCores(), "Cinzento");
        Cor vermelho = procurar(dados.getCores(), "Vermelho");
        Cor verde = procurar(dados.getCores(), "Verde");
        List<Cor> quatroCores = List.of(preto, branco, azul, vermelho);
        CombinacaoCores nova = administracao.definirCombinacaoCores(admin, "Quatro cores", quatroCores);
        verificar(nova.temMesmasCores(quatroCores), "admin pode criar paleta de quatro cores");
        esperarErro(() -> administracao.definirCombinacaoCores(admin, "Cópia", List.of(branco, preto)),
                "não repete uma paleta com a ordem invertida");
        esperarErro(() -> administracao.definirCombinacaoCores(admin, "Cópia", List.of(preto, preto)),
                "não repete cor dentro da paleta");
        esperarErro(() -> administracao.definirCombinacaoCores(admin, " quatro cores ", List.of(azul, verde)),
                "não repete designação");
        esperarErro(() -> administracao.definirCombinacaoCores(admin, "Uma", List.of(preto)),
                "uma cor não basta");
        esperarErro(() -> administracao.definirCombinacaoCores(admin, "Fora", List.of(preto, new Cor(-1, "Fora"))),
                "não aceita cores de fora do catálogo");
        esperarErro(() -> administracao.definirCombinacaoCores(utilizador, "Outra", List.of(azul, verde)),
                "só o administrador define paletas");
        verificar(!dados.garantirCombinacoesCoresPredefinidas(), "predefinidas idempotentes com paleta personalizada");

        TipoRoupa calcas = procurar(dados.getTiposRoupa(), "Calças");
        TipoRoupa camisola = procurar(dados.getTiposRoupa(), "Camisola");
        Ocasiao aulas = procurar(dados.getOcasioes(), "Aulas");
        Ocasiao jantar = procurar(dados.getOcasioes(), "Jantar");
        Estacao inverno = procurar(dados.getEstacoes(), "Inverno");
        Estacao outono = procurar(dados.getEstacoes(), "Outono");
        GuardaRoupaController guarda = app.getGuardaRoupa();
        PecaVestuario estampadas = guarda.adicionar(utilizador, "Calças estampadas", calcas,
                List.of(preto, verde, cinzento), List.of(inverno), List.of(aulas));
        PecaVestuario escuras = guarda.adicionar(utilizador, "Calças escuras", calcas,
                List.of(preto), List.of(outono, inverno), List.of(aulas, jantar));
        PecaVestuario pretas = guarda.adicionar(utilizador, "Calças pretas", calcas,
                List.of(preto), List.of(inverno), List.of(aulas));
        PecaVestuario corErrada = guarda.adicionar(utilizador, "Camisola verde", camisola,
                List.of(verde), List.of(inverno), List.of(aulas));
        PecaVestuario clara = guarda.adicionar(utilizador, "Camisola branca", camisola,
                List.of(branco), List.of(inverno), List.of(aulas));

        boolean usouEscuras = false;
        boolean usouPretas = false;
        for (int i = 0; i < 64; i++) {
            Outfit outfit = app.getOutfits().solicitar(utilizador, aulas, inverno).getOutfit();
            verificar(outfit.getCombinacaoCores().temMesmasCores(List.of(preto, branco))
                            && outfit.getPecas().contains(clara) && !outfit.getPecas().contains(estampadas)
                            && !outfit.getPecas().contains(corErrada),
                    "prefere Preto + Branco entre escolhas válidas");
            usouEscuras |= outfit.getPecas().contains(escuras);
            usouPretas |= outfit.getPecas().contains(pretas);
        }
        verificar(usouEscuras && usouPretas, "sorteio entre peças de cores e tipos iguais");
        guarda.marcarIndisponivel(utilizador, escuras);
        guarda.marcarIndisponivel(utilizador, pretas);
        Outfit multicolorido = app.getOutfits().solicitar(utilizador, aulas, inverno).getOutfit();
        verificar(multicolorido.getPecas().equals(List.of(estampadas, clara))
                        && multicolorido.getCombinacaoCores().temMesmasCores(List.of(preto, branco)),
                "aceita quatro cores quando os extras vêm da peça multicolorida");
        guarda.marcarIndisponivel(utilizador, clara);
        verificar(app.getOutfits().solicitar(utilizador, aulas, inverno) == null
                        && app.getOutfits().motivoFalha(utilizador, aulas, inverno).equals("semCoresOutfit"),
                "rejeita paletas incompletas e cores avulsas fora da paleta");

        TipoRoupa casaco = procurar(dados.getTiposRoupa(), "Casaco");
        TipoRoupa vestido = procurar(dados.getTiposRoupa(), "Vestido");
        administracao.definirCombinacao(admin, "Quatro tipos", List.of(calcas, camisola, casaco, vestido));
        guarda.marcarIndisponivel(utilizador, estampadas);
        guarda.marcarIndisponivel(utilizador, corErrada);
        guarda.tornarDisponivel(utilizador, pretas, true);
        guarda.adicionar(utilizador, "Camisola azul", camisola, List.of(azul), List.of(inverno), List.of(aulas));
        guarda.adicionar(utilizador, "Casaco branco", casaco, List.of(branco), List.of(inverno), List.of(aulas));
        guarda.adicionar(utilizador, "Vestido vermelho", vestido, List.of(vermelho), List.of(inverno), List.of(aulas));
        verificar(app.getOutfits().solicitar(utilizador, aulas, inverno) == null,
                "quatro peças de uma só cor são rejeitadas mesmo com paleta aprovada");

        File temporario = File.createTempFile("vestuario-uc16-", ".dat");
        temporario.deleteOnExit();
        FicheiroDados ficheiro = new FicheiroDados(temporario.getAbsolutePath());
        ficheiro.guardar(dados);
        DadosAplicacao carregados = ficheiro.carregar();
        verificar(carregados.getCombinacoesCores().size() == 8 && ficheiro.carregar().getCombinacoesCores().size() == 8,
                "paletas persistem sem duplicar ao carregar");
        verificar(carregados.getCombinacoesCores().get(7).getId() == nova.getId(), "paleta do admin mantém o ID");
        Utilizador lia = new AssistenteDigital(carregados).getAutenticacao().entrar("lia@teste.pt", "senha1");
        verificar(lia.getPedidos().get(64).getOutfit().getCombinacaoCores().temMesmasCores(List.of(preto, branco)),
                "histórico mantém a paleta que foi aplicada");
    }
}
