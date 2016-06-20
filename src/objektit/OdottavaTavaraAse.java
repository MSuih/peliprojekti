package objektit;

import aseet.Ase;
import aseet.AseHallinta;
import aseet.AseHallinta.AseTyyppi;

/** Kentälle asetettu ase, joka odottaa että pelaaja poimisi sen. Eroaa normaalista TavaraAseesta siten, että poimittaessa ase luodaan joka kerta uudestaan. Täten poimittavaa  */
public class OdottavaTavaraAse extends Tavara {
    private final AseTyyppi atyyppi;

    /** Luo uusi odottava ase.
     * @param x Sijainti x-koordinaateilla.
     * @param y Sijainti y-koordinaateilla.
     * @param t Aseen tyyppi.*/
    public OdottavaTavaraAse(double x, double y, AseTyyppi t) {
        super(x, y, Tavara.TavaraTyyppi.ase);
        atyyppi = t;
    }
    @Override
    public String getSprite() {
        return atyyppi.getSprite() + "_tavara";
    }
    @Override
    public boolean onkoAse() {
        return true;
    }
    @Override
    public Ase getAse() {
        return AseHallinta.getAse(atyyppi);
    }

}
