package aseet;

/** Lähitaisteluaseella tehtävä lyönti. Tämän kautta voisi välittää lyönnin vaikutuksia pelaajaan tai viholliseen. */
public class Lyonti {
    private final double vahinko;
    /** Luo uusi lyönti
     * @param vahinko Lyönnin tekemä vahinko.*/
    public Lyonti(double vahinko) {
        this.vahinko = vahinko;
    }
    /** Palauttaa lyönnin tekemän vahingon.
     * @return Lyönnin tekemä vahinko. */
    public double getVahinko() {
        return vahinko;
    }
    
    /* TODO: 
     * Tämän kautta voisi välittää erilaisia lyöönin vaikutuksia viholliseen.
     */
}
