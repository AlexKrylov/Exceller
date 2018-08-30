package ru.AlexKRylov;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public ListView<String> fileChooser;
    @FXML
    public Button filesDir;
    @FXML
    private ListView<String> connView;
    @FXML
    private Button startButton;
    @FXML
    private Button refreshButton;
    @FXML
    private TextArea sqlArea;
    @FXML
    private TextField inColumn;
    @FXML
    private TextField outColumn;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button txtButton;

    private String sql;
    private String in;
    private String out;

    /*Script example:
    SELECT Name || '|' || LastName FROM Employees WHERE PhoneNumber = :d1 */

    @FXML
    public void onClickConnect() {
        String fileName = fileChooser.getSelectionModel().getSelectedItem();
        String fileFormat = getFileExtension(fileName);
        if (sql != null && connView.getSelectionModel().getSelectedItem() != null && in != null && out != null && fileName != null && in.matches("[0-9]*") && out.matches("[0-9]*")) {
            assert fileFormat != null;
            if (!fileFormat.equals(".xlsx") && !fileFormat.equals(".xls")) {
                Messager.setMessage("Не верный формат");
            } else {
                disableButton();
                SqlHandler sqlHandler = new SqlHandler(connView.getSelectionModel().getSelectedItem(), sql, in, out, fileName, fileFormat);
                progressBar.setProgress(0);
                progressBar.progressProperty().unbind();
                progressBar.progressProperty().bind(sqlHandler.progressProperty());
                sqlHandler.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                        t -> {
                            progressBar.progressProperty().unbind();
                            enableButton();
                        });
                new Thread(sqlHandler).start();
            }
        } else {
            assert in != null;
            assert out != null;
            if (!in.matches("[0-9]*") || !out.matches("[0-9]*")) {
                Messager.setMessage("Введите число!");
            } else {
                Messager.setMessage("Не все параметры выбраны!");
            }
        }
    }

    @FXML
    public void onClickOpenDir() throws IOException {
        String path = System.getProperty("user.dir") + "/files";
        Desktop.getDesktop().open(new File(path));
    }

    public void onClickOpenTxt() throws IOException {
        String path = System.getProperty("user.dir") + "/connect.txt";
        Desktop.getDesktop().open(new File(path));
    }

    @FXML
    public void onClickRefresh() throws IOException {
        fileChooser.getItems().clear();
        connView.getItems().clear();
        fileChooser.getItems().addAll(AddList.fileList());
        connView.getItems().addAll(AddList.connectList());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileChooser.getItems().addAll(AddList.fileList());
        sqlArea.textProperty().addListener((observable, oldValue, newValue) -> sql = newValue);
        sqlArea.setWrapText(true);
        inColumn.textProperty().addListener((observable, oldValue, newValue) -> in = newValue);
        outColumn.textProperty().addListener((observable, oldValue, newValue) -> out = newValue);
        try {
            connView.getItems().addAll(AddList.connectList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFileExtension(String file) {
        int index = file.indexOf('.');
        return index == -1 ? null : file.substring(index);
    }

    private void enableButton() {
        fileChooser.setEditable(false);
        filesDir.setDisable(false);
        connView.setEditable(false);
        startButton.setDisable(false);
        refreshButton.setDisable(false);
        sqlArea.setEditable(true);
        inColumn.setEditable(true);
        outColumn.setEditable(true);
    }

    private void disableButton() {
        fileChooser.setEditable(true);
        filesDir.setDisable(true);
        connView.setEditable(true);
        startButton.setDisable(true);
        refreshButton.setDisable(true);
        sqlArea.setEditable(false);
        inColumn.setEditable(false);
        outColumn.setEditable(false);
    }
}
