# ContaProMax

Sistema contable de escritorio desarrollado en Java 17 + Maven con MySQL 8+.

## Funciones principales
- Login y roles.
- Catálogo de cuentas.
- Registro de operaciones y asientos.
- Validación de Partida Doble.
- Libro Diario y Mayorización.
- Balance de Comprobación.
- Estado de Resultados.
- Balance General.
- Kardex PEPS.
- Períodos contables, IVA, auditoría y reportes.

## Instalación rápida
1. Instalar Java 17, Maven y MySQL 8+.
2. Ejecutar `schema.sql`.
3. Ejecutar `catalogo_cuentas.sql`.
4. Revisar la conexión en `Conexion.java`: `localhost:3306/sistema_contable`, usuario `root`.
5. Ejecutar `mvn clean package`.
6. Ejecutar el JAR generado en `target`.

## Base de datos
- Motor: MySQL 8+
- Base: `sistema_contable`
- Script de estructura: `schema.sql`
- Datos base y catálogo: `catalogo_cuentas.sql`

## Roles
| Rol | Descripción |
|---|---|
| ADMINISTRADOR | Acceso total, catálogo, IVA, usuarios y edición de asientos manuales |
| CONTABLE | Registra operaciones/asientos y consulta reportes |
| CONSULTA | Solo lectura del Libro Diario y reportes |

## Dependencias
- MySQL Connector/J 8.4.0
- Apache POI 5.4.1
- Apache PDFBox 3.0.5

## Documentación incluida
- `Manual_de_Usuario_ContaProMax.docx`
- `Manual_de_Instalacion_ContaProMax.docx`
- `Documentacion_Tecnica_y_Analisis_ContaProMax.docx`

> Para detalles de instalación y uso consulte los manuales incluidos.
