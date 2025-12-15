package com.camaras.seguridadcamarasweb.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.camaras.seguridadcamarasweb.model.Product;
import com.camaras.seguridadcamarasweb.repository.ProductRepository;



@Service // Marca la clase como un componente de servicio Spring
public class ProductService {

    // Inyecta el repositorio para interactuar con la base de datos
    @Autowired
    private ProductRepository productRepository;

    /**
     * Obtiene una lista de todas las marcas únicas disponibles en el catálogo.
    */
    public List<String> findDistinctBrands() {
        // Delega la consulta a un método que definiremos en el Repositorio
        return productRepository.findDistinctBrands();
    }

    /**
     * Obtiene todos los productos del catálogo.
     * Usado cuando no hay filtro de tipo.
     */
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Obtiene un producto por su ID.
     * Útil para ver el detalle del producto (si lo implementas).
     */
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }


    /**
     * Método para administración: guardar o actualizar un producto.
     */
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Obtiene productos aplicando filtros dinámicos y término de búsqueda.
     * Utiliza comodines para la búsqueda de texto.
     */
    public List<Product> findFilteredProducts(String busqueda, String type, String brand, BigDecimal minPrice, BigDecimal maxPrice) {
        

        // 1. Normalizar el filtro de Marca y Tipo (si son cadenas vacías)
        String normalizedBrand = (brand != null && brand.isEmpty()) ? null : brand;
        String normalizedType = (type != null && type.equalsIgnoreCase("Todos") || type.isEmpty()) ? null : type;

        // 1. Manejo del texto de búsqueda: Añadir comodines
        // Si el texto de búsqueda es null o vacío, lo dejamos null.
        String searchPattern = null;
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            // Añadimos los comodines % al inicio y al final
            searchPattern = "%" + busqueda.toLowerCase() + "%";
        }
        
        // 2. Llamada al Repositorio (Necesita un nuevo método en ProductRepository)
        return productRepository.findFiltered(
                searchPattern, 
                normalizedType,
                normalizedBrand,  
                minPrice, 
                maxPrice
        );
    }

    
}


