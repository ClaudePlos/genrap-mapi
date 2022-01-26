package pl.kskowronski.views.admin.component;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.binder.Binder;
import pl.kskowronski.data.entity.User;
import pl.kskowronski.data.entity.report.ReportPermission;
import pl.kskowronski.data.service.UserService;
import pl.kskowronski.data.service.admin.reportPermission.ReportPermDataProvider;
import pl.kskowronski.data.service.admin.reportPermission.ReportPermService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class PermissionToReportDialog extends Dialog {

    private ReportPermService reportPermService;
    private UserService userService;
    private BigDecimal repId;

    private Crud<ReportPermission> crudPerm;

    private String PERM_USERNAME = "permUsername";
    private String EDIT_COLUMN = "vaadin-crud-edit-column";

    public PermissionToReportDialog(ReportPermService reportPermService, UserService userService, BigDecimal idRap) {
        this.reportPermService = reportPermService;
        this.userService = userService;
        this.repId = idRap;
        setDraggable(true);
        setWidth("700px");
        setHeight("700px");
        crudPerm = new Crud<>(ReportPermission.class, createEditor());

        crudPerm.addSaveListener( item -> {
            item.getItem().setPermRapId(idRap);
        });

        setupGrid();
        setupDataProvider();

        add(new Label("Uprawnienia:"), crudPerm);
    }

    public void open(BigDecimal repId) {
        this.repId = repId;
        open();
    }

    private void setupGrid() {
        Grid<ReportPermission> grid = crudPerm.getGrid();

        // Only show these columns (all columns shown by default):
        List<String> visibleColumns = Arrays.asList(
                PERM_USERNAME,
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
                grid.getColumnByKey(EDIT_COLUMN)
        );
    }

    private void setupDataProvider() {
        ReportPermDataProvider dataProvider = new ReportPermDataProvider(reportPermService, repId);
        crudPerm.setDataProvider(dataProvider);
        crudPerm.addDeleteListener(deleteEvent ->
                dataProvider.delete(deleteEvent.getItem())
        );
        crudPerm.addSaveListener(saveEvent ->
                dataProvider.persist(saveEvent.getItem())
        );
    }


    private CrudEditor<ReportPermission> createEditor() {
        Binder<ReportPermission> binder = new Binder<>(ReportPermission.class);

        ComboBox<User> boxUsername = getSelectUser();

        FormLayout form = new FormLayout(boxUsername);
        binder.forField(boxUsername).asRequired().bind(ReportPermission::getUser, ReportPermission::setUser);

        return new BinderCrudEditor<>(binder, form);
    }

    private ComboBox<User> getSelectUser() {
        ComboBox<User> selectUser = new ComboBox<>();
        selectUser.setItems(  userService.findAll() );
        selectUser.setItemLabelGenerator(User::getUsername);
        //selectSK.setEmptySelectionCaption(listSK.get(0).getSkKod());
        selectUser.setLabel("User");
        return selectUser;
    }

}
