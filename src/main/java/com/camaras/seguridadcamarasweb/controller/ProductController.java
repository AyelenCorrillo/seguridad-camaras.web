package com.camaras.seguridadcamarasweb.controller;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.camaras.seguridadcamarasweb.enums.ProductType;
import com.camaras.seguridadcamarasweb.model.Product;
import com.camaras.seguridadcamarasweb.service.ProductService;



@Controller
@RequestMapping("/catalogo")
public class ProductController {

    @Autowired 
    private ProductService productService;

    @GetMapping
    public String listProducts(
            
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String ordenar,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model) {


        // Normalizar el parámetro 'type' antes de la lógica de filtrado y el modelo
        String normalizedType = type;
        if (normalizedType == null || normalizedType.equalsIgnoreCase("Todos") || normalizedType.isEmpty()) {
            normalizedType = "Todos"; // Asegura que 'Todos' es el valor si no hay filtro
        }

        // 1. Ejecutar la búsqueda con TODOS los filtros (Llamada al Service)
        // El servicio se encarga de convertir a LOWERCASE y añadir comodines (%).
        
        List<Product> products = productService.findFilteredProducts(
            busqueda,
            normalizedType,
            brand, 
            minPrice, 
            maxPrice
        );

        // 2. Lógica de Ordenación (siempre al final de la lista obtenida)
        if (ordenar != null) {
            switch (ordenar) {
                case "precio-asc":
                    products.sort(Comparator.comparing(Product::getUnitPrice));
                    break;
                case "precio-desc":
                    products.sort(Comparator.comparing(Product::getUnitPrice).reversed());
                    break;
            }
        }
        
        // 3. Obtener Marcas disponibles dinámicamente (Delegamos al Servicio)
        // Esto asume que el método existe en ProductService.
        List<String> availableBrands = productService.findDistinctBrands(); 

        // 4. Agregar atributos al modelo
        model.addAttribute("products", products);
        model.addAttribute("availableBrands", availableBrands); 
        model.addAttribute("productTypes", ProductType.values()); 
        model.addAttribute("numResultados", products.size());
        
        // Mantener el estado de los filtros
        model.addAttribute("busquedaActual", busqueda);
        model.addAttribute("tipoActual", normalizedType);
        model.addAttribute("ordenarActual", ordenar);
        model.addAttribute("brandActual", brand);
        model.addAttribute("minPriceActual", minPrice);
        model.addAttribute("maxPriceActual", maxPrice);

        return "catalog"; 
    }    

    /**
     * Muestra la vista de detalle de un único producto por su ID.
     */
    @GetMapping("/{id}")
    public String viewProductDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        
        Optional<Product> product = productService.findProductById(id);
        
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "products/product-detail";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "El producto solicitado no existe.");
            return "redirect:/catalogo"; 
        }
    }

    
}
