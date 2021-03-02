package ru.netology.patient;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicalServiceImplTest {

    final PatientInfo patientInfo1 = new PatientInfo("id1","Иван", "Петров", LocalDate.of(1980, 11, 26),
            new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80)));
    final PatientInfo patientInfo2 = new PatientInfo("id2","Семен", "Михайлов", LocalDate.of(1982, 1, 16),
            new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78)));

    PatientInfoRepository patientInfoRepository;
    SendAlertService alertService;
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);;

    @Before
    public void initParams(){
        patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById("id1")).thenReturn(patientInfo1);
        Mockito.when(patientInfoRepository.getById("id2")).thenReturn(patientInfo2);

        alertService = Mockito.mock(SendAlertServiceImpl.class);
    }

    @Test
    public void testCheckBloodPressure(){
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);

        medicalService.checkBloodPressure("id1", new BloodPressure(60, 120));
        Mockito.verify(alertService).send(argumentCaptor.capture());
        Mockito.verify(alertService, Mockito.times(1)).send(Mockito.anyString());
        Assert.assertEquals("Warning, patient with id: id1, need help", argumentCaptor.getValue());

        Mockito.reset(alertService);
        medicalService.checkBloodPressure("id2", new BloodPressure(125, 78));
        Mockito.verify(alertService, Mockito.never()).send(Mockito.anyString());
    }

    @Test
    public void testCheckTemperature(){
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);

        medicalService.checkTemperature("id1", new BigDecimal("35.1"));
        Mockito.verify(alertService).send(argumentCaptor.capture());
        Mockito.verify(alertService, Mockito.times(1)).send(Mockito.anyString());
        Assert.assertEquals("Warning, patient with id: id1, need help", argumentCaptor.getValue());

        Mockito.reset(alertService);
        medicalService.checkTemperature("id2", new BigDecimal("35.1"));
        Mockito.verify(alertService, Mockito.never()).send(Mockito.anyString());
    }
}
