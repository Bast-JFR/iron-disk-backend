package cl.irondisk.backend.service;

import cl.irondisk.backend.dto.ProductoRequest;
import cl.irondisk.backend.dto.ProductoResponse;
import cl.irondisk.backend.exception.RecursoNoEncontradoException;
import cl.irondisk.backend.exception.ReglaNegocioException;
import cl.irondisk.backend.model.Categoria;
import cl.irondisk.backend.model.Producto;
import cl.irondisk.backend.repository.CategoriaRepository;
import cl.irondisk.backend.repository.DetalleBoletaRepository;
import cl.irondisk.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Logica de negocio de los productos
// Reglas: el codigo es unico, la categoria debe existir y no se elimina un producto que ya se vendio
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepo;
    private final CategoriaRepository categoriaRepo;
    private final DetalleBoletaRepository detalleRepo;

    public List<ProductoResponse> listar() {
        return productoRepo.findAllByOrderByIdAsc().stream().map(ProductoResponse::from).toList();
    }

    public ProductoResponse obtener(Long id) {
        return ProductoResponse.from(buscar(id));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest req) {
        validarCodigoUnico(req.codigo(), null);
        Producto p = new Producto();
        copiar(req, p);
        return ProductoResponse.from(productoRepo.save(p));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest req) {
        Producto p = buscar(id);
        validarCodigoUnico(req.codigo(), id);
        copiar(req, p);
        return ProductoResponse.from(p);
    }

    // Un producto que aparece en boletas no se elimina para no perder el historial de ventas
    @Transactional
    public void eliminar(Long id) {
        Producto p = buscar(id);
        if (detalleRepo.existsByProductoId(id)) {
            throw new ReglaNegocioException("No se puede eliminar \"" + p.getNombre() + "\" porque aparece en boletas de venta");
        }
        productoRepo.delete(p);
    }

    private Producto buscar(Long id) {
        return productoRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El producto " + id + " no existe"));
    }

    private void validarCodigoUnico(String codigo, Long idActual) {
        productoRepo.findByCodigoIgnoreCase(codigo.trim())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe un producto con el código " + otro.getCodigo());
                });
    }

    private void copiar(ProductoRequest req, Producto p) {
        Categoria categoria = categoriaRepo.findById(req.categoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("La categoría " + req.categoriaId() + " no existe"));
        p.setCodigo(req.codigo().trim().toUpperCase());
        p.setNombre(req.nombre().trim());
        p.setArtista(req.artista().trim());
        p.setFormato(req.formato());
        p.setDescripcion(req.descripcion());
        p.setPrecio(req.precio());
        p.setOfertaPorcentaje(req.ofertaPorcentaje() == null ? 0 : req.ofertaPorcentaje());
        p.setStock(req.stock());
        p.setStockCritico(req.stockCritico() == null ? 0 : req.stockCritico());
        p.setImagen(req.imagen());
        p.setCategoria(categoria);
    }
}
