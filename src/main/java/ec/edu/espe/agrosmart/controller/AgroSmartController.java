package ec.edu.espe.agrosmart.controller;

import ec.edu.espe.agrosmart.model.Producto;
import ec.edu.espe.agrosmart.service.AgroSmartAIClient;
import ec.edu.espe.agrosmart.service.AgroSmartAIService;
import ec.edu.espe.agrosmart.service.ProductoService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class AgroSmartController {

    private final ProductoService productoService;
    private final AgroSmartAIClient iaService;

    public AgroSmartController(
            ProductoService productoService,
            AgroSmartAIClient iaService) {

        this.productoService = productoService;
        this.iaService = iaService;
    }


    @GetMapping("/productos")
    public Flux<Producto> obtenerProductos() {

        return productoService.obtenerProductosComercializables();
    }


    @GetMapping("/productos/{id}")
    public Mono<Producto> buscarProducto(@PathVariable Long id) {

        return productoService.buscarPorId(id);
    }


    @GetMapping(value = "/agrosmart/publicidad", produces = "text/plain")
    public Mono<String> generarPublicidad(
            @RequestParam String producto,
            @RequestParam String audiencia) {

        return iaService.generarPublicidad(producto, audiencia);
    }
}