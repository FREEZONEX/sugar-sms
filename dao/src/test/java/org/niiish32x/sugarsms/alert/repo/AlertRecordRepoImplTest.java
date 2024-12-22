package org.niiish32x.sugarsms.alert.repo;


import com.alibaba.fastjson2.JSON;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.niiish32x.sugarsms.alert.AlertRecordDO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;
import org.niiish32x.sugarsms.alert.persistence.converter.AlertRecordConverter;
import org.niiish32x.sugarsms.alert.persistence.dao.AlertRecordDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AlertRecordRepoImplTest {

    @Mock
    private AlertRecordDAO alertRecordDAO;

    @Mock
    private AlertRecordConverter alertRecordConverter;

    @InjectMocks
    private AlertRecordRepoImpl alertRecordRepo;

    private AlertRecordDO alertRecordDO;
    private AlertRecordEO alertRecordEO;

    private List<AlertRecordDO> alertRecordDOList;

    @Before
    public void setUp() {
        alertRecordDO = new AlertRecordDO();
        alertRecordDO.setStatus(1);
        alertRecordDO.setType("sms");

        alertRecordEO = new AlertRecordEO();
        alertRecordDOList = new ArrayList<>();
    }

    @Test
    public void findAlertsBeforeDays_ShouldReturnAlerts() {
        List<AlertRecordDO> alertRecordDOList = new ArrayList<>();
        alertRecordDOList.add(alertRecordDO);

        when(alertRecordDAO.findAlertBeforeDays(10)).thenReturn(alertRecordDOList);
        when(alertRecordConverter.toEO(alertRecordDO)).thenReturn(alertRecordEO);

        List<AlertRecordEO> result = alertRecordRepo.findAlertsBeforeDays(10);


        assertEquals(1, result.size());
        assertEquals(alertRecordEO, result.get(0));
    }

    @Test
    public void findAlertsBeforeDays_ShouldReturnEmptyList() {
        when(alertRecordDAO.findAlertBeforeDays(10)).thenReturn(Collections.emptyList());

        List<AlertRecordEO> result = alertRecordRepo.findAlertsBeforeDays(10);

        assertEquals(0, result.size());
    }

    @Test
    public void findAlertsBeforeDays_ShouldReturnSingleAlert() {
        List<AlertRecordDO> alertRecordDOList = new ArrayList<>();
        alertRecordDOList.add(alertRecordDO);

        when(alertRecordDAO.findAlertBeforeDays(10)).thenReturn(alertRecordDOList);
        when(alertRecordConverter.toEO(alertRecordDO)).thenReturn(alertRecordEO);

        List<AlertRecordEO> result = alertRecordRepo.findAlertsBeforeDays(10);


        assertEquals(1, result.size());
        assertEquals(alertRecordEO, result.get(0));
    }

    @Test
    public void findAlertsBeforeDays_ShouldReturnMultipleAlerts() {
        List<AlertRecordDO> alertRecordDOList = new ArrayList<>();
        alertRecordDOList.add(alertRecordDO);
        alertRecordDOList.add(alertRecordDO);

        when(alertRecordDAO.findAlertBeforeDays(10)).thenReturn(alertRecordDOList);
        when(alertRecordConverter.toEO(alertRecordDO)).thenReturn(alertRecordEO);

        List<AlertRecordEO> result = alertRecordRepo.findAlertsBeforeDays(10);

        assertEquals(2, result.size());
        assertEquals(alertRecordEO, result.get(0));
        assertEquals(alertRecordEO, result.get(1));
    }


    @Test
    public void findDurationFromStatToEnd_NoRecords_ReturnsZero() {
        when(alertRecordDAO.lambdaQuery().eq(AlertRecordDO::getAlertId, 1L).eq(AlertRecordDO::getType, MessageType.SMS).list())
                .thenReturn(Collections.emptyList());

        Long duration = alertRecordRepo.findDurationFromStatToEnd(1L, MessageType.SMS);

        assertEquals(Long.valueOf(0), duration);
    }

    @Test
    public void findDurationFromStatToEnd_SingleRecord_ReturnsZero() {
        AlertRecordDO alertRecordDO = new AlertRecordDO();
        alertRecordDO.setSendTime(new Date(1000L));
        alertRecordDOList.add(alertRecordDO);

        when(alertRecordDAO.lambdaQuery().eq(AlertRecordDO::getAlertId, 1L).eq(AlertRecordDO::getType, MessageType.SMS).list())
                .thenReturn(alertRecordDOList);

        Long duration = alertRecordRepo.findDurationFromStatToEnd(1L, MessageType.SMS);

        assertEquals(Long.valueOf(0), duration);
    }

    @Test
    public void findDurationFromStatToEnd_MultipleRecords_ReturnsCorrectDuration() {
        AlertRecordDO alertRecordDO1 = new AlertRecordDO();
        alertRecordDO1.setSendTime(new Date(1000L));
        alertRecordDOList.add(alertRecordDO1);

        AlertRecordDO alertRecordDO2 = new AlertRecordDO();
        alertRecordDO2.setSendTime(new Date(2000L));
        alertRecordDOList.add(alertRecordDO2);

        when(alertRecordDAO.lambdaQuery().eq(AlertRecordDO::getAlertId, 1L).eq(AlertRecordDO::getType, MessageType.SMS).list())
                .thenReturn(alertRecordDOList);

        Long duration = alertRecordRepo.findDurationFromStatToEnd(1L, MessageType.SMS);

        assertEquals(Long.valueOf(1000), duration);
    }

    @Test
    public void findDurationFromStatToEnd_DifferentTypes_IgnoresOtherTypes() {
        AlertRecordDO alertRecordDO1 = new AlertRecordDO();
        alertRecordDO1.setSendTime(new Date(1000L));
        alertRecordDO1.setType("email");
        alertRecordDOList.add(alertRecordDO1);

        AlertRecordDO alertRecordDO2 = new AlertRecordDO();
        alertRecordDO2.setSendTime(new Date(2000L));
        alertRecordDO2.setType("sms");
        alertRecordDOList.add(alertRecordDO2);

        when(alertRecordDAO.lambdaQuery().eq(AlertRecordDO::getAlertId, 1L).eq(AlertRecordDO::getType, MessageType.SMS).list())
                .thenReturn(alertRecordDOList);

        Long duration = alertRecordRepo.findDurationFromStatToEnd(1L, MessageType.SMS);

        assertEquals(Long.valueOf(0), duration);
    }


}
