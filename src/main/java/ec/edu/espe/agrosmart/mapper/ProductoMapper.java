package ec.edu.espe.agrosmart.mapper;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.entity.ProductoEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProductoMapper {


    public static Producto toDominio(ProductoEntity entity) {


        List<String> correos;


        if(entity.getCorreosNotificacion() == null ||
                entity.getCorreosNotificacion().isBlank()) {

            correos = Collections.emptyList();

        } else {

            correos = Arrays.asList(
                    entity.getCorreosNotificacion().split(",")
            );
        }


        return new Producto(
                entity.getIdProducto(),
                entity.getNombreProducto(),
                entity.getCategoria(),
                entity.getPrecioUsd(),
                correos
        );
    }
}