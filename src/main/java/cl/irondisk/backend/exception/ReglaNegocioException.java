package cl.irondisk.backend.exception;

// Se rompe una regla de negocio, por ejemplo un codigo repetido o stock insuficiente, responde 409
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
