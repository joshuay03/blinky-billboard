package BillboardSupport;

import javax.swing.*;
import java.awt.*;

public class RenderedBillboard extends JPanel {

    private static final String defaultBillboardFont = "Trebuchet";

    public RenderedBillboard(Billboard board, Dimension d) throws Exception {

        // HANDLE RENDERING PROPORTIONS SETUP
        // ONLY message present...
        if (board.getBillboardImage() == null && board.getInformation() == null && board.getMessage() != null) {


            // ONLY picture present
            if (board.getBillboardImage() != null && board.getInformation() == null && board.getMessage() == null) {

            }

            // ONLY information present
            if (board.getBillboardImage() == null && board.getInformation() != null && board.getMessage() == null) {

            }

            // Message AND picture present
            if (board.getBillboardImage() != null && board.getInformation() == null && board.getMessage() != null) {

            }

            // Message AND information present
            if (board.getBillboardImage() == null && board.getInformation() != null && board.getMessage() != null) {

            }

            // Picture AND information present
            if (board.getBillboardImage() != null && board.getInformation() != null && board.getMessage() == null) {

            }

            // Picture AND image AND information present
            if (board.getBillboardImage() != null && board.getInformation() != null && board.getMessage() != null) {

            }

            // Un-renderable billboard - no data
            if (board.getBillboardImage() == null && board.getInformation() == null && board.getMessage() == null) {
                throw new Exception("board billboard does not contain any data to render. Please check it was properly instantitated");
            }

        }

    }
}
