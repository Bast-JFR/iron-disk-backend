package cl.irondisk.backend.model;

// Roles del sistema segun el enunciado
public enum Rol {
    // Acceso total
    ADMIN,
    // Solo ve la lista y el detalle de productos y ordenes
    VENDEDOR,
    // Solo accede a la tienda
    CLIENTE
}
