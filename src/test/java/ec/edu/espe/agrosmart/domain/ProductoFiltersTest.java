package ec.edu.espe.agrosmart.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoFiltersTest {


    @Test
    void isValid_conProductoValido_devuelveTrue() {

        Producto producto = new Producto(
                1L,
                "Banano",
                "Banano",
                new BigDecimal("5.50"),
                List.of("ventas@mail.com")
        );

        assertTrue(ProductoFilters.IS_VALID.test(producto));
    }


    @Test
    void isValid_conPrecioCero_devuelveFalse() {

        Producto producto = new Producto(
                1L,
                "Banano",
                "Banano",
                BigDecimal.ZERO,
                List.of("ventas@mail.com")
        );

        assertFalse(ProductoFilters.IS_VALID.test(producto));
    }


    @Test
    void isValid_sinCorreos_devuelveFalse() {

        Producto producto = new Producto(
                1L,
                "Banano",
                "Banano",
                new BigDecimal("5.50"),
                List.of()
        );

        assertFalse(ProductoFilters.IS_VALID.test(producto));
    }
}