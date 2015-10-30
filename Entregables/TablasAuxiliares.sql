DROP TABLE tablaaux;
CREATE TABLE tablaaux(
id_local NUMBER(8),
nombre VARCHAR(50) NOT NULL,
categoria NUMBER(1) NOT NULL CHECK(categoria BETWEEN 1 AND 5),
tipo VARCHAR(3) NOT NULL,
identificacion_visitante NUMBER(8),
numero_trayectoria NUMBER(8),
X1 NUMBER(8),
Y1 NUMBER(8),
X2 NUMBER(8),
Y2 NUMBER(8)
);

DROP TABLE promedio;
CREATE TABLE promedio(
identificacion_visitante NUMBER(8),
numero_trayectoria NUMBER(8),
promedio NUMBER
);