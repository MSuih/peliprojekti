package objektit;

import aseet.Ammus;
import aseet.Ase;
import static aseet.Ase.Ase_animaatio.*;
import aseet.Lyonti;
import aseet.Nyrkit;
import java.util.List;

/** Pelaajan liikuttama hahmo. */
public class Pelaaja extends Hahmo {
    private Ase ase = new Nyrkit();
    
    /** Luo uusi pelaaja annettuun sijaintiin.
     * @param x Sijainnin x-koordinaatit.
     * @param y Sijainnin y-koordinaatit. */
    public Pelaaja(double x, double y) {
        super(x, y, 100d);
    }
    
    @Override
    public void paivita(double d) {
        ase.paivita(d);
    }
    
    /** Palauttaa pelaajan aseen spriten.
     * @return Aseen spriten nimi. */
    public String getAseSprite() {
        //TODO: Älä palauta jos hieno pyörähusanimaatio on kesken
        return ase.getSprite();
    }
    /** Lataa pelaajan käyttämän aseen
     * @see aseet.Ase#lataa(boolean)
     */
    public void lataaAse() {
        if (ase.onkoLippaitaJaljella()) ase.lataa(true);
    }
    /** Palauttaa ammusten määrän.
     * @return Ammusten määrä.
     * @see aseet.Ase#lataa(boolean)
     */
    public int getAmmusMaara() {
        return ase.getAmmusMaara();
    }
    /** Onko ammukset vähissä. Ammukset on vähissä, jos niitä on alle 40% jäljellä.
     * @return true jos ammukset ovat vähissä. */
    public boolean onkoAmmuksetVahissa() {
        return ase.getAmmusMaara() / (double) ase.getAmmusMaksimi() < 0.4;
    }
    /** Palauttaa lippaan koon.
     * @return Lippaan koko
     * @see aseet.Ase#getAmmusMaksimi() 
     */
    public int getLippaanKoko() {
        return ase.getAmmusMaksimi();
    }
    /** Palauttaa jäljellä olevien lippaiden määrän.
     * @return Lippaiden määrä.
     * @see aseet.Ase#getLipasMaara() 
     */
    public int getLipasMaara() {
        return ase.getLipasMaara();
    }
    /** Palauttaa lippaan ikonin.
     * @return Lippaan ikoni.
     * @see aseet.Ase#getLipasIkoni()
     */
    public String getLipasIkoni() {
        return ase.getLipasIkoni();
    }
    /** Ampuu aseella, jos mahdollista. Luodit lähtevät pelaajan sijainnista kohti hänen katseensa suuntaa.
     * @return Ampumisen tekemät ammukset. Null, jos ampuminen ei toiminut.
     * @see aseet.Ase#ammu(double, double, double)
     */
    public List<Ammus> ammuAseella() {
        return ase.ammu(getX(), getY(), getSuunta());
    }
    /** Lyö kädessä olevalla aseella, jos mahdollista.
     * @return Aseen lyönti tai null. */
    public Lyonti lyoAseella() {
        return ase.lyo();
    }
    /** Ammutaanko pelaajan kädessä olevalla aseella.
     * @return Onko ase ampuma-ase.
     * @see aseet.Ase#onkoAmpumaAse()
     */
    public boolean getOnkoAmpumaAse() {
        return ase.onkoAmpumaAse();
    }
    /** Onko ase sarjatuliase. Jos on, ase ampuu niin kauan kun painike on pohjassa
     * @return true jos ase on sarjatuliase
     * @see aseet.Ase#onkoSarjatuliAse()
     */
    public boolean getOnkoSarjatuliase() {
        return ase.onkoSarjatuliAse();
    }
    /** Palauttaa, onko pelaajalla ase kädessä.
     * @return Jos pelaajalla on nyrkit eli ei poimittua asetta, palauttaa false. */
    public boolean getOnkoPoimittuaAsetta() {
        return !(ase instanceof Nyrkit);
    }
    /** Voiko pelaajan asetta käyttää.
     * @return Voiko asetta käyttää.
     * @see aseet.Ase#voikoKayttaa()
     */
    public boolean getVoikoKayttaaAsetta() {
        return ase.voikoKayttaa();
    }
    /** Asettaa pelaajan aseeksi jonkun.
     * @param a Uusi ase.*/
    public void setAse(Ase a) {
        ase = a;
    }
    /** Tiputtaa kädessä olevan aseen. Pelaaja vaihtaa nyrkkeihin.
     * @return Tiputettu ase. */
    public Ase tiputaAse() {
        Ase heitettava= ase;
        ase = new Nyrkit();
        return heitettava;
    }
    
    @Override
    public String getSprite() {
        if (!this.onkoElossa()) return "pelaaja_kuollut";
        switch (ase.getTyyppi()) {
            case haulikko:
                if (ase.getAnimaatio() == kayta) return "pelaaja_haulikko_ammu";
                if (ase.getAnimaatio() == lataa) return "pelaaja_haulikko_lataa";
                return "pelaaja_haulikko";
            case nyrkit:
                if (ase.getAnimaatio() == kayta) return "pelaaja_nyrkit_lyo";
                return "pelaaja_nyrkit";
            case lahitaistelu:
                if (ase.getAnimaatio() == kayta) return "pelaaja_lahit_lyo";
                return "pelaaja_lahit";
            case pistooli:
                if (ase.getAnimaatio() == kayta) return "pelaaja_pist_ammu";
                if (ase.getAnimaatio() == lataa) return "pelaaja_pist_lataa";
                return "pelaaja_pist";
            case kahdenkaden:
                if (ase.getAnimaatio() == kayta) return "pelaaja_rynk_ammu";
                if (ase.getAnimaatio() == lataa) return "pelaaja_rynk_lataa";
                return "pelaaja_rynk";
        }
        return "pelaaja";
    }

    @Override
    public String getKuollutSprite() {
        return "pelaaja_kuollut";
    }
}