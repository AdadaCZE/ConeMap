package pointDraw;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

public class MyTextField extends JTextField{
	private String label;
    private JTextField t;

    public MyTextField(String _label) {
        label = _label;
        t = this;
        this.setText(label);
        this.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
                if (t.getText().equals(label)){
                    t.setText("");
                }
                else{
                    t.selectAll();
                }
			}

			@Override
			public void focusLost(FocusEvent e) {
                if (t.getText().equals("")){
                    t.setText(label);
                }
			}
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (c == KeyEvent.VK_ENTER){t.setFocusable(false);t.setFocusable(true);}

            if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE) || (c == '-'))){
                e.consume();
            }
          }
        });
    }

}
