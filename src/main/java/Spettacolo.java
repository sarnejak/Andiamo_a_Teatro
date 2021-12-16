import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table (name = "spettacolo")
public class Spettacolo {

    private long id;
    private String nome;
    private Genere genere;
    private int durata;
    private double prezzo;
    private LocalDateTime dateTime;
    private Sede sede;
    private Sala sala;

    public Spettacolo(){}

    public Spettacolo(String nome, Genere genere, int durata, double prezzo, LocalDateTime dateTime,
                      Sede sede, Sala sala) {
        this.nome = nome;
        this.genere = genere;
        this.durata = durata;
        this.prezzo = prezzo;
        this.dateTime = dateTime;
        this.sede = sede;
        this.sala = sala;
    }

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    @Column (name = "nome")
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Enumerated (EnumType.STRING)
    @Column (name = "genere", nullable = false)
    public Genere getGenere() {
        return genere;
    }

    public void setGenere(Genere genere) {
        this.genere = genere;
    }

    @Column (name = "durata", nullable = false)
    public int getDurata() {
        return durata;
    }

    public void setDurata(int durata) {
        this.durata = durata;
    }

    @Column (name = "prezzo", nullable = false)
    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    @Column (name = "data", nullable = false)
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @ManyToOne
    @JoinColumn (name = "sede_id", referencedColumnName = "id")
    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    @ManyToOne
    @JoinColumn(name = "sala_id", referencedColumnName = "id")
    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public String toString() {
        return nome + "\n data: " + dateTime + "\n sede: " + sede.getNome() + "\n citta: "
                + sede.getCitta().getNome();
    }
}
