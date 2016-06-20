package logiikka;

/** Kaksiuloitteinen koordinaatti, jolla on myös suunta. */
public class KoordSuunnalla extends Koord.Double {
    /** Koordinaatin suunta. */
    public final double suunta;
    /** Luo uuden koordinaatin suunnalla.
    * @param x Arvo x-akselilla.
    * @param y Arvo y-akselilla.
    * @param suunta Koordinaatin suunta. */
    public KoordSuunnalla(double x, double y, double suunta) {
        super(x, y);
        this.suunta = suunta;
    }
    
}
