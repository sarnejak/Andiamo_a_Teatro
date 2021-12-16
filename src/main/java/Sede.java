import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table (name = "sede")

public class Sede {

    private String nome, indirizzo;
    private long id;
    private boolean open;
    private Citta citta;
    private List<Sala> sale;
    private List<Spettacolo> programmazione;

    public Sede(){}

    public Sede(String nome, boolean open, List<Sala> sale,
                List<Spettacolo> programmazione) {
        this.nome = nome;
        this.open = open;
        this.sale = sale;
        this.programmazione = programmazione;
    }

    @Column (name = "nome", nullable = false)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column (name = "indirizzo", nullable = true)
    @ColumnDefault("NULL")
    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column (name = "open")
    @ColumnDefault("false")
    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @OneToMany (mappedBy = "sede")
    public List<Sala> getSale() {
        return sale;
    }

    public void setSale(List<Sala> sale) {
        this.sale = sale;
    }

    @OneToMany (mappedBy = "sede")
    public List<Spettacolo> getProgrammazione() {
        return programmazione;
    }

    public void setProgrammazione(List<Spettacolo> programmazione) {
        this.programmazione = programmazione;
    }

    @ManyToOne
    @JoinColumn (name = "citta", referencedColumnName = "cap")
    public Citta getCitta() {
        return citta;
    }

    public void setCitta(Citta citta) {
        this.citta = citta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sede sede = (Sede) o;
        return indirizzo.equals(sede.indirizzo) && citta.equals(sede.citta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indirizzo, citta);
    }

    @Override
    public String toString(){
        return nome;
    }

    /**
     *  Orari Programmazione:
     * - PRIMO SPETTACOLO: Ore 18:00
     * - SECONDO SPETTACOLO: Ore 22:00
     */
    public static MyHashMap<LocalDateTime> creaTabellaOrari(){

        List<LocalDateTime> orari = new ArrayList<>();
        LocalDateTime dataAttuale = LocalDateTime.now();
        orari.add(LocalDateTime.of(dataAttuale.getYear(),dataAttuale.getMonthValue(),
                dataAttuale.getDayOfMonth(),18, 0));
        orari.add(LocalDateTime.of(dataAttuale.getYear(),dataAttuale.getMonthValue(),
                dataAttuale.getDayOfMonth(),22,0));

        return new MyHashMap<>(orari);
    }


    /**
     * Programma sale
     */
    public void setProgrammaSale() {

        Random random = new Random();
        final int numSpettacoli = 2; //spettacoli giornalieri
        MyHashMap<LocalDateTime> orari = creaTabellaOrari(); //orari predefiniti
        List<Spettacolo> spettacoli; //variabile di appoggio

        for(int i = 1; i <= numSpettacoli; i++) {
            spettacoli = programmazione;
            for (Sala sala : sale) {
                int rand = random.nextInt(spettacoli.size()); //assegnazione random
                sala.getProgramma().add(programmazione.get(rand));
            }
        }
    }
}