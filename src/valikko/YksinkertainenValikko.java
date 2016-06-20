package valikko;

/** Yksinkertainen valikko, jossa on yksi sivu näppäimiä joista jokaisella on yksi staattinen teksti. */
public abstract class YksinkertainenValikko implements Valikko {
    private final String[] tekstit;
    private int valittu_painike = -1;
    
    /** Luo uusi yksinkertainen valikko.
     * @param s Valikon painikkeiden tekstit. */
    public YksinkertainenValikko(String[] s) {
        tekstit = s;
    }
    
    @Override
    public int getPainikeLkm() {
        return tekstit.length;
    }

    @Override
    public String getTeksti(int i) {
        return tekstit[i];
    }

    @Override
    public void setValittu(int i) {
        if (i < tekstit.length) valittu_painike = i;
    }

    @Override
    public void liiku(int i) {
        valittu_painike += i;
        if (valittu_painike < 0) valittu_painike = 0;
        else if (valittu_painike >= tekstit.length) valittu_painike = tekstit.length - 1;
    }

    @Override
    public int getValittu() {
        return valittu_painike;
    }
}