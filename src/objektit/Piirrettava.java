package objektit;

import java.io.Serializable;

/** Pelikentällä oleva asia, joka voidaan piirtää näytölle. Piirtämistä varten asialla täytyy olla sekä sijainti että sprite. */
public abstract class Piirrettava implements Serializable {
    private double x;
    private double y;
    
    /** Luo uusi piirrettävä.
     * @param x Sijainnin x-koordinaatti.
     * @param y Sijainnin y-koordinaatti.
     */
    public Piirrettava(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** Palauttaa tämän sijainnin x-koordinaatin.
     * @return Tämän sijainti x-koordinaattiaskelilla.
     */
    public double getX() {
        return x;
    } 
    /** Palauttaa tämän sijainnin y-koordinaatin.
     * @return Tämän sijainti y-koordinaattiaskelilla.
     */
    public double getY() {
        return y;
    }
    /** Siirtää tämän uuteen sijaintiin.
     * @param x Uusi sijainti x-akselilla.
     */
    public void setX(double x) {
        this.x = x;
    }
    /** Siirtää tämän uuteen sijaintiin.
     * @param y Uusi sijainti y-akselilla.
     */
    public void setY(double y) {
        this.y = y;
    }
    /** Palauttaa spriten nimen, jota käytetään kun tämä piirretään näytölle.
     * @return Spriten nimi. */
    public abstract String getSprite();
}
