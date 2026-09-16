package com.mycompany.sistemacontable.servicio;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {
    private static final int ITERACIONES = 120000;
    private static final int LONGITUD = 256;

    private PasswordUtil() {}

    public static String generarHash(String password) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            byte[] hash = derivar(password.toCharArray(), salt, ITERACIONES);
            return "PBKDF2$" + ITERACIONES + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo proteger la contraseña.", e);
        }
    }

    public static boolean verificar(String password, String almacenado) {
        try {
            if (almacenado == null || !almacenado.startsWith("PBKDF2$")) return false;
            String[] partes = almacenado.split("\\$");
            if (partes.length != 4) return false;
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] actual = derivar(password.toCharArray(), salt, iteraciones);
            if (actual.length != esperado.length) return false;
            int diferencia = 0;
            for (int i = 0; i < actual.length; i++) diferencia |= actual[i] ^ esperado[i];
            return diferencia == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] derivar(char[] password, byte[] salt, int iteraciones) throws Exception {
        KeySpec spec = new PBEKeySpec(password, salt, iteraciones, LONGITUD);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }
}
