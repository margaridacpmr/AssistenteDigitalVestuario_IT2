package org;

import org.interfaceutilizador.InterfaceConsola;
import org.interfaceutilizador.Textos;
import org.modelo.AssistenteDigital;
import org.modelo.DadosAplicacao;
import org.persistencia.FicheiroDados;
import java.io.IOException;
import java.util.Scanner;

/** Executar esta classe no IntelliJ. */
public class Main {
    public static void main(String[] args) {
        Textos textos = new Textos("pt");
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Idioma / Language / Langue: 1 - Português, 2 - English, 3 - Français");
            String escolha = scanner.nextLine().trim();
            textos.mudarIdioma(escolha.equals("2") ? "en" : escolha.equals("3") ? "fr" : "pt");
            FicheiroDados ficheiro = new FicheiroDados("dados/vestuario.dat");
            DadosAplicacao dados = ficheiro.carregar();
            AssistenteDigital sistema = new AssistenteDigital(dados);
            new InterfaceConsola(sistema, ficheiro, scanner, textos).executar();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(textos.get("erro") + textos.mensagemErro(e.getMessage(), "erroFicheiroDados"));
        }
    }
}
