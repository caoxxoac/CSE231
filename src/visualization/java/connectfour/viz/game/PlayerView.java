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

import connectfour.core.Player;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * @author Finn Voichick
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class PlayerView extends StackPane {
	private final Circle circle = new Circle();

	private PlayerView(int size, Player player) {
		circle.setRadius(size / 2);
		circle.setStroke(Color.BLACK);
		this.getChildren().setAll(circle);

		this.setPrefWidth(size + 4);
		this.setPrefHeight(size + 4);
		this.setPlayer(player);
	}

	private PlayerView(int size) {
		this(size, null);
	}
	
	public PlayerView(Player player) {
		this(24, player);
	}

	public PlayerView() {
		this(24);
	}

	public void setPlayer(Player player) {
		if (player != null) {
			if (player == Player.RED) {
				circle.setFill(Color.RED);
			} else {
				circle.setFill(Color.YELLOW);
			}
		} else {
			circle.setFill(Color.LIGHTGRAY);
		}
	}
}
