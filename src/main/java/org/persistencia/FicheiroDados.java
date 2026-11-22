package org.persistencia;

import org.controladores.Seguranca;
import org.modelo.DadosAplicacao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;

/** Guarda objetos Serializable num ficheiro cifrado e mantém a origem legada intacta. */
public class FicheiroDados {
    private final Path legado;
    private final Path cifrado;
    private final Path chave;

    public FicheiroDados(String caminho) {
        legado = Path.of(caminho).toAbsolutePath();
        cifrado = legado.resolveSibling(legado.getFileName() + ".enc");
        chave = legado.resolveSibling(legado.getFileName() + ".key");
    }

    public DadosAplicacao carregar() throws IOException, ClassNotFoundException {
        boolean temCifrado = !Files.notExists(cifrado);
        DadosAplicacao dados;
        if (temCifrado) {
            dados = desserializar(new ByteArrayInputStream(lerDadosCifrados()));
        } else if (!Files.notExists(legado)) {
            dados = desserializar(Files.newInputStream(legado));
        } else {
            dados = new DadosAplicacao();
        }
        boolean catalogosAlterados = dados.garantirCatalogosPredefinidos();
        boolean combinacoesAlteradas = dados.garantirCombinacoesPredefinidas();
        boolean combinacoesCoresAlteradas = dados.garantirCombinacoesCoresPredefinidas();
        if (!temCifrado || catalogosAlterados || combinacoesAlteradas || combinacoesCoresAlteradas) {
            guardar(dados);
        }
        return dados;
    }

    public void guardar(DadosAplicacao dados) throws IOException {
        Files.createDirectories(cifrado.getParent());
        byte[] chaveDados = obterChaveParaGravar();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream saida = new ObjectOutputStream(bytes)) {
            saida.writeObject(dados);
        }

        byte[] protegidos;
        try {
            protegidos = Seguranca.cifrarDados(bytes.toByteArray(), chaveDados);
        } catch (GeneralSecurityException e) {
            throw new IOException("erroCifrarDados", e);
        }
        Path temporario = Files.createTempFile(cifrado.getParent(), "vestuario-", ".tmp");
        try {
            Files.write(temporario, protegidos);
            try {
                Files.move(temporario, cifrado, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, cifrado, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
    }

    private DadosAplicacao desserializar(InputStream fluxo) throws IOException, ClassNotFoundException {
        try (InputStream origem = fluxo; ObjectInputStream entrada = new ObjectInputStream(origem)) {
            entrada.setObjectInputFilter(ObjectInputFilter.Config.createFilter(
                    "org.modelo.*;java.base/java.lang.*;java.base/java.util.*;java.base/java.time.*;!*"));
            if (entrada.readObject() instanceof DadosAplicacao dados) { return dados; }
            throw new IOException("formatoDadosInvalido");
        }
    }

    private byte[] lerDadosCifrados() throws IOException {
        if (Files.notExists(chave)) { throw new IOException("chaveDadosEmFalta"); }
        try {
            return Seguranca.decifrarDados(Files.readAllBytes(cifrado), Files.readAllBytes(chave));
        } catch (GeneralSecurityException e) {
            throw new IOException("erroDecifrarDados", e);
        }
    }

    private byte[] obterChaveParaGravar() throws IOException {
        if (Files.notExists(chave)) {
            if (!Files.notExists(cifrado)) { throw new IOException("chaveDadosEmFalta"); }
            // O temporário tem permissões restritas no sistema de ficheiros.
            Path temporario = Files.createTempFile(chave.getParent(), "vestuario-chave-", ".tmp");
            try {
                Files.write(temporario, Seguranca.criarChaveDados());
                try {
                    Files.move(temporario, chave);
                } catch (FileAlreadyExistsException e) {
                    // Outra instância já criou a chave.
                }
            } finally {
                Files.deleteIfExists(temporario);
            }
        }
        return Files.readAllBytes(chave);
    }
}
