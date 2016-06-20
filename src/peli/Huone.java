package peli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import logiikka.Koord;

/** Kentän huone. Huonetta käytetään vihollisen liikuttamiseen; Vihollinen liikkuu huoneeesta toiseen tai huoneen sisällä pisteeseen. */
public class Huone implements Serializable {
    
    private double aloitusx = 1;
    private double aloitusy = 1;
    private double sivux = 1;
    private double sivuy = 1;
    
    private final ConcurrentHashMap<Huone, Koord> yhteydet = new ConcurrentHashMap();
    
    /*  POISTETTU: Parempi ratkaisutapa mutta rikkoo vanhat kentät koska serlializable-ID muuttuu
        TODO: Palauta käyttöön
    */
    /* Luo uusi huone *
    public Huone() {
        this(1,1,1,1);
    }
    /** Luo uusi Huone.
     * @param x Huoneen vasen yläreuna x-akselilla.
     * @param y Huoneen vasen yläreuna y-akselilla.
     * @param sivux Huoneen sivun pituus x-akselilla.
     * @param sivuy Huoneen sivun pituus y-akselilla.*
    public Huone(double x, double y, double sivux, double sivuy) {
        this.aloitusx = x;
        this.aloitusy = y;
        this.sivux = x;
        this.sivuy = y;
    }*/
    /** Luo uusi huone */
    public Huone() {
        
    }
    /** Palauttaa huoneen sijainnin (vasen yläreuna) x-akselilla.
     * @return Huoneen sijainti x-akselilla. */
    public double getX() {
        return aloitusx;
    }
    /** Palauttaa huoneen sijainnin (vasen yläreuna) y-akselilla.
     * @return Huoneen sijainti y-akselilla. */
    public double getY() {
        return aloitusy;
    }
    /** Asettaa huoneen sijainnin (vasen yläreuna) x-akselilla.
     * @param x Huoneen uusi sijainti x-akselilla. */
    public void setX(double x) {
        this.aloitusx = x;
    }
    /** Asettaa huoneen sijainnin (vasen yläreuna) y-akselilla.
     * @param y Huoneen uusi sijainti y-akselilla. */
    public void setY(double y) {
        this.aloitusy = y;
    }
    
    /** Palauttaa huoneen sivun pituuden x-akselilla.
     * @return Sivun pituus x-akselilla.
     */
    public double getSivuX() {
        return sivux;
    }
    /** Palauttaa huoneen sivun pituuden y-akselilla.
     * @return Sivun pituus y-akselilla
     */
    public double getSivuY() {
        return sivuy;
    }
    /** Asettaa huoneen sivun pituuden x-akselilla.
     * @param x Sivun pituus x-akselilla.
     */
    public void setSivuX(double x) {
        this.sivux = x;
    }
    /** Asettaa huoneen sivun pituuden y-akselilla.
     * @param y Sivun pituus y-akselilla.
     */
    public void setSivuY(double y) {
        this.sivuy = y;
    }
    /** Palauttaa huoneen keskikohdan sijainnin x-akselilla.
     * @return Keskikohta x-akselilla.
     */
    public double getKeskusX() {
        return aloitusx + sivux / 2;
    }
    /** Palauttaa huoneen keskikohdan sijainnin x-akselilla.
     * @return Keskikohta x-akselilla.
     */
    public double getKeskusY() {
        return aloitusy + sivuy / 2;
    }
    
    /** Onko piste huoneen sisällä
     * @param x Pisteen x-koordinaatti.
     * @param y Pisteen y-koordinaatti.
     * @return true jos piste on huoneen sisällä.*/
    public boolean onkoSisalla(double x, double y) {
        if (x < aloitusx || y < aloitusy) return false;
        return x < aloitusx + sivux && y < aloitusy + sivuy;
    }
    /** Lisää yhteyden tästä huoneesta toiseen huoneeseen.
     * @param h Huone, johon yhteys muodostetaan.
     * @param k Sijainti, jossa yhteys on. */
    public void lisaaYhteys(Huone h, Koord k) {
        yhteydet.put(h, k);
    }
    /** Lisää yhteyden tästä huoneesta toisen huoneen keskipisteeseen. 
     * @param h Huone, johon yhteys muodostetaan. */
    public void lisaaYhteys(Huone h) {
        Koord k = new Koord.Double(h.getKeskusX(), h.getKeskusY());
        yhteydet.put(h, k);
    }
    /** Poistaa yhteyden toiseen huoneeseen.
     * @param h Huone, johon menevä yhteys poistetaan.*/
    public void poistaYhteys(Huone h) {
        yhteydet.remove(h);
    }

    /** Onko tästä huoneesta yhteyttä toiseen huoneeseen.
     * @param h Huone, johon liittyvää yhteyttä tarkistetaan.
     * @return true jos tästä huoneesta on yhteys annettuunhuoneeseen.
     */
    public boolean onkoYhteytta(Huone h) {
        return yhteydet.containsKey(h);
    }
    /** Palauttaa kaikki huoneet, joihin on yhteys tästä huoneesta.
     * @return Huoneet, joihin on yhteys */
    public List<Huone> getYhteydet() {
        List<Huone> lista = new ArrayList<>();
        lista.addAll(yhteydet.keySet());
        return lista;
    }
    /** Palauttaa yhteyden toiseen huoneeseen.
     * @param h Huone, jonka yhteys haetaan. 
     * @return Piste, johon liikkuessa pääsee toiseen huoneeseen.*/
    public Koord getYhteysHuoneeseen(Huone h) {
        return yhteydet.get(h);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.aloitusx) ^ (Double.doubleToLongBits(this.aloitusx) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.aloitusy) ^ (Double.doubleToLongBits(this.aloitusy) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.sivux) ^ (Double.doubleToLongBits(this.sivux) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.sivuy) ^ (Double.doubleToLongBits(this.sivuy) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Huone)) {
            return false;
        }
        final Huone h = (Huone) obj;
        double tarkkuus = 0.1;
        return Math.abs(this.getKeskusX() - h.getKeskusX()) < tarkkuus &&
                Math.abs(this.getKeskusY() - h.getKeskusY()) < tarkkuus ;
    }
    /** Arvotaan satunnainen sijainti tämän huoneen sisältä. Välttää sijainteja jotka on liian lähellä huoneen reunoja.
     * @return Satunnainen sijainti huoneen sisällä. */
    public Koord arvoSatunnainenSijainti() {
        //Arvotaan kaksi numeroa väliltä 0.0 - 1.0
        double x = Math.random();
        double y = Math.random();
        
        //kerrotaan sivun mitta äskeisellä numerolla -> saadaan satunnaisluku sivunmitan sisältä
        //vähennetään 1 joka reunasta, jottei hahmot menisi liian lähelle seiniä
        return new Koord.Double(aloitusx + 1 + ((sivux - 2) * x), aloitusy + 1 + ((sivuy - 2) * y));
    }

    /** Palauttaa seuraavan yhteuden listalta.
     * @param yhteys Edellinen yhteys.
     * @return Seuraava yhteys.
     */
    public Map.Entry<Huone, Koord> getSeuraavaYhteys(Map.Entry<Huone, Koord> yhteys) {
        if (yhteydet.isEmpty()) return null;
        int i = 0;
        List<Huone> list = new ArrayList<>(yhteydet.keySet());
        if (yhteys != null) {
            i = list.indexOf(yhteys.getKey()) + 1;
            if (i >= list.size()) i = 0;
        }
        for (Map.Entry<Huone, Koord> e : yhteydet.entrySet()) {
            if (e.getKey().equals(list.get(i))) return e;
        }
        return null;
    }
}