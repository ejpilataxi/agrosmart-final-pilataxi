package ec.edu.espe.agrosmart.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {


    @Test
    void constructor_conDatosValidos_gettersDevuelvenValoresEsperados() {

        Producto producto = new Producto(
                1L,
                "Cacao fino",
                "Cacao",
                new BigDecimal("120.50"),
                List.of("ventas@agrosmart.ec")
        );

        assertEquals(1L, producto.getId());
        assertEquals("Cacao fino", producto.getNombre());
        assertEquals("Cacao", producto.getCategoria());
        assertEquals(new BigDecimal("120.50"), producto.getPrecioUsd());
        assertEquals(
                List.of("ventas@agrosmart.ec"),
                producto.getCorreosNotificacion()
        );
    }


    @Test
    void getCorreosNotificacion_alMutarLaListaOriginal_noDebeAfectarAlProducto() {

        // Arrange
        List<String> correos = new ArrayList<>();
        correos.add("ventas@agrosmart.ec");

        Producto producto = new Producto(
                1L,
                "Cacao fino",
                "Cacao",
                new BigDecimal("120.50"),
                correos
        );

        // Act
        correos.add("intruso@mail.com");

        // Assert
        assertEquals(1, producto.getCorreosNotificacion().size());
        assertNotSame(correos, producto.getCorreosNotificacion());
    }


    @Test
    void getCorreosNotificacion_listaDevueltaDebeSerInmodificable() {

        // Arrange
        Producto producto = new Producto(
                1L,
                "Cacao fino",
                "Cacao",
                new BigDecimal("120.50"),
                List.of("ventas@agrosmart.ec")
        );

        // Assert
        assertThrows(
                UnsupportedOperationException.class,
                () -> producto.getCorreosNotificacion()
                        .add("otro@mail.com")
        );
    }
}