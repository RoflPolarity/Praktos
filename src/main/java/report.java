import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.concurrent.atomic.AtomicInteger;

public class report {
    Veivlet main;
    public report(Veivlet main){
        this.main = main;
    }
    @FXML
    private Button exitButton;

    @FXML
    private Button nextButton;

    @FXML
    private TextArea report;


    @FXML
    void initialize(){
        AtomicInteger i = new AtomicInteger();
        report.setText("SKO(Sobel, Grad) = "+ main.Dog.SKOGRAD_SKOSobel +
                "\nSKO(DOG, Grad) = " + main.Dog.SKOWavelet +
                "\nSKO(Wave, Grad) = " + main.WAVE.SKOWavelet +
                "\nSKO(MHAT, Grad) = " + main.MHAT.SKOWavelet +
                "\n\t После пороговой обработки"+
                "\nSKO(DOG, Grad) = " + main.Dog.SKOPorog +
                "\nSKO(Wave, Grad) = " + main.WAVE.SKOPorog +
                "\nSKO(MHAT, Grad) = " + main.MHAT.SKOPorog
        );
        nextButton.setOnAction(event -> {
            i.getAndIncrement();
            if (i.get() ==0){
                report.setText("SKO(Sobel, Grad) = "+ main.Dog.SKOGRAD_SKOSobel +
                        "\nSKO(DOG, Grad) = " + main.Dog.SKOWavelet +
                        "\nSKO(Wave, Grad) = " + main.WAVE.SKOWavelet +
                        "\nSKO(MHAT, Grad) = " + main.MHAT.SKOWavelet +
                        "\n\t После пороговой обработки"+
                        "\nSKO(DOG, Grad) = " + main.Dog.SKOPorog +
                        "\nSKO(Wave, Grad) = " + main.WAVE.SKOPorog +
                        "\nSKO(MHAT, Grad) = " + main.MHAT.SKOPorog);

            }if (i.get()==1){
                report.setText("SNRGG(Sobel) = "+ main.Dog.SNRGGGrab +
                        "\nSNRGG(DOG) = " + main.Dog.SNRGGWavelet +
                        "\nSNRGG(Wave) = " + main.WAVE.SNRGGWavelet +
                        "\nSNRGG(MHAT) = " + main.MHAT.SNRGGWavelet +
                        "\n\t После пороговой обработки"+
                        "\nSNRGG(DOG) = " + main.Dog.SNRGGPorog +
                        "\nSNRGG(Wave) = " + main.WAVE.SNRGGPorog +
                        "\nSNRGG(MHAT) = " + main.MHAT.SNRGGPorog);
            }if (i.get()==2){
                report.setText("SNRGG(Sobel) = "+ main.Dog.SNRGGGrab +
                        "\nSNRF(DOG) = " + main.Dog.SNRFWavelet +
                        "\nSNRF(Wave) = " + main.WAVE.SNRFWavelet +
                        "\nSNRF(MHAT) = " + main.MHAT.SNRFWavelet +
                        "\n\t После пороговой обработки"+
                        "\nSNRF(DOG) = " + main.Dog.SNRFPorog +
                        "\nSNRF(Wave) = " + main.WAVE.SNRFPorog +
                        "\nSNRF(MHAT) = " + main.MHAT.SNRFPorog);
            }if (i.get()>2){
                i.set(0);
                report.setText("SKO(Sobel, Grad) = "+ main.Dog.SKOGRAD_SKOSobel +
                        "\nSKO(DOG, Grad) = " + main.Dog.SKOWavelet +
                        "\nSKO(Wave, Grad) = " + main.WAVE.SKOWavelet +
                        "\nSKO(MHAT, Grad) = " + main.MHAT.SKOWavelet +
                        "\n\t После пороговой обработки"+
                        "\nSKO(DOG, Grad) = " + main.Dog.SKOPorog +
                        "\nSKO(Wave, Grad) = " + main.WAVE.SKOPorog +
                        "\nSKO(MHAT, Grad) = " + main.MHAT.SKOPorog);

            }
        });

    }
}

