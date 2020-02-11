package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import module.AndroidString;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import task.GetAndroidStringTask;
import module.LANG;
import ui.ConvertDialog;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class XMLToCSV extends AnAction {
    private List<AndroidString> mAndroidStrings;
    private Project mProject;
    private VirtualFile mSelectFile;

    @Override
    public void actionPerformed(AnActionEvent e) {
        mProject = e.getData(CommonDataKeys.PROJECT);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);

        GetAndroidStringTask getAndroidStringTask = new GetAndroidStringTask(mProject, "Load strings.xml...", file);
        getAndroidStringTask.setOnGetAndroidStringListener(new GetAndroidStringTask.OnGetAndroidStringListener() {
            @Override
            public void onGetSuccess(@NotNull List<AndroidString> list) {
                if (!isTranslatable(list)) {
                    Messages.showInfoMessage("strings.xml has no text to translate!", "Prompt");
                    return;
                }
                mAndroidStrings = list;

                showTransferDialog();
            }

            @Override
            public void onGetError(@NotNull Throwable error) {
                Messages.showErrorDialog("Load strings.xml error: " + error, "Error");
            }
        });
        getAndroidStringTask.queue();

    }

    private void showTransferDialog() {
        String parentPath = mSelectFile.getParent().getPath();

        ConvertDialog transferDialog = new ConvertDialog(parentPath, JFileChooser.DIRECTORIES_ONLY);
        transferDialog.setOnClickListener(new ConvertDialog.OnClickListener() {
            @Override
            public void onClickListener(String path) {
                File writeFile = new File(path, "string.csv");// getStringFile(getCode(), true, "string.csv");
                if (TextUtils.isEmpty(path)) {
                    writeFile = getStringFile(getCode(), true, "string.csv");
                }

                writeToCSV(writeFile, mAndroidStrings);
            }
        });
        transferDialog.show();
    }

    private File getStringFile(String langCode, boolean mkdirs, String fileName) {
        String parentPath = mSelectFile.getParent().getParent().getPath();
        File stringFile;
        if (mkdirs) {
            File parentFile = new File(parentPath, getDirNameForCode(langCode));
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            stringFile = new File(parentFile, fileName); // "strings.xml"
            if (!stringFile.exists()) {
                try {
                    stringFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            stringFile = new File(parentPath.concat(File.separator).concat(getDirNameForCode(langCode)), "strings.xml");
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

    private void writeToCSV(File file, List<AndroidString> androidStrings) {
        // TODO CSV
        VirtualFile parent = mSelectFile.getParent();
        if (parent == null)
            return;

        String parentName = parent.getName();
        if (!parentName.contains("values"))
            return;
        String lang = parentName.replace("values-", "");

        ApplicationManager.getApplication().invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
                bw.write("key," + lang);
                bw.newLine();
                for (AndroidString androidString : androidStrings) {
                    bw.write(androidString.getName() + "," + androidString.getValue());
                    bw.newLine();
                }
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }


    /**
     * Verify that there is a text in the strings.xml file that needs to be translated.
     *
     * @param list strings.xml text list.
     * @return true: there is text that needs to be translated.
     */
    private boolean isTranslatable(@NotNull List<AndroidString> list) {
        boolean isTranslatable = false;
        for (AndroidString androidString : list) {
            if (androidString.isTranslatable()) {
                isTranslatable = true;
                break;
            }
        }
        return isTranslatable;
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        // The translation option is only show when strings.xml is selected.
        mSelectFile = e.getData(CommonDataKeys.VIRTUAL_FILE);

        boolean isSelectStringsFile = isSelectStringsFile(mSelectFile);
        e.getPresentation().setEnabledAndVisible(isSelectStringsFile);
    }

    /**
     * Verify that the selected file is a strings.xml file.
     *
     * @param file selected file
     * @return true: indicating that the selected file is the strings.xml file.
     */
    private boolean isSelectStringsFile(VirtualFile file) {
        if (file == null) return false;

        VirtualFile parent = file.getParent();
        if (parent == null) return false;

        String parentName = parent.getName();
        if (!parentName.contains("values")) return false;

        return "strings.xml".equals(file.getName());
    }
}
