import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

public class Sample {

        @FXML
        private Button getRes;
        @FXML
        private ProgressBar Progress;
        @FXML
        private ImageView DOG;
        @FXML
        private ImageView DOGPorog;
        @FXML
        private ImageView MHAT;
        @FXML
        private ImageView MHATPorog;
        @FXML
        private ImageView MainPic;
        @FXML
        private ImageView Sobel;
        @FXML
        private ImageView WAVE;
        @FXML
        private ImageView WAVPorog;
        @FXML
        private Button getFile;
        @FXML
        private TextField prievue;
        @FXML
        private Button run;


    @FXML
    void initialize(){
        Progress.setDisable(true);
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
                MainPic.setImage(new Image("file:///" + JFC.getSelectedFile().getAbsolutePath()));
            }
        });
        run.setOnAction(event -> {
            try {
                long start = System.currentTimeMillis();
                Veivlet main = new Veivlet(new String(files.get().getAbsolutePath().getBytes(), "windows-1251"));
                Sobel.setImage(main.getsobelImg());
                main.Dog.join();
                main.MHAT.join();
                main.WAVE.join();
                long finish = System.currentTimeMillis();
                System.out.println((finish-start)/1000);
                DOG.setImage(main.Dog.getWavelet());
                DOGPorog.setImage(main.Dog.getWaveletPorog());
                MHAT.setImage(main.MHAT.getWavelet());
                MHATPorog.setImage(main.MHAT.getWaveletPorog());
                WAVE.setImage(main.WAVE.getWavelet());
                WAVPorog.setImage(main.WAVE.getWaveletPorog());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}