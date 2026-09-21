package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.dao.ProductoDAO;
import com.mycompany.sistemacontable.modelo.Producto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProductoService {

    private final ProductoDAO productoDAO;

    public ProductoService() {
        productoDAO = new ProductoDAO();
    }

    public void registrar(Producto producto) {
        validar(producto);
        normalizarDatosMaestros(producto);

        // El catálogo de productos no crea inventario por sí solo.
        producto.setCostoInicial(BigDecimal.ZERO.setScale(2));
        producto.setValorInventarioInicial(BigDecimal.ZERO.setScale(2));
        producto.setExistenciaInicial(BigDecimal.ZERO.setScale(6));
        producto.setExistenciaActual(BigDecimal.ZERO.setScale(6));
        producto.setActivo(true);

        productoDAO.insertar(producto);
    }

    public void actualizar(Producto producto) {
        if (producto.getIdProducto() <= 0) {
            throw new IllegalArgumentException("El producto que desea actualizar no es válido.");
        }

        validar(producto);
        normalizarDatosMaestros(producto);

        // ProductoDAO.actualizar solo modifica la ficha. No toca existencias,
        // costo inicial ni capas PEPS ya registradas.
        productoDAO.actualizar(producto);
    }


    public void cambiarEstado(int idProducto, boolean activo) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("El producto seleccionado no es válido.");
        }
        productoDAO.cambiarActivo(idProducto, activo);
    }

    private void validar(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto es obligatorio.");
        }

        if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El código del producto es obligatorio.");
        }

        if (producto.getCodigo().trim().length() > 50) {
            throw new IllegalArgumentException("El código no puede superar 50 caracteres.");
        }

        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }

        if (producto.getNombre().trim().length() > 150) {
            throw new IllegalArgumentException("El nombre no puede superar 150 caracteres.");
        }

        if (producto.getDescripcion() != null
                && producto.getDescripcion().trim().length() > 255) {
            throw new IllegalArgumentException("La descripción no puede superar 255 caracteres.");
        }

        validarNoNegativo(producto.getCostoCompra(), "El costo de compra de referencia");
        validarNoNegativo(producto.getPrecioVenta(), "El precio de venta de referencia");
    }

    private void validarNoNegativo(BigDecimal valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(campo + " no puede ser negativo.");
        }
    }

    private void normalizarDatosMaestros(Producto producto) {
        producto.setCodigo(producto.getCodigo().trim().toUpperCase());
        producto.setNombre(producto.getNombre().trim());
        producto.setDescripcion(limpiar(producto.getDescripcion()));
        producto.setCostoCompra(producto.getCostoCompra().setScale(2, RoundingMode.HALF_UP));
        producto.setPrecioVenta(producto.getPrecioVenta().setScale(2, RoundingMode.HALF_UP));
    }

    private String limpiar(String valor) {
        if (valor == null || valor.isBlank()) return null;
        return valor.trim();
    }
}
