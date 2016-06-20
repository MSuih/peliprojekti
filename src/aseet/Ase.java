package aseet;

import java.util.List;

/** Pelissä käytettävä ase. Ase voi olla lähitaistelu- tai ampuma-ase.*/
public interface Ase {
    /** Voiko asetta käyttää eli voiko sillä ampua tai lyödä.
     * @return true jos lataaminen ei ole kesken, edellisestä ampumiskerrasta on kulunut tarpeeksi kauan ja aseessa on ammuksia.
     */
    public boolean voikoKayttaa();
    /** Onko aseen jokin animaatio kesken. Jos animaatio on kesken, asetta ei voi vielä käyttää. 
     * @return true jos animaatio on kesken. */
    public boolean onkoAnimaatioKesken();
    /** Päivittää aseen. Siirtää lataus- tai ampumisanimaatiota niin että asetta voi ampumisen jälkeen käyttää.
     * @param siirtyma Kuinka paljon aikaa on kulunut.
     * @see aseet.Ase#voikoKayttaa()
     */
    public void paivita(double siirtyma);
    /** Lyö aseella, jos aseella voi lyödä. Ei tarkista onko lataaminen tai edellinen lyönti kesken, joten muista tarkistaa voikoKayttaa()-metodin tulos ensin.
     * @return Aseen tekemä lyönti. Jos ase ei ole lyöntiase, palauttaa null-arvon.
     * @see aseet.Ase#voikoKayttaa()
     */
    public Lyonti lyo();
    /** Ampuu aseella, jos aseella voi ampua. Ei tarkasta onko lataaminen tai edellinen lyönti kesken tai onko aseessa ammuksia.
     * @param x Koordinaatti, josta aseella ammutaan.
     * @param y Koodrinaatti, josta aseella ammutaan.
     * @param suunta Suunta, johon aseella ammutaan.
     * @return Aseen ampumat ammukset. Jos aseella ei voi ampua, palauttaa null-arvon.
     * @see aseet.Ase#voikoKayttaa()
     * @see aseet.Ase#getAmmusMaara() 
     */
    public List<Ammus> ammu(double x, double y, double suunta);
    /** Onko ase sarjatuliase. Jos ase on sarjatuliase, niin sillä ammutaan hiiren painiketta pohjassa pitämällä. Jos ei, niin jokainen laukaus pitää klikata erikseen.
     * @return Onko kyseessä sarjatuliase.
     */
    public boolean onkoSarjatuliAse();
    /** Onko ase ampuma-ase. Jos ase on, sitä käytetään ammu()-metodilla. 
     * @return true jos ase on ampuma-ase. */
    public boolean onkoAmpumaAse();
    /** Lataa aseen, jos se on mahdollista.
     * @param kulutaLippaita kayttaako lippaita
     */
    public void lataa(boolean kulutaLippaita);
    /** Palauttaa kuinka monta ammusta aseessa on sisällä.
     * @return Ammusten määrä.
     */
    public int getAmmusMaara();
    /** Onko aseen lipas tyhjä. 
     * @return true jos lipas on tyhjä. */
    public boolean onkoLipasTyhja();
    /** Onko aseessa vielä lippaita. Jos on, lippaan voi vaihtaa uuteen.
     * @return true, jos lippaita on jäljellä. */
    public boolean onkoLippaitaJaljella();
    /** Vähentää aseen lippaassa olevien ammusten määrää.
     * @param i Kuinka monta ammusta käytetään.*/
    public void vahennaAmmusMaaraa(int i);
    /** Palauttaa kuinka monta ammusta aseessa voi olla. Tämä on määrä, jolloin ase on ladattu täyteen.
     * @return Ammusten maksimimäärä.
     */
    public int getAmmusMaksimi();
    /** Palauttaa kuinka monta lipasta aseessa on.
     * @return Lippaiden määrä.
     */
    public int getLipasMaara();
    /** Palauttaa aseen lippaiden käyttämän spriten. Tätä käytetään käyttöliittymässä.
     * @return  Lippaan sprite. */
    public String getLipasIkoni();

    /** Palauttaa aseen spriten.
     * @return aseen sprite.
     */
    public String getSprite();

    /** Aseen tyyppi.
     */
    public enum Ase_tyyppi {
        /**Nyrkit. */
        nyrkit,
        /** Lähitaisteluase. Esimerkiksi miekka tai muu lyöntiase. */
        lahitaistelu,
        /** Yhden käden pistooli. */
        pistooli,
        /** Kahden käden ampuma-ase, esim. rynnäkkökivääri */
        kahdenkaden,
        /** Haulikko. */
        haulikko;
    }
    /** Palauttaa aseen tyypin.
     * @return Aseen tyyppi. */
    public Ase_tyyppi getTyyppi();
    /** Aseen animaatio. */
    public enum Ase_animaatio {
        /** Mikään animaatio ei ole kesken */
        ei_mitaan,
        /** Asetta käytetään eli sillä lyödään tai ammutaan. */
        kayta,
        /** Asetta ladataan. */
        lataa;
    }
    /** Palauttaa animaation, jota ase tekee.
     * @return Aseen animaatio. */
    public Ase_animaatio getAnimaatio();

    /** Asettaa aseelle animaation.
     * @param animaatio Mitä animaatiota käytetään.
     * @param kesto Kuinka kauan animaatio kestää.
     */
    public void setAnimaatio(Ase_animaatio animaatio, double kesto);

    /** Nollaa aseen animaation.
     */
    public void nollaaAnimaatio();
}