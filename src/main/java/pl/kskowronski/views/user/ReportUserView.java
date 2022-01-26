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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@PageTitle("Twoje raporty")
@Route(value = "user-reports", layout = MainLayout.class)
@RolesAllowed("user")
public class ReportUserView extends VerticalLayout {

    private ReportService reportService;
    private ReportPermService reportPermService;
    private ReportDetailService reportDetailService;
    private ReportRunService reportRunService;
    private User user;

    private List<ReportDetail> paramList;
    private ComboBox<Report> comboListReport;
    private Grid<Map<String, String>> gridData = new Grid<>();

    public ReportUserView(UserService userService, ReportPermService reportPermService
            , ReportService reportService, ReportDetailService reportDetailService, ReportRunService reportRunService) {
        this.reportPermService = reportPermService;
        this.reportDetailService = reportDetailService;
        this.reportRunService = reportRunService;
        this.reportService = reportService;
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userService.getByUsername(userDetails.getUsername());

        comboListReport = getReportListForUser();

        comboListReport.addValueChangeListener( item -> {
            getParametersForReport(item.getValue().getId());
        });

        var buttonRun = new Button("Uruchom");
        buttonRun.addClickListener( e -> {
            runReport();
        });


        add(comboListReport, buttonRun, gridData);
    }

    private void getParametersForReport(BigDecimal repId ) {
       var h01 = new HorizontalLayout();
       paramList = reportDetailService.findReportDetailBySrpRapId(repId);
        paramList.stream().forEach( item -> {
            Component component = reportDetailService.getComponentForParameter( item );
            addEvenListenerChangeToParameter(component);
            h01.add(component);
        });
        add(h01);
    }

    private ComboBox<Report> getReportListForUser() {
        ComboBox<Report> selectReport = new ComboBox<>();
        List<ReportPermission> listUserReports = reportPermService.findReportPermissionByPermUserId(user.getId());
        List<Report> listReport = new ArrayList<>();
        listUserReports.stream().forEach( item -> {
            listReport.add( reportService.getReportById(item.getPermRapId()) );
        });
        selectReport.setItems( listReport );
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
        gridData = reportRunService.runSqlForGrid(comboListReport.getValue().getRapSql(), paramList, gridData);
    }

}
