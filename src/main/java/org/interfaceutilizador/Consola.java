package org.interfaceutilizador;

import org.modelo.ElementoCatalogo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Leitura e validação de entradas do utilizador; 0 permite cancelar seleções. */
public class Consola {
    private final Scanner scanner;
    private final Textos textos;

    public Consola(Scanner scanner, Textos textos) {
        this.scanner = scanner;
        this.textos = textos;
    }
    public String t(String chave) { return textos.get(chave); }

    public String apresentar(Object valor) { return textos.formatar(valor); }

    public String nomeCombinacao(String nome, List<? extends ElementoCatalogo> elementos) {
        return textos.nomeCombinacao(nome, elementos);
    }

    public void mostrarErro(IllegalArgumentException erro) {
        System.out.println(t("erro") + textos.mensagemErro(erro.getMessage(), "operacaoInvalida"));
    }

    public void mudarIdioma(String idioma) {
        textos.mudarIdioma(idioma);
    }

    public String ler(String pergunta) {
        System.out.print(pergunta + ": ");
        return scanner.nextLine().trim();
    }

    public int numero(String pergunta, int minimo, int maximo) {
        while (true) {
            try {
                int valor = Integer.parseInt(ler(pergunta));
                if (valor >= minimo && valor <= maximo) { return valor; }
            } catch (NumberFormatException e) {
                // A mensagem de erro é mostrada abaixo.
            }
            System.out.println(t("opcaoInvalida"));
        }
    }

    public boolean confirmar() {
        String resposta = ler(t("confirmar"));
        return resposta.equalsIgnoreCase("s") || resposta.equalsIgnoreCase("sim")
                || resposta.equalsIgnoreCase("y") || resposta.equalsIgnoreCase("yes")
                || resposta.equalsIgnoreCase("o") || resposta.equalsIgnoreCase("oui");
    }

    /** Escolhe um item pelo número mostrado no ecrã. */
    public <T> T escolher(String titulo, List<T> itens) {
        if (itens.isEmpty()) {
            System.out.println(t("listaVazia"));
            return null;
        }
        System.out.println("\n" + titulo);
        for (int i = 0; i < itens.size(); i++) {
            System.out.println((i + 1) + " - " + apresentar(itens.get(i)));
        }
        int escolha = numero(t("selecionarNumero"), 0, itens.size());
        return escolha == 0 ? null : itens.get(escolha - 1);
    }

    /** Exemplo de entrada: 1,3,4. É obrigatório selecionar pelo menos um. */
    public <T> List<T> escolherVarios(String titulo, List<T> itens) {
        ArrayList<T> selecionados = new ArrayList<>();
        if (itens.isEmpty()) { System.out.println(t("listaVazia")); return selecionados; }
        while (true) {
            System.out.println("\n" + titulo);
            for (int i = 0; i < itens.size(); i++) {
                System.out.println((i + 1) + " - " + apresentar(itens.get(i)));
            }
            String entrada = ler(t("variosNumeros"));
            if (entrada.equals("0")) { return new ArrayList<>(); }
            selecionados.clear();
            boolean valido = !entrada.isBlank();
            if (valido) {
                String[] numeros = entrada.split(",");
                for (String n : numeros) {
                    try {
                        int posicao = Integer.parseInt(n.trim());
                        if (posicao < 1 || posicao > itens.size()
                                || selecionados.contains(itens.get(posicao - 1))) {
                            valido = false;
                            break;
                        }
                        selecionados.add(itens.get(posicao - 1));
                    } catch (NumberFormatException e) {
                        valido = false;
                        break;
                    }
                }
            }
            if (valido && !selecionados.isEmpty()) { return new ArrayList<>(selecionados); }
            System.out.println(t("opcaoInvalida"));
        }
    }
}
