package org.controladores;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/** Exemplo didático: não guarda palavras-passe diretamente. */
public class Seguranca {
    public static String criarSal() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
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
