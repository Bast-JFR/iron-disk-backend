package cl.irondisk.backend.service;

import java.util.List;

// Reglas de formato del RUN y del correo, las mismas que valida el frontend
public final class ValidadorUsuario {

    public static final List<String> DOMINIOS_PERMITIDOS = List.of("duoc.cl", "profesor.duoc.cl", "gmail.com");

    private ValidadorUsuario() {
    }

    // Deja el RUN sin espacios y con la K en mayuscula
    public static String normalizarRun(String run) {
        return run == null ? null : run.trim().toUpperCase();
    }

    // RUN sin puntos ni guion, de 7 a 9 caracteres y con digito verificador correcto
    public static boolean runValido(String run) {
        if (run == null || !run.matches("^\\d{6,8}[0-9K]$")) {
            return false;
        }
        String cuerpo = run.substring(0, run.length() - 1);
        String dv = run.substring(run.length() - 1);
        return calcularDv(cuerpo).equals(dv);
    }

    // Digito verificador con el algoritmo del modulo 11
    static String calcularDv(String cuerpo) {
        int suma = 0;
        int multiplo = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplo;
            multiplo = multiplo == 7 ? 2 : multiplo + 1;
        }
        int resto = 11 - (suma % 11);
        if (resto == 11) {
            return "0";
        }
        if (resto == 10) {
            return "K";
        }
        return String.valueOf(resto);
    }

    // Solo se aceptan los dominios permitidos
    public static boolean dominioPermitido(String correo) {
        if (correo == null || !correo.contains("@")) {
            return false;
        }
        String dominio = correo.substring(correo.lastIndexOf('@') + 1).toLowerCase();
        return DOMINIOS_PERMITIDOS.contains(dominio);
    }
}
