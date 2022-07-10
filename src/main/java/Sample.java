import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicReference;

public class Sample {
    @FXML
    private Button GetReport;

    @FXML
    private Tab DOGTAB;

    @FXML
    private ImageView DOGWavelet;

    @FXML
    private ImageView DOGWaveletPorog;


    @FXML
    private Tab MHATTAB;

    @FXML
    private ImageView MHATWavelet;

    @FXML
    private ImageView MHATWaveletPorog;


    @FXML
    private ImageView Original;


    @FXML
    private ImageView Sobel;

    @FXML
    private Tab WAVETAB;

    @FXML
    private ImageView WAVEWavelet;

    @FXML
    private ImageView WAVEWaveletPorog;

    @FXML
    private Button getFile;

    @FXML
    private TextField prievue;

    @FXML
    private Button run;

    @FXML
    private Button extBtn;

    public void exit(){
        System.exit(1);
    }

    @FXML
    void initialize(){
        AtomicReference<Thread> JFS = new AtomicReference<>();
    Veivlet[] main = new Veivlet[1];
        AtomicReference<File> files = new AtomicReference<>();
        run.setDisable(true);
        GetReport.setDisable(true);
        getFile.setOnAction(event -> {
            getFile.setDisable(true);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    JFrame frame = new JFrame();
                    JFileChooser JFC = new JFileChooser();
                    JFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    int result = JFC.showOpenDialog(frame);
                    if (result == JFileChooser.APPROVE_OPTION ){
                        try {
                            prievue.setText(new String(JFC.getSelectedFile().getAbsolutePath().getBytes(), "windows-1251"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        files.set(JFC.getSelectedFile());
                        run.setDisable(false);
                        Original.setImage(new Image("file:///" + JFC.getSelectedFile().getAbsolutePath()));
                    }
                    return null;
                }
            };
            JFS.set(new Thread(task));
            JFS.get().start();
        });
        run.setOnAction(event -> {
            System.out.println(JFS.get().isAlive());
            run.setDisable(true);
            try {
                long start = System.currentTimeMillis();
                main[0] = new Veivlet(new String(files.get().getAbsolutePath().getBytes(), "windows-1251"));
            Task task = new Task<Void>(){
                    protected Void call() throws Exception{
                        DOGTAB.setDisable(true);
                        MHATTAB.setDisable(true);
                        WAVETAB.setDisable(true);
                        main[0].Dog.join();
                        main[0].MHAT.join();
                        main[0].WAVE.join();
                        DOGWavelet.setImage(main[0].Dog.getWavelet());
                        DOGWaveletPorog.setImage(main[0].Dog.getWaveletPorog());
                        DOGTAB.setDisable(false);
                        MHATWavelet.setImage(main[0].MHAT.getWavelet());
                        MHATWaveletPorog.setImage(main[0].MHAT.getWaveletPorog());
                        MHATTAB.setDisable(false);
                        WAVEWavelet.setImage(main[0].WAVE.getWavelet());
                        WAVEWaveletPorog.setImage(main[0].WAVE.getWaveletPorog());
                        WAVETAB.setDisable(false);
                        long finish = System.currentTimeMillis() - start;
                        System.out.println(finish/1000);
                        run.setDisable(false);
                        getFile.setDisable(false);
                        GetReport.setDisable(false);
                        return null;
                    }
                };
                Sobel.setImage(main[0].getsobelImg());
                new Thread(task).start();
                } catch (IOException e) {
                e.printStackTrace();
            }
        });
        GetReport.setOnAction(event->{
                try {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("report.fxml"));
                    Parent root = loader.load();

                    report report = loader.getController();
                    report.init(main[0]);

                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                e.printStackTrace();
            }
            });

    }
}