package cl.irondisk.backend.exception;

// Credenciales o token incorrectos, responde 401
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
