package ec.edu.espe.agrosmart.service;


import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;


class AgroSmartAIClientTest {


    @Test
    void generarPublicidad_modeloResponde_emiteTextoGenerado() {

        // Arrange
        AgroSmartAIService ia = mock(AgroSmartAIService.class);

        when(ia.generarPublicidad("Banano","exportadores"))
                .thenReturn("Compra banano premium");


        AgroSmartAIClient service =
                new AgroSmartAIClient(ia);


        // Act + Assert
        StepVerifier.create(
                        service.generarPublicidad(
                                "Banano",
                                "exportadores"))
                .expectNext("Compra banano premium")
                .verifyComplete();
    }



    @Test
    void generarPublicidad_cuandoProveedorFalla_emiteMensajeRespaldo() {


        // Arrange
        AgroSmartAIService ia = mock(AgroSmartAIService.class);


        when(ia.generarPublicidad(any(), any()))
                .thenThrow(new RuntimeException("429"));


        AgroSmartAIClient service =
                new AgroSmartAIClient(ia);



        // Act + Assert
        StepVerifier.create(
                        service.generarPublicidad(
                                "Cacao",
                                "exportadores"))
                .expectNextMatches(
                        texto -> texto.contains("no disponible"))
                .verifyComplete();
    }
}