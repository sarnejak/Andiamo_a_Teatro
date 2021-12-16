import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import static java.nio.file.Files.newBufferedReader;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

public class DbUtility {

    private final EntityManager manager;
    private static final int ultimePrenotazioni = 3; //prenotazioni analizzate per suggerimenti
    private static final int maxPosti = 4; //max posti prenotabili

    public DbUtility(EntityManager manager) {
        this.manager = manager;
    }

    /**
     * MENU
     * @throws ParseException generato da check su data
     */
    public void menu() throws ParseException {

        showMessageDialog(null,"ANDIAMO A TEATRO TI DA' IL BENVENUTO! ");
        String scelta = JOptionPane.showInputDialog("Scegli una delle seguenti opzioni " +
                "**DIGITA NUMERO CORRISPONDENTE\n" + "1  REGISTRATI\n" + "2  PRENOTA SPETTACOLO\n"
                + "3  FILTRA SPETTACOLI");
        int opzioneScelta = Integer.parseInt(scelta);

        switch (opzioneScelta) {

            case 1:
                Utente u = registraUtente();
                if (!isRegistrato(u.getEmail())) {
                    manager.persist(u);
                    System.out.println("Ciao " + u.getNome() + "! REGISTRAZIONE AVVENUTA CON SUCCESSO!");
                }
                break;

            case 2:
                String email = showInputDialog("Inserisci la tua email");
                while (!isValidate(email)) {
                    email = showInputDialog("EMAIL NON VALIDA: Inserisci email **ENTER per uscire");
                    if (email.equals(""))
                        return;
                }
                String qry = "FROM Utente WHERE email = :value";
                Query query = manager.createQuery(qry).setParameter("value", email);
                Utente utente = (Utente) query.getSingleResult();
                int numPosti = Integer.parseInt(showInputDialog("Numero posti da prenotare **max 4"));
                if(numPosti <= maxPosti){
                    List<Prenotazione> prenotazioni = prenotaSpettacolo(utente, numPosti);
                    System.out.println("PRENOTAZIONE AVVENUTA CON SUCCESSO!");
                    for(Prenotazione p : prenotazioni) {
                        System.out.println(p.toString());
                        manager.persist(p);
                    }
                } else {
                    System.out.println("MAX 4 POSTI!");
                }
                break;

            case 3:
                scelta = showInputDialog("IMPOSTA PARAMETRI\n" + "1 CITTA' e DATA\n"
                        + "CITTA', DATA e GENERE\n" + "3 CITTA', DATA, " +
                        "GENERE e SEDE");
                opzioneScelta = Integer.parseInt(scelta);
                switch (opzioneScelta) {

                    case 1:
                        String citta = cittaValida();
                        LocalDateTime data = LocalDateTime.parse
                                (dataValida(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        List<Spettacolo> spettacoli = filtraSpettacoli(citta,data);
                        stampa(spettacoli);
                        break;

                    case 2:
                        citta = cittaValida();
                        data = LocalDateTime.parse
                                (dataValida(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        Genere genere = Genere.valueOf(genereValido());
                        spettacoli = filtraSpettacoli(citta,data,genere);
                        stampa(spettacoli);
                        break;

                    case 3:
                        citta = cittaValida();
                        data = LocalDateTime.parse
                                (dataValida(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        genere = Genere.valueOf(genereValido());
                        String sede = sedeValida(getCapFromString(citta));
                        spettacoli = filtraSpettacoli(citta,data,genere,sede);
                        stampa(spettacoli);
                        break;

                    default:
                        showInputDialog(null,"GRAZIE PER AVERCI SCELTO");
                }
        }
    }


    /**
     * SUGGERIMENTI PROSSIMI SPETTACOLI
     * tutti gli spettacoli del prossimo mese che hanno
     * lo stesso genere degli ultimi tre spettacoli visti
     */
    public List<Spettacolo> suggerimenti(Utente u) {

        String qry;
        Query query;


        //Ordino per data tutte le prenotazioni effettuate dall'utente
        qry = "FROM Prenotazione WHERE utente_email = :value ORDER by data DESC";
        query = manager.createQuery(qry).setParameter("value", u.getEmail());
        List<Prenotazione> prenotazioni = query.getResultList();


        //ultime N prenotazioni effettuate
        ArrayList<Prenotazione> prenotazioniRecenti = new ArrayList<>();
        Set<Genere> generi = new HashSet<>(); //genere ultime prenotazioni
        for (int i = 0; i < ultimePrenotazioni; i++) {
            prenotazioniRecenti.add(prenotazioni.get(i));
            //generi.add(prenotazioniRecenti.get(i).getSpettacolo().getGenere());
        }


        //prossimo mese ##data => 1 del mese successivo --- ora => 00:01
        LocalDateTime dataAttuale = LocalDateTime.now();
        int mese = dataAttuale.getMonthValue() == 12 ? 1 : dataAttuale.getMonthValue() + 1; //prossimo mese
        int anno = dataAttuale.getMonthValue() == 12 ? dataAttuale.getYear() + 1 : dataAttuale.getYear();
        LocalDateTime data = LocalDateTime.of(anno, mese, 1, 0, 1); //data


        //programma prossimo mese
        qry = "From Spettacolo WHERE data >= : value";
        query = manager.createQuery(qry).setParameter("value", data);
        List<Spettacolo> spettacoli = query.getResultList();
        List<Spettacolo> filtratiPerGenere = new ArrayList<>();
        for (int i = 0; i < prenotazioniRecenti.size(); i++) {
            if (contain(prenotazioniRecenti.get(i).getSpettacolo().getGenere(), spettacoli))
                filtratiPerGenere.add(spettacoli.get(i));
        }

        return filtratiPerGenere;
    }


    /**
     * REGISTRAZIONE UTENTE
     * @return utente da inserire in DB
     */
    public Utente registraUtente() {
        //inserisco l'email
        String email = showInputDialog("Email");
        Utente u = new Utente();
        //controllo se l'email è valida
        while (!isValidate(email)) {
            showMessageDialog(null, "EMAIL NON VALIDA!");
            email = showInputDialog("Inserisci email VALIDA : ");
        }
        //se l'email non è già presente nel DB chiedo i dati dell'utente
        if (!isRegistrato(email)) {
            //nome
            String nome = showInputDialog("Nome");
            u.setNome(nome);
            //cognome
            String cognome = showInputDialog("Cognome");
            u.setCognome(cognome);
            //indirizzo
            String indirizzo = showInputDialog("Indirizzo");
            u.setIndirizzo(indirizzo);
            //email
            u.setEmail(email);
            //password
            String pass = showInputDialog("Password");
            u.setPassword(pass);
            //telefono
            String telefono = showInputDialog("Numero di Telefono **facoltativo**");
            if (!telefono.equals("")) {
                u.setTelefono(telefono);
            } else {
                u.setTelefono("NULL");
            }
        } else {
            showMessageDialog(null, "UTENTE GIA REGISTRATO!");
            u = getUtenteFromDB(email);
        }

        return u;
    }

    /**
     * Check utente registrato
     * @param email dell'utente
     * @return se utente è presente nel database
     */
    public boolean isRegistrato(String email) {

        String qry = "FROM Utente WHERE email = :value1";
        Query query = manager.createQuery(qry).setParameter("value1", email);
        List result = query.getResultList();
        return result.size() == 1;

    }

    /**
     * Utente dal DB
     * @param email dell'utente
     * @return utente registrato con email
     */
    public Utente getUtenteFromDB(String email){

        String qry = "From Utente WHERE email = :value";
        Query query = manager.createQuery(qry).setParameter("value", email);

        return (Utente) query.getSingleResult(); //UNIQUE;
    }

    /**
     * Email Validator
     * @param email da validare
     * @return se email inserita è valida
     */
    public boolean isValidate(String email) {
        final Pattern EMAIL_REGEX = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:." +
                "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9]" +
                "(?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        return EMAIL_REGEX.matcher(email).matches();
    }


    /**
     * PRENOTAZIONE SPETTACOLO
     * @param utente che prenota lo spettacolo
     * @param numeroPosti che l'utente intende prenotare (max quattro)
     * l'utente può prenotare un max di 4 p ---> max 4 prenotazioni per
     * un determinato spettacolo in una determinata data
     * @return prenotazione/i effettuata
     */
   public List<Prenotazione> prenotaSpettacolo(Utente utente, int numeroPosti) {

       String qry;
       int scelta; //scelta effettuata
       Query query; //query da risolvere
       List<Prenotazione> prenotazioni = new ArrayList<>(numeroPosti);
       Spettacolo spettacolo;


       //stampa lista spettacoli
       qry = "SELECT nome FROM Spettacolo";
       query = manager.createQuery(qry);
       List<String> listaNomi = query.getResultList();
       MyHashMap<String> nomiSpettacoli = new MyHashMap<>(listaNomi);
       nomiSpettacoli.stampaPossibiliScelte();
       scelta = Integer.parseInt(showInputDialog
              ("Seleziona spettacolo: **inserisci numero corrispondente"));
       String nome = nomiSpettacoli.get(scelta); //NOME


       //seleziona una città (che ha in programma lo spettacolo)
       qry = "FROM Spettacolo WHERE nome = :value";
       query = manager.createQuery(qry).setParameter("value", nome);
       List<Spettacolo> listaSpettacoli = query.getResultList();
       MyHashMap<Spettacolo> spettacoli = new MyHashMap<>(listaSpettacoli);
       MyHashMap.stampaSpettacoli(spettacoli);
       scelta = Integer.parseInt(showInputDialog
               ("Scegli città **inserisci numero corrispondente"));
       Citta city = spettacoli.get(scelta).getSede().getCitta(); //CITTA


       //seleziona una sede con spettacolo in programma
       qry = "FROM Sede WHERE citta = '" + city.getCap() + "'";
       query = manager.createQuery(qry);
       List<Sede> listaSedi = query.getResultList();
       StringBuilder sediID = new StringBuilder("(");
       for(Sede sede : listaSedi){
           sediID.append(sede.getId()+",");
       }
       sediID = sediID.delete(sediID.length()-1,sediID.length()).append(")");
       String fromSediID = sediID.toString();


       //sedi con spettacolo in programma in città
       qry = "FROM Spettacolo WHERE nome = :value AND sede_id IN " + fromSediID;
       query = manager.createQuery(qry).setParameter("value",nome);
       List<Spettacolo> spettacoliInSede = query.getResultList();
       listaSedi = new ArrayList<>();
       for(Spettacolo s : spettacoliInSede){
           listaSedi.add(s.getSede());
       }
       MyHashMap<Sede> sedi = new MyHashMap<>(listaSedi);
       sedi.stampaPossibiliScelte();
       scelta = Integer.parseInt(showInputDialog
               ("Scegli la sede: **inserisci numero corrispondente"));
       Sede sede = sedi.get(scelta); //SEDE


       //sala e relativi posti in sede scelta
       qry = "FROM Sala WHERE sede_id = :value";
       query = manager.createQuery(qry).setParameter("value", sede.getId());
       List<Sala> listaSale = query.getResultList();
       List<LocalDateTime> listaOrari = new ArrayList<>();
       for(Spettacolo s : spettacoliInSede       ) {
           listaOrari.add(s.getDateTime());
       }
       /*Se ci sono due spettacoli in programma (stesso giorno e stessa sede)
       saranno sicuramente in orari differenti*/
       MyHashMap<LocalDateTime> orari = new MyHashMap<>(listaOrari);
       orari.stampaPossibiliScelte();
       scelta = Integer.parseInt(showInputDialog("Scegli orario spettacolo"));
       LocalDateTime dateTime = orari.get(scelta); //ORARIO


       //sale con spettacolo in programma
       Sala sala = new Sala();
       Genere genere = null;
       int durata = 0;
       double prezzo = 0;
       for(int i = 0; i < listaSale.size(); i++) {
           for(int j = 0; j < listaSale.get(i).getProgramma().size(); j++){
               if(nome.equalsIgnoreCase(listaSale.get(i).getProgramma().get(j).getNome())
               && dateTime == listaSale.get(i).getProgramma().get(j).getDateTime()) {
                   sala = listaSale.get(i); //SALA
                   genere = listaSale.get(i).getProgramma().get(j).getGenere(); //GENERE
                   durata = listaSale.get(i).getProgramma().get(j).getDurata(); //DURATA
                   prezzo = listaSale.get(i).getProgramma().get(j).getPrezzo() ; //PREZZO
                   break;
               }
           }
       }


       //posti disponibili al momento della prenotazione
       ArrayList<Posto> postiLiberi = new ArrayList<>();
       for (int i = 0; i < sala.getPosti().size(); i++) {
           if (sala.getPosti().get(i).isLibero()) {
               postiLiberi.add(sala.getPosti().get(i));
           }
       }


       //seleziona posto
       HashMap<Integer,Posto> postiInPrenotazione = new HashMap<>(); //POSTI PRENOTATI
       if (postiLiberi.size() >= numeroPosti) {
           do {
               new MyHashMap<>(postiLiberi).stampaPossibiliScelte();
               scelta = Integer.parseInt(showInputDialog
                       ("Scegli posto: **inserisci numero corrispondente"));
               postiInPrenotazione.put(scelta, postiLiberi.get(scelta - 1));
               postiLiberi.get(scelta - 1).setLibero(false);
           } while (postiInPrenotazione.size() < numeroPosti);
       } else {
           System.out.println("Posti liberi rimasti: " + postiLiberi.size());
       }


       //spettacolo in citta e sede selezionati
       qry = "FROM Spettacolo WHERE nome = :value1 AND sede_id = :value2";
       query = manager.createQuery(qry)
               .setParameter("value1", nome)
               .setParameter("value2", sala.getSede().getId());
       listaSpettacoli = query.getResultList();


       //PRENOTAZIONE/I
       for(int i = 0; i < numeroPosti; i++){
           spettacolo = new Spettacolo(nome,genere,durata,prezzo,dateTime,sede,sala);
           Prenotazione prenotazione = new Prenotazione(LocalDateTime.now(),utente,spettacolo);
           prenotazione.setPosto(postiInPrenotazione.get(i));
           prenotazioni.add(prenotazione);
       }


       //totale da pagare
       System.out.println("TOTALE DA PAGARE: " + (prezzo * numeroPosti));


        return prenotazioni;
    }


    /**FILTRI SPETTACOLO
     * Prenota uno spettacolo #ricerca per città e data
     * @param citta in cui prenotare lo spettacolo
     * @param data giorno e orario in cui si vuole andare a teatro
     * @return spettacoli filtrati per città e data
     */
    public List<Spettacolo> filtraSpettacoli(String citta, LocalDateTime data) {

        String qry;
        Query query; //query da risolvere
        Citta c; //città scelta per lo spettacolo


        //String città => CAP città
        qry = "FROM Citta WHERE nome = : value";
        query = manager.createQuery(qry).setParameter("value", citta);
        c = (Citta) query.getSingleResult();
        String cap = c.getCap(); //CAP città


        //Sedi in città selezionata (CAP)
        qry = "FROM Sede WHERE citta = '" + cap + "'";
        query = manager.createQuery(qry);
        List<Sede> sedi = query.getResultList();
        //MyHashMap<Sede> sediInCitta = new MyHashMap(sedi);


        //spettacoli in città e orario selezionati
        qry = "FROM Spettacolo WHERE data = :value1 AND sede_id IN (";
        StringBuilder sb = new StringBuilder(qry);
        for(Sede sede : sedi){
            sb.append(sede.getId()).append(",");
        }
        sb.delete(sb.length() - 1, sb.length()).append(")");//elimino l'ultima virgola
        query = manager.createQuery(sb.toString())
                .setParameter("value1", data);

        return query.getResultList();

    }

    /**
     * Prenota uno spettacolo #ricerca per città, data e genere
     * @param citta in cui prenotare lo spettacolo
     * @param data giorno e ora in cui si vuole andare a teatro
     * @param genere spettacolo
     * @return spettacoli filtrati per città, data e genere
     */
    public List<Spettacolo> filtraSpettacoli(String citta, LocalDateTime data, Genere genere) {

        List<Spettacolo> spettacoli = filtraSpettacoli(citta,data);


        //elimino spettacoli con genere diverso da quello cercato
        spettacoli.removeIf(s -> s.getGenere() != genere);


        return spettacoli;
    }

    /**
     * Prenota uno spettacolo #ricerca per città, data, genere e sede
     * @param citta in cui vedere lo spettacolo
     * @param data giorno e ora in cui andare a teatro
     * @param genere spettacolo
     * @param sede in cui vedere lo spettacolo
     * @return spettacoli che soddisfano i parametri in ingresso
     */
    public List<Spettacolo> filtraSpettacoli(String citta, LocalDateTime data, Genere genere, String sede) {

        List<Spettacolo> filteredBy = filtraSpettacoli(citta,data,genere);


        //spettacoli in sede selezionata
        List<Spettacolo> spettacoli = new ArrayList<>();
        for(Spettacolo s : filteredBy){
            if(s.getSede().getNome().equals(sede))
                spettacoli.add(s);
        }

        return spettacoli;

    }


    /**
     * Check genere spettacolo
     * @param genere spettacolo
     * @param list di spettacoli
     * @return se lista contiene uno spettacolo di quel genere
     */
    public boolean contain(Genere genere, List<Spettacolo> list) {

        for (Spettacolo spettacolo : list) {
            if (spettacolo.getGenere() == genere)
                return true;
        }
        return false;
    }


    /**
     * CHECK SU INPUT UTENTE
     */
        //CITTA'
    public String scegliCitta(){

        String citta; //inserimento utente

        do {
            citta = showInputDialog("Città in cui vedere lo spettacolo");
        } while (citta.equals(""));


        try {
            citta = citta.substring(0,1).toUpperCase() + citta.substring(1).toLowerCase();
            String qry = "FROM Citta WHERE nome = :value";
            Query query = manager.createQuery(qry).setParameter("value",citta);
            Citta city = (Citta) query.getSingleResult();
        } catch (NoResultException noResultException) {
            showMessageDialog(null,"CITTA NON PRESENTE!");
            citta = "ASSENTE";
        }

        return citta;
    }
    public String cittaValida(){

        String citta = scegliCitta();


        while (citta.equals("ASSENTE")){
            citta = scegliCitta();
        }

        return citta;
    }

        //CAP
    public String getCapFromString(String nomeCitta){

        String qry = "From Citta where nome = :value";
        Query query = manager.createQuery(qry).setParameter("value",nomeCitta);
        Citta result = (Citta) query.getSingleResult();

        return result.getCap();
    }

        //GENERE
    public String scegliGenere(){

        String genere;
        boolean exist = false;

        do {
            genere = showInputDialog("Genere spettacolo");
        } while (genere.equals(""));


        genere = genere.substring(0,1).toUpperCase() + genere.substring(1).toLowerCase();
        Genere[] generi = Genere.values();
        for(Genere g : generi) {
            if (genere.equals(g.name())){
                exist = true;
                break;
            }
        }

        if(!exist) {
            String message = "NESSUN RISULTATO TROVATO!";
            showMessageDialog(null, message);
            genere = message;
        }

        return genere;
    }
    public String genereValido(){

        String genere = scegliGenere();
        while (genere.equals("NESSUN RISULTATO TROVATO!")) {
            genere = scegliGenere();
        }

        return genere;
    }

        //SEDE
    public String scegliSede(String cap){

        String sede;

        do {
            sede = showInputDialog("Nome sede in cui vedere lo spettacolo: ");
        } while(sede.equals(""));


        try {
            sede = sede.substring(0,1).toUpperCase() + sede.substring(1).toLowerCase();
            String qry = "FROM Sede WHERE nome = :value AND citta = '" + cap + "'";
            Query query = manager.createQuery(qry)
                    .setParameter("value", sede);
            Sede s = (Sede) query.getSingleResult(); //UNIQUE
        } catch (NoResultException noResultException) {
            showMessageDialog(null,"SEDE NON PRESENTE!");
            sede = "ASSENTE";
        }

        return sede;
    }
    public String sedeValida(String cap){

        String sede = scegliSede(cap);


        while(sede.equals("ASSENTE")){
            sede = sedeValida(cap);
        }

        return sede;
    }

        //DATA
    public String scegliData(){

        String data;

        String pattern = "yyyy-MM-dd HH:mm"; //formato data
        data = showInputDialog("Inserisci Data: " + pattern);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setLenient(false); //check validità data

        try {
            Date date = simpleDateFormat.parse(data);
        } catch (ParseException parseException) {
            showMessageDialog(null,"DATA NON VALIDA!");
            data = "ERROR";
        }

        return data;
    }
    public String dataValida() throws ParseException {

        String data = scegliData();


        while (data.equals("ERROR")) {
            data = scegliData();
        }

        return data;
    }


    /**
     *INSERIMENTO DATI IN TABELLE
     */
        //TAB POSTO
    public List<Posto> creaPosti(int numPosti) {

        //cinque file con stesso numero di posti per fila
        Character[] file = {'A','B','C','D','E'};
        List<Posto> posti = new ArrayList<>();
        List<Sala> sale = manager.createQuery("FROM Sala").getResultList();

        for(Sala s : sale) {
            for (Character character : file) {
                for (int j = 1; j <= numPosti; j++) {
                    Posto p = new Posto(j, character);
                    p.setSala(s);
                    posti.add(p);
                    manager.persist(p);
                }
            }
        }

        return posti;
    }

        //TAB SALA
    //per convenzione (MIA) tutte le sale hanno stesso numero posti e file
    public List<Sala> creaSale() {

        String qry = "FROM Sede";
        Query query = manager.createQuery(qry);
        List<Sede> lista = query.getResultList();
        final int numeroSale = 2; //creo due sale per sede
        ArrayList<Sala> sale = new ArrayList<>();

        for(Sede sede : lista){
            for(int i = 0; i < numeroSale; i++) {
                Sala sala = new Sala();
                sala.setNome(showInputDialog("Nome sala: "));
                sala.setSede(sede);
                manager.persist(sala);
                sale.add(sala);
            }
        }

        return sale;
    }

        //TAB SPETTACOLO
    public String leggiFile(){

       Path path = Paths.get("src/main/java/spettacoli.txt");
       String str  = "";

       try(BufferedReader br = newBufferedReader(path)){

           while(br.ready()){
               str += br.readLine();
           }

       } catch (IOException e) {
           e.printStackTrace();
       }

       return str;
    }
    public String[] creaArrayDaFile(){

       return leggiFile().split("\\$");
    }
    public String[][] creaArrayDiArray(){

       String[] str = creaArrayDaFile();
       String[][] array = new String[str.length][str[0].split(",").length];

       for(int i = 0; i < array.length; i++) {
           array[i] = str[i].split(",");
       }

       return array;

    }
    public void inserisciSpettacoli(){

       String[][] arr = creaArrayDiArray();

        for (String[] strings : arr) {
            Spettacolo spettacolo = new Spettacolo();
            spettacolo.setNome(strings[0]); //Nome
            LocalDateTime dateTime = LocalDateTime.parse(strings[4],
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            spettacolo.setDateTime(dateTime); //Data
            spettacolo.setDurata(Integer.parseInt(strings[2])); //Durata
            spettacolo.setGenere(Genere.valueOf(strings[1])); //Genere
            spettacolo.setPrezzo(Double.parseDouble(strings[3])); //Prezzo
            long sedeId = Long.parseLong(strings[5]);
            String qry = "From Sede where id = :value";
            Query query = manager.createQuery(qry).setParameter("value", sedeId);
            Sede sede = (Sede) query.getSingleResult(); //UNIQUE
            spettacolo.setSede(sede); //Sede
            long salaId = Long.parseLong(strings[6]);
            qry = "From Sala where id = :value";
            Sala sala = (Sala) manager.createQuery(qry).setParameter("value",salaId).getSingleResult();
            spettacolo.setSala(sala); //Sala
            manager.persist(spettacolo);
        }

    }


    /**
     * STAMPA
     * @param lista da stampare
     */
    public void stampa(List<Spettacolo> lista) {
        for(Spettacolo s : lista){
            System.out.println(s.toString());
        }
    }
}