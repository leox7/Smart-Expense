package view;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("removal")
public class SummaryApplet extends JApplet {
    private final JLabel totalLabel = new JLabel("Total: 0.00");

    @Override
    public void init() {
        SwingUtilities.invokeLater(() -> {
            setLayout(new BorderLayout());
            totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
            totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 18f));
            add(totalLabel, BorderLayout.CENTER);
        });
    }

    public void setTotal(double total) {
        totalLabel.setText("Total: " + String.format("%.2f", total));
    }
}


