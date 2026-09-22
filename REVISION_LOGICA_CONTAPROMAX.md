# Revisión lógica ContaProMax

Base revisada: `ContaProMax_Periodos_Graficas_Reportes.zip` (la versión anterior al parche específico de $444.26).

## Correcciones aplicadas

- Apertura de período: ya no toma automáticamente `valor_inventario_inicial` de Productos. La apertura automática queda Caja contra Capital Social.
- Inventario por período: el recálculo PEPS trabaja únicamente con operaciones del período ABIERTO; los movimientos/capas de períodos cerrados no se eliminan al recalcular el período actual.
- Al iniciar un período se reinicia el estado operativo de existencias de Productos a cero. Los movimientos históricos permanecen en Operaciones, Asientos y Kardex.
- Balance General: ya no reemplaza el saldo contable de Inventario con un saldo PEPS global.
- Estado de Resultados: ya no consulta el PEPS global de todos los ejercicios para calcular el período actual.
- Devoluciones: una devolución de compra o venta no puede apuntar a una operación perteneciente a otro período contable.
- Cierre de período: se bloquea si existen asientos BORRADOR o asientos contabilizados descuadrados.
- Apertura de período: valida previamente que las cuentas operativas críticas del catálogo fijo de ContaProMax existan y estén activas.
- Dashboard: se retiró la sección de alertas/pendientes y se dejó orientado a análisis visual con Ventas por mes, Compras por mes, Costos y gastos y Estructura contable.
- Dashboard sin período abierto: muestra valores vacíos/cero en vez de fallar por ausencia de período.

## Decisión sobre catálogo de cuentas

No se cambió la estructura del catálogo. Como se descartó la importación de catálogos personalizados, mantener el catálogo fijo es coherente con el código actual, que todavía utiliza algunos códigos contables concretos en reglas de negocio. Se agregó una validación temprana de las cuentas críticas al iniciar un período para evitar fallos a mitad de una transacción.

## Regla de períodos adoptada

ContaProMax trata cada período como un ejercicio independiente para las operaciones visibles y el inventario operativo. Cerrar un período no borra su histórico. Abrir uno nuevo no arrastra silenciosamente saldos ni existencias del anterior. Si en el futuro se desea continuidad real de saldos entre ejercicios, debe implementarse como un proceso explícito de cierre/apertura, no mediante lecturas globales de tablas.

## Pruebas manuales recomendadas antes de entregar

1. Abrir período y registrar únicamente Caja $50,000 / Capital Social $50,000. Inventario y utilidad deben quedar en $0.
2. Compra de inventario; verificar Diario, Mayor, Balance de Comprobación, IVA y Kardex.
3. Venta parcial; verificar PEPS y existencia.
4. Devolución de compra y devolución de venta del mismo período.
5. Intentar devolución contra una operación de un período cerrado: debe bloquearse.
6. Intentar cerrar con un asiento BORRADOR o descuadrado: debe bloquearse.
7. Cerrar período, revisar que sus datos no aparezcan como período actual y exportar histórico.
8. Abrir período siguiente y comprobar Dashboard/Diario/Mayor/Balance sin datos del anterior.
