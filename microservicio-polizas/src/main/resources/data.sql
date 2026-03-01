-- TiposPoliza
INSERT INTO tipos_poliza (codigo, descripcion)
SELECT 'VIDA', 'Póliza de Vida'
WHERE NOT EXISTS (SELECT 1 FROM tipos_poliza WHERE codigo = 'VIDA');

INSERT INTO tipos_poliza (codigo, descripcion)
SELECT 'VEHICULO', 'Póliza de Vehículo'
WHERE NOT EXISTS (SELECT 1 FROM tipos_poliza WHERE codigo = 'VEHICULO');

INSERT INTO tipos_poliza (codigo, descripcion)
SELECT 'SALUD', 'Póliza de Salud'
WHERE NOT EXISTS (SELECT 1 FROM tipos_poliza WHERE codigo = 'SALUD');

-- TiposDocumento
INSERT INTO tipos_documento (id, codigo, descripcion, activo)
SELECT 1, 'CC', 'Cédula de Ciudadanía', 1
WHERE NOT EXISTS (SELECT 1 FROM tipos_documento WHERE id = 1);

INSERT INTO tipos_documento (id, codigo, descripcion, activo)
SELECT 2, 'CE', 'Cédula de Extranjería', 1
WHERE NOT EXISTS (SELECT 1 FROM tipos_documento WHERE id = 2);

INSERT INTO tipos_documento (id, codigo, descripcion, activo)
SELECT 3, 'PAS', 'Pasaporte', 1
WHERE NOT EXISTS (SELECT 1 FROM tipos_documento WHERE id = 3);

INSERT INTO tipos_documento (id, codigo, descripcion, activo)
SELECT 4, 'NIT', 'Número de Identificación Tributaria', 1
WHERE NOT EXISTS (SELECT 1 FROM tipos_documento WHERE id = 4);

-- TiposCoberturaSalud
INSERT INTO tipos_cobertura_salud (id, codigo, descripcion)
SELECT 1, 'SOLO_CLIENTE', 'Cobertura únicamente para el cliente'
WHERE NOT EXISTS (SELECT 1 FROM tipos_cobertura_salud WHERE id = 1);

INSERT INTO tipos_cobertura_salud (id, codigo, descripcion)
SELECT 2, 'CLIENTE_PADRES', 'Cobertura para cliente y sus padres'
WHERE NOT EXISTS (SELECT 1 FROM tipos_cobertura_salud WHERE id = 2);

INSERT INTO tipos_cobertura_salud (id, codigo, descripcion)
SELECT 3, 'CLIENTE_CONYUGE_HIJOS', 'Cobertura para cliente, cónyuge e hijos'
WHERE NOT EXISTS (SELECT 1 FROM tipos_cobertura_salud WHERE id = 3);

-- TiposParentesco
INSERT INTO tipos_parentesco (id, codigo, descripcion)
SELECT 1, 'PADRE', 'Padre del titular'
WHERE NOT EXISTS (SELECT 1 FROM tipos_parentesco WHERE id = 1);

INSERT INTO tipos_parentesco (id, codigo, descripcion)
SELECT 2, 'MADRE', 'Madre del titular'
WHERE NOT EXISTS (SELECT 1 FROM tipos_parentesco WHERE id = 2);

INSERT INTO tipos_parentesco (id, codigo, descripcion)
SELECT 3, 'CONYUGE', 'Cónyuge del titular'
WHERE NOT EXISTS (SELECT 1 FROM tipos_parentesco WHERE id = 3);

INSERT INTO tipos_parentesco (id, codigo, descripcion)
SELECT 4, 'HIJO', 'Hijo del titular'
WHERE NOT EXISTS (SELECT 1 FROM tipos_parentesco WHERE id = 4);
