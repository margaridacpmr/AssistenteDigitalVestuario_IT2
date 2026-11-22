package org.interfaceutilizador;

import org.modelo.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/** Traduções usadas apenas para apresentar os dados na consola. */
public class Textos {
    private ResourceBundle frases;

    public Textos(String idioma) { mudarIdioma(idioma); }

    public void mudarIdioma(String idioma) {
        frases = ResourceBundle.getBundle("mensagens", Locale.forLanguageTag(idioma));
    }

    public String get(String chave) { return frases.getString(chave); }

    public String mensagemErro(String erro, String chaveGenerica) {
        if (erro != null) {
            int separador = erro.indexOf(':');
            String chave = separador < 0 ? erro : erro.substring(0, separador);
            if (frases.containsKey(chave)) {
                return separador < 0 ? get(chave) : get(chave) + ": " + erro.substring(separador + 1);
            }
        }
        return get(chaveGenerica);
    }

    public String formatar(Object valor) {
        if (valor instanceof ElementoCatalogo elemento) {
            String nome = elemento.getDesignacao();
            String chave = "catalogo." + nome.toLowerCase(Locale.ROOT);
            return frases.containsKey(chave) ? get(chave) : nome;
        }
        if (valor instanceof List<?> lista) {
            ArrayList<String> textos = new ArrayList<>();
            for (Object item : lista) { textos.add(formatar(item)); }
            return textos.toString();
        }
        if (valor instanceof PecaVestuario peca) {
            return peca.getNome() + " (" + formatar(peca.getTipoRoupa()) + ")";
        }
        if (valor instanceof CombinacaoPossivel tipos) {
            return nomeCombinacao(tipos.getDesignacao(), tipos.getTiposRoupa()) + " " + formatar(tipos.getTiposRoupa());
        }
        if (valor instanceof CombinacaoCores paleta) {
            return nomeCombinacao(paleta.getDesignacao(), paleta.getCores()) + " " + formatar(paleta.getCores());
        }
        return String.valueOf(valor);
    }

    public String nomeCombinacao(String nome, List<? extends ElementoCatalogo> elementos) {
        ArrayList<String> originais = new ArrayList<>();
        ArrayList<String> traduzidos = new ArrayList<>();
        for (ElementoCatalogo elemento : elementos) {
            originais.add(elemento.getDesignacao());
            traduzidos.add(formatar(elemento));
        }
        // As combinações predefinidas têm como nome a junção das designações originais.
        return nome.equals(String.join(" + ", originais)) ? String.join(" + ", traduzidos) : nome;
    }
}
