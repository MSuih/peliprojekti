package peli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import logiikka.Koord;

/** Pelin kenttä. Kenttä sisältää vain pohjan, ei esim. vihollisia tai tavaroita. */
public class Kentta implements Serializable {
    private final Ruutu[][] ruudut;
    private final int reunanpituusx;
    private final int reunanpituusy;
    
    private final List<Huone> huoneet = new ArrayList<>();
    
    private double aloitusX = 0.1;
    private double aloitusY = 0.1;
    
    private double lopetusAlkuX = 0;
    private double lopetusAlkuY = 0;
    private double lopetusSivuX = 1;
    private double lopetusSivuY = 1;
    
    /** Luo uuden kentän.
     * @param xkoko Kentän koko x-akselilla
     * @param ykoko Kentän koko y-akselilla.*/
    public Kentta(int xkoko, int ykoko) {
        ruudut = new Ruutu[xkoko][ykoko];
        for (int x = 0; x < xkoko ; x++) {
            for (int y = 0 ; y < ykoko ; y++) {
                ruudut[x][y] = new Ruutu();
            }
        }
        reunanpituusx = xkoko * 10;
        reunanpituusy = ykoko * 10;
    }
    
    /** Palauttaa pelaajan aloitussijainnin x-akselilla.
     * @return Aloitussijainti x-akselilla.*/
    public double getAloitusX() {
        return aloitusX;
    }
    /** Palauttaa pelaajan aloitussijainnin y-akselilla.
     * @return Aloitussijainti y-akselilla.*/
    public double getAloitusY() {
        return aloitusY;
    }
    /** Asettaa pelaajan aloitussijainnin x-akselilla.
     * @param x Uusi aloitussijainti x-akselilla.*/
    public void setAloitusX(double x) {
        aloitusX = x;
    }
    /** Asettaa pelaajan aloitussijainnin y-akselilla.
     * @param y Uusi aloitussijainti y-akselilla.*/
    public void setAloitusY(double y) {
        aloitusY = y;
    }
    
    /** Palauttaa lopetusalueen sijainnin x-akselilla.
     * @return Lopetusalueen sijainti x-akselilla.*/
    public double getLopetusAlueX() {
        return lopetusAlkuX;
    }
    /** Palauttaa lopetusalueen sivun mitan x-akselilla.
     * @return Lopetusalueen sivun mitta x-akselilla.*/
    public double getLopetusAlueSivuX() {
        return lopetusSivuX;
    }
    /** Palauttaa lopetusalueen sijainnin y-akselilla.
     * @return Lopetusalueen sijainti y-akselilla.*/
    public double getLopetusAlueY() {
        return lopetusAlkuY;
    }
    /** Palauttaa lopetusalueen sivun mitan y-akselilla.
     * @return Lopetusalueen sivun mitta y-akselilla.*/
    public double getLopetusAlueSivuY() {
        return lopetusSivuY;
    }
    /** Asettaa lopetusalueen sijainnin x-akselilla.
     * @param x Uusi lopetusalueen sijainti x-akselilla.*/
    public void setLopetusAlueX(double x) {
        lopetusAlkuX = x;
    }
    /** Asettaa lopetusalueen sijainnin y-akselilla.
     * @param y Uusi lopetusalueen sijaintiy-akselilla.*/
    public void setLopetusAlueY(double y) {
        lopetusAlkuY = y;
    }
    /** Asettaa lopetusalueen sivun mitan x-akselilla.
     * @param x Lopetusalueen uusi sivun mitta x-akselilla.*/
    public void setLopetusSivuX(double x){
        lopetusSivuX = Math.max(x, 0);
    }
    /** Asettaa lopetusalueen sivun mitan y-akselilla.
     * @param y Lopetusalueen uusi sivun mitta y-akselilla.*/
    public void setLopetusSivuY(double y){
        lopetusSivuY = Math.max(y, 0);
    }
    /** Onko piste lopetusalueella.
     * @param x Tarkistettava x-sijainti.
     * @param y Tarkistettava y-sijainti.
     * @return true jos piste on lopetusalueeella.*/
    public boolean getOnkoLopetusAlueella(double x, double y) {
        if (x > lopetusAlkuX && y > lopetusAlkuY) {
            return x < lopetusAlkuX + lopetusSivuX && y < lopetusAlkuY + lopetusSivuY;
        }
        return false;
    }
    
    /** Palauttaa kentän pituuden x-akselilla.
     * @return  Ruutumäärä x-akselilla.*/
    public int getKokoX() {
        return reunanpituusx;
    }
    /** Palauttaa kentän pituuden y-akselilla.
     * @return  Ruutumäärä y-akselilla.*/
    public int getKokoY() {
        return reunanpituusy;
    }
    
    /** Palauttaa ruudun, jossa kooordinaatti on.
     * @param koordX x-koordinaatti, joka tarkistetaan.
     * @return x-akselin ruutu */
    public int getRuutuX(double koordX) {
        if (koordX < 0) return -1;
        return (int) (koordX / 10);
    }
    /** Palauttaa ruudun, jossa kooordinaatti on.
     * @param koordY y-koordinaatti, joka tarkistetaan.
     * @return y-akselin ruutu */
    public int getRuutuY(double koordY) {
        if (koordY < 0) return -1;
        return (int) (koordY / 10);
    }
    
    /** Palauttaa kentän ruutumäärän x-akselilla.
     * @return  Ruutumäärä x-akselilla.*/
    public int getRuutuMaaraX() {
        return ruudut.length;
    }
    /** Palauttaa kentän ruutumäärän y-akselilla.
     * @return  Ruutumäärä y-akselilla.*/
    public int getRuutuMaaraY() {
        return ruudut[0].length;
    
    }
    /** Palauttaa ruudun kuvan.
     * @param x Ruudun x-koordinaatti.
     * @param y Ruudun y-koordinaatti.
     * @return Ruudun kuva.*/
    public String getRuutuKuva(int x, int y) {
        if (x < 0 || y < 0 || x > getRuutuMaaraX() - 1 || y > getRuutuMaaraY() - 1) return null;
        if (ruudut[x][y] != null) return ruudut[x][y].getPohjaKuva();
        return null;
    }
    /** Asettaa ruudun kuvan.
     * @param x Ruudun x-koordinaatti.
     * @param y Ruudun y-koordinaatti.
     * @param s Uusi kuva.*/
    public void setRuutuKuva(int x, int y, String s) {
        ruudut[x][y].setPohjaKuva(s);
    }

    /** Onko ruutua olemassa.
     * @param ruutux Ruudun x-koordinaatti.
     * @param ruutuy Ruudun y-koordinaatti.
     * @return true, jos ruutu on olemassa.
     */
    public boolean onkoRuutua(int ruutux, int ruutuy) {
        return ruudut[ruutux][ruutuy] != null;
    }
    /** Poistaa ruudun käytöstä.
     * @param ruutux Ruudun x-koordinaatti.
     * @param ruutuy Ruudun y-koordinaatti.
     */
    public void poistaRuutuKaytosta(int ruutux, int ruutuy) {
        ruudut[ruutux][ruutuy] = null;
    }

    /** Ottaa ruudun käyttöön. Ruutu luodaan uudestaan, eli se ei käydä aikaisemmin olleen ruudun tietoja.
     * @param ruutux Ruudun x-koordinaatti.
     * @param ruutuy Ruudun y-koordinaatti.
     */
    public void otaRuutuKayttoon(int ruutux, int ruutuy) {
        ruudut[ruutux][ruutuy] = new Ruutu();
    }
    /** Voiko pisteeseen liikua eli onko siinä seinää.
     * @param x Koordinaatti x-akselilla.
     * @param y Koordinaatti y-akselilla.
     * @return true jos piste on kentän sisällä ja siihen voi liikkua */
    public boolean voikoLiikkua(double x, double y) {
        final int ruutux = getRuutuX(x);
        final int ruutuy = getRuutuY(y);
        if (ruutux < 0 || ruutuy < 0) return false;
        if (ruutux > ruudut.length -1 || ruutuy > ruudut[ruutux].length -1) return false;
        if (ruudut[ruutux][ruutuy] == null) return false;
        return ruudut[ruutux][ruutuy].getVoikoLiikkua(x % 10, y % 10);
    }
    /** Voiko ruudun seinäruutuun liikkua eli onko siinä seinää.
     * @param ruutux Ruudun x-koordinaatti.
     * @param ruutuy Ruudun y-koordinaatti.
     * @param x Seinäruutu x-akselilla.
     * @param y Seinäruutu y-akselilla.
     * @return true jos ruudussa on seinä. */
    public boolean voikoLiikkua(int ruutux, int ruutuy, int x, int y) {
        return ruudut[ruutux][ruutuy].getVoikoLiikkua(x, y);
    }

    /** Vaihda ruudun seinäruudun liikkumistilaa eli seinän olemassaoloa.
     * @param ruutux Ruudun x-koordinaatti.
     * @param ruutuy Ruudun y-koordinaatti.
     * @param x Seinäruutu x-akselilla.
     * @param y Seinäruutu y-akselilla.
     */
    public void vaihdaLiikkumisTilaa(int ruutux, int ruutuy, int x, int y) {
        ruudut[ruutux][ruutuy].vaihdaVoikoLiikkua(x, y);
    }
    /** Vaihda ruudun seinäruutujen liikkumistilaa vaakasuunnassa. Vaihta koordinaatin kaikki samalla rivillä olevat ruudut.
     * @param ruutux Ruudun x-koordinaatti.
     * @param ruutuy Ruudun y-koordinaatti.
     * @param x Seinäruutu x-akselilla.
     * @param y Seinäruutu y-akselilla.
     */
    public void vaihdaLiikkumisTilaaVaaka(int ruutux, int ruutuy, int x, int y) {
        boolean b = ruudut[ruutux][ruutuy].getVoikoLiikkua(x, y);
        for (int i = 0; i < 10 ; i++) {
            ruudut[ruutux][ruutuy].setVoikoLiikkua(i, y, !b);
        }
    }
    /** Vaihda ruudun seinäruutujen liikkumistilaa pystysuunnassa. Vaihta koordinaatin kaikki samalla sarakkeella olevat ruudut.
     * @param ruutux Ruudun x-koordinaatti.
     * @param ruutuy Ruudun y-koordinaatti.
     * @param x Seinäruutu x-akselilla.
     * @param y Seinäruutu y-akselilla.
     */
    public void vaihdaLiikkumisTilaaPysty(int ruutux, int ruutuy, int x, int y) {
        boolean b = ruudut[ruutux][ruutuy].getVoikoLiikkua(x, y);
        for (int i = 0; i < 10 ; i++) {
            ruudut[ruutux][ruutuy].setVoikoLiikkua(x, i, !b);
        }
    }

    /** Palauttaa kaikki kentän ruutukuvat latausta varten.
     * @return Set, jossa kentän ruutukuvat. */
    public Set<String> getLadattavat() {
        Set<String> set = new HashSet<>();
        for (Ruutu[] ra : ruudut) {
            for (Ruutu r : ra) {
                if (r != null) set.add(r.getPohjaKuva());
            }
        }
        return set;
    }
    /** Lisää kenttään huone.
     * @param h Huone, joka lisätään. */
    public void lisaaHuone(Huone h) {
        huoneet.add(h);
    }
    /** Palauttaa pisteessä olevan ruudun.
     * @param x Koordinaatti x-akselilla.
     * @param y Koordinaatti y-akselilla.
     * @return Pisteessä oleva huone tai null. */
    public Huone getHuone(double x, double y) {
        for (Huone h : huoneet) {
            if (h.onkoSisalla(x, y)) return h;
        }
        return null;
    }

    /** Poistaa huoneen kentästä.
     * @param h Huone, joka poistetaan.
     */
    public void poistaHuone(Huone h) {
        huoneet.remove(h);
    }
    /** Onko kentässä enemmän kun yksi huone.
     * @return true jos huoneita on enemmän kun 1. */
    public boolean onkoSeuraavaaHuonetta() {
        return huoneet.size() > 1;
    }
    /** Pyytää kentältä seuraavan huoneen
     * @param h Huone, josta halutaan seuraava huone.
     * @return Seuraava huone.*/
    public Huone getSeuraavaHuone(Huone h) {
        if (h != null && huoneet.contains(h)) {
            int i = huoneet.indexOf(h) + 1;
            if (i >= huoneet.size()) return huoneet.get(0);
            return huoneet.get(i);
        }
        else return huoneet.get(0);
    }
    /** Palauttaa viimeiseksi luodun huoneen.
     * @return Pyydetty huone. */
    public Huone getViimeisinHuone() {
        return huoneet.get(huoneet.size() - 1);
    }
    /** Palauttaa kaikki kentän huoneet.
     * @return Lista huoneista. */
    public List<Huone> getHuoneet() {
        List<Huone> list = new ArrayList<>();
        list.addAll(huoneet);
        return list;
    }
    /** Palauttaa huoneen seuraavan yhteyden.
     * @param huone Huone.
     * @param yhteys Yhteys, jonka perusteella lasketaan seuraava.
     * @return Seuraava yhteys.
     * @see peli.Huone#getSeuraavaYhteys(java.util.Map.Entry)
     */
    public Entry<Huone, Koord> getSeuraavaYhteys(Huone huone, Entry<Huone, Koord> yhteys) {
        return huone.getSeuraavaYhteys(yhteys);
    }
}