package aseet;

import java.io.Serializable;
import java.util.List;

/** Lähitaistelussa käytettävä ase. Tällä lyödään.*/
public abstract class LahitaisteluAse implements Ase, Serializable {
    
    private final double vahinko; 
    private Ase_animaatio animaatio = Ase_animaatio.ei_mitaan;
    private double animaatio_aika = 0;
    
    /** Luo uusi lähitaisteluase.
     * @param vahinko Kuinka paljon vahinkoa yksi lyönti tekee. */
    public LahitaisteluAse(double vahinko) {
        this.vahinko = vahinko;
    }

    @Override
    public boolean voikoKayttaa() {
        return animaatio_aika < 0;
    }

    @Override
    public boolean onkoAnimaatioKesken() {
        return animaatio_aika >= 0;
    }

    @Override
    public void paivita(double siirtyma) {
        animaatio_aika -= siirtyma;
        if (animaatio_aika < 0) animaatio = Ase_animaatio.ei_mitaan;
    }

    @Override
    public List<Ammus> ammu(double x, double y, double suunta) {
        return null;
    }
    double getLyontiVahinko() {
        return vahinko;
    }
    @Override
    public abstract Lyonti lyo();

    @Override
    public boolean onkoSarjatuliAse() {
        return false;
    }

    @Override
    public boolean onkoAmpumaAse() {
        return false;
    }

    @Override
    public void lataa(boolean b) {
        // Ei käytössä
    }
    @Override
    public boolean onkoLipasTyhja() {
        return false;
    }
    @Override
    public boolean onkoLippaitaJaljella() {
        return false;
    }

    @Override
    public int getAmmusMaara() {
        return -1;
    }
    @Override
    public String getLipasIkoni() {
        return "ei_lipasta";
    }

    @Override
    public void vahennaAmmusMaaraa(int i) {
        // Ei käytössä
    }

    @Override
    public int getAmmusMaksimi() {
        return -1;
    }

    @Override
    public int getLipasMaara() {
        return -1;
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