package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import module.AndroidString;
import module.LANG;
import ui.ConvertDialog;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CSVToXML extends AnAction {

    private VirtualFile mSelectFile;

    @Override
    public void actionPerformed(AnActionEvent e) {
        showTransferDialog();
    }

    private List<List<String>> readCSV(String path) {
        String COMMA_DELIMITER = ",";
        List<List<String>> records = new ArrayList<>();
        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(path));
            BufferedReader br = new BufferedReader(new InputStreamReader(dataInputStream, StandardCharsets.UTF_8));//這裡如果csv檔案編碼格式是
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                records.add(Arrays.asList(values));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return records;
    }

    private void showTransferDialog() {
        String parentPath = mSelectFile.getPath();

        ConvertDialog transferDialog = new ConvertDialog(parentPath, JFileChooser.FILES_ONLY);
        transferDialog.setOnClickListener(new ConvertDialog.OnClickListener() {
            @Override
            public void onClickListener(String path) {
                List<List<String>> csvList = readCSV(path);
                if (csvList.size() == 0) {
                    return;
                }
                List<String> stringList = csvList.get(0);
                for (int j = 1; j < stringList.size(); j++) {
                    List<AndroidString> androidStrings = new ArrayList<>();
                    String langCode = stringList.get(j);
                    for (int i = 1; i < csvList.size(); i++) {
                        List<String> strings = csvList.get(i);
                        if (strings.size() == 0 || strings.size()-1 < j) {
                            continue;
                        }
                        System.out.print("key : " + strings.get(0) + " , " + strings.get(j) + "\n");
                        AndroidString androidString = new AndroidString(strings.get(0), strings.get(j), true);
                        androidStrings.add(androidString);
                    }
                    File writeFile = getStringFile(path, langCode);
                    write(writeFile, androidStrings);
                }
            }
        });
        transferDialog.show();
    }

    private void write(File file, List<AndroidString> androidStrings) {
        ApplicationManager.getApplication().invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
                bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                bw.newLine();
                bw.write("<resources>");
                bw.newLine();
                for (AndroidString androidString : androidStrings) {
                    bw.write("\t<string name=\"" + androidString.getName() + "\">" + androidString.getValue() + "</string>");
                    bw.newLine();
                }
                bw.write("</resources>");
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        // The translation option is only show when strings.xml is selected.
        mSelectFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
//
//        boolean isSelectStringsFile = isSelectStringsFile(mSelectFile);
//        e.getPresentation().setEnabledAndVisible(isSelectStringsFile);
    }

    private File getStringFile(String path, String langCode) {
//        String parentPath = mSelectFile.getParent().getParent().getPath();
        File file = new File(path);
        path = file.getParentFile().getPath();
        File stringFile;
        File parentFile = new File(path, getDirNameForCode(langCode));
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        stringFile = new File(parentFile, "strings.xml"); // "strings.xml"
        if (!stringFile.exists()) {
            try {
                stringFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringFile;
    }

    private String getDirNameForCode(String langCode) {
        String suffix;
        if (langCode.equals(LANG.ChineseSimplified.getCode())) {
            suffix = "zh-rCN";
        } else if (langCode.equals(LANG.ChineseTraditional.getCode())) {
            suffix = "zh-rTW";
        } else if (langCode.equals(LANG.Filipino.getCode())) {
            suffix = "fil";
        } else if (langCode.equals(LANG.Indonesian.getCode())) {
            suffix = "in-rID";
        } else if (langCode.equals(LANG.Javanese.getCode())) {
            suffix = "jv";
        } else {
            suffix = langCode;
        }
        return "values-".concat(suffix);
    }

    private String getCode() {
        VirtualFile parent = mSelectFile.getParent();
        if (parent == null)
            return "";

        String parentName = parent.getName();
        if (!parentName.contains("values"))
            return "";
        return parentName.replace("value-", "");
    }
}
