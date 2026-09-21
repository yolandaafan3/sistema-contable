# sistema-contable
Sistema Contable Proyecto


## Novedades 2026 - instalación
Antes de abrir esta versión sobre una base existente, ejecutar `database/006_novedades_contables.sql`.
Este script agrega conciliación bancaria, bitácora de auditoría y sus índices/triggers. Maven descargará Apache POI (Excel) y PDFBox (PDF) al compilar por primera vez.

La conciliación acepta CSV, XLS y XLSX. Encabezados reconocidos: Fecha, Descripción/Concepto, Referencia y Monto; alternativamente Débito/Cargo y Crédito/Abono.
