import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.ParseException;

public class Main {

    private static EntityManagerFactory factory
            = Persistence.createEntityManagerFactory("TestPersistenceUnit");
    private static EntityManager manager
            = factory.createEntityManager();

    public static void main(String[] args) throws ParseException {

        DbUtility dbUtility = new DbUtility(manager);
        manager.getTransaction().begin();


        dbUtility.menu();


        //Spettacoli prossimo mese su interessi utente
        //List<Spettacolo> spettacoli = dbUtility.suggerimenti(utente);


        manager.getTransaction().commit();
        manager.close();

    }
}