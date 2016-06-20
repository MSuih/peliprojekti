package objektit;

import aseet.Ammus;
import aseet.Ase;
import java.io.Serializable;
import java.util.List;
import logiikka.Koord;
import peli.Huone;

/** Kentällä oleva vihollinen, joka jahtaa pelaajaa. */
public class Vihollinen extends Hahmo {
    
    private final String sprite;
    
    private Tekeminen nykyinen_tekeminen = Tekeminen.harhaile;
    private Koord kohde = null;
    private double tekeminen_aika = 0;
    private double hyokkays_aika = 0;
    
    /** Juttu, jota vihollinen parhaillaan tekee. */
    public enum Tekeminen implements Serializable {
        /** Vihollinen harhailee huoneen sisällä. */
        harhaile,
        /** Vihollinen on samassa huoneessa kun pelaaja ja liikkuu häntä kohden. */
        hyokkaa,
        /** Vihollinen haluaisi hyökätä, mutta on eri huoneessa. */
        etsi_pelaaja;
        //odota paikallaan -tila?
        //partioi tiettyä reittiä -tila?
    }
    
    /** Luo uusi vihollinen.
     * @param x Sijainti x-akselilla.
     * @param y Sijainti y-akselilla.
     * @param elamat Vihollisen elämät.
     * @param sprite Vihollisen sprite.*/
    public Vihollinen(double x, double y, double elamat, String sprite) {
        super(x, y, elamat);
        this.sprite = sprite;
    }
    /** Muuttaa odottavan vihollisen viholliseksi.
     * @param v Odottava vihollinen.*/
    public Vihollinen(OdottavaVihollinen v) {
        super(v.getX(), v.getY(), v.getElamat());
        sprite = v.getSprite();
        setSuunta(v.getKulma());
        setNopeus(v.getNopeus());
    }

    @Override
    public String getSprite() {
        return sprite;
    }
    @Override
    public String getKuollutSprite() {
        return sprite + "_kuollut";
    }
    /** Palauttaa tämän vihollisen kuolema-efektin.
     * @return  Efekti kuolleesta vihollisesta, joka on samassa sijainnissa kun nykyinen vihollinen. */
    public Efekti getKuollutVihollinen() {
        return new Efekti.KuollutVihollinen(getX(), getY(), getSuunta(), getKuollutSprite());
    }
    @Override
    public double getNopeus() {
        double nopeus = super.getNopeus();
        if (nykyinen_tekeminen == Tekeminen.harhaile) {
            return nopeus * 0.7;
        }
        return nopeus;
    }
    /** Palauttaa mitä vihollinen on tekemässä.
     * @return Tekeminen, jota vihollinen tekee. */
    public Tekeminen getTekeminen() {
        return nykyinen_tekeminen;
    }
    /** Asettaa viholliselle uuden tekemisen.
     * @param t Tekeminen.*/
    public void setTekeminen(Tekeminen t) {
        this.setTekeminen(t, null);
    }
    /** Asettaa viholliselle uuden tekemisen.
     * @param t Uusi tekeminen.
     * @param kohde Kohde, johon tekeminen kohdistuu. */
    public void setTekeminen(Tekeminen t, Koord kohde) {
        nykyinen_tekeminen = t;
        this.kohde = kohde;
    }
    /** Palauttaa tekemisen kohteen sijainnin.
     * @return Tekemisen sijainti. */
    public Koord getKohdeSijainti() {
        /*Huone huone = getHuone();
        tekeminen_aika -= siirtyma;
        if (tekeminen_aika < 0) {
            kohde = null;
            tekeminen_aika = 1 + Math.random() * 6;
        }
        if (kohde == null && huone != null) {
            kohde = huone.arvoSatunnainenSijainti();
        }*/
        return kohde;
    }
    /** Arpoo uuden tekemisen sijainnin nykyisestä huoneesta. */
    public void arvoKohdeSijainti() {
        if (getHuone() == null) return;
        kohde = getHuone().arvoSatunnainenSijainti();
    }
    /** Hyökkää pelaajaa kohden.
     * @return  Hyökkäyksen tekemä vahinko. 0, jos vihollinen ei voi jostain syystä hyökätä. */
    public double hyokkaa() {
        if (hyokkays_aika < 0) {
            hyokkays_aika = 0.2d + (0.4 * Math.random());
            return 30d;
        }
        return 0;
    }
    /** Käyttääkö vihollinen asetta.
     * @return true, jos käyttää.*/
    public boolean kayttaakoAsetta() {
        return false;
    }
    /** Tiputtaako vihollinen aseen kuollessaan.
     * @return true, jos tiputtaa. */
    public boolean tiputtaakoAseen() {
        return false;
    }
    /** Ampuu aseella.
     * @return Ammuset, jotka ase teki. Null jos asetta ei ole tai sillä ei voi ampua.
     */
    public List<Ammus> ammu() {
        return null;
    }
    /** Voiko aseella ampua.
     * @return true, jos voi ampua. */
    public boolean voikoAmpua() {
        return false;
    }
    /** Palauttaa vihollsen aseen.
     * @return Ase, joka vihollisella on. Null jos ei asetta. */
    public Ase getAse() {
        return null;
    }
    /** Onko tällä vihollisella asetta tai muuta piirrettävää.
     * @return true, jos on */
    public boolean onkoPiirrettavaaTavaraa() {
        return false;
    }
    /** Palauttaa tavaran spriten
     * @return Tavaran spriten nimi. */
    public String getTavaraSprite() {
        throw new NullPointerException("Tällä vihollisella ei ole tavaroita!");
    }
    @Override
    public void paivita(double siirtyma) {
        hyokkays_aika -= siirtyma;
        if (nykyinen_tekeminen == Tekeminen.harhaile) {
            Huone huone = getHuone();
            tekeminen_aika -= siirtyma;
            if (tekeminen_aika < 0) {
                kohde = null;
                tekeminen_aika = 1 + Math.random() * 6;
            }
            if (kohde == null && huone != null) {
                kohde = huone.arvoSatunnainenSijainti();
            }
        }
    }
}