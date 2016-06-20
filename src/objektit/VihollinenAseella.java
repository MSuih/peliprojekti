package objektit;

import aseet.Ammus;
import aseet.Ase;
import aseet.AseHallinta;
import aseet.Rynnakkokivaari;
import java.util.ArrayList;
import java.util.List;

/** Vihollinen, joka käyttää asetta. */
public class VihollinenAseella extends Vihollinen {
    private final Ase ase;

    /** Luo uusi vihollinen aseella.
     * @param x Sijainti x-akselilla.
     * @param y Sijainti y-akselilla.
     * @param elamat Vihollisen elämät.
     * @param sprite Vihollisen spriten nimi. */
    public VihollinenAseella(double x, double y, double elamat, String sprite) {
        super(x, y, elamat, sprite);
        ase = new Rynnakkokivaari();
    }

    /** Luo uusi vihollinen aseella.
     * @param v Odottava vihollinen.
     */
    public VihollinenAseella(OdottavaVihollinen v) {
        super(v);
        ase = AseHallinta.getAse(v.getAse());
    }
    @Override
    public boolean kayttaakoAsetta() {
        return ase.onkoAmpumaAse();
    }
    @Override
    public boolean tiputtaakoAseen() {
        return true;
    }
    @Override
    public List<Ammus> ammu() {
        if (ase.voikoKayttaa()) {
            List l = new ArrayList();
            ase.ammu(getX(), getY(), getSuunta()).stream().forEach((a) -> {
                a.muutaVihollisenAmmukseksi();
                l.add(a);
            });
            return l;
        }
        else if (ase.onkoLipasTyhja() && !ase.onkoAnimaatioKesken()) ase.lataa(false);
        return null;
    }
    @Override
    public boolean voikoAmpua() {
        return ase.onkoAnimaatioKesken();
    }
    @Override
    public Ase getAse() {
        return ase;
    }
    @Override
    public boolean onkoPiirrettavaaTavaraa() {
        return true;
    }
    @Override
    public String getTavaraSprite() {
        return ase.getSprite();
    }
    @Override
    public void setTekeminen(Tekeminen t) {
        if (t == this.getTekeminen()) return;
        super.setTekeminen(t);
        //Annetaan hieman aikaa jotta pelaaja voisi ampua ensin
        //ilman tätä pelaaja ottaisi automaattisesti pari osumaa ennen kun hänen ampumat kudit osuvat viholliseen
        ase.setAnimaatio(Ase.Ase_animaatio.lataa, 0.3);
    }
    @Override
    public void paivita(double siirtyma) {
        super.paivita(siirtyma);
        ase.paivita(siirtyma);
    }
    @Override
    public String getSprite() {
        if (ase.getAnimaatio() == Ase.Ase_animaatio.kayta) return super.getSprite() + "_ammu";
        if (ase.getAnimaatio() == Ase.Ase_animaatio.lataa) return super.getSprite() + "_lataa";
        return super.getSprite();
    }
}