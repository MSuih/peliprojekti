package valikko;

/** Valikon napin painamisesta aiheutuva toiminto. */
public class ValikkoToiminto {
    /** Ei mitään lisätietoa. */
    public static final byte EI_MITAAN = 0;
    /** Mahdolliset toiminnot. */
    public enum Toiminto {
        /** Palaa takaisin edelliseen valikkoon. */
        takaisin,
        /** Aloita uusi peli. Tieto = avattava kenttä. */
        uusipeli,
        /** Sulje peli. */
        sulje,
        /** Avaa asetusvalikko. */
        v_asetukset,
        /** Avaa kenttävalikko. */
        v_aloita;
    }
    
    /** Painikkeen toiminto. */
    public final Toiminto toiminto;
    /** Painikkeen mahdollinen lisätieto. */
    public final byte lisatieto; 
    
    /** Luo uusi toiminto.
     * @param toiminto Tämän toiminto.
     * @param tieto Lisätieto.*/
    public ValikkoToiminto(Toiminto toiminto, byte tieto) {
        this.toiminto = toiminto;
        this.lisatieto = tieto;
    }
    /** Luo uusi toiminto.
     * @param toiminto Tämän toiminto.*/
    public ValikkoToiminto(Toiminto toiminto) {
        this(toiminto, EI_MITAAN);
    }
}