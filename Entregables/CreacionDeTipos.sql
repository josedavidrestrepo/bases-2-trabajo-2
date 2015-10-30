CREATE OR REPLACE TYPE ControlDeTrayectoria AS  Object ( TiempoInicial Number(5), TiempoFinal Number(5));-- Clase que controla el tiempo inicial y final de un tramo de una trayectoria
/
CREATE OR REPLACE TYPE TrayectoriaPunto AS Object( X Number(4),Y Number(4));-- Clase que respresenta un punto dentro de una trayectoria
/
CREATE OR REPLACE TYPE PuntoLocal AS Object (X1 Number(4),X2 Number(4),Y1 Number(4),Y2 Number(4),Tipo Varchar2(20), Categoria Number(4) , Nombre Varchar2(5000) , Id Number(8));-- Clase que representa la posicion de un negocio en el mapa
/
CREATE OR REPLACE TYPE PuntoBasico AS Object (X Number(4),Y Number(4));-- Un punto basico que conforma un vertice del cuadrado de un local.
/