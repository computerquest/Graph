package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.sym.error;

public class Main extends Application {
    Graph graph = new Graph();
    Random rand = new Random();
    ChoiceBox box;
    LinkedList<String> fileUsed = new LinkedList();
    int nextLandmark = 0;

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Graph");
        primaryStage.setScene(new Scene(root, 1000, 300));
        primaryStage.show();

        ScrollPane node = (ScrollPane) root.lookup("#ScrollBar");
        node.setContent(graph.canvas);
        node.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        node.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        ((VBox) root.lookup("#ControlContainer")).setStyle("-fx-background-color: #E3E1E1;");

        box = (ChoiceBox) root.lookup("#Frame");

        Button key = (Button) root.lookup("#key");
        key.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                for (int i = 0; i < fileUsed.size(); i++) {
                    HBox hbox = new HBox();

                    CheckBox checkBox = new CheckBox();
                    checkBox.setTextFill(graph.lineMl.get(i).color);
                    checkBox.setText(fileUsed.get(i));
                    checkBox.setSelected(graph.lineMl.get(i).used);

                    checkBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        public void handle(MouseEvent event) {
                            graph.lineMl.get(graph.findLineI((Color) checkBox.getTextFill())).used = checkBox.isSelected();
                            if (graph.secondaryFrame == -2) {
                                graph.drawAll(graph.currentFrame);
                            } else {
                                graph.joinFrame(graph.currentFrame, graph.secondaryFrame);
                            }
                            System.out.println(checkBox.isSelected());
                        }
                    });

                    Button button = new Button();
                    button.setTextFill(graph.lineMl.get(i).color);
                    button.setText("Linear Regression");

                    button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        public void handle(MouseEvent event) {
                            LinkedList data = new LinkedList();

                            Line line = graph.lineMl.get(graph.findLineI((Color) button.getTextFill()));
                            for (int i = (int) graph.currentFrame; i < line.dataMl.size() & i < (graph.secondaryFrame != -2 ? graph.secondaryFrame : graph.currentFrame + 100); i++) {
                                data.add(line.dataMl.get(i));
                            }

                            graph.linearRegression(data);
                        }
                    });

                    hbox.getChildren().add(checkBox);
                    hbox.getChildren().add(button);

                    vBox.getChildren().add(hbox);
                }

                Stage keyStage = new Stage();
                keyStage.setTitle("Key");
                keyStage.setScene(new Scene(vBox, 500, 150));

                keyStage.show();
            }
        });

        Button join = (Button) root.lookup("#Join");
        join.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                graph.joinFrame(graph.currentFrame, (box.getSelectionModel().getSelectedIndex()) * 100);
            }
        });

        Button refresh = (Button) root.lookup("#refresh");
        refresh.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                graph.clearAll();
                for (int i = 0; i < fileUsed.size(); i++) {
                    readFile(fileUsed.get(i));
                }

                if (graph.secondaryFrame != -2) {
                    graph.joinFrame(graph.currentFrame, graph.secondaryFrame);
                } else {
                    graph.drawAll((box.getSelectionModel().getSelectedIndex() + 1) * 100);
                }
            }
        });

        //C:/Users/jared_000/Desktop/Word prediction project/New folder/tempDelete/tempDelete/history.txt
        //C:/Users/jared_000/Desktop/oldHistory.txt
        //C:/Users/jared_000/Desktop/Word prediction project/New folder/tempDelete/tempDelete/history0.000100 0.900000 1 2.txt
        TextField fileInput = (TextField) root.lookup("#fileInput");
        fileInput.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                String character = e.getCode().toString();
                if (character == "ENTER") {
                    fileUsed.add(fileInput.getText());
                    readFile(fileInput.getText());
                    fileInput.clear();
                }
            }
        });

        box.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                String character = event.getCode().toString();
                if (character == "ENTER") {
                    graph.setToDefault();
                    graph.secondaryFrame = -2;
                    graph.drawAll((box.getSelectionModel().getSelectedIndex()) * 100);
                }
            }
        });

        graph.canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                double posX = event.getX() + (graph.lastX * graph.xScale);
                double posY = event.getY();

                ((Text) root.lookup("#answer")).setText(Double.toString(Math.round(graph.makeRegX(posX))) + "," + Double.toString(Math.round(graph.makeRegY(posY))));

                LinkedList<Color> colors = graph.linesContaining(Math.round(graph.makeRegX(posX)), Math.round(graph.makeRegY(posY)));

                if (colors.size() > 0) {
                    VBox vbox = new VBox();
                    vbox.setAlignment(Pos.CENTER);
                    vbox.getChildren().add(new Text("These Lines contain: "));

                    for (int i = 0; i < colors.size(); i++) {
                        Line line = graph.findLine(colors.get(i));

                        Text text = new Text(line.file);
                        text.setFill(line.color);

                        vbox.getChildren().add(text);
                    }

                    Stage stage = new Stage();
                    stage.setTitle("Lines");
                    stage.setScene(new Scene(vbox, 350, 175));
                    stage.show();
                }
            }
        });
    }

    public LinkedList<String> breakInput(String phrase) {
        LinkedList<String> answers = new LinkedList<>();
        String stuff;
        int beginInt = 0;
        do {
            answers.add(phrase.substring(0, phrase.indexOf(" ")));
            beginInt = phrase.indexOf(" ") + 1;
            phrase = phrase.substring(beginInt, phrase.length());
        } while (phrase.indexOf(" ") != -1);

        answers.add(phrase);

        return answers;
    }

    public void readFile(String file) {
        try {

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            Color color = Color.hsb(rand.nextInt(360), .4 + (.6) * rand.nextDouble(), 1);

            while (checkColor(color) == false) {
                color = Color.hsb(rand.nextInt(360), .4 + (.6) * rand.nextDouble(), 1);
            }

            graph.newLine(color);

            graph.lineMl.get(graph.findLineI(color)).file = file;

            for (int i = 0; (line = reader.readLine()) != null; i++) {
                LinkedList<String> temp = breakInput(line);
                System.out.println(temp.get(0) + " " + temp.get(1));
                graph.newPoint(Double.parseDouble(temp.get(0)), Double.parseDouble(temp.get(1)), color);

                if (Double.parseDouble(temp.get(0)) >= nextLandmark) {
                    box.getItems().addAll(nextLandmark);
                    nextLandmark += 100;
                }
            }

            if (graph.findLine(color).lineMl.get(graph.findLine(color).lineMl.size() - 1).end.x > nextLandmark - 100) {
                box.getItems().addAll(nextLandmark);
                nextLandmark += 100;
            }
            reader.close();

        } catch (IOException | NullPointerException errorA) {
            System.out.println("File not found " + file);
        }
    }

    public boolean checkColor(Color color) {
        for (int i = 0; i < graph.lineMl.size(); i++) {
            if (Math.abs(graph.lineMl.get(i).color.getHue() - color.getHue()) < 10 & Math.abs(graph.lineMl.get(i).color.getSaturation() - color.getSaturation()) < .1) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
