CREATE OR REPLACE TRIGGER Borrado -- Trigger para que no existen mas de 3 tipos diferentes en una misma trayectoria
before delete ON HISTORIAL_VISITANTE
FOR EACH ROW 
DECLARE
BEGIN
NULL;
DELETE FROM tablaaux where identificacion_visitante  = :OLD.identificacion ;
END;
/