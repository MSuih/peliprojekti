package aseet;

/** Miekka, jolla voi lyödä. */
public class Miekka extends LahitaisteluAse {
    private static final double aika_per_lyonti = 0.2;
    /** Luo uusi miekka. */
    public Miekka() {
        super(151);
    }

    @Override
    public Lyonti lyo() {
        super.setAnimaatio(Ase_animaatio.kayta, aika_per_lyonti);
        return new Lyonti(super.getLyontiVahinko());
    }

    @Override
    public String getSprite() {
        if (this.getAnimaatio() == Ase_animaatio.kayta) return "miekka_lyo";
        return "miekka";
    }

    @Override
    public Ase_tyyppi getTyyppi() {
        return Ase_tyyppi.lahitaistelu;
    }
}