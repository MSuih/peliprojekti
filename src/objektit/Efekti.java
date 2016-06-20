package objektit;

/** Pelimaailmassa oleva efekti. */
public abstract class Efekti extends Piirrettava{
    private final double suunta;
    /** Luo uusi efekti haluttuun pisteeseen
     * @param x Sijainti x-akselilla.
     * @param y Sijainti y-akselilla.
     * @param suunta Efektin suunta. */
    public Efekti(double x, double y, double suunta) {
        super(x, y);
        this.suunta = suunta;
    }
    
    /**
     * @param siirtyma Kuinka paljon aikaa on kulunut edellisestä päivityskerrasta. */
    public abstract void paivita(double siirtyma);

    /** Pitääkö efekti poistaa. Jotkut efektit voivat olla näkyvissä vain rajatun ajan, jonka jälkeen ne täytyy poistaa pelimaailmasta.
     * @return true, jos efekti pitää poistaa.
     */
    public abstract boolean pitaakoPoistaa();
    /** Palauttaa efektin suunnan
     * @return Suunta, johon efektin sprite pitää kääntää.*/
    public double getSuunta() {
        return suunta;
    }
    
    //Toteutukset:
    /*public class Nuoli extends Efekti{
        private double liike_siirto = 0;
        private boolean liike_suunta = true;
        private final int nuolen_suunta;
        
        public static final int ylos = 0;
        public static final int alas = 1;
        public static final int oikealle = 2;
        public static final int vasemmalle = 3;
        
        
        public Nuoli(double x, double y, double suunta) {
            super(x, y, suunta);
            nuolen_suunta = suunta;
        }
        
        @Override
        public double getX() {
            switch (nuolen_suunta) {
                case ylos:
                case alas:
                default:
                    return super.getX();
                case oikealle:
                case vasemmalle:
                    return super.getX() + liike_siirto;
            }
        }
        @Override
        public double getY() {
            switch (nuolen_suunta) {
                case ylos:
                case alas:
                default:
                    return super.getY() + liike_siirto;
                case oikealle:
                case vasemmalle:
                    return super.getY();
            }
        }

        @Override
        public void paivita(double siirtyma) {
            liike_siirto += (liike_suunta ? siirtyma : -siirtyma);
            if (liike_suunta && liike_siirto > 1) liike_suunta = !liike_suunta;
            else if (!liike_suunta && liike_siirto < -1) liike_suunta = !liike_suunta;
        }

        @Override
        public String getSprite() {
            switch (nuolen_suunta) {
                default:
                case ylos:
                    return "nuoli_y";
                case alas:
                    return "nuoli_a";
                case oikealle:
                    return "nuoli_o";
                case vasemmalle:
                    return "nuoli_v";
            }
        }

        @Override
        public boolean pitaakoPoistaa() {
            return false;
        }
    }*/
    /** Kuolleen vihollisen ruumis */
    public static class KuollutVihollinen extends Efekti {
        private final String sprite;
        
        /** Luo uusi ruumis.
         * @param x Sijainti x-akselilla.
         * @param y Sijainti y-akselilla.
         * @param suunta Suunta, johon vihollinen katsoi ennen kuolemaa.
         * @param sprite Ruumiin sprite. */
        public KuollutVihollinen(double x, double y, double suunta, String sprite) {
            super(x, y, suunta);
            this.sprite = sprite;
        }

        @Override
        public void paivita(double siirtyma) {
            // ei tehdä mitään
        }

        @Override
        public boolean pitaakoPoistaa() {
            return false;
        }

        @Override
        public String getSprite() {
            return sprite;
        }
        
    }
}