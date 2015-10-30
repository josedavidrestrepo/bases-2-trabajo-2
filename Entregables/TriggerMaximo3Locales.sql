CREATE OR REPLACE TRIGGER NoTipos -- Trigger para que no existen mas de 3 tipos diferentes en una misma trayectoria-Y el pormedio sea mayor a 3
before insert ON HISTORIAL_VISITANTE
FOR EACH ROW 
DECLARE
XmlViejo HISTORIAL_VISITANTE.TRAYECTORIAS%TYPE ;--Xml que se intentea ingresar
TYPE ArregloTipo IS TABLE OF VARCHAR2(20) INDEX BY BINARY_INTEGER;-- Tipo arreglo de los tipos de local que existen en una trayectoria
ArregloTipo1 ArregloTipo;--Arreglo de tipo Arreglo tipo
PosicionEnX1 Number;-- Posicion de un local en X1
PosicionEnY1 Number;-- Posicion de un local en Y1
PosicionEnXT Number;-- Posicion Actual en X dentro de la recta 
PosicionEnYT Number;-- Posicion Actual de Y dentro de la recta
PosicionEnX2 Number;-- Posicion en X2 del local
PosicionEnY2 Number;-- Posicion en Y2 del lcoal 
PosicionPX1 Number;-- Posicion en P1
PromedioDeTrayectoria NUMBER;
NumeroDeLocalesInterceptados Number;-- Contador de los locales que fueron interceptados
CambioDeLocal BOOLEAN :=FALSE;-- Variable que se encarga de ver cuando se pasa a otro local 
PosicionPY1 Number;
PosicionPX2 Number;
UltimaPoscionEstabaDentro BOOLEAN;
NumeroDeCorridas Number:=1000000;
 Dentro_Del_Local BOOLEAN;-- Boolean para comprobar si un punto se encuentra dentro del local
 PrimerLocal BOOLEAN:=False;
 CompraborDeInicio BOOLEAN :=FALSE;--Boolean para comprobar si el punto inicial se encuentra dentro de una trayectoria
TramosDeTrayectoria Number:=1000000;--Numero de tramos en que se dividira el tramo de  la trayectoria
ExisteDentroDelVector BOOLEAN;--Bool para ver si un local existe dentro del arreglo de locales actuales visitados
ExisteDentroDeArreglo BOOLEAN;--Bool para ver si un Tipo de local existe dentro del arreglo de Tipos actuales 
PosicionPY2 Number;
PosicionXmenor Number;-- Posicion X menor de un local 
PosicionXmayor Number;-- Posicion X mayor de un local
PosicionYmayor Number;-- Posicion Y mayor de un local 
PosicionYmenor Number;-- Posicion Y menor de un local
P1 PuntoBasico;--P1 de la posicion de un local
p2 PuntoBasico;--P2 de la posicion de un local
p3 PuntoBasico;--P3 de la posicion de un local
p4 PuntoBasico;--P4 de la posicion de un local
PosicionEnXrelativa Number;--Posicion Actual en X de tramo 
PosicionEnYrelativa Number;--Posicion Actual en Y del tramo 
Trayectoria  DBMS_XMLDOM.DOMNodelist; -- Lista de Puntos 
 Posicionx DBMS_XMLDOM.DOMNode; -- Nodo que representa el elemento "platos" de cada restaurante
Puntos DBMS_XMLDOM.DOMNodelist; -- Lista de nodos donde se guardarán los platos ofrecidos por cada restaurante
XmlLocal XMLTYPE;
TYPE ArregloPuntoLocal IS TABLE OF PuntoLocal INDEX BY BINARY_INTEGER;-- Tipo Arreglo de Control de trayectoria
PuntoActual PuntoLocal;
UltimoLocalVisitado PuntoLocal:=NULL;
Arreglo1 ArregloPuntoLocal;-- Arreglo que contine TODOS LOS LOCALES ACTUALES EN LA BD 
Arreglo2 ArregloPuntoLocal;
XMLActual XMLTYPE;-- xml para tipo actual 
Cursor CursorDePuntos  is
Select * from local;--Cursor que selecciona todo la informacion de los locales
StringPosicionX VARCHAR(5000);-- String posicion X
StringPosicionY VARCHAR(5000);-- String posicion Y
StringPosicionX1 VARCHAR(5000);-- String posicion X
StringPosicionY1 VARCHAR(5000);-- String posicion Y
StringPosicionX2 VARCHAR(5000);-- String posicion X
StringPosicionY2 VARCHAR(5000);-- String posicion Y
Punto  DBMS_XMLDOM.DOMELEMENT; -- Lista de Puntos
PuntoEnX DBMS_XMLDOM.DOMNode; -- Nodo que representa el elemento "platos" de cada restaurante
PuntoEnY DBMS_XMLDOM.DOMNode; -- Nodo que representa el elemento "platos" de cada restaurante
Puntos DBMS_XMLDOM.DOMNodelist; -- Lista de nodos donde se guardarán los platos ofrecidos por cada restaurante
NumeroPuntos NUMBER(2);-- Numero de puntos que hay en la trayectoria actual
BEGIN
For PuntosL IN CursorDePuntos LOOP -- Para cada una de las los locales 
XmlLocal:= PuntosL.coordenadas;-- Se obitne las cordenadas de local
Punto:=DBMS_XMLDOM.getDocumentElement(DBMS_XMLDOM.newDOMDocument(XmlLocal));--Se obtiene el elemento de coordenadas
StringPosicionX1:=DBMS_XMLDOM.GETNODEVALUE(DBMS_XMLDOM.GETFIRSTCHILD(DBMS_XMLDOM.item((DBMS_XMLDOM.GETCHILDRENBYTAGNAME(Punto,'x1')),0)));-- Obtiene y almacena la coordena X1
StringPosicionY1:=DBMS_XMLDOM.GETNODEVALUE(DBMS_XMLDOM.GETFIRSTCHILD(DBMS_XMLDOM.item((DBMS_XMLDOM.GETCHILDRENBYTAGNAME(Punto,'y1')),0)));-- Obtiene y almacena la coordena Y1
StringPosicionX2:=DBMS_XMLDOM.GETNODEVALUE(DBMS_XMLDOM.GETFIRSTCHILD(DBMS_XMLDOM.item((DBMS_XMLDOM.GETCHILDRENBYTAGNAME(Punto,'x2')),0)));-- Obtiene y almacena la coordena X2
StringPosicionY2:=DBMS_XMLDOM.GETNODEVALUE(DBMS_XMLDOM.GETFIRSTCHILD(DBMS_XMLDOM.item((DBMS_XMLDOM.GETCHILDRENBYTAGNAME(Punto,'y2')),0)));-- Obtiene y almacena la coordena Y2
PuntoActual:=PuntoLocal(to_number(StringPosicionX1),to_number(StringPosicionX2),to_number(StringPosicionY1),to_number(StringPosicionY2),PuntosL.tipo,PuntosL.categoria ,PuntosL.nombre,PuntosL.id);--Crea el punto local que representa la posicion del local
Arreglo1(Arreglo1.COUNT):=PuntoActual;--Almacena en el arreglo la posicion del local actual
END LOOP;-- Fin del loop para hayar cada uno de los locales.
 XmlViejo := :NEW.trayectorias;-- Obtencion del XML Nested
 For i IN 1.. XmlViejo.COUNT LOOP-- Para cada una de las trayectorias
 PromedioDeTrayectoria:=0;
 Arreglo2.DELETE();-- Elimina la informacion del arreglo 2
 XMLActual:=XmlViejo(I);-- Selecciona el XML type
 CompraborDeInicio:=FALSE;
Trayectoria:=DBMS_XMLDOM.getElementsByTagName(DBMS_XMLDOM.getDocumentElement(DBMS_XMLDOM.newDOMDocument(XMLActual)), 'punto');
NumeroPuntos:=DBMS_XMLDOM.GETLENGTH(Trayectoria);
 For j in 0..NumeroPuntos-1 LOOP
 Posicionx:= DBMS_XMLDOM.ITEM(Trayectoria,J);
 CambioDeLocal  :=FALSE;
PosicionEnX1:=to_number(DBMS_XMLDOM.GETNODEVALUE(DBMS_XMLDOM.GETFIRSTCHILD(DBMS_XMLDOM.ITEM(DBMS_XMLDOM.GETCHILDNODES(Posicionx),0))));
PosicionEnY1:=to_number(DBMS_XMLDOM.GETNODEVALUE(DBMS_XMLDOM.GETFIRSTCHILD(DBMS_XMLDOM.ITEM(DBMS_XMLDOM.GETCHILDNODES(PosicionX),1))));
if j=0 then 
   dbms_output.put_line('Almenos entre una Vez');
  For T in 0..Arreglo1.COUNT-1 LOOP 
	 PosicionPX1:=Arreglo1(T).X1;
	  PosicionPY1:=Arreglo1(T).Y1;
	  PosicionPX2:=Arreglo1(T).X2;
	  PosicionPY2:=Arreglo1(T).Y2;
	  IF   PosicionPX1< PosicionPX2 Then 
	  PosicionXmayor:=PosicionPX2;
	  PosicionXmenor:= PosicionPX1;
	  Else 
	   PosicionXmayor:=PosicionPX1;
	  PosicionXmenor:= PosicionPX2;
	  END IF;
	  IF  PosicionPY1< PosicionPY2 THEN
	  PosicionYmayor:=PosicionPY2;
	 PosicionYmenor:=PosicionPY1;
	 ELSE
	  PosicionYmayor:=PosicionPY1;
	 PosicionYmenor:=PosicionPY2;
	  END IF ;
	  IF PosicionEnX1 > PosicionXmenor AND PosicionEnX1 < PosicionXmayor AND PosicionEnY1 > PosicionYmenor AND PosicionEnY1 < PosicionYmayor THEN
	     dbms_output.put_line('Estoy en un local en mi punto 0');
		CompraborDeInicio:=TRUE;
		 END IF;
		 END LOOP;
		 END IF;
	IF J+1 <= NumeroPuntos-1 THEN
	Posicionx:= DBMS_XMLDOM.ITEM(Trayectoria,J+1);
PosicionEnX2:=to_number(DBMS_XMLDOM.GETNODEVALUE(DBMS_XMLDOM.GETFIRSTCHILD(DBMS_XMLDOM.ITEM(DBMS_XMLDOM.GETCHILDNODES(Posicionx),0))));
PosicionEnY2:=to_number(DBMS_XMLDOM.GETNODEVALUE(DBMS_XMLDOM.GETFIRSTCHILD(DBMS_XMLDOM.ITEM(DBMS_XMLDOM.GETCHILDNODES(PosicionX),1))));
 PosicionEnXrelativa:=PosicionEnX2-PosicionEnX1;
 PosicionEnYrelativa:=PosicionEnY2-PosicionEnY1;
 dbms_output.put_line('Posicion en Tracto X1 :'||PosicionEnX1);
  dbms_output.put_line('Posicion en Y1 :'||PosicionEnY1);
   dbms_output.put_line('Posicion en X2 :'||PosicionEnX2);
    dbms_output.put_line('Posicion en Y2 :'||PosicionEnY2);
		--  dbms_output.put_line(ATAN(1/0));
		For H IN 0..NumeroDeCorridas LOOP
		 Dentro_Del_Local:=FALSE;
		NumeroDeLocalesInterceptados:=0;
		ExisteDentroDelVector:=FALSE;
		PosicionEnXT :=PosicionEnX1+ H*(PosicionEnXrelativa/NumeroDeCorridas);
        PosicionEnYT:= PosicionEnY1+ H*(PosicionEnYrelativa/NumeroDeCorridas);
		--Dentro_Del_Local:=False;--Set de Falso como condicional inicial de un punto
	  For T in 0..Arreglo1.COUNT-1 LOOP 
	 PosicionPX1:=Arreglo1(T).X1;
	  PosicionPY1:=Arreglo1(T).Y1;
	  PosicionPX2:=Arreglo1(T).X2;
	  PosicionPY2:=Arreglo1(T).Y2;
	  IF   PosicionPX1< PosicionPX2 Then 
	  PosicionXmayor:=PosicionPX2;
	  PosicionXmenor:= PosicionPX1;
	  Else 
	   PosicionXmayor:=PosicionPX1;
	  PosicionXmenor:= PosicionPX2;
	  END IF;
	  IF  PosicionPY1< PosicionPY2 THEN
	  PosicionYmayor:=PosicionPY2;
	 PosicionYmenor:=PosicionPY1;
	 ELSE
	  PosicionYmayor:=PosicionPY1;
	 PosicionYmenor:=PosicionPY2;
	  END IF ;
	  IF (PosicionEnXT > PosicionXmenor AND PosicionEnXT < PosicionXmayor AND PosicionEnYT > PosicionYmenor AND PosicionEnYT < PosicionYmayor )  OR (  UltimaPoscionEstabaDentro = TRUE AND PosicionEnXT >= PosicionXmenor AND PosicionEnXT <= PosicionXmayor AND PosicionEnYT >= PosicionYmenor AND PosicionEnYT <= PosicionYmayor   )  THEN
	  IF UltimaPoscionEstabaDentro = TRUE AND( PosicionEnXT = PosicionXmenor OR PosicionEnXT = PosicionXmayor OR PosicionEnYT = PosicionYmenor OR PosicionEnYT = PosicionYmayor )  THEN
			 dbms_output.put_line('Estaba en una esquina'||H);
			   dbms_output.put_line('X1:'|| PosicionXmenor);
    dbms_output.put_line('X2:'|| PosicionXmayor);
	  dbms_output.put_line('Y1:'|| PosicionYmenor);
	    dbms_output.put_line('Y2:'|| PosicionYmayor);
		dbms_output.put_line('Estaba en la posicion '||H);
			   dbms_output.put_line('X1:'|| PosicionEnXT);
    dbms_output.put_line('X2:'|| PosicionEnYT);
	
		END IF;
	    Dentro_Del_Local:=TRUE;
		NumeroDeLocalesInterceptados:=NumeroDeLocalesInterceptados + 1;
		UltimaPoscionEstabaDentro:=TRUE;
		
	  FOR A IN  0..Arreglo2.COUNT-1 LOOP
	  IF  Arreglo2(A).X1 = Arreglo1(T).X1 AND Arreglo2(A).Y1 = Arreglo1(T).Y1  AND Arreglo2(A).X2 = Arreglo1(T).X2  AND Arreglo2(A).Y2 = Arreglo1(T).Y2 THEN 
	  ExisteDentroDelVector:=true;
	  END IF;
		END LOOP;
		IF ExisteDentroDelVector = FALSE  THEN
		IF UltimoLocalVisitado IS NULL THEN 
		UltimoLocalVisitado:=Arreglo1(T);--El Ultimo Local que se visito.
		-- Arreglo2(Arreglo2.COUNT):=Arreglo1(T) ;
		 ELSIF  UltimoLocalVisitado.X1 <> Arreglo1(T).X1 OR UltimoLocalVisitado.Y1 <> Arreglo1(T).Y1  OR UltimoLocalVisitado.X2 <> Arreglo1(T).X2  OR UltimoLocalVisitado.Y2 <> Arreglo1(T).Y2 THEN 
		 if NumeroDeLocalesInterceptados < 2 then 
		CambioDeLocal:=TRUE;
		End if;
		 dbms_output.put_line('Cambie de local'||H);
		END IF ;
		 END IF;
		END IF;
	  END LOOP;
	  IF Dentro_Del_Local = FALSE  OR CambioDeLocal = TRUE THEN
	  		
	  IF UltimoLocalVisitado IS NOT NULL THEN
	  
	  IF CompraborDeInicio= FALSE THEN
	 
	  CambioDeLocal:=FALSE;
	  Dentro_Del_Local:=FALSE;-- Nuebo
	  UltimaPoscionEstabaDentro:=FALSE;
	  Arreglo2(Arreglo2.COUNT):=UltimoLocalVisitado;
	     dbms_output.put_line('Sali Del Local: '||H);
	   dbms_output.put_line('Posicion en  X1  :'||TO_CHAR (PosicionEnXT));
  dbms_output.put_line('Posicion en Y1 :'||TO_CHAR (PosicionEnYT));
  dbms_output.put_line('X1:'|| UltimoLocalVisitado.X1);
    dbms_output.put_line('X2:'|| UltimoLocalVisitado.X2);
	  dbms_output.put_line('Y1:'|| UltimoLocalVisitado.Y1);
	    dbms_output.put_line('Y2:'|| UltimoLocalVisitado.Y2);
	  ELSE 
	    dbms_output.put_line('Estoy saliendo de un local antes de iniciar mi trayectoria');
	  CompraborDeInicio:=FALSE;
	  UltimoLocalVisitado:=NULL;
	  end if;
  UltimoLocalVisitado:=NULL;
	  END IF;
	  END IF;
	    END LOOP;
	  END IF;
 END LOOP;
   dbms_output.put_line('Numero de locales  vistados en la trayectoria:'||Arreglo2.COUNT);
    ArregloTipo1.DELETE();
  For Z In 0..Arreglo2.COUNT-1 LOOP
    ExisteDentroDeArreglo := FALSE;
   IF ArregloTipo1.COUNT = 0 THEN
   ArregloTipo1(ArregloTipo1.COUNT):=Arreglo2(z).Tipo;
    ExisteDentroDeArreglo := TRUE;
   END IF;
   FOR M IN 0..ArregloTipo1.COUNT-1 LOOP
   IF ArregloTipo1(M) = Arreglo2(Z).Tipo THEN
   ExisteDentroDeArreglo := TRUE;
   END IF;
   END LOOP;
     IF ExisteDentroDeArreglo = FALSE THEN 
	ArregloTipo1(ArregloTipo1.Count):=Arreglo2(Z).Tipo;
   END IF;
  IF ArregloTipo1.COUNT > 3 THEN
 RAISE_APPLICATION_ERROR(-20501,'En el recorrido existen mas de 3 tipos diferentes de locales #'||ArregloTipo1.COUNT);
END IF;
For Z In 0..Arreglo2.COUNT-1 LOOP
   PromedioDeTrayectoria:=PromedioDeTrayectoria+Arreglo2(z).categoria;
  END LOOP;
  IF Arreglo2.COUNT > 0 THEN 
    dbms_output.put_line('Promedio De la Trayectoria:'||PromedioDeTrayectoria/Arreglo2.COUNT);
	IF PromedioDeTrayectoria/Arreglo2.COUNT < 3 THEN 
	 RAISE_APPLICATION_ERROR(-20502,'La categoria promedio de la trayectoria es inferior a 3');
	END IF;
	END IF;

END LOOP; 
For Z In 0..Arreglo2.COUNT-1 LOOP
   Insert into tablaaux VALUES( Arreglo2(z).Id , Arreglo2(z).nombre , Arreglo2(z).categoria , Arreglo2(z).tipo , :NEW.identificacion  , i , Arreglo2(z).X1, Arreglo2(z).Y1 ,Arreglo2(z).X2 ,Arreglo2(z).Y2);
  END LOOP;
END LOOP;
  

END;
/