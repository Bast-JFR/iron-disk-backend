package cl.irondisk.backend.exception;

// El recurso pedido no existe, responde 404
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
