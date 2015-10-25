/**
 * Created by José David on 25/10/2015.
 */
public class Opcion {

    Opciones opcion;
    String valor;

    public Opcion(Opciones opcion, String valor) {
        this.opcion = opcion;
        this.valor = valor;
    }

    public Opciones getOpcion() {
        return opcion;
    }

    public String getValor() {
        return valor;
    }
}
