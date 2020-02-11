import action.CSVToXML;
import action.XMLToCSV;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

public class ToolRegistration implements ApplicationComponent {
    @NotNull
    public String getComponentName() {
        return "OnePlugin";
    }

    public void initComponent() {
        ActionManager am = ActionManager.getInstance();
        CSVToXML menuAction = new CSVToXML();
        am.registerAction("CSVToXML", menuAction);

        XMLToCSV xmlToCSV = new XMLToCSV();
        am.registerAction("XMLToCSV", xmlToCSV);
        DefaultActionGroup windowsM = (DefaultActionGroup) am.getAction("WindowMenu");

        windowsM.addSeparator();
        windowsM.add(menuAction);
    }

    // Disposes system resources.
    public void disposeComponent() {
    }
}