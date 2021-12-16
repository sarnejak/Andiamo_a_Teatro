import javax.persistence.*;
import java.util.List;

@Entity
@Table (name = "citta")
public class Citta {

    private String nome, cap, provincia;
    private List<Sede> sede;

    public Citta(){}

    public Citta(String nome, String cap, String provincia, List<Sede> sede) {
        this.nome = nome;
        this.cap = cap;
        this.provincia = provincia;
        this.sede = sede;
    }

    @Column (name = "nome", nullable = false)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Id
    @Column (name = "cap")
    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    @Column (name = "provincia", nullable = true)
    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    @OneToMany (mappedBy = "citta")
    public List<Sede> getSede() {
        return sede;
    }

    public void setSede(List<Sede> sede) {
        this.sede = sede;
    }

    public String toString(){
        return nome + "  " + "Provincia di " + provincia;
    }
}
