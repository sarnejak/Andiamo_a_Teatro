import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table (name = "posto")
public class Posto {

    private long id;
    private int numero;
    private char fila;
    private boolean libero;
    private Sala sala;

    public Posto(){}

    public Posto(int numero, char fila) {
        this.numero = numero;
        this.fila = fila;
        this.libero = true;
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

    @Column (name = "numero", nullable = false)
    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    @Column (name = "fila", nullable = false, length = 3)
    public char getFila() {
        return fila;
    }

    public void setFila(char fila) {
        this.fila = fila;
    }

    @Column (name = "libero")
    @ColumnDefault ("true")
    public boolean isLibero() {
        return libero;
    }

    public void setLibero(boolean libero) {
        this.libero = libero;
    }

    @ManyToOne
    @JoinColumn (name = "sala_id", referencedColumnName = "id")
    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Posto posto = (Posto) o;
        return numero == posto.numero && fila == posto.fila && sala.getId() == posto.sala.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, fila, sala.getId());
    }

    public String toString(){
        return isLibero() ? "Posto numero " + numero + " Fila " + fila + " DISPONIBILE"
                : "Posto numero " + numero + " Fila " + fila + " NON DISPONIBILE";
    }
}
