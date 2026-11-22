package org.interfaceutilizador;

import java.util.Locale;
import java.util.ResourceBundle;

/** As traduções ficam em três ficheiros .properties, sem bibliotecas externas. */
public class Textos {
    private final ResourceBundle frases;

    public Textos(String idioma) {
        frases = ResourceBundle.getBundle("mensagens", Locale.forLanguageTag(idioma));
    }
    public String get(String chave) { return frases.getString(chave); }
}
