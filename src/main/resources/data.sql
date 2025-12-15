-- Borra datos existentes e inicializa el contador de ID.
-- Descomenta esta línea si quieres que los productos se borren y vuelvan a cargar en cada inicio:
TRUNCATE TABLE product RESTART IDENTITY CASCADE; 

-- NOTA: Los precios a continuación están en Pesos Argentinos (ARS).

-- ===========================================
--  CÁMARAS IP (DAHUA) - ID 1, 2, 3
-- ===========================================
INSERT INTO product (name, description, type, unit_price, stock, brand) VALUES
('Cámara Domo IP 4K', 'Domo de ultra alta resolución 4K con visión nocturna de 30m. Ideal para interiores.', 'IP', 125990.00, 50, 'DAHUA');

INSERT INTO product (name, description, type, unit_price, stock, brand) VALUES
('Cámara Bullet IP 5MP', 'Cámara tipo bala de 5 megapíxeles. Resistente a la intemperie (IP67).', 'IP', 95500.00, 80, 'DAHUA');

INSERT INTO product (name, description, type, unit_price, stock, brand) VALUES
('Cámara PTZ IP 2MP Zoom', 'Cámara con movimiento Pan-Tilt-Zoom. Ideal para vigilancia de grandes áreas.', 'IP', 350000.00, 15, 'DAHUA');


-- ===========================================
--  CÁMARAS IP (HIKVISION) - ID 4, 5
-- ===========================================
INSERT INTO product (name, description, type, unit_price, stock, brand) VALUES
('Mini Domo IP 2MP', 'Cámara discreta de 2MP con micrófono integrado. Perfecta para oficinas.', 'IP', 75000.00, 120, 'HIKVISION');

INSERT INTO product (name, description, type, unit_price, stock, brand) VALUES
('Cámara Bullet Termal IP', 'Cámara avanzada con tecnología termal y detección de temperatura.', 'IP', 890000.00, 5, 'HIKVISION');


-- ===========================================
--  CÁMARAS ANALÓGICAS (HIKVISION / DAHUA / GENERIC) - ID 6, 7, 8
-- ===========================================
INSERT INTO product (name, description, type, unit_price, stock, brand) VALUES
('Domo Analógico 1080P', 'Domo HDCVI 1080P de bajo costo para sistemas de vigilancia básicos.', 'ANALOGICA', 45000.00, 200, 'DAHUA');

INSERT INTO product (name, description, type, unit_price, stock, brand) VALUES
('Bullet Analógica 4K', 'Cámara TurboHD de alta definición, 8MP. Lente varifocal.', 'ANALOGICA', 110750.00, 60, 'HIKVISION');

INSERT INTO product (name, description, type, unit_price, stock, brand) VALUES
('Kit 4 Cámaras 720P', 'Kit de iniciación con 4 cámaras analógicas y DVR de 4 canales.', 'ANALOGICA', 199990.00, 30, 'GENERIC');