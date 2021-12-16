import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.swing.*;

@Entity
@Table(name = "utente")
public class Utente {

    private String nome, cognome, indirizzo, email, password, telefono;
    //private long telefono;

    public Utente(){}

    public Utente(String nome, String cognome, String indirizzo, String email, String password, String telefono) {
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
    }

    public Utente(String nome, String cognome, String indirizzo, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.email = email;
        this.password = password;
    }

    @Column (name  = "nome", nullable = false)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        while(nome.equals("")){
            nome = JOptionPane.showInputDialog("ATTENZIONE! Inserisci nome: ");
        }
        this.nome = nome;
    }

    @Column (name = "cognome", nullable = false)
    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        while(cognome.equals("")){
            cognome = JOptionPane.showInputDialog("ATTENZIONE! Inserisci cognome: ");
        }
        this.cognome = cognome;
    }

    @Column (name  = "indirizzo", nullable = false)
    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        while(indirizzo.equals("")){
            indirizzo = JOptionPane.showInputDialog("ATTENZIONE! Inserisci indirizzo: ");
        }
        this.indirizzo = indirizzo;
    }

    @Id
    @Column (name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column (name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column (name = "telefono")
    @ColumnDefault("NULL")
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}
