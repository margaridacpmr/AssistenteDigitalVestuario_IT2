package org.persistencia;

import org.modelo.DadosAplicacao;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Usa Serializable: é um ficheiro de objetos Java, NÃO uma base de dados SQL. */
public class FicheiroDados {
    private final File ficheiro;

    public FicheiroDados(String caminho) { this.ficheiro = new File(caminho); }

    public DadosAplicacao carregar() throws IOException, ClassNotFoundException {
        if (!ficheiro.exists()) {
            DadosAplicacao dados = new DadosAplicacao();
            dados.garantirCombinacoesPredefinidas();
            guardar(dados);
            return dados;
        }
        DadosAplicacao dados;
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ficheiro))) {
            dados = (DadosAplicacao) entrada.readObject();
        }
        // Mantém utilizadores e peças existentes; só acrescenta valores em falta.
        boolean catalogosAlterados = dados.garantirCatalogosPredefinidos();
        boolean combinacoesAlteradas = dados.garantirCombinacoesPredefinidas();
        boolean combinacoesCoresAlteradas = dados.garantirCombinacoesCoresPredefinidas();
        if (catalogosAlterados || combinacoesAlteradas || combinacoesCoresAlteradas) {
            guardar(dados);
        }
        return dados;
    }

    public void guardar(DadosAplicacao dados) throws IOException {
        File pasta = ficheiro.getParentFile();
        if (pasta != null && !pasta.exists() && !pasta.mkdirs()) {
            throw new IOException("Não foi possível criar a pasta de dados.");
        }
        Path destino = ficheiro.toPath().toAbsolutePath();
        Path temporario = Files.createTempFile(destino.getParent(), "vestuario-", ".tmp");
        try {
            // Conserva o ficheiro anterior se a serialização for interrompida.
            try (ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(temporario.toFile()))) {
                saida.writeObject(dados);
            }
            try {
                Files.move(temporario, destino, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
    }
}
