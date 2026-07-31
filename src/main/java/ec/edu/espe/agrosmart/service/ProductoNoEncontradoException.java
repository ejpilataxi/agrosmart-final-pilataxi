package ec.edu.espe.agrosmart.service;

public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(Long id) {
        super("Producto no encontrado con id: " + id);
    }
}