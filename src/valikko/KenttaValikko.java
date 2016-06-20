package valikko;

/** Kentän valinnan valikko. */
public class KenttaValikko extends YksinkertainenValikko {
    private static final String[] tekstit = new String[]{"1: Enismmäinen kenttä", "2: Toinen kenttä"};

    /** Luo uusi kenttävalikko */
    public KenttaValikko() {
        super(tekstit);
    }

    @Override
    public ValikkoToiminto paina(int i) {
        return new ValikkoToiminto(ValikkoToiminto.Toiminto.uusipeli, (byte) (i + 1));
    }
    
}
