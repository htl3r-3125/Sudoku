import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Sudoku extends Application {

    private static final int CELL_SIZE = 50;

    @Override
    public void start(Stage primaryStage) {
        GridPane outerGrid = new GridPane();
        outerGrid.setHgap(2);
        outerGrid.setVgap(2);
        outerGrid.setPadding(new Insets(10));

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
                        btn.setText(""); // Optional: z. B. „5“ zur Demo
                        block.add(btn, col, row);
                    }
                }

                outerGrid.add(block, blockCol, blockRow);
            }
        }

        VBox root = new VBox(outerGrid);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sudoku-Board Only");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

