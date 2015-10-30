import java.util.HashSet;

/**
 * Created by José David on 30/10/2015.
 */
public class ParejaDeTrayectorias implements Comparable{
    Trayectoria t1;
    Trayectoria t2;

    public ParejaDeTrayectorias(Trayectoria t1, Trayectoria t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public int getCantidadLocalesEnComun()
    {
        HashSet<String> tiposLocalesT1 = new HashSet<>();
        for (Local l : t1.getLocalesVisitados())
        {
            tiposLocalesT1.add(l.getTipo());
        }

        HashSet<String> tiposLocalesT2 = new HashSet<>();
        for (Local l : t2.getLocalesVisitados())
        {
            tiposLocalesT2.add(l.getTipo());
        }

        tiposLocalesT1.retainAll(tiposLocalesT2);

        return tiposLocalesT1.size();
    }

    public Trayectoria getT1() {
        return t1;
    }

    public Trayectoria getT2() {
        return t2;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof ParejaDeTrayectorias) {
            ParejaDeTrayectorias t = (ParejaDeTrayectorias) o;
            return this.getCantidadLocalesEnComun() - t.getCantidadLocalesEnComun();
        }
        return 0;
    }
}
