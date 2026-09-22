USE sistema_contable;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE detalle_consumo_peps;
TRUNCATE TABLE capas_peps;
TRUNCATE TABLE kardex;
TRUNCATE TABLE detalle_asientos;
TRUNCATE TABLE asientos_contables;
TRUNCATE TABLE operaciones;
UPDATE productos SET existencia_inicial = 0, existencia_actual = 0;
SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Datos operativos reiniciados correctamente' AS resultado;
