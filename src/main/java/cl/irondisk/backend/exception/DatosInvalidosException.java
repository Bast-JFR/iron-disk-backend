package cl.irondisk.backend.exception;

// Datos que llegan mal formados o incompletos, responde 400
public class DatosInvalidosException extends RuntimeException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
