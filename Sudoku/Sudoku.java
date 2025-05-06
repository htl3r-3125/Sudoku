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

public class SudokuProject extends Application {

    private Button[][] board = new Button[9][9];
    private Button selectedCell = null;
    private static final int CELL_SIZE = 50;

    @Override
    public void start(Stage primaryStage) {
        launchSudokuBoard(primaryStage);
    }

    private void launchSudokuBoard(Stage stage) {
        GridPane outerGrid = new GridPane();
        outerGrid.setHgap(2);
        outerGrid.setVgap(2);
        outerGrid.setPadding(new Insets(10));

        this.board = new Button[9][9];
        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                GridPane block = new GridPane();
                block.setHgap(1);
                block.setVgap(1);
                block.setPadding(new Insets(2));
                block.setStyle("-fx-border-color: black; -fx-border-width: 2;");

                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        int absoluteRow = blockRow * 3 + row;
                        int absoluteCol = blockCol * 3 + col;
                        Button btn = new Button();
                        btn.setPrefSize(CELL_SIZE, CELL_SIZE);
                        btn.setFont(Font.font(22));
                        btn.setId("r" + (absoluteRow + 1) + "c" + (absoluteCol + 1));
                        board[absoluteRow][absoluteCol] = btn;

                        btn.setOnAction(e -> selectCell(btn));
                        block.add(btn, col, row);
                    }
                }
                outerGrid.add(block, blockCol, blockRow);
            }
        }

        HBox numberPanel = new HBox(5);
        numberPanel.setAlignment(Pos.CENTER);
        numberPanel.setPadding(new Insets(10));

        for (int i = 1; i <= 9; i++) {
            Button numBtn = new Button(String.valueOf(i));
            numBtn.setStyle("-fx-font-size: 22px;");
            numBtn.setPrefSize(CELL_SIZE, CELL_SIZE);
            numBtn.setOnAction(e -> setNumber(numBtn.getText()));
            numberPanel.getChildren().add(numBtn);
        }

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(outerGrid, numberPanel);

        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Sudokuboard");
        stage.show();
    }

    private void selectCell(Button btn) {
        if (selectedCell != null) {
            selectedCell.setStyle("-fx-font-size: 22px;");
        }
        selectedCell = btn;
        selectedCell.setStyle("-fx-font-size: 22px; -fx-background-color: lightblue;");
    }

    private void setNumber(String number) {
        selectedCell.setText(number);
    }

    public static void main(String[] args) {
        launch(args);
    }

    //als nächstes bitte das Sudoku erstellen(die Methode zur erstellung einen Fehler freiene Sudokus)
}
