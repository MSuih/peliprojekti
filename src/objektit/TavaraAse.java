package objektit;

import aseet.Ase;

/** Tavara, joka kuvaa pelissä olevaa asetta. */
public class TavaraAse extends Tavara {
    private final Ase ase;

    /** Luo annetun aseen sisältävän tavaran.
     * @param x Sijainnin x-arvo.
     * @param y Sijainnin y-arvo.
     * @param a Ase.*/
    public TavaraAse(double x, double y, Ase a) {
        super(x, y, Tavara.TavaraTyyppi.ase);
        ase = a;
    }
    @Override
    public String getSprite() {
        return ase.getSprite() + "_tavara";
    }
    @Override
    public boolean onkoAse() {
        return true;
    }
    @Override
    public Ase getAse() {
        return ase;
    }
}