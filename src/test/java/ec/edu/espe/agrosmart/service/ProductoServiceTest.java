package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.repository.ProductoRepository;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class ProductoServiceTest {


    @Test
    void obtenerProductosComercializables_conTresValidosYDosInvalidos_debeEmitirSoloLosValidos() {

        // Arrange
        ProductoRepository repo = mock(ProductoRepository.class);

        when(repo.findAll()).thenReturn(datosDePrueba());

        ProductoService service = new ProductoService(repo);


        // Act
        Flux<?> flujo = service.obtenerProductosComercializables();


        // Assert
        StepVerifier.create(flujo)
                .expectNextCount(3)
                .verifyComplete();
    }


    @Test
    void obtenerProductosComercializables_sinProductosValidos_debeEmitirProductoGenerico() {

        // Arrange
        ProductoRepository repo = mock(ProductoRepository.class);

        ProductoEntity invalido = new ProductoEntity();
        invalido.setIdProducto(1L);
        invalido.setNombreProducto("Producto invalido");
        invalido.setCategoria("Banano");
        invalido.setPrecioUsd(BigDecimal.ZERO);
        invalido.setStockKg(10);
        invalido.setCorreosNotificacion("");

        when(repo.findAll())
                .thenReturn(List.of(invalido));

        ProductoService service = new ProductoService(repo);


        // Act
        Flux<?> flujo = service.obtenerProductosComercializables();


        // Assert
        StepVerifier.create(flujo)
                .expectNextCount(1)
                .verifyComplete();
    }


    @Test
    void buscarPorId_cuandoNoExiste_debeLanzarProductoNoEncontradoException() {

        // Arrange
        ProductoRepository repo = mock(ProductoRepository.class);

        when(repo.findById(999L))
                .thenReturn(Optional.empty());

        ProductoService service = new ProductoService(repo);


        // Act
        Flux<?> flujo = service.buscarPorId(999L).flux();


        // Assert
        StepVerifier.create(flujo)
                .expectError(ProductoNoEncontradoException.class)
                .verify();
    }


    private List<ProductoEntity> datosDePrueba() {

        ProductoEntity p1 = new ProductoEntity();
        p1.setIdProducto(1L);
        p1.setNombreProducto("Banano Cavendish");
        p1.setCategoria("Banano");
        p1.setPrecioUsd(new BigDecimal("5.50"));
        p1.setStockKg(100);
        p1.setCorreosNotificacion("ventas@agrosmart.ec");


        ProductoEntity p2 = new ProductoEntity();
        p2.setIdProducto(2L);
        p2.setNombreProducto("Banano Organico");
        p2.setCategoria("Banano");
        p2.setPrecioUsd(new BigDecimal("8.50"));
        p2.setStockKg(200);
        p2.setCorreosNotificacion("ventas@agrosmart.ec");


        ProductoEntity p3 = new ProductoEntity();
        p3.setIdProducto(3L);
        p3.setNombreProducto("Banano Premium");
        p3.setCategoria("Banano");
        p3.setPrecioUsd(new BigDecimal("10.00"));
        p3.setStockKg(300);
        p3.setCorreosNotificacion("ventas@agrosmart.ec");


        ProductoEntity invalidoPrecio = new ProductoEntity();
        invalidoPrecio.setIdProducto(4L);
        invalidoPrecio.setNombreProducto("Precio cero");
        invalidoPrecio.setCategoria("Banano");
        invalidoPrecio.setPrecioUsd(BigDecimal.ZERO);
        invalidoPrecio.setStockKg(100);
        invalidoPrecio.setCorreosNotificacion("ventas@agrosmart.ec");


        ProductoEntity invalidoCorreo = new ProductoEntity();
        invalidoCorreo.setIdProducto(5L);
        invalidoCorreo.setNombreProducto("Sin correo");
        invalidoCorreo.setCategoria("Banano");
        invalidoCorreo.setPrecioUsd(new BigDecimal("10.00"));
        invalidoCorreo.setStockKg(100);
        invalidoCorreo.setCorreosNotificacion("");


        return List.of(
                p1,
                p2,
                p3,
                invalidoPrecio,
                invalidoCorreo
        );
    }
}