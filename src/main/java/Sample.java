import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicReference;

public class Sample {

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
    void initialize(){
        AtomicReference<File> files = new AtomicReference<>();
        run.setDisable(true);
        getFile.setOnAction(event -> {
            JFrame frame = new JFrame();
            JFileChooser JFC = new JFileChooser();
            JFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = JFC.showOpenDialog(frame);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        });
        run.setOnAction(event -> {
            try {
                long start = System.currentTimeMillis();
                Veivlet main = new Veivlet(new String(files.get().getAbsolutePath().getBytes(), "windows-1251"));
                Sobel.setImage(main.getsobelImg());
                DOGTAB.setDisable(true);
                MHATTAB.setDisable(true);
                WAVETAB.setDisable(true);
                main.Dog.join();
                DOGWavelet.setImage(main.Dog.getWavelet());
                DOGWaveletPorog.setImage(main.Dog.getWaveletPorog());
                DOGTAB.setDisable(false);
                main.MHAT.join();
                MHATWavelet.setImage(main.MHAT.getWavelet());
                MHATWaveletPorog.setImage(main.MHAT.getWaveletPorog());
                MHATTAB.setDisable(false);
                main.WAVE.join();
                WAVEWavelet.setImage(main.WAVE.getWavelet());
                WAVEWaveletPorog.setImage(main.WAVE.getWaveletPorog());
                WAVETAB.setDisable(false);
                long finish = System.currentTimeMillis();
                System.out.println((finish-start)/1000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}