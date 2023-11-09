package front;

import javax.swing.*;
import java.awt.*;

public class RoundedButtonR extends JButton {
    public RoundedButtonR() {
        super();
        decorate();
    }

    public RoundedButtonR(String text) {
        super(text);
        decorate();
    }

    public RoundedButtonR(Action action) {
        super(action);
        decorate();
    }

    public RoundedButtonR(Icon icon) {
        super(icon);
        decorate();
    }

    public RoundedButtonR(String text, Icon icon) {
        super(text, icon);
        decorate();
    }

    protected void decorate() {
        setBorderPainted(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Color o = Color.WHITE; //배경색 결정
        Color c = new Color(240, 112, 103); //글자색 결정
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (getModel().isArmed()) {
            graphics.setColor(c.darker());
        } else if (getModel().isRollover()) {
            graphics.setColor(c.brighter());
        } else {
            graphics.setColor(c);
        }
        graphics.fillRoundRect(0, 0, width, height, 10, 10);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle stringBounds = fontMetrics.getStringBounds(this.getText(), graphics).getBounds();
        int textX = (width - stringBounds.width) / 2;
        int textY = (height - stringBounds.height) / 2 + fontMetrics.getAscent();
        graphics.setColor(o);
        graphics.setFont(getFont());
        graphics.drawString(getText(), textX, textY);
        graphics.dispose();
        super.paintComponent(g);
    }
}