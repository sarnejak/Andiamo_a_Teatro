import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table (name = "sala")
public class Sala {

    private long id;
    private String nome;
    private List<Posto> posti;
    private Sede sede;
    private List<Spettacolo> programma;

    public Sala(){}

    public Sala(String nome, List<Posto> posti) {
        this.nome = nome;
        this.posti = posti;
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

    @Column (name = "nome", nullable = false)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @OneToMany (mappedBy = "sala")
    public List<Posto> getPosti() {
        return posti;
    }

    public void setPosti(List<Posto> posti) {
        this.posti = posti;
    }

    @ManyToOne
    @JoinColumn (name = "sede_id", referencedColumnName = "id")
    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    @OneToMany (mappedBy = "sala")
    public List<Spettacolo> getProgramma() {
        return programma;
    }

    public void setProgramma(List<Spettacolo> programma) {
        this.programma = programma;
    }

    public String toString(){
        return "Sala " + nome + " in " + sede.getNome();
    }

}
