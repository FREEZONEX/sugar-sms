package org.niiish32x.sugarsms.alarm.repo;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.niiish32x.sugarsms.alarm.AlarmDO;
import org.niiish32x.sugarsms.alarm.domain.entity.AlarmEO;
import org.niiish32x.sugarsms.alarm.persistence.dao.AlarmDAO;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * AlarmRepoImplTest
 *
 * @author shenghao ni
 * @date 2024.12.27 14:53
 */
@RunWith(MockitoJUnitRunner.class)
public class AlarmRepoImplTest {

    @InjectMocks
    private AlarmRepoImpl alarmRepo;

    @Mock
    private AlarmDAO alarmDAO;

    @Before
    public void setUp() {
        // 任何通用的设置代码可以放在这里
    }

    @Test
    public void find_NoAlarms_ReturnsEmptyList() {
        when(alarmDAO.lambdaQuery().list()).thenReturn(new ArrayList<>());

        List<AlarmEO> result = alarmRepo.find();

        assertEquals(0, result.size());
    }

    @Test
    public void find_MultipleAlarms_ReturnsConvertedAlarms() {
        List<AlarmDO> alarmDOList = new ArrayList<>();
        alarmDOList.add(new AlarmDO(1L, "alarm1", "Alarm 1", 1, true, ">", "10", 0.1, "type1", "comment1", "type1", "en1", "templateEn1", "templateDisplay1", "instanceEn1", "instanceDisplay1", "attributeEn1", "attributeDisplay1", "attributeComment1", "instanceLabels1", "attributeLabels1", 0, new java.util.Date(), new java.util.Date()));
        alarmDOList.add(new AlarmDO(2L, "alarm2", "Alarm 2", 2, false, "<", "5", 0.2, "type2", "comment2", "type2", "en2", "templateEn2", "templateDisplay2", "instanceEn2", "instanceDisplay2", "attributeEn2", "attributeDisplay2", "attributeComment2", "instanceLabels2", "attributeLabels2", 0, new java.util.Date(), new java.util.Date()));

        when(alarmDAO.lambdaQuery().list()).thenReturn(alarmDOList);

        List<AlarmEO> result = alarmRepo.find();

        assertEquals(2, result.size());
        assertEquals("alarm1", result.get(0).getAlarmId());
        assertEquals("alarm2", result.get(1).getAlarmId());
    }

    @Test
    public void find_SingleAlarm_ReturnsSingleConvertedAlarm() {
        List<AlarmDO> alarmDOList = new ArrayList<>();
        alarmDOList.add(new AlarmDO(1L, "alarm1", "Alarm 1", 1, true, ">", "10", 0.1, "type1", "comment1", "type1", "en1", "templateEn1", "templateDisplay1", "instanceEn1", "instanceDisplay1", "attributeEn1", "attributeDisplay1", "attributeComment1", "instanceLabels1", "attributeLabels1", 0, new java.util.Date(), new java.util.Date()));

        when(alarmDAO.lambdaQuery().list()).thenReturn(alarmDOList);

        List<AlarmEO> result = alarmRepo.find();

        assertEquals(1, result.size());
        assertEquals("alarm1", result.get(0).getAlarmId());
    }
}