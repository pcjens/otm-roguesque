package otm.roguesque.net;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Yhtä leaderboardilla olevaa ennätystä kuvaava luokka.
 *
 * @author Jens Pitkänen
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {

    private final String name;
    private final int score;
    private final LocalDateTime time;

    /**
     * Luo uuden ennätystä kuvaavan olion.
     *
     * @param name Ennätyksen tekijän nimi.
     * @param score Ennätyspisteet.
     */
    public LeaderboardEntry(String name, int score) {
        this.name = name;
        this.score = score;
        this.time = null;
    }

    /**
     * Luo uuden ennätystä kuvaavan olion.
     *
     * @param name Ennätyksen tekijän nimi.
     * @param score Ennätyspisteet.
     * @param date Hetki jolloin ennätys luotiin.
     */
    public LeaderboardEntry(String name, int score, LocalDateTime date) {
        this.name = name;
        this.score = score;
        this.time = date;
    }

    /**
     * Palauttaa ennätyksen tekijän nimen.
     *
     * @return Nimi.
     */
    public String getName() {
        return name;
    }

    /**
     * Palauttaa ennätyksen pisteet.
     *
     * @return Pisteet.
     */
    public int getScore() {
        return score;
    }

    /**
     * Palauttaa hetken jolloin tämä ennätys tuli palvelimelle.
     *
     * @return Ennätyksen luomishetki.
     */
    public LocalDateTime getDateTime() {
        return time;
    }

    @Override
    public int compareTo(LeaderboardEntry t) {
        return Integer.compare(score, t.score);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + this.score;
        hash = 59 * hash + Objects.hashCode(this.time);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LeaderboardEntry)) {
            return false;
        } else {
            LeaderboardEntry other = (LeaderboardEntry) obj;
            return other.name.equals(name) && other.score == score && other.time.equals(time);
        }
    }

    /**
     * Palauttaa tiedon siitä, onko nimi sopiva Leaderboardille.
     *
     * @param name Nimi jota testataan.
     * @return Onko nimessä tasan 3 kirjainta, ja onko sen kaikki merkit Basic
     * Latin -blokin näkyviä symboleita?
     */
    public static boolean isValid(String name) {
        if (name.length() != 3) {
            return false;
        }
        for (int i : name.codePoints().toArray()) {
            if (!codepointIsValid(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Kertoo onko merkki Basic Latin -blokin näkyviä symboleita, käytetään
     * isValidissa.
     *
     * @see otm.roguesque.net.LeaderboardEntry#isValid(java.lang.String)
     *
     * @param codepoint Merkin codepoint.
     * @return Onko merkki validi.
     */
    public static boolean codepointIsValid(int codepoint) {
        return codepoint > 0x20 && codepoint < 0x7F;
    }
}
