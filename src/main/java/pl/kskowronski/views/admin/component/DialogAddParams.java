package pl.kskowronski.views.admin.component;

import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.beans.factory.annotation.Autowired;
import pl.kskowronski.data.entity.ReportDetail;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import pl.kskowronski.data.service.admin.reportDetail.ReportDetailDataProvider;
import pl.kskowronski.data.service.admin.reportDetail.ReportDetailService;
import pl.kskowronski.views.cardlist.Person;

import java.util.Arrays;
import java.util.List;

public class DialogAddParams extends Dialog {

    @Autowired
    ReportDetailService reportDetailService;

    private Crud<ReportDetail> crudDetails;

    private String SRP_TYP = "typ"; //NAPIS, CALKOWITA, DATA
    private String SRP_NAME = "nazwa";
    private String SRP_SQL = "sql";
    private String EDIT_COLUMN = "vaadin-crud-edit-column";

    public DialogAddParams() {
        crudDetails = new Crud<>(ReportDetail.class, createEditor());
        setupGrid();
    }

    public void open() {

        add(crudDetails);
    }

    private void setupGrid() {
        Grid<ReportDetail> grid = crudDetails.getGrid();

        // Only show these columns (all columns shown by default):
        List<String> visibleColumns = Arrays.asList(
                SRP_TYP,
                SRP_NAME,
                SRP_SQL,
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
                grid.getColumnByKey(SRP_TYP),
                grid.getColumnByKey(SRP_NAME),
                grid.getColumnByKey(SRP_SQL),
                grid.getColumnByKey(EDIT_COLUMN)
        );
    }

    private void setupDataProvider() {
        ReportDetailDataProvider dataProvider = new ReportDetailDataProvider(reportDetailService);
        crudDetails.setDataProvider(dataProvider);
        crudDetails.addDeleteListener(deleteEvent ->
                dataProvider.delete(deleteEvent.getItem())
        );
        crudDetails.addSaveListener(saveEvent ->
                dataProvider.persist(saveEvent.getItem())
        );
    }


    private CrudEditor<ReportDetail> createEditor() {

        TextField textType      = new TextField("Typ");
        TextField textParamName = new TextField("Nazwa parametru");
        TextField textSql       = new TextField("Sql");

        FormLayout form = new FormLayout(textType, textParamName, textSql);



        Binder<ReportDetail> binder = new Binder<>(ReportDetail.class);
        binder.forField(textType).asRequired().bind(ReportDetail::getSrpTyp, ReportDetail::setSrpTyp);
        binder.forField(textParamName).asRequired().bind(ReportDetail::getSrpName, ReportDetail::setSrpName);
        binder.forField(textSql).asRequired().bind(ReportDetail::getSrpSql, ReportDetail::setSrpSql);

        return new BinderCrudEditor<>(binder, form);

    }
}
