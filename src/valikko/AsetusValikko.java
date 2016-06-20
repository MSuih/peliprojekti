package valikko;

/** Asetuksien muokkaamisen valikko. */
public class AsetusValikko extends YksinkertainenValikko {
    private static final String tekstit[] = {"Ruudunpäivityksen rajoitin", "Joku"};
    /** Luo uusi asetusvalikko. */
    public AsetusValikko() {
        super(tekstit);
    }

    @Override
    public ValikkoToiminto paina(int i) {
        return new ValikkoToiminto(ValikkoToiminto.Toiminto.takaisin);
    }
    
}
