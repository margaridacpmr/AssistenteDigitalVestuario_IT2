package org.controladores;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/** Hash de palavras-passe e cifragem autenticada dos dados. */
public class Seguranca {
    private static final SecureRandom ALEATORIO = new SecureRandom();
    private static final byte[] CABECALHO = {'A', 'D', 'V', 1};
    private static final int TAMANHO_NONCE = 12;
    private static final int BITS_AUTENTICACAO = 128;

    public static String criarSal() {
        return Base64.getEncoder().encodeToString(gerarBytes(16));
    }

    public static boolean verificarPalavraPasse(String palavraPasse, String sal, String hash) {
        byte[] calculado = calcularHash(palavraPasse, sal).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(calculado, hash.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] criarChaveDados() { return gerarBytes(32); }

    public static byte[] cifrarDados(byte[] dados, byte[] chave) throws GeneralSecurityException {
        byte[] nonce = gerarBytes(TAMANHO_NONCE);
        byte[] cifrado = configurarCifra(Cipher.ENCRYPT_MODE, chave, nonce).doFinal(dados);
        return ByteBuffer.allocate(CABECALHO.length + nonce.length + cifrado.length)
                .put(CABECALHO).put(nonce).put(cifrado).array();
    }

    public static byte[] decifrarDados(byte[] dados, byte[] chave) throws GeneralSecurityException {
        int inicioCifra = CABECALHO.length + TAMANHO_NONCE;
        if (dados.length < inicioCifra + BITS_AUTENTICACAO / 8
                || !Arrays.equals(CABECALHO, Arrays.copyOf(dados, CABECALHO.length))) {
            throw new GeneralSecurityException("formatoDadosInvalido");
        }
        byte[] nonce = Arrays.copyOfRange(dados, CABECALHO.length, inicioCifra);
        return configurarCifra(Cipher.DECRYPT_MODE, chave, nonce)
                .doFinal(dados, inicioCifra, dados.length - inicioCifra);
    }

    private static Cipher configurarCifra(int modo, byte[] chave, byte[] nonce) throws GeneralSecurityException {
        if (chave.length != 32) { throw new GeneralSecurityException("chaveDadosInvalida"); }
        Cipher cifra = Cipher.getInstance("AES/GCM/NoPadding");
        cifra.init(modo, new SecretKeySpec(chave, "AES"), new GCMParameterSpec(BITS_AUTENTICACAO, nonce));
        cifra.updateAAD(CABECALHO);
        return cifra;
    }

    private static byte[] gerarBytes(int tamanho) {
        byte[] bytes = new byte[tamanho];
        ALEATORIO.nextBytes(bytes);
        return bytes;
    }

    public static String calcularHash(String palavraPasse, String sal) {
        try {
            MessageDigest algoritmo = MessageDigest.getInstance("SHA-256");
            byte[] bytes = (sal + ":" + palavraPasse).getBytes(StandardCharsets.UTF_8);
            return Base64.getEncoder().encodeToString(algoritmo.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("O Java não suporta SHA-256.", e);
        }
    }
}
