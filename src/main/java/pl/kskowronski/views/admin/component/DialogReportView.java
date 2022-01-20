package pl.kskowronski.views.admin.component;


import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;
import pl.kskowronski.data.entity.Report;

public class DialogReportView extends Dialog {

    public DialogReportView() {
    }

    public void open(Report report ) {
        var textSql = new TextField();
        textSql.setValue(report.getRapSql());
        add(textSql);
        open();
    }
}
