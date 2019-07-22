package connectfour.viz.game;

import static edu.wustl.cse231s.v5.V5.launchAppWithReturn;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import connectfour.core.BitBoard;
import connectfour.core.Board;
import connectfour.core.Player;
import connectfour.instructor.ConnectFourInstructorUtils;
import connectfour.studio.ConnectFour;
import connectfour.studio.WinOrLoseHeuristic;
import connectfour.viz.core.BoardPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class ConnectFourViz extends Application {
	private static enum TurnTaker {
		USER() {
			@Override
			public int takeTurn(Board board, BoardPane boardPane, AutomaticPlayerConfig config) {
				int column;
				do {
					column = boardPane.awaitMove();
				} while (!board.canPlay(column));
				return column;
			}
		},
		STUDENT() {
			@Override
			public int takeTurn(Board board, BoardPane boardPane, AutomaticPlayerConfig config)
					throws InterruptedException, ExecutionException {
				return launchAppWithReturn(() -> {
					return ConnectFour.selectNextColumn(board, config.getHeuristic(), config.getSearchDepthPredicate(),
							config.getParallelDepthPredicate());
				});
			}
		},
		INSTRUCTOR() {
			@Override
			public int takeTurn(Board board, BoardPane boardPane, AutomaticPlayerConfig config) {
				return launchAppWithReturn(() -> {
					return ConnectFourInstructorUtils.selectNextColumn(board, config.getSearchDepth());
				});
			}
		};
		public abstract int takeTurn(Board board, BoardPane boardPane, AutomaticPlayerConfig config)
				throws InterruptedException, ExecutionException;
	}

	private ComboBox<TurnTaker> redControllerComboBox;
	private ComboBox<TurnTaker> yellowControllerComboBox;

	@Override
	public void start(Stage primaryStage) throws Exception {

		ObservableList<TurnTaker> redControllerOptions = FXCollections.observableArrayList(TurnTaker.values());
		this.redControllerComboBox = new ComboBox<>(redControllerOptions);
		this.redControllerComboBox.setValue(TurnTaker.STUDENT);

		ObservableList<TurnTaker> yellowControllerOptions = FXCollections.observableArrayList(TurnTaker.values());
		this.yellowControllerComboBox = new ComboBox<>(yellowControllerOptions);
		this.yellowControllerComboBox.setValue(TurnTaker.INSTRUCTOR);

		BoardPane pane = new BoardPane();

		PlayerView winnerView = new PlayerView();

		ToggleGroup turnToggleGroup = new ToggleGroup();
		RadioButton redTurn = new RadioButton();
		redTurn.setDisable(true);
		redTurn.setUserData(Player.RED);
		redTurn.setToggleGroup(turnToggleGroup);

		RadioButton yellowTurn = new RadioButton();
		yellowTurn.setDisable(true);
		yellowTurn.setUserData(Player.YELLOW);
		yellowTurn.setToggleGroup(turnToggleGroup);

		AutomaticPlayerConfig redConfig = new AutomaticPlayerConfig();
		AutomaticPlayerConfig yellowConfig = new AutomaticPlayerConfig();

		HBox redPane = new HBox(redTurn, new PlayerView(Player.RED), this.redControllerComboBox);
		HBox yellowPane = new HBox(yellowTurn, new PlayerView(Player.YELLOW), this.yellowControllerComboBox);

		redPane.setAlignment(Pos.CENTER_LEFT);
		redPane.getChildren().addAll(redConfig.getNodes());
		yellowPane.setAlignment(Pos.CENTER_LEFT);
		yellowPane.getChildren().addAll(yellowConfig.getNodes());

		Button runButton = new Button("run");
		runButton.setStyle("-fx-font: 18 Serif; -fx-base: #71FF71;");
		runButton.setPadding(new Insets(8, 32, 8, 32));
		
		FlowPane status = new FlowPane(new Label("winner:"), winnerView);
		VBox left = new VBox(redPane, yellowPane);
		VBox right = new VBox(runButton, status);
		right.setAlignment(Pos.CENTER_RIGHT);
		status.setAlignment(Pos.CENTER_RIGHT);
		BorderPane top = new BorderPane();
		top.setLeft(left);
		top.setRight(right);

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(pane);
		borderPane.setTop(top);
		Scene scene = new Scene(borderPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect Four");

		ExecutorService ex = Executors.newSingleThreadExecutor();
		runButton.setOnAction(e -> {
			runButton.setDisable(true);
			ex.submit(() -> {
				Board board = new BitBoard();
				pane.update(board);
				for (Player current = Player.RED; !board.isDone(); current = current.getOpponent()) {
					Player _current = current;
					Player _winner = board.getWinner();
					Platform.runLater(() -> {
						winnerView.setPlayer(_winner);
						if (_current == Player.RED) {
							redTurn.setSelected(true);
						} else {
							yellowTurn.setSelected(true);
						}
					});

					TurnTaker turnTaker;
					AutomaticPlayerConfig config;
					if (current == Player.RED) {
						turnTaker = redControllerComboBox.getValue();
						config = redConfig;
					} else {
						turnTaker = yellowControllerComboBox.getValue();
						config = yellowConfig;
					}
					int column = turnTaker.takeTurn(board, pane, config);
					board = board.createNextBoard(column);
					pane.update(board);
				}
				Player _winner = board.getWinner();
				Platform.runLater(() -> {
					winnerView.setPlayer(_winner);
					runButton.setDisable(false);
				});
				return null;
			});
		});

		primaryStage.setOnCloseRequest(e -> {
			ex.shutdown();
			System.exit(0);
		});

		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
