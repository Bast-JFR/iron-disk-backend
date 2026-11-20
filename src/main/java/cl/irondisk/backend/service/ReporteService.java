package cl.irondisk.backend.service;

import cl.irondisk.backend.dto.ProductoVendidoResponse;
import cl.irondisk.backend.dto.ResumenVentasResponse;
import cl.irondisk.backend.model.EstadoBoleta;
import cl.irondisk.backend.repository.BoletaRepository;
import cl.irondisk.backend.repository.DetalleBoletaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Calcula el resumen de ventas, las boletas anuladas no cuentan
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private static final int CANTIDAD_MAS_VENDIDOS = 5;

    private final BoletaRepository boletaRepo;
    private final DetalleBoletaRepository detalleRepo;

    public ResumenVentasResponse resumen() {
        long cantidad = boletaRepo.countByEstadoNot(EstadoBoleta.ANULADA);
        long total = boletaRepo.sumarTotalSinEstado(EstadoBoleta.ANULADA);
        // Si no hay ventas el promedio es 0 para no dividir por cero
        long promedio = cantidad == 0 ? 0 : Math.round((double) total / cantidad);
        List<ProductoVendidoResponse> masVendidos =
                detalleRepo.masVendidos(EstadoBoleta.ANULADA, PageRequest.of(0, CANTIDAD_MAS_VENDIDOS));
        return new ResumenVentasResponse(total, cantidad, promedio, masVendidos);
    }
}
