import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class report {
    Veivlet main;
    @FXML
    private Button exitButton;

    @FXML
    private Button nextButton;



    @FXML
    private Label reportArea;
    AtomicInteger i = new AtomicInteger();
    public void init(Veivlet main) throws UnsupportedEncodingException {
        String str = "После пороговой обработки";
        str = new String(str.getBytes(),UTF_8);
    this.main = main;
        reportArea.setText("SKO(Sobel, Grad) = "+ main.Dog.SKOGRAD_SKOSobel +
                "\nSKO(DOG, Grad) = " + main.Dog.SKOWavelet +
                "\nSKO(Wave, Grad) = " + main.WAVE.SKOWavelet +
                "\nSKO(MHAT, Grad) = " + main.MHAT.SKOWavelet +
                "\n\t"+ str +
                "\nSKO(DOG, Grad) = " + main.Dog.SKOPorog +
                "\nSKO(Wave, Grad) = " + main.WAVE.SKOPorog +
                "\nSKO(MHAT, Grad) = " + main.MHAT.SKOPorog
        );
    }

    public void next(){
        String str = "После пороговой обработки";
        str = new String(str.getBytes(),UTF_8);
        i.getAndIncrement();
        if (i.get() ==0){
            reportArea.setText("SKO(Sobel, Grad) = "+ main.Dog.SKOGRAD_SKOSobel +
                        "\nSKO(DOG, Grad) = " + main.Dog.SKOWavelet +
                        "\nSKO(Wave, Grad) = " + main.WAVE.SKOWavelet +
                        "\nSKO(MHAT, Grad) = " + main.MHAT.SKOWavelet +
                        "\n\t"+ str +
                        "\nSKO(DOG, Grad) = " + main.Dog.SKOPorog +
                        "\nSKO(Wave, Grad) = " + main.WAVE.SKOPorog +
                        "\nSKO(MHAT, Grad) = " + main.MHAT.SKOPorog);

        }if (i.get()==1){
            reportArea.setText("SNRGG(Sobel) = "+ main.Dog.SNRGGGrab +
                        "\nSNRGG(DOG) = " + main.Dog.SNRGGWavelet +
                        "\nSNRGG(Wave) = " + main.WAVE.SNRGGWavelet +
                        "\nSNRGG(MHAT) = " + main.MHAT.SNRGGWavelet +
                        "\n\t"+ str +
                        "\nSNRGG(DOG) = " + main.Dog.SNRGGPorog +
                        "\nSNRGG(Wave) = " + main.WAVE.SNRGGPorog +
                        "\nSNRGG(MHAT) = " + main.MHAT.SNRGGPorog);
        }if (i.get()==2){
            reportArea.setText("SNRGG(Sobel) = "+ main.Dog.SNRGGGrab +
                        "\nSNRF(DOG) = " + main.Dog.SNRFWavelet +
                        "\nSNRF(Wave) = " + main.WAVE.SNRFWavelet +
                        "\nSNRF(MHAT) = " + main.MHAT.SNRFWavelet +
                        "\n\t"+ str +
                        "\nSNRF(DOG) = " + main.Dog.SNRFPorog +
                        "\nSNRF(Wave) = " + main.WAVE.SNRFPorog +
                        "\nSNRF(MHAT) = " + main.MHAT.SNRFPorog);
        }if (i.get()>2) {
            i.set(0);
            reportArea.setText("SKO(Sobel, Grad) = " + main.Dog.SKOGRAD_SKOSobel +
                    "\nSKO(DOG, Grad) = " + main.Dog.SKOWavelet +
                    "\nSKO(Wave, Grad) = " + main.WAVE.SKOWavelet +
                    "\nSKO(MHAT, Grad) = " + main.MHAT.SKOWavelet +
                    "\n\t"+ str +
                    "\nSKO(DOG, Grad) = " + main.Dog.SKOPorog +
                    "\nSKO(Wave, Grad) = " + main.WAVE.SKOPorog +
                    "\nSKO(MHAT, Grad) = " + main.MHAT.SKOPorog);

            }
        }

    @FXML
    public void initialize(){
    }
}

