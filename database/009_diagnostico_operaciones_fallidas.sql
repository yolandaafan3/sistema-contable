USE sistema_contable;

-- Diagnóstico de operaciones de inventario que quedaron registradas por
-- versiones antiguas del sistema. Este script NO elimina nada.
-- Cambia el ID solamente si necesitas revisar otra operación.
SET @id_operacion_revisar = 23;

SELECT *
FROM operaciones
WHERE id_operacion = @id_operacion_revisar;

SELECT a.*
FROM asientos_contables a
WHERE a.id_operacion = @id_operacion_revisar;

SELECT d.*
FROM detalle_asientos d
JOIN asientos_contables a ON a.id_asiento = d.id_asiento
WHERE a.id_operacion = @id_operacion_revisar;

SELECT k.*
FROM kardex k
JOIN asientos_contables a ON a.id_asiento = k.id_asiento
WHERE a.id_operacion = @id_operacion_revisar;

SELECT dc.*
FROM detalle_consumo_peps dc
WHERE dc.id_operacion_salida = @id_operacion_revisar;
