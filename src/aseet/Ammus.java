package aseet;

import objektit.Piirrettava;

/** Pelikentällä liikkuva ammus. Ammus liikkuu annettuun suuntaan annetulla nopeudella, kunnes se törmää kohteeseen tai seinään.
 * Ammus kuuluu pelaajalle tai viholliselle. Vihollisen ampuma ammus vahingoittaa vain pelaajaa ja toisin päin.
 */
public class Ammus extends Piirrettava {
    /** Nopeus, jolla ammus oletuksena liikkuu */
    public static final double OLETUS_NOPEUS = 30;
    
    private final double suunta;
    private final double nopeus;
    private final double vahinko;
    private boolean pelaajanAmpuma;
    
    /** Luo uuden ammuksen haluttuun pisteeseen. Ammus on pelaajan ampuma ja liikkuu oletusnopeudella.
     * @param x Ammuksen aloituspisteen x-koordinaatti.
     * @param y Ammuksen aloituspisteen y-koordinaatti.
     * @param suunta Ammuksen liikkumasuunta.
     * @param vahinko Kuinka paljon vahinkoa ammus tekee osuessaan.
     */
    public Ammus(double x, double y, double suunta, double vahinko) {
        this(x, y, suunta, vahinko, true);
    }
    /** Luo uuden ammuksen pisteeseen. Ammus liikkuu oletusnopeudella.
     * @param x Ammuksen aloituspisteen x-koordinaatti.
     * @param y Ammuksen aloituspisteen y-koordinaatti.
     * @param suunta Ammuksen liikkumasuunta.
     * @param vahinko Kuinka paljon vahinkoa ammus tekee osuessaan.
     * @param ampuja True jos ampuja on pelaaja, false muuten.
     */
    public Ammus(double x, double y, double suunta, double vahinko, boolean ampuja) {
        this(x, y, suunta, OLETUS_NOPEUS, vahinko, ampuja);
    }

    /** Luo uuden ammuksen pisteeseen.
     * @param x Ammuksen aloituspisteen x-koordinaatti.
     * @param y Ammuksen aloituspisteen y-koordinaatti.
     * @param suunta Ammuksen liikkumasuunta.
     * @param nopeus Kuinka nopeasti ammus liikkuu (ruutua per sekunti)
     * @param vahinko Kuinka paljon vahinkoa ammus tekee osuessaan.
     * @param ampuja True jos ampuja on pelaaja, false muuten.
     */
    public Ammus(double x, double y, double suunta, double nopeus, double vahinko, boolean ampuja) {
        super(x, y);
        this.suunta = suunta;
        this.nopeus = nopeus;
        this.vahinko = vahinko;
        pelaajanAmpuma = ampuja;
    }
    
    /** Muuttaa ammuksen vihollisen ampumaksi. */
    public void muutaVihollisenAmmukseksi() {
        pelaajanAmpuma = false;
    }
    
    /** Palauttaa ammuksen suunnan
     * @return Ammuksen suunta. 
     */
    public double getSuunta() {
        return suunta;
    }
    /** Palauttaa ammuksen tekemän vahingon.
     * @return Vahinko, jonka ammus tekee osuessaan.
     */
    public double getVahinko() {
        return vahinko;
    }
    /** Palauttaa ammuksen nopeuden.
     * @return Ammuksen nopeus.
     */
    public double getNopeus() {
        return nopeus;
    }
    /** Voiko ammus lävistää hahmoja.
     * @return true jos voi lävistää.
     */
    public boolean voikoLavistaa() {
        return false;
    }
    
    /** Ampuiko pelaaja tämän ammuksen
     * @return  Onko ammus pelaajan ampuma.
     */
    public boolean onkoPelaajanAmpuma() {
        return pelaajanAmpuma;
    }
    
    @Override
    public String getSprite() {
        return "luoti";
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ammus)) return false;
        Ammus a = (Ammus) o;
        if (Double.doubleToLongBits(a.getX()) != Double.doubleToLongBits(this.getX())) return false;
        if (Double.doubleToLongBits(a.getY()) != Double.doubleToLongBits(this.getY())) return false;
        return Double.doubleToLongBits(a.suunta) == Double.doubleToLongBits(this.suunta) &&
                Double.doubleToLongBits(a.vahinko) == Double.doubleToLongBits(this.vahinko);
    }
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (int) (java.lang.Double.doubleToLongBits(suunta) ^ (java.lang.Double.doubleToLongBits(suunta) >>> 32));
        hash = 19 * hash + (int) (java.lang.Double.doubleToLongBits(vahinko) ^ (java.lang.Double.doubleToLongBits(vahinko) >>> 32));
        hash = 19 * hash + (int) (java.lang.Double.doubleToLongBits(getX()) ^ (java.lang.Double.doubleToLongBits(getX()) >>> 32));
        hash = 19 * hash + (int) (java.lang.Double.doubleToLongBits(getY()) ^ (java.lang.Double.doubleToLongBits(getY()) >>> 32));           return hash;
    }
    
}
