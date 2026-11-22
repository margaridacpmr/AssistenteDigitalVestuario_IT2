package org;

import org.interfaceutilizador.InterfaceConsola;
import org.modelo.AssistenteDigital;
import org.modelo.DadosAplicacao;
import org.persistencia.FicheiroDados;
import java.io.IOException;
import java.util.Scanner;

/** Executar esta classe no IntelliJ. */
public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Idioma / Language / Langue: 1 - Português, 2 - English, 3 - Français");
            String escolha = scanner.nextLine().trim();
            String idioma = escolha.equals("2") ? "en" : escolha.equals("3") ? "fr" : "pt";
            FicheiroDados ficheiro = new FicheiroDados("dados/vestuario.dat");
            DadosAplicacao dados = ficheiro.carregar();
            AssistenteDigital sistema = new AssistenteDigital(dados);
            new InterfaceConsola(sistema, ficheiro, scanner, idioma).executar();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro a carregar ou guardar dados: " + e.getMessage());
            System.err.println("Não apague o ficheiro de dados sem guardar uma cópia.");
        }
    }
}
