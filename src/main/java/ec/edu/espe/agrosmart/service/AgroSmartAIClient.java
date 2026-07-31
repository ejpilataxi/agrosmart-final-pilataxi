package ec.edu.espe.agrosmart.service;

import java.time.Duration;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class AgroSmartAIClient {

    private final AgroSmartAIService aiService;

    public AgroSmartAIClient(AgroSmartAIService aiService) {
        this.aiService = aiService;
    }


    public Mono<String> generarPublicidad(String producto, String audiencia) {
        return Mono.fromCallable(() -> aiService.generarPublicidad(producto, audiencia))
                .subscribeOn(Schedulers.boundedElastic())   // la llamada HTTP bloquea
                .timeout(Duration.ofSeconds(30))
                // onErrorResume: un fallo del proveedor externo no puede tumbar el endpoint
                .onErrorResume(e -> Mono.just(
                        "Publicidad no disponible en este momento (" + e.getClass().getSimpleName() + ")"));
    }
}