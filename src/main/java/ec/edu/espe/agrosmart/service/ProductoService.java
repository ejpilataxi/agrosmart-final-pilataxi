package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.domain.ProductoFilters;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.mapper.ProductoMapper;
import ec.edu.espe.agrosmart.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    private static final Producto PRODUCTO_GENERICO =
            new Producto(
                    0L,
                    "PRODUCTO GENERICO",
                    "Banano",
                    BigDecimal.ZERO,
                    List.of()
            );


    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }


    public Flux<Producto> obtenerProductosComercializables() {

        return Mono.fromCallable(repository::findAll)
                // La consulta JPA es bloqueante, por eso se manda a otro hilo
                .subscribeOn(Schedulers.boundedElastic())

                // Convierte la lista obtenida del repositorio en un flujo reactivo
                .flatMapMany(Flux::fromIterable)

                // Convierte entidad Hibernate a modelo de dominio
                .map(ProductoMapper::toDominio)

                // Crea un nuevo Producto con nombre en mayúsculas
                .map(ProductoFilters.A_MAYUSCULAS)

                // Filtra productos con precio válido y correos configurados
                .filter(ProductoFilters.IS_VALID)

                // Solo registra información, no modifica el producto
                .doOnNext(ProductoFilters.LOG_PRODUCTO)

                // Si después del filtro no queda nada, devuelve uno genérico
                .defaultIfEmpty(PRODUCTO_GENERICO);
    }



    public Mono<Producto> buscarPorId(Long id) {

        return Mono.fromCallable(() -> repository.findById(id))

                // Ejecuta la consulta bloqueante fuera del event loop
                .subscribeOn(Schedulers.boundedElastic())

                // Optional vacío se transforma en Mono vacío
                .flatMap(Mono::justOrEmpty)

                // Entidad ORM -> dominio inmutable
                .map(ProductoMapper::toDominio)

                // Si no existe, genera error dentro del flujo reactivo
                .switchIfEmpty(
                        Mono.error(new ProductoNoEncontradoException(id))
                );
    }
}