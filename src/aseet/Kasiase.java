package aseet;

import java.io.Serializable;
import kayttoliittyma.Asetukset;

/** Ase, joka ampuu jotain. Yleensä luoteja. */
public abstract class Kasiase implements Ase, Serializable {
    private static final int lippaiden_maara_oletus = 2;
    private static final int lippaiden_maara_max = 5;
    
    private final Ase_tyyppi tyyppi;
    private final int lippaan_koko;
    private int lippaiden_maara;
    private int ammusten_maara;
    private double animaatio_aika = 0;
    private Ase_animaatio animaatio = Ase_animaatio.ei_mitaan; 
    
    /** Luo uusi käsiase.
     * @param tyyppi Aseen tyyppi.
     * @param lippaan_koko Kuinka monta panosta mahtuu yhteen lippaaseen.
     * @param lippaiden_maara Kuinka monta lipasta aseen mukana tulee.
     */
    public Kasiase(Ase_tyyppi tyyppi, int lippaan_koko, int lippaiden_maara) {
        this.tyyppi = tyyppi;
        this.lippaan_koko = lippaan_koko;
        this.lippaiden_maara = lippaiden_maara;
        this.ammusten_maara = lippaan_koko;
    }
    /** Luo uuden käsiaseen.
     * @param tyyppi Aseen tyyppi.
     * @param lippaan_koko Kuinka monta panosta yhteen lippaaseen mahtuu. */
    public Kasiase(Ase.Ase_tyyppi tyyppi, int lippaan_koko) {
        this(tyyppi, lippaan_koko, lippaiden_maara_oletus);
    }
    
    @Override
    public boolean onkoAmpumaAse() {
        return true;
    }
    @Override
    public Ase_tyyppi getTyyppi() {
        return tyyppi;
    }

    @Override
    public boolean voikoKayttaa() {
        return ammusten_maara > 0 && animaatio_aika <= 0;
    }
    @Override
    public boolean onkoAnimaatioKesken() {
        return animaatio_aika > 0;
    }

    @Override
    public void paivita(double siirtyma) {
        animaatio_aika -= siirtyma;
        if (animaatio != Ase_animaatio.ei_mitaan && animaatio_aika < 0) animaatio = Ase_animaatio.ei_mitaan;
    }

    @Override
    public Lyonti lyo() {
        return null;
    }

    @Override
    public void lataa(boolean kaytaLippaita) {
        if (lippaiden_maara > 0 || !kaytaLippaita) {
            if (kaytaLippaita) lippaiden_maara--;
            ammusten_maara = lippaan_koko;
        }
    }
    
    @Override
    public boolean onkoLipasTyhja() {
        return ammusten_maara <= 0;
    }
    @Override
    public boolean onkoLippaitaJaljella() {
        return lippaiden_maara > 0;
    }

    @Override
    public int getAmmusMaara() {
        return ammusten_maara;
    }
    
    @Override
    public String getLipasIkoni() {
        return "lipas_norm";
    }

    @Override
    public void vahennaAmmusMaaraa(int i) {
        ammusten_maara -= i;
    }

    @Override
    public int getAmmusMaksimi() {
        return lippaan_koko;
    }

    @Override
    public int getLipasMaara() {
        return lippaiden_maara;
    }
    /** Lisää aseeseen lippaita. Aseessa ei voi kuitenkaan olla maksimimäärää enemmän lippaita.
     * @param i Lippaiden määrä. */
    public void lisaaLipas(int i) {
        lippaiden_maara = Math.min(lippaiden_maara + i, lippaiden_maara_max);
    }
    @Override
    public Ase_animaatio getAnimaatio() {
        return animaatio;
    }
    @Override
    public void setAnimaatio(Ase_animaatio animaatio, double kesto) {
        this.animaatio = animaatio;
        animaatio_aika = kesto;
    }
    @Override
    public void nollaaAnimaatio() {
        this.animaatio = Ase.Ase_animaatio.ei_mitaan;
        animaatio_aika = -1;
    }
}