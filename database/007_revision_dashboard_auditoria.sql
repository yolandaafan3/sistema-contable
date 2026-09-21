USE sistema_contable;

-- La auditoría ahora se registra desde la aplicación (DAO), no mediante triggers.
-- Esto conserva de forma confiable el usuario autenticado y evita duplicados.
DROP TRIGGER IF EXISTS trg_asiento_ai;
DROP TRIGGER IF EXISTS trg_asiento_au;
DROP TRIGGER IF EXISTS trg_operacion_ai;
DROP TRIGGER IF EXISTS trg_operacion_au;

CREATE TABLE IF NOT EXISTS auditoria (
 id_auditoria BIGINT AUTO_INCREMENT PRIMARY KEY,
 id_usuario INT NULL,
 entidad VARCHAR(60) NOT NULL,
 id_entidad BIGINT NOT NULL,
 accion ENUM('CREO','MODIFICO','ANULO','APROBO','ELIMINO') NOT NULL,
 datos_anteriores TEXT NULL,
 datos_nuevos TEXT NULL,
 fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 INDEX idx_auditoria_fecha(fecha),
 INDEX idx_auditoria_entidad(entidad,id_entidad),
 CONSTRAINT fk_auditoria_usuario FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario)
) ENGINE=InnoDB;
