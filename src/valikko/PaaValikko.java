package valikko;

/** Päävalikko, josta pääsee muihin valikoihin. */
public class PaaValikko extends YksinkertainenValikko {
    private static final String[] tekstit = new String[]{"Uusi peli","Asetukset", "Lopeta"};
    
    /** Luo uusi päävalikko */
    public PaaValikko() {
        super(tekstit);
    }

    @Override
    public ValikkoToiminto paina(int i) {
        switch (i) {
            case 0:
                return new ValikkoToiminto(ValikkoToiminto.Toiminto.v_aloita);
            case 1:
                return new ValikkoToiminto(ValikkoToiminto.Toiminto.v_asetukset);
            case 2:
                return new ValikkoToiminto(ValikkoToiminto.Toiminto.sulje);
            default:
                return null;
        }
    }
}
