package pl.kskowronski.views.admin.component;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import pl.kskowronski.data.entity.report.ParamType;
import pl.kskowronski.data.entity.report.ReportDetail;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import pl.kskowronski.data.service.admin.reportDetail.ReportDetailDataProvider;
import pl.kskowronski.data.service.admin.reportDetail.ReportDetailService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class AddParamsDialog extends Dialog {

    ReportDetailService reportDetailService;

    private Crud<ReportDetail> crudDetails;
    private Binder<ReportDetail> binder = new Binder<>(ReportDetail.class);

    private String SRP_LP = "srpLp";
    private String SRP_TYP = "srpTyp"; //NAPIS, CALKOWITA, DATA
    private String SRP_NAME = "srpName";
    private String SRP_SQL = "srpSql";
    private String EDIT_COLUMN = "vaadin-crud-edit-column";

    private BigDecimal idRap;

    public AddParamsDialog(ReportDetailService reportDetailService, BigDecimal idRap) {
        this.idRap = idRap;
        this.reportDetailService = reportDetailService;
        setDraggable(true);
        setWidth("700px");
        setHeight("700px");
        crudDetails = new Crud<>(ReportDetail.class, createEditor());

        setupGrid();
        setupDataProvider();

        add(new Label("Params:"), crudDetails);
    }

    public void open(BigDecimal idRap) {
        this.idRap = idRap;
        open();
    }

    private void setupGrid() {
        Grid<ReportDetail> grid = crudDetails.getGrid();

        // Only show these columns (all columns shown by default):
        List<String> visibleColumns = Arrays.asList(
                SRP_LP,
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
                grid.getColumnByKey(SRP_LP),
                grid.getColumnByKey(SRP_TYP),
                grid.getColumnByKey(SRP_NAME),
                grid.getColumnByKey(SRP_SQL),
                grid.getColumnByKey(EDIT_COLUMN)
        );
    }

    private void setupDataProvider() {
        ReportDetailDataProvider dataProvider = new ReportDetailDataProvider(reportDetailService, idRap);
        crudDetails.setDataProvider(dataProvider);
        crudDetails.addDeleteListener(deleteEvent ->
                dataProvider.delete(deleteEvent.getItem())
        );
        crudDetails.addSaveListener(saveEvent ->
                dataProvider.persist(saveEvent.getItem())
        );
    }


    private CrudEditor<ReportDetail> createEditor() {

        TextField textRapId = new TextField("RapId");
        TextField textLp = new TextField("Lp");
        ComboBox<ParamType> pType = getParamType();
        TextField textParamName = new TextField("Nazwa parametru");
        TextField textSql       = new TextField("Sql");

        //textRapId.setEnabled(false);
        textRapId.setValue(idRap.toString());
        textRapId.setLabel("RapID przepisz: " + idRap.toString());

        FormLayout form = new FormLayout(textRapId, textLp, pType, textParamName, textSql);
        binder.forField(textRapId).asRequired().bind(ReportDetail::getRapId, ReportDetail::setRapId);
        binder.forField(textLp).asRequired().bind(ReportDetail::getSrpLp, ReportDetail::setSrpLp);
        binder.forField(pType).asRequired().bind(ReportDetail::getType, ReportDetail::setType);
        binder.forField(textParamName).asRequired().bind(ReportDetail::getSrpName, ReportDetail::setSrpName);
        binder.forField(textSql).asRequired().bind(ReportDetail::getSrpSql, ReportDetail::setSrpSql);

        return new BinderCrudEditor<>(binder, form);

    }


    private ComboBox<ParamType> getParamType() {
        ComboBox<ParamType> comboSK = new ComboBox<>();
        comboSK.setItems(ParamType.values());
        comboSK.setItemLabelGenerator(ParamType::name);
        comboSK.setLabel("Typ");
        return comboSK;
    }

}
