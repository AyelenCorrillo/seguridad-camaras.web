package com.camaras.seguridadcamarasweb.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.camaras.seguridadcamarasweb.model.Product;


public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE " +
           // Filtro de Texto (Busca en nombre o descripción)
           "(:searchPattern IS NULL OR LOWER(p.name) LIKE :searchPattern OR LOWER(p.description) LIKE :searchPattern) AND " +
           
           // Filtro por Tipo
           "(:type IS NULL OR p.type = :type) AND " +
           
           // Filtro por Marca
           "(:brand IS NULL OR p.brand = :brand) AND " +
           
           // Filtro por Precio Mínimo
           "(:minPrice IS NULL OR p.unitPrice >= :minPrice) AND " +
           
           // Filtro por Precio Máximo
           "(:maxPrice IS NULL OR p.unitPrice <= :maxPrice)")
    List<Product> findFiltered(
            @Param("searchPattern") String searchPattern,
            @Param("type") String type,
            @Param("brand") String brand,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    // Método para obtener las marcas
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL")
    List<String> findDistinctBrands();
    
    
}


    

