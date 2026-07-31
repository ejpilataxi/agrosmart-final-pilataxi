package ec.edu.espe.agrosmart.config;

import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductoRepository repository;

    public DataSeeder(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {

        if (repository.count() == 0) {

            // 3 productos válidos

            repository.save(crearProducto(
                    "Banano Cavendish",
                    "5.50",
                    100,
                    "Banano",
                    "ventas@agrosmart.com"
            ));

            repository.save(crearProducto(
                    "Banano Organico",
                    "8.75",
                    200,
                    "Banano",
                    "admin@agrosmart.com"
            ));

            repository.save(crearProducto(
                    "Banano Premium",
                    "12.00",
                    150,
                    "Banano",
                    "cliente@agrosmart.com"
            ));


            // 2 inválidos solicitados

            // precio_usd = 0
            repository.save(crearProducto(
                    "Banano Precio Cero",
                    "0",
                    50,
                    "Banano",
                    "prueba@agrosmart.com"
            ));

            // correos vacíos
            repository.save(crearProducto(
                    "Banano Sin Correos",
                    "4.50",
                    80,
                    "Banano",
                    ""
            ));
        }
    }


    private ProductoEntity crearProducto(
            String nombre,
            String precio,
            Integer stock,
            String categoria,
            String correos
    ) {

        ProductoEntity producto = new ProductoEntity();

        producto.setNombreProducto(nombre);
        producto.setPrecioUsd(new BigDecimal(precio));
        producto.setStockKg(stock);
        producto.setCategoria(categoria);
        producto.setCorreosNotificacion(correos);

        return producto;
    }
}