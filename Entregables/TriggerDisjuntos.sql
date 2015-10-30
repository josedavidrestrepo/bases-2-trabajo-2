CREATE OR REPLACE TRIGGER NoMismoTiempo 
before insert ON HISTORIAL_VISITANTE
FOR EACH ROW 
DECLARE
XmlViejo HISTORIAL_VISITANTE.TRAYECTORIAS%TYPE ;--Xml que se intentea ingresar
TYPE ArregloControl IS TABLE OF ControlDeTrayectoria INDEX BY BINARY_INTEGER;-- Tipo Arreglo de Control de trayectoria
Arreglo ArregloControl;-- Arreglo de tipo de control 
XMLAcutal XMLTYPE;-- xml para tipo actual 
Control ControlDeTrayectoria ;
StringArregloHorario VARCHAR(5000);-- Arreglo de strings
Trayectoria  DBMS_XMLDOM.DOMNodelist; -- Lista de Puntos 
 Tiempo DBMS_XMLDOM.DOMNode; -- Nodo que representa el elemento "platos" de cada restaurante
Puntos DBMS_XMLDOM.DOMNodelist; -- Lista de nodos donde se guardarán los platos ofrecidos por cada restaurante
codigo_nodo DBMS_XMLDOM.DOMNode; -- Nodo que representa el código de un plato ofrecido por un restaurante
codigo_varchar VARCHAR2(50); -- Cadena que representa el código de un plato ofrecido por un restaurante
NumeroPuntos NUMBER(2);-- Numero de puntos que hay en la trayectoria actual
NumeroPosicion Number(2);-- Posicion donde se almacena la aparicion del :
Horas VARCHAR2(2);--Horas del punto 
Minutos VARCHAR2(2);--Horas del punto 
TimpoTotal1 Number(5);--Horas + minutos del punto inicial
TimpoTotal2 Number(5);--Horas + minutos del punto final
BEGIN
 XmlViejo := :NEW.trayectorias;-- Obtencion del XML Nested
 For i IN 1.. XmlViejo.COUNT LOOP-- Para cada una de las trayectorias
 XMLAcutal:=XmlViejo(I);-- Selecciona el XML type
Trayectoria:=DBMS_XMLDOM.getElementsByTagName(DBMS_XMLDOM.getDocumentElement(DBMS_XMLDOM.newDOMDocument(XMLAcutal)), 'punto');
NumeroPuntos:=DBMS_XMLDOM.GETLENGTH(Trayectoria);
 For j in 0..NumeroPuntos-1 LOOP
 Tiempo:= DBMS_XMLDOM.ITEM(Trayectoria,J);
 StringArregloHorario:=DBMS_XMLDOM.GETNODEVALUE(DBMS_XMLDOM.GETFIRSTCHILD(DBMS_XMLDOM.ITEM(DBMS_XMLDOM.GETCHILDNODES(Tiempo),2)));
 NumeroPosicion:= (INSTR (StringArregloHorario, ':'));
  Horas:= SUBSTR(StringArregloHorario,0,NumeroPosicion-1);
  Minutos:=SUBSTR(StringArregloHorario,NumeroPosicion+1,2);
  TimpoTotal1:= to_number(Horas||Minutos);
	IF J+1 <= NumeroPuntos-1 THEN
	Tiempo:= DBMS_XMLDOM.ITEM(Trayectoria,J+1);
 StringArregloHorario:=DBMS_XMLDOM.GETNODEVALUE(DBMS_XMLDOM.GETFIRSTCHILD(DBMS_XMLDOM.ITEM(DBMS_XMLDOM.GETCHILDNODES(Tiempo),2)));
 NumeroPosicion:= (INSTR (StringArregloHorario, ':'));
  Horas:= SUBSTR(StringArregloHorario,0,NumeroPosicion-1);
  Minutos:=SUBSTR(StringArregloHorario,NumeroPosicion+1,2);
	    TimpoTotal2:= to_number(Horas||Minutos);
   Control := ControlDeTrayectoria(TimpoTotal1,TimpoTotal2);
   For x in 0..Arreglo.COUNT-1 LOOP
  IF Control.TiempoInicial < Arreglo(x).TiempoInicial THEN
  IF Control.TiempoFinal <= Arreglo(x).TiempoInicial THEN
  NULL;
  ELSE
   RAISE_APPLICATION_ERROR(-20501,'Tiempo No disjunto de   '||Control.TiempoInicial||':'||Control.TiempoFinal ||'y se solapa con '||Arreglo(x).TiempoInicial ||':'||Arreglo(x).TiempoFinal);
  END IF;
  ELSE IF Control.TiempoInicial = Arreglo(x).TiempoInicial THEN
  RAISE_APPLICATION_ERROR(-20501,'Tiempo No disjunto de   '||Control.TiempoInicial||':'||Control.TiempoFinal ||'y se solapa con '||Arreglo(x).TiempoInicial ||':'||Arreglo(x).TiempoFinal);
  ELSIF  Control.TiempoInicial >= Arreglo(x).TiempoFinal THEN
    NULL;
	ElSE 
	 RAISE_APPLICATION_ERROR(-20501,'Tiempo No disjunto de  '||Control.TiempoInicial||':'||Control.TiempoFinal ||'y se solapa con '||Arreglo(x).TiempoInicial ||':'||Arreglo(x).TiempoFinal);
   END IF;
   END IF;
     END LOOP;
    Arreglo(Arreglo.COUNT):= Control;
	END IF;
 END LOOP;
 NULL;
 END LOOP;
NULL;
END;
/
