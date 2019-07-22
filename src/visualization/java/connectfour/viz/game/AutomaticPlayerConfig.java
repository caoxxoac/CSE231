/*******************************************************************************
 * Copyright (C) 2016-2018 Dennis Cosgrove
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package connectfour.viz.game;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.IntPredicate;
import java.util.function.ToDoubleFunction;

import connectfour.challenge.OpenEndedHeuristic;
import connectfour.core.Board;
import connectfour.studio.WinOrLoseHeuristic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

/**
 * @author Finn Voichick
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class AutomaticPlayerConfig {
	private static enum HeuristicOption {
		WIN_OR_LOSE(WinOrLoseHeuristic.INSTANCE), OPEN_ENDED(OpenEndedHeuristic.INSTANCE);

		private final ToDoubleFunction<Board> heuristic;

		private HeuristicOption(ToDoubleFunction<Board> heuristic) {
			this.heuristic = heuristic;
		}

		public ToDoubleFunction<Board> getHeuristic() {
			return heuristic;
		}
	}

	private final Collection<Node> nodes;
	private final ComboBox<HeuristicOption> heuristicComboBox;
	private final Spinner<Integer> searchDepthSpinner;
	private final Spinner<Integer> parallelDepthSpinner;

	public AutomaticPlayerConfig() {
		ObservableList<HeuristicOption> heuristicOptions = FXCollections.observableArrayList(HeuristicOption.values());
		this.heuristicComboBox = new ComboBox<>(heuristicOptions);
		this.heuristicComboBox.setValue(HeuristicOption.WIN_OR_LOSE);

		SpinnerValueFactory<Integer> searchValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 12, 8);
		searchDepthSpinner = new Spinner<>();
		searchDepthSpinner.setValueFactory(searchValueFactory);
		searchDepthSpinner.setMaxWidth(64);

		SpinnerValueFactory<Integer> parallelValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 4, 2);
		parallelDepthSpinner = new Spinner<>();
		parallelDepthSpinner.setValueFactory(parallelValueFactory);
		parallelDepthSpinner.setMaxWidth(64);

		nodes = Arrays.asList(createSpacer(), new Label("heuristic:"), heuristicComboBox, createSpacer(),
				new Label("search depth:"), searchDepthSpinner, createSpacer(), new Label("parallel depth:"),
				parallelDepthSpinner);
	}

	public ToDoubleFunction<Board> getHeuristic() {
		return heuristicComboBox.getValue().getHeuristic();
	}

	public IntPredicate getSearchDepthPredicate() {
		return (depth) -> {
			return depth < searchDepthSpinner.getValue();
		};
	}

	public IntPredicate getParallelDepthPredicate() {
		return (depth) -> {
			return depth < Math.min(parallelDepthSpinner.getValue(), searchDepthSpinner.getValue());
		};
	}

	public int getSearchDepth() {
		return searchDepthSpinner.getValue();
	}

	private static Node createSpacer() {
		Region region = new Region();
		region.setPrefWidth(10);
		return region;
	}

	public Collection<Node> getNodes() {
		return nodes;
	}
}
