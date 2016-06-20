package aseet;

/** Nyrkit, jolla lyödään. */
public class Nyrkit extends LahitaisteluAse {
    private static final double aika_per_lyonti = 0.4;
    /** Luo uuden Nyrkit-aseen. */
    public Nyrkit() {
        super(151);
    }

    @Override
    public Lyonti lyo() {
        super.setAnimaatio(Ase_animaatio.kayta, aika_per_lyonti);
        return new Lyonti(super.getLyontiVahinko());
    }
    /** Nyrkkejä ei piirretä erikseen, vaan ne ovat osa pelaajaa. Tämä palauttaa siitä syystä tyhjän spriten.
     * @return "tyhjä" -niminen sprite. */
    @Override
    public String getSprite() {
        //ei piirretä erikseen asetta
        return "tyhja";
    }

    @Override
    public Ase_tyyppi getTyyppi() {
        return Ase_tyyppi.nyrkit;
    }
}