package logiikka;

import java.io.Serializable;
    
    //Huom. Vaatisi ehkä uudelleensuunnittelua.

/** Pelin koordinaatti. */
public interface Koord {
    /** Palauttaa koordinaatin x-akselilla kokonaislukuna.
     * @return x-koordinaatti kokonaislukuna. */
    public int getXInt();
    /** Palauttaa koordinaatin y-akselilla kokonaislukuna.
     * @return y-koordinaatti kokonaislukuna. */
    public int getYInt();
    /** Palauttaa koordinaatin x-akselilla doublena.
     * @return x-koordinaatti doublena. */
    public double getXDouble();
    /** Palauttaa koordinaatin x-akselilla doublena.
     * @return x-koordinaatti doublena. */
    public double getYDouble();
    /** Asettaa x-akselille uuden arvo.
     * @param x Uusi x-koordinaatti.*/
    public void setX(double x);
    /** Asettaa x-akselille uuden arvo.
     * @param y Uusi y-koordinaatti.*/
    public void setY(double y);
    
    /** Koordinaatin toteutus, joka käyttää kokonaislukua. */
    public class Int implements Koord, Serializable {
        /** Arvo x-akselilla.*/
        public int x;
        /** Arvo y-akselilla */
        public int y;
        /** Luo uusi koordinaatti.
         * @param x Arvo x-akselilla.
         * @param y Arvo y-akselilla.*/
        public Int(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int getXInt() {
            return x;
        }
        @Override
        public int getYInt() {
            return y;
        }

        @Override
        public double getXDouble() {
            return x;
        }
        @Override
        public double getYDouble() {
            return y;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 37 * hash + this.x;
            hash = 37 * hash + this.y;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Int)) {
                return false;
            }
            final Int other = (Int) obj;
            return this.x == other.x && this.y == other.y;
        }

        @Override
        public void setX(double x) {
            this.x = (int) x;
        }

        @Override
        public void setY(double y) {
            this.y = (int) y;
        }
    }
    /** Koordinaatin toteutus, joka käyttää doubleja. */
    public class Double implements Koord, Serializable {
        /** Arvo x-akselilla */
        public double x;
        /** Arvo y-akselilla */
        public double y;
        /** Luo uusi koordinaatti.
         * @param x Arvo x-akselilla.
         * @param y Arvo y-akselilla.*/
        public Double(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int getXInt() {
            return (int) x;
        }
        @Override
        public int getYInt() {
            return (int) y;
        }

        @Override
        public double getXDouble() {
            return x;
        }
        @Override
        public double getYDouble() {
            return y;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 19 * hash + (int) (java.lang.Double.doubleToLongBits(this.x) ^ (java.lang.Double.doubleToLongBits(this.x) >>> 32));
            hash = 19 * hash + (int) (java.lang.Double.doubleToLongBits(this.y) ^ (java.lang.Double.doubleToLongBits(this.y) >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Double)) {
                return false;
            }
            final Double other = (Double) obj;
            return (java.lang.Double.doubleToLongBits(this.x) == java.lang.Double.doubleToLongBits(other.x)) && 
                    (java.lang.Double.doubleToLongBits(this.y) == java.lang.Double.doubleToLongBits(other.y));
        }

        @Override
        public void setX(double x) {
            this.x = x;
        }

        @Override
        public void setY(double y) {
            this.y = y;
        }
    }
}