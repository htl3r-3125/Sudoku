package at.rennweg.htl.java.gui_ii.sudokuproject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Sudoku extends Application {

    // 9x9 Sudoku-Feld aus Buttons
    private Button[][] board = new Button[9][9];

    // aktuell ausgewählte Zelle
    private Button selectedCell = null;

    // Größe jeder Zelle in Pixeln
    private static final int CELL_SIZE = 50;

    // Speicherung der vollständigen Lösung, um Eingaben zu überprüfen
    private int[][] solution;

    // Schwierigkeitsgrad
    private static Sudoku.difficulty difficulty;

    // Referenz auf das Haupt-Stage für spätere Refferenz
    private Stage primaryStage;

    // Zähler für Fehler
    private int fehlerCounter = 0;

    private int seconds;

    Timeline timeline;

    Label timerLable = new Label("Time: 0 min 0 sec");

    @Override
    public void start(Stage primaryStage) {
        // Speichern des übergebenen Stages, um später darauf zugreifen zu können
        this.primaryStage = primaryStage;
        showDifficultySelection(primaryStage);
    }

    private void launchSudokuBoard(Stage stage) {
        // Äußerer GridPane: enthält 3x3 Blocks (jeweils 3x3 Zellen)
        GridPane outerGrid = new GridPane();
        outerGrid.setHgap(2);
        outerGrid.setVgap(2);
        outerGrid.setPadding(new Insets(10));

        // Neues 9x9-Array von Buttons
        this.board = new Button[9][9];

        // Erzeugen der 3x3-Blöcke
        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                // Jeder Block ist selbst ein GridPane mit 3x3 Zellen
                GridPane block = new GridPane();
                block.setHgap(1);
                block.setVgap(1);
                block.setPadding(new Insets(2));
                // Setze eine dicke Umrandung für den 3x3-Block
                block.setStyle("-fx-border-color: black; -fx-border-width: 2;");

                // Für jede Zeile/Spalte innerhalb des Blocks (3x3)
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        // Berechne die “echte” Zeilen- und Spaltenposition im 9x9-Gesamtfeld
                        int absoluteRow = blockRow * 3 + row;
                        int absoluteCol = blockCol * 3 + col;

                        // Erzeuge einen Button als “Zelle” in dem Sudoku-Feld
                        Button btn = new Button();
                        btn.setPrefSize(CELL_SIZE, CELL_SIZE);
                        btn.setFont(Font.font(22));

                        // Setze eine eindeutige ID, um später anhand der ID die Koordinaten zu ermitteln
                        btn.setId("r" + absoluteRow + "c" + absoluteCol);

                        board[absoluteRow][absoluteCol] = btn;

                        // Wenn eine Tastatur-Zahl eingegeben wird, soll setNumber aufgerufen werden
                        btn.setOnKeyTyped(keyEvent -> setNumber(keyEvent.getCharacter()));
                        // Wenn der Button geklickt wird, soll er als “ausgewählt” markiert werden
                        btn.setOnAction(e -> selectCell(btn));

                        block.add(btn, col, row);
                    }
                }

                // Füge den kompletten 3x3-Block an die richtige Position im äußeren Grid
                outerGrid.add(block, blockCol, blockRow);
            }
        }

        // Erstelle Sudoku-Daten (int[][]) je nach Schwierigkeit und fülle die Buttons
        setSudoku(generateSudoku(difficulty));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        seconds = 0;

        timerLable.setMinWidth(CELL_SIZE);
        timerLable.setAlignment(Pos.CENTER);
        timerLable.setStyle("-fx-font-size: 22px;");

        Timeline timelineUpdateText = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> {
                    timerLable.setText(timeDisplayCorrect(seconds));
                })
        );

        timelineUpdateText.setCycleCount(Timeline.INDEFINITE);
        timelineUpdateText.play();

        startTimer();

        // Erstelle eine horizontale Box (HBox) für die Zahlentasten 1 bis 9
        HBox numberPanel = new HBox(5);
        numberPanel.setAlignment(Pos.CENTER);
        numberPanel.setPadding(new Insets(10));

        // Für jede Zahl von 1 bis 9
        for (int i = 1; i <= 9; i++) {
            Button numBtn = new Button(String.valueOf(i));
            numBtn.setStyle("-fx-font-size: 22px;");
            numBtn.setPrefSize(CELL_SIZE, CELL_SIZE);

            // Wenn eine Zahlentaste gedrückt wird, setze diese Zahl in die ausgewählte Zelle
            numBtn.setOnAction(e -> setNumber(numBtn.getText()));

            numberPanel.getChildren().add(numBtn);
        }

        outerGrid.setAlignment(Pos.CENTER);

        // Hauptlayout: vertikale Box, die zuerst das Sudoku-Board und darunter die Zahlenleiste enthält
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setMinWidth(700);
        root.getChildren().addAll(timerLable, outerGrid, numberPanel);

        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Sudokuboard");
        stage.show();
    }

    public String timeDisplayCorrect (int sec) {

        int minutes = sec / 60;
        int seconds = sec % 60;
        return "Time: " + minutes + " min " + seconds + " sec";

    }

    public void startTimer() {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    seconds++;
                    timerLable.setText(timeDisplayCorrect(seconds));
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private void showDifficultySelection(Stage stage) {
        Stage dialog = new Stage();
        // Setzt Modality so, dass das Hauptfenster blockiert ist, bis der Dialog geschlossen wird
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Schwierigkeit wählen");

        // Buttons für die drei Schwierigkeitsstufen
        Button easyBtn = new Button("EASY");
        Button mediumBtn = new Button("MEDIUM");
        Button hardBtn = new Button("HARD");

        // Wenn “Easy” gedrückt wird:
        easyBtn.setOnAction(e -> {
            difficulty = Sudoku.difficulty.EASY;
            dialog.close();
            launchSudokuBoard(stage);
        });
        // Wenn “Medium” gedrückt wird:
        mediumBtn.setOnAction(e -> {
            difficulty = Sudoku.difficulty.MEDIUM;
            dialog.close();
            launchSudokuBoard(stage);
        });
        // Wenn “Hard” gedrückt wird:
        hardBtn.setOnAction(e -> {
            difficulty = Sudoku.difficulty.HARD;
            dialog.close();
            launchSudokuBoard(stage);
        });

        // Einheitliche Breite für jede Schaltfläche festsetzen
        easyBtn.setPrefWidth(100);
        mediumBtn.setPrefWidth(100);
        hardBtn.setPrefWidth(100);

        // Layout für den Dialog
        VBox layout = new VBox(10, easyBtn, mediumBtn, hardBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Szene mit fester Größe erstellen und dem Dialog zuweisen
        Scene scene = new Scene(layout, 400, 200);
        dialog.setScene(scene);
        dialog.show();
    }

    private int[][] generateSudoku(difficulty diff) {
        // Initialisiere leeres 9x9-Board mit Nullen
        int[][] board = new int[9][9];
        fillBoard(board);
        // Speichere die vollständige Lösung
        solution = deepCopy(board);

        // Bestimme, wie viele Zahlen wir entfernen wollen (0 setzen), je nach Difficulty
        int removeCount = switch (diff) {
            case EASY -> 1; // Zur Presentation von dem Endscreen
            case MEDIUM -> 45;
            case HARD -> 55;
        };

        Random rand = new Random();
        // Entferne so lange Zahlen, bis removeCount gleich 0 ist
        while (removeCount > 0) {
            int row = rand.nextInt(9);
            int col = rand.nextInt(9);
            if (board[row][col] != 0) {
                board[row][col] = 0;
                removeCount--;
            }
        }
        return board;
    }

    private boolean fillBoard(int[][] board) {
        //Zeile
        for (int row = 0; row < 9; row++) {
            //Spalte
            for (int col = 0; col < 9; col++) {
                // Wenn Zelle leer -> versuche Zahl einzusetzen
                if (board[row][col] != 0) {
                    continue;
                }
                List<Integer> numbers = new ArrayList<>();
                for (int i = 1; i <= 9; i++) {
                    numbers.add(i);
                }

                Collections.shuffle(numbers);

                // Probiere jede Zahl in zufälliger Reihenfolge
                for (int number : numbers) {
                    // Prüfe, ob number in row,col gültig ist
                    if (!isValid(board, row, col, number)) {
                        continue;
                    }
                    board[row][col] = number;
                    // Rekursiver Aufruf
                    if (fillBoard(board)) {
                        return true;
                    }
                    // Wenns nicht passt -> Zurücksetzen
                    board[row][col] = 0;
                }
                // Wenn keine Zahl passt -> abbruch
                return false;
            }
        }
        // Wenn hier dann passt alles
        return true;
    }

    private int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            // clone() kopiert die 1D-Zeile
            copy[i] = original[i].clone();
        }
        return copy;
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        // Zeile und Spalte prüfen
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num) {  // Wenn dieselbe Zahl bereits in Zeile oder Spalte vorkommt, nicht gültig.
                return false;
            }
        }
        // Bestimme den Startindex des 3x3-Blocks
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        // Durchlaufe die 3 Zeilen und 3 Spalten des Blocks
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (board[r][c] == num) {
                    // Wenn die Zahl bereits im Block vorkommt, nicht gültig
                    return false;
                }
            }
        }
        // Ansonsten ist das Einsetzen erlaubt
        return true;
    }

    private void setSudoku(int[][] sudokuIntArray) {
        // Gehe jede Zelle im 9x9-Board durch
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = sudokuIntArray[row][col];
                Button btn = board[row][col];
                if (value != 0) {
                    // Vorgabe-Zahl: Button-Text setzen, Button deaktivieren, hellgraue Hintergrundfarbe
                    btn.setText(String.valueOf(value));
                    btn.setDisable(true);
                    btn.setStyle("-fx-font-size: 22px; -fx-background-color: lightgray; -fx-opacity: 1; -fx-base: lightgray;");
                } else {
                    // Leere Zelle: Text entfernen, Button aktivieren, Standard-Styling
                    btn.setText("");
                    btn.setDisable(false);
                    btn.setStyle("-fx-font-size: 22px;");
                }
            }
        }
    }

    private void selectCell(Button btn) {
        // Wenn bereits eine Zelle ausgewählt war, entferne deren Hervorhebung
        if (selectedCell != null) {
            selectedCell.setStyle("-fx-font-size: 22px;");
        }

        selectedCell = btn;
        selectedCell.setStyle("-fx-font-size: 22px; -fx-background-color: lightblue;");
    }

    private void setNumber(String number) {
        // Nur ausführen, wenn eine Zelle ausgewählt ist und das Zeichen eine gültige Zahl von 1–9 ist
        if (selectedCell != null && "123456789".contains(number)) {
            selectedCell.setText(number);
            // Koordinaten der Zelle anhand ihrer ID auslesen
            int[] coords = parseCoordinates(selectedCell.getId());
            int row = coords[0];
            int col = coords[1];

            int entered = Integer.parseInt(number);

            // Vergleich mit der zuvor gespeicherten Lösung
            if (entered == solution[row][col]) {
                // Wenn korrekt: markiere grün, fixiere Eintrag
                selectedCell.setStyle("-fx-font-size: 22px; -fx-background-color: lightgreen; -fx-opacity: 1; -fx-base: lightgreen;");
                selectedCell.setDisable(true);
            } else {
                // Wenn falsch: markiere rot, entferne Text, erhöhe Fehlerzähler
                selectedCell.setStyle("-fx-font-size: 22px; -fx-background-color: lightcoral; -fx-opacity: 1; -fx-base: lightcoral;");
                selectedCell.setText("");
                fehlerCounter++;
            }

            selectedCell = null;

            checkCompletion();
        }
    }

    private void checkCompletion() {
        boolean complete = true;
        // Schleife über alle Zellen; falls eine leere gefunden wird, incomplete
        for (int row = 0; row < 9 && complete; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col].getText().isEmpty()) {
                    complete = false;
                    break;
                }
            }
        }
        if (complete) {
            primaryStage.setTitle("Sudokuboard zum bewundern!");
            showCompletionDialog();
            primaryStage.close();
        }
    }

    private void showCompletionDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Ergebnis");

        stopTimer();

        Label time = new Label(timeDisplayCorrect(seconds));
        Label fehlerText = new Label("Fehler: " + fehlerCounter);
        Button schließenBtn = new Button("Schließen?");
        Button erneutBtn = new Button("Nochmal versuchen");

        // Setze Fehlerzähler zurück
        fehlerCounter = 0;

        schließenBtn.setPrefWidth(150);
        erneutBtn.setPrefWidth(150);

        // Action für Schließen-Button: Dialog schließen
        schließenBtn.setOnAction(e -> {
            dialog.close();
        });
        // Action für Erneut-Button: Dialog schließen und Schwierigkeitsauswahl erneut anzeigen
        erneutBtn.setOnAction(e -> {
            dialog.close();
            showDifficultySelection(primaryStage);
        });

        // Layout für den Dialog: vertikale Box mit Elementen
        VBox layout = new VBox(10, time, fehlerText, schließenBtn, erneutBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        
        Scene scene = new Scene(layout, 300, 150);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private int[] parseCoordinates(String id) {
        // nimmt sich die row aus der id raus
        int row = Integer.parseInt(id.substring(1, id.indexOf('c')));
        // nimmt sich die collum aus der id raus
        int col = Integer.parseInt(id.substring(id.indexOf('c') + 1));
        return new int[] {row, col};
    }

    public static void main(String[] args) {
        launch(args);
    }

    enum difficulty {
        EASY, MEDIUM, HARD
    }
}
