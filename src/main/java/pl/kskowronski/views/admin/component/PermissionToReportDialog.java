package pl.kskowronski.views.admin.component;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import pl.kskowronski.data.entity.report.ParamType;
import pl.kskowronski.data.entity.report.Report;
import pl.kskowronski.data.entity.report.ReportDetail;
import pl.kskowronski.data.entity.report.ReportPermission;
import pl.kskowronski.data.service.admin.reportDetail.ReportDetailDataProvider;
import pl.kskowronski.data.service.admin.reportDetail.ReportDetailService;
import pl.kskowronski.data.service.admin.reportPermission.ReportPermDataProvider;
import pl.kskowronski.data.service.admin.reportPermission.ReportPermService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class PermissionToReportDialog extends Dialog {

    private ReportPermService reportPermService;
    private BigDecimal repId;

    private Crud<ReportPermission> crudDetails;
    private Binder<ReportPermission> binder = new Binder<>(ReportPermission.class);


    private String PERM_USERNAME = "permUsername";
    private String PERM_RAP_ID = "permUserId";
    private String EDIT_COLUMN = "vaadin-crud-edit-column";

    public PermissionToReportDialog(ReportPermService reportPermService) {
        this.reportPermService = reportPermService;
        setDraggable(true);
        setWidth("700px");
        setHeight("700px");
        crudDetails = new Crud<>(ReportPermission.class, createEditor());

        setupGrid();

    }

    public void open(BigDecimal repId) {
        this.repId = repId;
        open();
    }

    private void setupGrid() {
        Grid<ReportPermission> grid = crudDetails.getGrid();

        // Only show these columns (all columns shown by default):
        List<String> visibleColumns = Arrays.asList(
                PERM_USERNAME,
                PERM_RAP_ID,
                EDIT_COLUMN
        );
        grid.getColumns().forEach(column -> {
            String key = column.getKey();
            if (!visibleColumns.contains(key)) {
                grid.removeColumn(column);
            }
        });

        // Reorder the columns (alphabetical by default)
        grid.setColumnOrder(
                grid.getColumnByKey(PERM_USERNAME),
                grid.getColumnByKey(PERM_RAP_ID),
                grid.getColumnByKey(EDIT_COLUMN)
        );
    }

    private void setupDataProvider() {
        ReportPermDataProvider dataProvider = new ReportPermDataProvider(reportPermService, repId);
        crudDetails.setDataProvider(dataProvider);
        crudDetails.addDeleteListener(deleteEvent ->
                dataProvider.delete(deleteEvent.getItem())
        );
        crudDetails.addSaveListener(saveEvent ->
                dataProvider.persist(saveEvent.getItem())
        );
    }


    private CrudEditor<ReportPermission> createEditor() {

        TextField textUsername = new TextField("RapId");
        TextField textReportName = new TextField("Lp");

        FormLayout form = new FormLayout(textUsername, textReportName);
        binder.forField(textUsername).asRequired().bind(ReportPermission::getPermUSername, ReportPermission::setPermUSername);
        binder.forField(textReportName).asRequired().bind(ReportPermission::getPermRapName, ReportPermission::setPermRapName);

        return new BinderCrudEditor<>(binder, form);
    }
}
