package group;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

public class StringConvertGroup extends DefaultActionGroup {
//    @NotNull
//    @Override
//    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
//        return new AnAction[]{ new CSVToString(), new StringToCSV(), new Replace()};
//    }

    @Override
    public void update(AnActionEvent event) {
        // Enable/disable depending on whether user is editing...
    }
}
