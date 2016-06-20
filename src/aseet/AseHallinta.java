package aseet;

import java.util.EnumSet;
import java.util.Set;

/** Aseiden yleinen hallinta. Luotu sitä varten, että editorista  */
public class AseHallinta {
    /** Aseen tyyppi. */
    public enum AseTyyppi {
        /** Rynnäkkökivääri. */
        rk, 
        /** Miekka. */
        miekka,
        /** Haulikko. */
        haul;
        @Override
        public String toString() {
            switch (this) {
                case rk:
                    return "Rynnakkokivaari";
                case miekka:
                    return "Miekka";
                case haul:
                    return "Haulikko";
                default:
                    throw new RuntimeException("Tyyppiä ei voi muuttaa tekstiksi.");
            }
        }
        /** Palauttaa aseen spirten.
         * @return Spriten nimi. */
        public String getSprite() {
            switch (this) {
                case rk:
                    return "rynk";
                case miekka:
                    return "miekka";
                case haul:
                    return "haulikko";
                default:
                    throw new RuntimeException("Tyypillä ei ole kuvaa.");
            }
        }
    }
    //Ei konstruktoria
    private AseHallinta(){}
    
    /** Palauttaa kaikki mahdolliset asetyypit.
     * @return Kaikki asetyypit. */
    public static Set<AseTyyppi> getAseTyypit() {
        return EnumSet.allOf(AseTyyppi.class);
    }
    
    /** Muuttaa asetyypin pelissä käytettäväksi aseeksi.
     * @param atyyppi Tyyppi, jota ase on.
     * @return Ase, jota pelaaja tai vihollinen voi käyttää. */
    public static Ase getAse(AseTyyppi atyyppi) {
        switch (atyyppi) {
            case rk:
                return new Rynnakkokivaari();
            case miekka:
                return new Miekka();
            case haul:
                return new Haulikko();
            default:
                return null;
        }
    }
}