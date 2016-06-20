package objektit;

import aseet.AseHallinta.AseTyyppi;

/** Vihollinen, joka odottaa kentälle asettamista. Tätä luokkaa käytetään, jotta vihollisen levytilan ja muistin käyttö pysyisi mahdollisimman pienenä ja peliin asetettaessa ei tarvitsisi huolehtia vihollisen nollaamisesta. */
public class OdottavaVihollinen extends Piirrettava {
    private double elamat = Hahmo.OLETUS_NOPEUS;
    private String sprite = "oletus";
    private double nopeus = Hahmo.OLETUS_ELAMAT;
    private double kulma = 0;
    private boolean kayttaakoAsetta = false;
    private AseTyyppi ase = null;
    
    /** Luo uusi odottava vihollinen. */
    public OdottavaVihollinen() {
        super(2, 2);
    }
    /** Palauttaa vihollisen elämäpisteiden maksimimäärän.
     * @return Elämien maksimimäärä. */
    public double getElamat() {
        return elamat;
    }
    /** Asettaa elämäpisteiden maksimimäärän.
     * @param elamat Uusi maksimi. */
    public void setElamat(double elamat) {
        this.elamat = elamat;
    }
    
    /** Palauttaa vihollisen nopeuden.
     * @return Nopeus. */
    public double getNopeus() {
        return nopeus;
    }
    /** Asettaa uuden nopeuden.
     * @param nopeus Uusi nopeus.*/
    public void setNopeus(double nopeus) {
        this.nopeus = nopeus;
    }
    /** Palauttaa suunnan, johon vihollinen katsoo.
     * @return Vihollisen suunta.*/
    public double getKulma() {
        return kulma;
    }
    /** Asettaa vihollisen uuden suunnan.
     * @param kulma Uusi suunta. */
    public void setKulma(double kulma) {
        this.kulma = kulma;
    }
    
    /** Käyttääkö tämä vihollinen asetta.
     * @return True, jos käyttää. */
    public boolean kayttaakoAsetta() {
        return kayttaakoAsetta;
    }
    /** Asettaa viholliselle uuden aseen.
     * @param a Aseen tyyppi.*/
    public void setAse(AseTyyppi a) {
        ase = a;
        kayttaakoAsetta = true;
    }
    /** Poistaa viholliselta aseen. */
    public void poistaAse() {
        ase = null;
        kayttaakoAsetta = false;
    }
    /** Palauttaa vihollisen aseen.
     * @return aseen tyyppi. */
    public AseTyyppi getAse() {
        return ase;
    }
    
    @Override
    public String getSprite() {
        return sprite;
    }
    /** Asettaa vihollisen spriten.
     * @param s Uusi sprite.*/
    public void setSprite(String s) {
        sprite = s;
    }
    /** Palauttaa aseen spriten
     * @return Aseen sprite. Null, jos asetta ei ole.*/
    public String getAseSprite() {
        if (ase == null) return null;
        return ase.getSprite();
    }
    
    /** Muuttaa annetun odottavan vihollisen kentällä käytettäväksi viholliseksi.
     * @param v Vihollinen, joka muutetaan.
     * @return Vihollinen-luokan mukainen vihollinen. */
    public static Vihollinen muutaViholliseksi(OdottavaVihollinen v) {
        if (v.kayttaakoAsetta()) return new VihollinenAseella(v);
        else return new Vihollinen(v);
    }
}
