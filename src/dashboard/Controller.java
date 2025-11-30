package dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane; // Per il grafico (inizialmente vuoto)
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets; // Spaziatura
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.ComboBox;


import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Controller {
    private BorderPane root;
    private TableView<Item> databaseTable;
    private Pane storicoView; // Contenitore per il futuro grafico
    private Pane filterTable; // Contenitore per il futuro filtro per la tabella
    private Pane nullTable; // Vuoto da mettere a dx della tabella
    private Pane storicoPane; // Vuoto da mettere a dx della tabella

    private ObservableList<Item> allItems = FXCollections.observableArrayList(); // Lista di appoggio per memorizzare i dati del database

    public Controller() {
        
        root = new BorderPane();

        Label welcomeLabel = new Label("Benvenuto! Seleziona 'Database' o 'Storico' per iniziare.");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333;");
        StackPane.setMargin(welcomeLabel, new Insets(0, 0, 50, 0));
        
        // Usiamo StackPane per centrare perfettamente la Label
        StackPane welcomePane = new StackPane(welcomeLabel);
        
        // 1.0 Inizializzazione della TableView e dei filtri laterali (Database)
        databaseTable = new TableView<>();
        filterTable = new VBox();
        nullTable = new VBox();
        filterTable.setPadding(new Insets(5,50,10,10));
        nullTable.setPadding(new Insets(10));
        
        // 1.1 Aggiunta di filtro regione
        Label lblFiltroRegioni = new Label("Filtro Regione");

        ComboBox<String> cmbRegioni = new ComboBox<>();
        cmbRegioni.getItems().add("Nessun filtro");
        cmbRegioni.getItems().addAll(
            "Abruzzo",
            "Basilicata",
            "Calabria",
            "Campania",
            "Emilia Romagna",
            "Friuli Venezia Giulia",
            "Lazio",
            "Liguria",
            "Lombardia",
            "Marche",
            "Molise",
            "Piemonte",
            "Puglia",
            "Sardegna",
            "Sicilia",
            "Toscana",
            "Trentino Alto Adige",
            "Umbria",
            "Valle d'Aosta",
            "Veneto"
        );
        cmbRegioni.setValue("Nessun filtro");

        // 1.2 Aggiunta filtro anno
        Label lblFiltroAnno = new Label("Filtro Anno");

        ComboBox<String> cmbAnno = new ComboBox<>();
        cmbAnno.getItems().add("Nessun filtro");
        cmbAnno.getItems().addAll("2013/2014", "2014/2015", "2015/2016"); // metti tutti gli anni disponibili
        cmbAnno.setValue("Nessun filtro");

        // 1.3 Aggiunta filtro 

        // 1.3 applica filtro
        cmbRegioni.setOnAction(e -> applicaFiltri(cmbRegioni.getValue(),cmbAnno.getValue()));
        cmbAnno.setOnAction(e -> applicaFiltri(cmbRegioni.getValue(),cmbAnno.getValue()));

        VBox root1 = new VBox(10, lblFiltroRegioni, cmbRegioni, lblFiltroAnno, cmbAnno);
        root1.setPadding(new Insets(20));

        filterTable.getChildren().add(root1);
        
        
        // 1.4 Inizializzazione della tabella
        initializeDatabaseTable();

        
        // 2.0 Inizializzazione della vista "Storico" (Inizialmente vuota)
        storicoView = new VBox(); // VBox o StackPane, a seconda delle esigenze
        storicoView.setPadding(new Insets(10));
        Label storicoLabel = new Label("WORK IN PROGRESS: Antonio pensaci tu");
        storicoLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: #911;"); 
        StackPane.setMargin(storicoLabel, new Insets(0, 0, 50, 0));
        
        // Usiamo StackPane per centrare perfettamente la Label
        storicoPane = new StackPane(storicoLabel);

        // ((VBox) storicoView).getChildren().add(new javafx.scene.control.Label("Area per il Grafico Storico"));
        
        // 3.0 Creazione e Configurazione dei Pulsanti (Top)
        Button databaseButton = new Button("Database");
        Button storicoButton = new Button("Storico");
        
        databaseButton.setOnAction(e -> showDatabaseView());
        storicoButton.setOnAction(e -> showStoricoView());
        
        HBox topButtons = new HBox(10, databaseButton, storicoButton); // Spaziatura 10
        topButtons.setPadding(new Insets(10)); // Margine attorno ai pulsanti
        
        root.setTop(topButtons);
        
        // 4.0 Settaggio della vista iniziale
        aggiornaDati();   // Carica i dati
        root.setCenter(welcomePane);
    }
    
    // Metodo per inizializzare la struttura della TableView
    private void initializeDatabaseTable() {
        TableColumn<Item, Integer> colId = new TableColumn<>("Id");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Item, String> colAnno = new TableColumn<>("Anno");
        colAnno.setCellValueFactory(new PropertyValueFactory<>("anno"));

        TableColumn<Item, String> colNomeAteneo = new TableColumn<>("Nome_Ateneo");
        colNomeAteneo.setCellValueFactory(new PropertyValueFactory<>("nomeAteneo"));

        TableColumn<Item, String> colRegione = new TableColumn<>("Regione");
        colRegione.setCellValueFactory(new PropertyValueFactory<>("regione"));

        TableColumn<Item, String> colAreaGeografica = new TableColumn<>("Area_Geografica");
        colAreaGeografica.setCellValueFactory(new PropertyValueFactory<>("areaGeografica"));

        TableColumn<Item, String> colTipoLaurea = new TableColumn<>("Tipologia_Laurea");
        colTipoLaurea.setCellValueFactory(new PropertyValueFactory<>("tipologiaLaurea"));

        TableColumn<Item, String> colCorso = new TableColumn<>("Corso");
        colCorso.setCellValueFactory(new PropertyValueFactory<>("corso"));

        TableColumn<Item, Integer> colFemmine= new TableColumn<>("F");
        colFemmine.setCellValueFactory(new PropertyValueFactory<>("f"));

        TableColumn<Item, Integer> colMaschi = new TableColumn<>("M");
        colMaschi.setCellValueFactory(new PropertyValueFactory<>("m"));

        TableColumn<Item, Integer> colTotale = new TableColumn<>("Totale");
        colTotale.setCellValueFactory(new PropertyValueFactory<>("totale"));

        databaseTable.getColumns().addAll(colId, colAnno, colNomeAteneo, colRegione, colAreaGeografica, colTipoLaurea, colCorso, colFemmine, colMaschi, colTotale);
    }

    public BorderPane getRoot() { return root; }
    
    // Metodi di Visualizzazione
    
    @FXML
    private void showDatabaseView() {
        // Mostra la TableView
        root.setLeft(filterTable); //METTERE I FILTRI
        root.setCenter(databaseTable);
        root.setRight(nullTable);
    }
    
    @FXML
    private void showStoricoView() {
        //root.setRight(null);
        //root.setLeft(null);
        root.setCenter(storicoPane);
    }



    // Metodo di Caricamento Dati
    
    @FXML
    private void aggiornaDati() {
        allItems.clear();

        try (Connection conn = DatabaseConnection.getConnection();
            Statement instruction = conn.createStatement();
            ResultSet rs = instruction.executeQuery(
                "SELECT id, Anno AS anno, Nome_Ateneo AS nomeAteneo, Regione AS regione, Area_Geografica AS areaGeografica, Tipologia_Laurea AS tipologiaLaurea, Corso AS corso, F AS f, M AS m, Totale AS totale FROM imm_final"
            )) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String anno = rs.getString("anno");
                String nome_ateneo = rs.getString("nomeAteneo");
                String regione = rs.getString("regione");
                String area_geografica = rs.getString("areaGeografica");
                String tipologia_laurea = rs.getString("tipologiaLaurea");
                String corso = rs.getString("corso");
                int femmine = rs.getInt("f");
                int maschi = rs.getInt("m");
                int totale = rs.getInt("totale");

                allItems.add(new Item(id, anno, nome_ateneo, regione, area_geografica, tipologia_laurea, corso, femmine, maschi, totale));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // assegna tutti i dati alla TableView
        databaseTable.setItems(allItems);
    }

    // Classe Item 

    public static class Item {
        private int id;
        private String anno;
        private String nomeAteneo;
        private String regione;
        private String areaGeografica;
        private String tipologiaLaurea;
        private String corso;
        private int femmine;
        private int maschi;
        private int totale;


        public Item(int id, String anno, String nomeAteneo, String regione, String areaGeografica, String tipologiaLaurea, String corso, int femmine, int maschi, int totale) {
            this.id = id;
            this.anno = anno;
            this.nomeAteneo = nomeAteneo;
            this.regione = regione;
            this.areaGeografica = areaGeografica;
            this.tipologiaLaurea = tipologiaLaurea;
            this.corso = corso;
            this.femmine = femmine;
            this.maschi = maschi;
            this.totale = totale;
        }

        public int getId() { return id; }
        public String getAnno() { return anno; }
        public String getNomeAteneo() { return nomeAteneo; }
        public String getRegione() { return regione; }
        public String getAreaGeografica() { return areaGeografica; }
        public String getTipologiaLaurea() { return tipologiaLaurea; }
        public String getCorso() { return corso; }
        public int getF() { return femmine; }
        public int getM() { return maschi; }
        public int getTotale() { return totale; }



    }

    private void applicaFiltri(String regioneSelezionata, String annoSelezionato) {

        ObservableList<Item> filtrati = FXCollections.observableArrayList();

        for (Item it : allItems) {
            boolean matchRegione = regioneSelezionata.equals("Nessun filtro") || it.getRegione().equals(regioneSelezionata);
            boolean matchAnno = annoSelezionato.equals("Nessun filtro") || it.getAnno().equals(annoSelezionato);

            if (matchRegione && matchAnno) {
                filtrati.add(it);
            }
        }

        databaseTable.setItems(filtrati);
    }


}