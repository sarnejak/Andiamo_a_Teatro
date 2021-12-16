import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table (name = "prenotazione")
public class Prenotazione {

    private long id;
    private LocalDateTime localDateTime;
    private Utente utente;
    private Spettacolo spettacolo;
    private Posto posto;

    public Prenotazione (){}

    public Prenotazione(LocalDateTime localDateTime, Utente utente, Spettacolo spettacolo) {
        this.localDateTime = localDateTime;
        this.utente = utente;
        this.spettacolo = spettacolo;
    }

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column (name = "data", nullable = false)
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @OneToOne
    @JoinColumn (name = "utente_email", referencedColumnName = "email")
    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    @OneToOne
    @JoinColumn (name = "spettacolo", referencedColumnName = "id")
    public Spettacolo getSpettacolo() {
        return spettacolo;
    }

    public void setSpettacolo(Spettacolo spettacolo) {
        this.spettacolo = spettacolo;
    }

    public String toString(){
        return "Prenotazione a nome di " + utente.getNome() + " " + utente.getCognome()
                + " spettacolo: " + spettacolo.getNome() + " in data " + spettacolo.getDateTime();
    }

    @OneToOne
    @JoinColumn(name = "posto", referencedColumnName = "id")
    public Posto getPosto() {
        return posto;
    }

    public void setPosto(Posto posto) {
        this.posto = posto;
    }
}
