package aseet;

import java.util.ArrayList;
import java.util.List;


/** Rumpulippaalla varustettu kertatulea ampuva haulikko. */
public class Haulikko extends Kasiase {
    private static final double vahinko_per_ammus = 53d;
    private static final double lataamisaika = 1.5d;
    private static final double laukausten_valinen_aika = 0.3d;
    private static final int lippaan_koko = 5;
    
    /** Luo uuden haulikon.  */
    public Haulikko() {
        super(Ase.Ase_tyyppi.haulikko, lippaan_koko);
    }
    /** Luo uuden haulkon, jossa annettu määrä lippaita.
     * @param lippaiden_maara Lipasmäärä.*/
    public Haulikko(int lippaiden_maara) {
        super(Ase.Ase_tyyppi.haulikko, lippaan_koko, lippaiden_maara);
    }

    @Override
    public List<Ammus> ammu(double x, double y, double suunta) {
        super.vahennaAmmusMaaraa(1);
        super.setAnimaatio(Ase_animaatio.kayta, laukausten_valinen_aika);
        // Yhdellä laukauksella ammutaan viisi haulia
        final int haulien_maara = 5;
        List<Ammus> lista = new ArrayList<>();
        //hauleja ei ammuta päällekkäin vaan ne hajoaa tietyn kulman mukaan
        final double suunta_muutos = Math.toRadians(3); //suuntaa muutetaan 5 astetta kerrallaan
        final double suunta_max = Math.toRadians(360); //maksimisuunta, tämän yli ei voi mennä
        double suunta_nyk = suunta - suunta_muutos * (haulien_maara / 2);  //asetetaan nykyinen suunta
        if (suunta_nyk < 0) suunta_nyk += suunta_max; //tarkistetaan, ettei mennä negatiivisiin lukuihin
        for (int i = 0; i < haulien_maara ; i++) {
            lista.add(new Ammus(x, y, suunta_nyk, vahinko_per_ammus));
            suunta_nyk += suunta_muutos; //liikuta oikealle
            if (suunta_nyk > suunta_max) suunta_nyk -= suunta_max; //tarkistetaan, ettei mennä yli maksimin
        }
        return lista;
    }
    @Override
    public void lataa(boolean b) {
        super.lataa(b);
        super.setAnimaatio(Ase_animaatio.lataa, lataamisaika);
    }

    @Override
    public boolean onkoSarjatuliAse() {
        return false;
    }

    @Override
    public String getSprite() {
        switch (this.getAnimaatio()) {
            case ei_mitaan:
            default:
                return "haulikko";
            case lataa:
                return "haulikko_lataa";
            case kayta:
                return "haulikko_ammu";
        }
    }
    @Override
    public String getLipasIkoni() {
        return "lipas_rumpu";
    }
}
