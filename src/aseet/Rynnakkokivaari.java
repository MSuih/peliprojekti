package aseet;

import java.util.ArrayList;
import java.util.List;

/** Rynnäkkökivääri. Kahden käden ase, joka ampuu sarjatulta. */
public class Rynnakkokivaari extends Kasiase {
    private static final int lippaan_koko = 30;
    private static final double lataamisaika = 1d;
    private static final double laukausten_valinen_aika = 0.05d;
    
    /** Luo uuden rynnäkkökiväärin. */
    public Rynnakkokivaari() {
        super(Ase.Ase_tyyppi.kahdenkaden, lippaan_koko);
    }

    @Override
    public List<Ammus> ammu(double x, double y, double suunta) {
        super.vahennaAmmusMaaraa(1);
        super.setAnimaatio(Ase_animaatio.kayta, laukausten_valinen_aika);
        List lista = new ArrayList<>();
        final double suunta_muutos = Math.toRadians(10) * Math.random() - Math.toRadians(5);
        lista.add(new Ammus(x, y, suunta + suunta_muutos, 44));
        return lista;
    }
    @Override
    public void lataa(boolean b) {
        super.lataa(b);
        super.setAnimaatio(Ase_animaatio.lataa, lataamisaika);
    }

    @Override
    public boolean onkoSarjatuliAse() {
        return true;
    }

    @Override
    public String getSprite() {
        switch (this.getAnimaatio()) {
            case ei_mitaan:
            default:
                return "rynk";
            case lataa:
                return "rynk_lataa";
            case kayta:
                return "rynk_ammu";
        }
    }
}