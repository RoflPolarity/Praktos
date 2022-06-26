import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private ImageView MainPic;

    @FXML
    private ImageView SobelPic;

    @FXML
    private Button getFile;

    @FXML
    private ImageView normPic;

    @FXML
    private TextField prievue;

    @FXML
    private Button run;

    @FXML
    private ImageView testPic;

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
                MainPic.setImage(new Image("file:///" + JFC.getSelectedFile().getAbsolutePath()));
            }
        });
        run.setOnAction(event -> {
            try {
                Veivlet main = new Veivlet(new String(files.get().getAbsolutePath().getBytes(), "windows-1251"));
                normPic.setImage(main.getNoiseImg());
                SobelPic.setImage(main.getsobelImg());
                testPic.setImage(main.getGrabImg());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}