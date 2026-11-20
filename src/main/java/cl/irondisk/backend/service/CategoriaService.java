package cl.irondisk.backend.service;

import cl.irondisk.backend.dto.CategoriaRequest;
import cl.irondisk.backend.dto.CategoriaResponse;
import cl.irondisk.backend.exception.RecursoNoEncontradoException;
import cl.irondisk.backend.exception.ReglaNegocioException;
import cl.irondisk.backend.model.Categoria;
import cl.irondisk.backend.repository.CategoriaRepository;
import cl.irondisk.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Logica de negocio de las categorias
// Reglas: el nombre es unico y no se elimina una categoria que tiene productos
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository repo;
    private final ProductoRepository productoRepo;

    public List<CategoriaResponse> listar() {
        return repo.findAllByOrderByIdAsc().stream().map(this::aRespuesta).toList();
    }

    public CategoriaResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest req) {
        validarNombreUnico(req.nombre(), null);
        Categoria c = new Categoria();
        copiar(req, c);
        return aRespuesta(repo.save(c));
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest req) {
        Categoria c = buscar(id);
        validarNombreUnico(req.nombre(), id);
        copiar(req, c);
        return aRespuesta(c);
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria c = buscar(id);
        if (productoRepo.existsByCategoriaId(id)) {
            throw new ReglaNegocioException("No se puede eliminar la categoría \"" + c.getNombre() + "\" porque tiene productos");
        }
        repo.delete(c);
    }

    private Categoria buscar(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La categoría " + id + " no existe"));
    }

    private void validarNombreUnico(String nombre, Long idActual) {
        repo.findByNombreIgnoreCase(nombre.trim())
                .filter(otra -> !otra.getId().equals(idActual))
                .ifPresent(otra -> {
                    throw new ReglaNegocioException("Ya existe una categoría con el nombre " + otra.getNombre());
                });
    }

    private void copiar(CategoriaRequest req, Categoria c) {
        c.setNombre(req.nombre().trim());
        c.setDescripcion(req.descripcion());
        c.setImagen(req.imagen());
    }

    private CategoriaResponse aRespuesta(Categoria c) {
        return CategoriaResponse.from(c, productoRepo.countByCategoriaId(c.getId()));
    }
}
