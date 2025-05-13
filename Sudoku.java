package at.rennweg.htl.java.gui_ii.sudokuproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Sudoku extends Application {

    //9x9 Sudoku-Feld bestehend aus Buttons
    private Button[][] board = new Button[9][9];

    //aktuell ausgewählte Zelle
    private Button selectedCell = null;

    //Größe jeder Zelle in Pixel
    private static final int CELL_SIZE = 50;

    @Override
    public void start(Stage primaryStage) {
        launchSudokuBoard(primaryStage);
    }

    //Initialisiert das Sudoku-Board und die Zahlenleiste
    private void launchSudokuBoard(Stage stage) {
        GridPane outerGrid = new GridPane(); // äußerer Grid für 3x3 Blöcke
        outerGrid.setHgap(2);
        outerGrid.setVgap(2);
        outerGrid.setPadding(new Insets(10));

        this.board = new Button[9][9];

        // Erstellen der 3x3 Blöcke mit je 3x3 Buttons
        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                GridPane block = new GridPane(); // einzelner 3x3 Block
                block.setHgap(1);
                block.setVgap(1);
                block.setPadding(new Insets(2));
                block.setStyle("-fx-border-color: black; -fx-border-width: 2;"); // Blockumrandung

                // Zellen innerhalb eines Blocks erstellen
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        // Berechnung der tatsächlichen Zeile und Spalte im 9x9 Feld
                        int absoluteRow = blockRow * 3 + row;
                        int absoluteCol = blockCol * 3 + col;

                        Button btn = new Button();
                        btn.setPrefSize(CELL_SIZE, CELL_SIZE);
                        btn.setFont(Font.font(22));

                        board[absoluteRow][absoluteCol] = btn; // Button ins Board einfügen

                        //beim Klick wird Zelle ausgewählt
                        btn.setOnAction(e -> selectCell(btn));

                        block.add(btn, col, row);
                    }
                }
                outerGrid.add(block, blockCol, blockRow);
            }
        }

        // Erstellen der Zahlenleiste von 1 bis 9
        HBox numberPanel = new HBox(5);
        numberPanel.setAlignment(Pos.CENTER);
        numberPanel.setPadding(new Insets(10));

        for (int i = 1; i <= 9; i++) {
            Button numBtn = new Button(String.valueOf(i));
            numBtn.setStyle("-fx-font-size: 22px;");
            numBtn.setPrefSize(CELL_SIZE, CELL_SIZE);

            //Zahl in die ausgewählte Zelle einfügen
            numBtn.setOnAction(e -> setNumber(numBtn.getText()));

            numberPanel.getChildren().add(numBtn);
        }

        // Hauptlayout mit Board und Zahlenleiste
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(outerGrid, numberPanel);

        // Szene und Stage konfigurieren
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Sudokuboard");
        stage.show();
    }

    // Markiert die aktuell ausgewählte Zelle
    private void selectCell(Button btn) {
        //vorherige Auswahl zurücksetzen
        if (selectedCell != null) {
            selectedCell.setStyle("-fx-font-size: 22px;");
        }

        selectedCell = btn;

        //neue Auswahl hervorheben
        selectedCell.setStyle("-fx-font-size: 22px; -fx-background-color: lightblue;");
    }

    // Setzt eine Zahl in die ausgewählte Zelle
    private void setNumber(String number) {
        if (selectedCell != null) {
            selectedCell.setText(number);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // als nächstes bitte das Sudoku erstellen (die Methode zur Erstellung eines fehlerfreien Sudokus)
}
