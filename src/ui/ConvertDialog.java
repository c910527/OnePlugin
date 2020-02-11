package ui;

import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class ConvertDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton selectPathButton;
    private JLabel label;
    private OnClickListener mOnClickListener;
    private String mParentPaht;
    private int mFileSelectionMode;

    public interface OnClickListener {
        void onClickListener(String path);
    }

    public ConvertDialog() {
        init();
    }

    public ConvertDialog(String parentPath, int fileSelectionMode) {
        init();
        mParentPaht = parentPath;
        label.setText(parentPath);
        mFileSelectionMode = fileSelectionMode;
    }

    private void init() {
        setContentPane(contentPane);
        setTitle("Convert Dialog");
        setResizable(true);
        setSize(500, 200);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        selectPathButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser(mParentPaht);
                if (TextUtils.isEmpty(mParentPaht)) {
                    jfc = new JFileChooser();
                }
                jfc.setFileSelectionMode(mFileSelectionMode);
                jfc.showDialog(new JLabel(), "選擇");
                File file = jfc.getSelectedFile();
                if (file.isDirectory()) {
                    System.out.println("資料夾:" + file.getAbsolutePath());
                } else if (file.isFile()) {
                    System.out.println("檔案:" + file.getAbsolutePath());
                }
                label.setText(jfc.getSelectedFile().getPath());
                System.out.println(jfc.getSelectedFile().getName());
            }

        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        if (mOnClickListener != null) {
            mOnClickListener.onClickListener(label.getText());
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

//    public static void main(String[] args) {
//        TransferDialog dialog = new TransferDialog();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
//    }
}
