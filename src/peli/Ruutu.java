package peli;

import java.io.Serializable;

/** Kentän yksittäinen ruutu. */
public class Ruutu implements Serializable {
    private final boolean[][] seina;
    private String pohjakuva;
    
    /** Luo uusi ruutu. */
    public Ruutu() {
        this("oletus");
    }
    
    /** Luo uusi ruutu.
     * @param s Ruudun pohjakuva.*/
    public Ruutu(String s) {
        this.seina = new boolean[10][10];
        pohjakuva = s;
    }
    /** Palauttaa tämän ruudun kuvan.
     * @return Ruudun pohjakuva.
     */
    public String getPohjaKuva() {
        return pohjakuva;
    }
    /** Asettaa ruudun pohjakuvan.
     * @param s Uusi pohjakuva. */
    public void setPohjaKuva(String s) {
        pohjakuva = s;
    }

    /** Asettaa ruudun seinän.
     * @param x Seinäruudun x-koordinaatti.
     * @param y Seinäruudun y-koordinaatti.
     * @param b Onko seinää vai ei.
     */
    public void setSeinaRuutu(int x, int y, boolean b) {
        seina[x][y] = b;
    }
    /** Tarkistaa voiko kohtaan ruudun sisällä liikkua.
     * @param x Seinäruudun x-koordinaatti.
     * @param y Seinäruudun y-koordinaatti.
     * @return true jos seinäruudussa ei ole seinää eli kohtaan voi liikkua. */
    public boolean getVoikoLiikkua(int x, int y) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        return !seina[x][y];
    }
    /** Tarkistaa voiko kohtaan ruudun sisällä liikkua.
     * @param x Seinäruudun x-koordinaatti.
     * @param y Seinäruudun y-koordinaatti.
     * @return true jos seinäruudussa ei ole seinää eli kohtaan voi liikkua. */
    public boolean getVoikoLiikkua(double x, double y) {
        return getVoikoLiikkua((int) x, (int) y);
    }
    /** Vaihtaa ruudun liikkumistilaa.
     * @param x Seinäruudun x-koordinaatti.
     * @param y Seinäruudun y-koordinaatti. */
    public void vaihdaVoikoLiikkua(int x, int y) {
        seina[x][y] = !seina[x][y];
    }
    /** Asettaa ruudun liikkumistilan.
     * @param x Seinäruudun x-koordinaatti.
     * @param y Seinäruudun y-koordinaatti.
     * @param b Voiko kohtaan likkua. */
    public void setVoikoLiikkua(int x, int y, boolean b) {
        seina[x][y] = !b;
    }
}