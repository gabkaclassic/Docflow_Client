echo off

java --module-path "./sdk/lib" --add-modules="javafx.controls,javafx.graphics,javafx.fxml" -jar Docflow_Client.jar documents https://docflow-server.onrender.com/



Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V -Allecho on