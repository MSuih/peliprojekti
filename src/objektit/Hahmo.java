package objektit;

import peli.Huone;

/** Pelimaailmassa oleva hahmo. Hahmo liikkuu, sitä voi vahingoittaa ja se voi hyökätä.*/
public abstract class Hahmo extends Piirrettava {
    /** Oletusnopeus. */
    public static final double OLETUS_NOPEUS = 6.5d;
    /** Oletuksena käytettävä elämämäärä. */
    public static final double OLETUS_ELAMAT = 100;
    
    private double suunta = 0;
    private double nopeus = OLETUS_NOPEUS;
    private double elamat_nyk;
    private final double elamat_max;
    
    private Huone nykyinen_huone = null;
    
    /** Luo uusi hahmo sijaintiin.
     * @param x Hahmon sijainti x-akselilla.
     * @param y Hahmon sijainti y-akselilla.*/
    public Hahmo(double x, double y) {
        this(x, y, OLETUS_ELAMAT);
    }
    /** Luo uusi hahmo sijaintiin.
     * @param x Hahmon sijainti x-akselilla.
     * @param y Hahmon sijainti y-akselilla.
     * @param elamat Hahmon elämät. */
    public Hahmo(double x, double y, double elamat) {
        super(x, y);
        elamat_nyk = elamat;
        elamat_max = elamat;
    }
    
    /** Aseta hahmolle uusi katsomis- ja liikkumissuunta.
     * @param kulma Suunta. */
    public void setSuunta(double kulma) {
        this.suunta = kulma;
    }

    /** Palauttaa hahmon suunnan
     * @return Hahmon suunta. */
    public double getSuunta() {
        return suunta;
    }
    /** Palauttaa hahmon nopeuden.
     * @return Hahmon liikkumisnopeus. */
    public double getNopeus() {
        return nopeus;
    }
    /** Asettaa hahmon liikkumisnopeuden.
     * @param nopeus Uusi nopeus. */
    public void setNopeus(double nopeus) {
        this.nopeus = nopeus;
    }
    /** Palauttaa, kuinka monta prosenttia hahmon elämistä on jäljellä.
     * @return Elämien prosenttimäärä, jossa 1 = maksimi.
     */
    public double getElamaProsentti() {
        return elamat_nyk / elamat_max;
    }
    /** Onko hahmo yhä elossa.
     * @return true, jos hahmo on elossa. */
    public boolean onkoElossa() {
        return elamat_nyk > 0;
    }
    /** Vahingoita hahmoa.
     * @param vahinko Vahinko, joka hahmoon tehdään.*/
    public void vahingoita(double vahinko) {
        if (vahinko < 0 ) throw new RuntimeException("Vahinko ei voi olla negatiivinen.");
        elamat_nyk -= vahinko;
    }
    /** Paranna hahmoa.
     * @param parannus Elämämäärä, jolla hahmoa parannetaan.*/
    public void paranna(double parannus) {
        if (parannus < 0 ) throw new RuntimeException("Parannus ei voi olla negatiivinen.");
        elamat_nyk = Math.min(elamat_max, elamat_nyk + parannus);
    }    
    
    /** Onko hahmo samassa huoneessa.
     * @return true, jos hahmolle on määritelty huone ja hahmo on yhä sen sisällä. */
    public boolean onkoSamassaHuoneessa() {
        if (nykyinen_huone == null) return false;
        return nykyinen_huone.onkoSisalla(getX(), getY());
    }
    /** Asettaa hahmolle uuden huoneen.
     * @param h Huone, joka hahmolle asetetaan. */
    public void setHuone(Huone h) {
        nykyinen_huone = h;
    }
    /** Palauttaa huoneen, joka hahmolle on asetettu.
     * @return Hahmon huone. Jos huonetta ei ole asetettu, palauttaa null. */
    public Huone getHuone() {
        return nykyinen_huone;
    }
    /** Päivittää hahmoa.
     * @param siirtyma Kuinka paljon aikaa on kulunut.*/
    public abstract void paivita(double siirtyma);
    /** Palauttaa spriten, jota käytetään kun tämä hahmo kuolee.
     * @return Spriten nimi. */
    public abstract String getKuollutSprite();
}