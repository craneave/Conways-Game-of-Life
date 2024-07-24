import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.concurrent.TimeUnit;
import java.util.Random;

//--== Personal Project ==--
// Name: Avery Crane
// Email:  adcrane@wisc.edu

/* 
 * Main class of the Conways Game of Life program, extends Application class from JavaFX 
 */
public class conway extends Application {

	// variables
	int xSceneSize = 750;
	int ySceneSize = 650;
	int xbuttonSize = 65;
	int ybuttonSize = 35;
	String universalFont = "Times New Roman";
	int fontSize = 24;

	// The rows and collumns are 150 simply for the fact of a buffer for going over
	// when checking cells, just to serve as a
	int col = 150;
	int row = 150;
	int rectSize = 5;
	double width = 0.2;

	// The range of what is visible on the screen
	int visLowRange = 10;
	int visHighRange = 110;

	// creating objects
	GridPane gridPane = new GridPane();

	// 151 for buffer
	Rectangle[][] recArray = new Rectangle[151][151];
	BorderPane borderPane = new BorderPane();
	Scene scene = new Scene(borderPane, xSceneSize, ySceneSize);
	Label titleLabel = new Label("Conways Game of Life");
	Button clear = new Button("Clear");
	Button stop = new Button("Stop");
	Button start = new Button("Start");
	Button rand = new Button("Rand");
	VBox buttonPane = new VBox();

	/*
	 * Main of this class, just launches the program
	 * 
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/*
	 * Start of the scene Only takes in the param of the stage
	 */
	public void start(Stage primaryStage) {

		// this creates the 2D array of rectangles so that the data of each rectangle is
		// easily accessible, then assigns the rectangles to a gridPane as well for the
		// user to see
		for (int x = 1; x < row; x++) {
			for (int y = 1; y < col; y++) {
				Rectangle r = new Rectangle();
				r.setStroke(Color.BLACK);
				r.setStrokeWidth(width);
				r.setHeight(rectSize);
				r.setFill(Color.WHITE);
				r.setWidth(rectSize);
				recArray[x][y] = r;
				// on add the visible range to the grid
				if (x >= visLowRange && x <= visHighRange && y <= visHighRange && y >= visLowRange)
					gridPane.add(r, x, y);
				rectListener(recArray[x][y]);
			}
		}

		// Alignments for everything, setting the sizes and fonts
		buttonPane.getChildren().addAll(start, clear, stop, rand);
		buttonPane.setAlignment(Pos.BOTTOM_LEFT);
		start.setPrefSize(xbuttonSize, ybuttonSize);
		clear.setPrefSize(xbuttonSize, ybuttonSize);
		stop.setPrefSize(xbuttonSize, ybuttonSize);
		rand.setPrefSize(xbuttonSize, ybuttonSize);
		titleLabel.setFont(new Font(universalFont, fontSize));
		borderPane.setTop(titleLabel);
		borderPane.setCenter(gridPane);
		borderPane.setLeft(buttonPane);
		gridPane.setAlignment(Pos.CENTER);
		BorderPane.setAlignment(titleLabel, Pos.TOP_CENTER);

		// Animation for the start of the simulation
		AnimationTimer startAnimation = new AnimationTimer() {
			private long lastUpdate = 0;

			@Override
			public void handle(long now) {
				// adjust to how frequently to update
				if ((now - lastUpdate) >= TimeUnit.MILLISECONDS.toNanos(10)) {
					conwaysAlgorithm();
					lastUpdate = now;
				}
			}
		};

		// Start will start the animation of the algorithm, while every other button
		// will stop the animation as well
		start.setOnAction(l -> startAnimation.start());
		stop.setOnAction(l -> startAnimation.stop());
		rand.setOnAction(l -> startAnimation.stop());
		clear.setOnAction(l -> startAnimation.stop());

		buttonListener(clear);
		buttonListener(rand);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	/*
	 * This is a listener for each rectangle object, if they are clicked they will
	 * change to alive, hence the color, you are able to freely edit them during any
	 * point of the simulation
	 * 
	 * Takes a param of a rectangle, i.e. which cell was clicked
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void rectListener(Rectangle r) {

		// If cell clicked, start new event
		r.setOnMouseClicked(new EventHandler() {
			// Handle the event
			public void handle(Event t) {

				// Color the rectangle
				colorRect();
			}

			/*
			 * Little helper method that will just flip the color of a cell
			 */
			public void colorRect() {
				if (r.getFill() == Color.WHITE) {
					r.setFill(Color.GREEN);

				} else
					r.setFill(Color.WHITE);
			}
		});
	}

	/*
	 * This is a listener for each button that needs to perform an action other than
	 * starting or stopping the animation
	 * 
	 * Takes a param of which button was pressed
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buttonListener(Button b) {

		// If a button is clicked
		b.setOnMouseClicked(new EventHandler() {
			// Handle the event
			public void handle(Event t) {

				// If it is the random button, use random helper method
				if (b.getText() == "Rand")
					random();

				// If its the clear button, use clear helper method
				if (b.getText() == "Clear")
					clear();
			}

			/*
			 * Little helper method that will just clear the color of a cell to white
			 */
			public void clear() {
				for (int x = 0; x < col; x++) {
					for (int y = 0; y < row; y++) {
						if (recArray[x][y] != null) {
							recArray[x][y].setFill(Color.WHITE);
							recArray[x][y].setId(null);
						}
					}
				}
			}

			/*
			 * Little helper method that will randomize the number of alive cells, along
			 * with where they are placed
			 */
			public void random() {

				// Create a separate random object
				Random rand = new Random();
				int numOfGreenRow = rand.nextInt(row);
				int numofGreenCol = rand.nextInt(col);
				for (int x = 50; x < numOfGreenRow; x++) {
					for (int y = 50; y < numofGreenCol; y++) {

						// Create new random object just for "extra" randomness ;)
						Random rand2 = new Random();
						int randomRowSpot = rand2.nextInt(visHighRange);
						int randomColSpot = rand2.nextInt(visHighRange);
						if (recArray[randomRowSpot][randomColSpot] != null)
							recArray[randomRowSpot][randomColSpot].setFill(Color.GREEN);
					}
				}
			}
		});
	}

	/*
	 * This is Conways Algorithm that will decide whether a cell lives or dies, uses
	 * multiple helper methods as well
	 *
	 */
	public void conwaysAlgorithm() {
		for (int x = 1; x < row; x++) {
			for (int y = 1; y < col; y++) {

				/*
				 * This section will check if a dead cell should come alive, and then will mark
				 * the ID of the cell. This is done because we cannot change the state of the
				 * cell without fully checking every cell first.
				 */
				if (recArray[x][y] != null) {
					if (recArray[x][y].getFill() == Color.WHITE) {
						if (checkNeighbors(x, y) == 3)
							recArray[x][y].setId("To be alive");
					} else {
						if (checkNeighbors(x, y) < 2) {
							recArray[x][y].setId("To be dead");

						}
						if (checkNeighbors(x, y) > 3)
							recArray[x][y].setId("To be dead");
					}
				}
			}
		}

		// Now that every cell has been checked, we can now go back through and change
		// the ones that need to be changed.
		for (int x = 1; x < row; x++) {
			for (int y = 1; y < col; y++) {

				// For the cells that had nothing changed get ignored
				if (recArray[x][y].getId() != null) {
					if (recArray[x][y].getId() == "To be alive")
						recArray[x][y].setFill(Color.GREEN);
					if (recArray[x][y].getId() == "To be dead")
						recArray[x][y].setFill(Color.WHITE);
				}
			}
		}

	}

	/*
	 * This helper method will search the surrounding cells and will add up the
	 * number of neighbors said cell has.
	 * 
	 * Takes in row and column index of cell needed to be checked Will return # of
	 * neighbors for cell
	 */
	public int checkNeighbors(int row, int col) {
		int livingNeighbor = 0;

		for (int a = row - 1; a <= row + 1; a++) {
			for (int b = col - 1; b <= col + 1; b++) {
				if (recArray[a][b] != recArray[row][col]) {
					// for the buffer slots in the array
					if (recArray[a][b] != null)
						if (recArray[a][b].getFill() == Color.GREEN) {
							livingNeighbor++;
						}
				}
			}
		}
		return livingNeighbor;
	}

}
