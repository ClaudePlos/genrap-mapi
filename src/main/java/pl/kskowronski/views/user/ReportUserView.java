package pl.kskowronski.views.user;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaadin.crudui.crud.impl.GridCrud;
import pl.kskowronski.data.entity.User;
import pl.kskowronski.data.entity.report.Report;
import pl.kskowronski.data.entity.report.ReportDetail;
import pl.kskowronski.data.entity.report.ReportPermission;
import pl.kskowronski.data.service.UserService;
import pl.kskowronski.data.service.admin.report.ReportRunService;
import pl.kskowronski.data.service.admin.report.ReportService;
import pl.kskowronski.data.service.admin.reportDetail.ReportDetailService;
import pl.kskowronski.data.service.admin.reportPermission.ReportPermService;
import pl.kskowronski.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@PageTitle("Twoje raporty")
@Route(value = "user-reports", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("user")
public class ReportUserView extends VerticalLayout {

    private ReportService reportService;
    private ReportPermService reportPermService;
    private ReportDetailService reportDetailService;
    private ReportRunService reportRunService;
    private User user;

    private List<ReportDetail> paramList;
    private ComboBox<Report> comboListReport;
    //private Grid<Map<String, String>> gridData = new Grid<>();

    private VerticalLayout v00 = new VerticalLayout();
    private HorizontalLayout h00 = new HorizontalLayout();
    private HorizontalLayout h01 = new HorizontalLayout();

    public ReportUserView(UserService userService, ReportPermService reportPermService
            , ReportService reportService, ReportDetailService reportDetailService, ReportRunService reportRunService) {
        this.reportPermService = reportPermService;
        this.reportDetailService = reportDetailService;
        this.reportRunService = reportRunService;
        this.reportService = reportService;
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userService.getByUsername(userDetails.getUsername());
        setHeight("100%");
        reportDetailService.gridData = new Grid<>();
        reportDetailService.gridData.setHeightFull();

        comboListReport = getReportListForUser();

        comboListReport.addValueChangeListener( item -> {
            getParametersForReport(item.getValue().getId());
        });

        var buttonRun = new Button("Uruchom");
        buttonRun.addClickListener( e -> {
            runReport();
        });

        h01.setClassName("h01param");
        h00.add(buttonRun);
        v00.add(comboListReport, h00, h01);

        add(v00, reportDetailService.gridData);
    }

    private void getParametersForReport(BigDecimal repId ) {
        h01.removeAll();
           paramList = reportDetailService.findReportDetailBySrpRapId(repId);
            paramList.stream().forEach( item -> {
                Component component = reportDetailService.getComponentForParameter( item );
                addEvenListenerChangeToParameter(component);
                h01.add(component);
            });
    }

    private ComboBox<Report> getReportListForUser() {
        ComboBox<Report> selectReport = new ComboBox<>();
        List<ReportPermission> listUserReports = reportPermService.findReportPermissionByPermUserId(user.getId());
        List<Report> listReport = new ArrayList<>();
        listUserReports.stream().forEach( item -> {
            listReport.add( reportService.getReportById(item.getPermRapId()) );
        });
        List<Report> sortedReports = listReport.stream()
                .sorted(Comparator.comparing(Report::getRapName))
                .collect(Collectors.toList());
        selectReport.setItems( sortedReports );
        selectReport.setItemLabelGenerator(Report::getRapName);
        selectReport.setLabel("Wybierz raport:");
        return selectReport;
    }

 /** manage components **/
    private void addEvenListenerChangeToParameter( Component component ) {
        if (component instanceof TextField) {
            TextField t = (TextField) component;
            t.addValueChangeListener(event -> {
                updateStringValueForParam(BigDecimal.valueOf(Long.valueOf(t.getId().get())), t.getValue());
            });
        } else if (component instanceof DatePicker) {
            DatePicker d = (DatePicker) component;
            d.addValueChangeListener(event ->
                    updateDateValueForParam(BigDecimal.valueOf(Long.valueOf(d.getId().get())), d.getValue())
            );
        }
    }

    private void updateStringValueForParam(BigDecimal paramId, String value){
        paramList.stream().filter(item -> item.getSrpId().equals(paramId)).collect(Collectors.toList()).get(0).setStringValue(value);
    }

    private void updateDateValueForParam(BigDecimal paramId, LocalDate value){
        paramList.stream().filter(item -> item.getSrpId().equals(paramId)).collect(Collectors.toList()).get(0).setDateValue(value);
    }

    private void runReport() {
        reportDetailService.gridData = reportRunService.runSqlForGrid(comboListReport.getValue().getRapSql(), paramList, reportDetailService.gridData);
    }

}
