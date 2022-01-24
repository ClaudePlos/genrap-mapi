package pl.kskowronski.views.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.crudui.crud.impl.GridCrud;
import pl.kskowronski.data.entity.report.Report;
import pl.kskowronski.data.service.admin.report.ReportRunService;
import pl.kskowronski.data.service.admin.report.ReportService;
import pl.kskowronski.data.service.admin.reportDetail.ReportDetailService;
import pl.kskowronski.views.MainLayout;
import pl.kskowronski.views.admin.component.ReportDialog;

import javax.annotation.security.RolesAllowed;

@PageTitle("Report")
@Route(value = "report", layout = MainLayout.class)
@RolesAllowed("admin")
public class ReportView  extends VerticalLayout {

    private ReportService reportService;
    private ReportRunService reportRunService;
    private ReportDetailService reportDetailService;

    public ReportView(ReportService reportService, ReportRunService reportRunService, ReportDetailService reportDetailService) {
        this.reportService = reportService;
        this.reportRunService = reportRunService;
        this.reportDetailService = reportDetailService;
        var crudReport = new GridCrud<>(Report.class, reportService);
        //crudReport.setFindAllOperation(() -> reportService.findAll());
        add(crudReport);
        setSizeFull();
        crudReport.getGrid().addComponentColumn( item -> createButtonTestReport(item)).setHeader("");
        crudReport.getGrid().addComponentColumn( item -> createButtonAddPermission(item)).setHeader("");
    }

    private Button createButtonTestReport(Report report) {
       var buttonOpenDialog = new Button("Edytor");
       buttonOpenDialog.addClickListener( e -> showDialogWithSql(report) );
       return buttonOpenDialog;
    }

    private Button createButtonAddPermission(Report report) {
        var buttonOpenDialogPerm = new Button("Uprawnienia");
        buttonOpenDialogPerm.addClickListener( e -> showDialogPermissionToReport(report) );
        return buttonOpenDialogPerm;
    }

    private void showDialogWithSql(Report report){
        var dialog = new ReportDialog(reportRunService, reportService, reportDetailService);
        dialog.open(report);
    }

    private void showDialogPermissionToReport(Report report){
        var dialog = new ReportDialog(reportRunService, reportService, reportDetailService);
        dialog.open(report);
    }

}
