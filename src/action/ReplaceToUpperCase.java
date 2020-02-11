package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import static com.intellij.codeInsight.editorActions.SelectWordUtil.JAVA_IDENTIFIER_PART_CONDITION;

public class ReplaceToUpperCase extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get all the required data from data keys
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        // Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();
        // Replace the selection with a fixed string.
        // Must do this document change in a write action context.
        if (editor.getSelectionModel().getSelectedText() == null) {
            return;
        }
        String strUpperCase = editor.getSelectionModel().getSelectedText().toUpperCase();
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(start, end, strUpperCase)
        );
        // De-select the text range that was just replaced
        primaryCaret.removeSelection();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        SelectionModel selectionModel = editor.getSelectionModel();

        String selectedText = selectionModel.getSelectedText();
        boolean isSelectStrings = true;
        if (TextUtils.isEmpty(selectedText)) {
            isSelectStrings = false;
        }
        e.getPresentation().setEnabledAndVisible(isSelectStrings);

    }

}
