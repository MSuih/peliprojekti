package objektit;

import aseet.Ase;
import aseet.AseHallinta.AseTyyppi;

/** Kentällä oleva tavara. Jokaisella tavaralla on satunnaisesti arvottu suunta. */
public class Tavara extends Piirrettava {
    /** Tavaran tyyppi */
    public enum TavaraTyyppi {
        /** Elämäpakkaus. Tyyppi on parannuksen määrä, joka tämän poimineeseen vaikuttaa. */
        elamat,
        /** Ase. */
        ase;
        //loput tyypit
    }
    private final TavaraTyyppi tyyppi;
    private final byte arvo;
    private final double kulma = Math.random() * Math.toRadians(360);
    
    /** Luo uuden tavaran.
     * @param x Sijainti x-akselilla.
     * @param y Sijainti y-akselilla.
     * @param t Tavaran tyyppi.*/
    public Tavara(double x, double y, TavaraTyyppi t) {
        this(x, y, t, (byte) 0);
    }
    /** Luo uuden tavaran.
     * @param x Sijainti x-akselilla.
     * @param y Sijainti y-akselilla.
     * @param tyyppi Tavaran tyyppi.
     * @param arvo Tyyppiin liittyvä arvo.*/
    public Tavara(double x, double y, TavaraTyyppi tyyppi, byte arvo) {
        super(x, y);
        this.tyyppi = tyyppi;
        this.arvo = arvo;
    }
    
    /** Palauttaa tavaran suunnan.
     * @return Tavaran suunta.  */
    public double getSuunta(){
        return kulma;
    }
    /** Palauttaa tavaran tyypin.
     * @return Tyyppi */
    public TavaraTyyppi getTyyppi() {
        return tyyppi;
    }
    /** Palauttaa tavaralle asetetun arvon.
     * @return Arvo tai 0. */
    public byte getArvo() {
        return arvo;
    }

    @Override
    public String getSprite() {
        switch (tyyppi) {
            //Todo
            case elamat:
                return "tavara_elama";
            case ase:
                return "tavara_null_ase";
            default:
                return "tavara_err";
        }
    }   
    /** Onko tämä tavara ase.
     * @return true jos tavara on ase. */
    public boolean onkoAse() {
        return false;
    }
    /** Palauttaa aseen, jos tämä ase on tavara.
     * @return Ase tai null. */
    public Ase getAse() {
        return null;
    }
    /** Muuttaa Aseen tavaraksi.
     * @param a Ase, joka muutetaan tavaraksi.
     * @return Tavara, jossa ase on. */
    public static Tavara luoTavara(Ase a) {
        a.nollaaAnimaatio();
        return new TavaraAse(0, 0, a);
    }
    /** Luo asetyypin perusteella tavara.
     * @param t AseTyyppi, josta tavara luodaan.
     * @return Aseen sisältävä tavara */
    public static Tavara luoTavara(AseTyyppi t) {
        return new OdottavaTavaraAse(1, 1, t);
    }
}