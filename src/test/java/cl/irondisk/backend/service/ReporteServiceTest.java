package cl.irondisk.backend.service;

import cl.irondisk.backend.dto.ProductoVendidoResponse;
import cl.irondisk.backend.dto.ResumenVentasResponse;
import cl.irondisk.backend.model.EstadoBoleta;
import cl.irondisk.backend.repository.BoletaRepository;
import cl.irondisk.backend.repository.DetalleBoletaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Pruebas del resumen de ventas, las consultas se reemplazan por mocks
@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private BoletaRepository boletaRepo;
    @Mock
    private DetalleBoletaRepository detalleRepo;
    @InjectMocks
    private ReporteService service;

    @Test
    void calculaElResumenSinContarLasBoletasAnuladas() {
        // Las consultas solo responden cuando se les pide excluir las anuladas
        when(boletaRepo.countByEstadoNot(EstadoBoleta.ANULADA)).thenReturn(4L);
        when(boletaRepo.sumarTotalSinEstado(EstadoBoleta.ANULADA)).thenReturn(100000L);
        when(detalleRepo.masVendidos(any(EstadoBoleta.class), any()))
                .thenReturn(List.of(new ProductoVendidoResponse(2L, "IM-002", "Powerslave", 7L)));

        ResumenVentasResponse resumen = service.resumen();

        assertThat(resumen.totalVendido()).isEqualTo(100000);
        assertThat(resumen.cantidadBoletas()).isEqualTo(4);
        assertThat(resumen.ticketPromedio()).isEqualTo(25000);
        assertThat(resumen.masVendidos()).hasSize(1);
    }
}
