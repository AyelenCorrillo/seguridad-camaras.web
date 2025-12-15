package com.camaras.seguridadcamarasweb.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Service;

@Service
public class InstallationService {
    
    private static final double BASE_INSTALLATION_PRICE = 50.00; 
    private static final LocalTime START_TIME = LocalTime.of(8, 0);   // 8:00 AM
    private static final LocalTime END_TIME = LocalTime.of(15, 0);    // 3:00 PM (15:00 HS)

    public double getInstallationPrice() {
        return BASE_INSTALLATION_PRICE;
    }

    /**
     * Valida si la fecha y hora cumplen con las restricciones de Lunes a Sábado, 8:00 a 15:00.
     */
    public boolean isValidInstallationTime(LocalDate date, LocalTime time) {
        
        // 1. Validar el día: No debe ser Domingo (DayOfWeek.SUNDAY)
        DayOfWeek day = date.getDayOfWeek();
        if (day == DayOfWeek.SUNDAY) {
            return false;
        }

        // 2. Validar la hora: Debe ser igual o posterior a START_TIME y
        //    igual o anterior a END_TIME.
        
        boolean isAfterStartTime = !time.isBefore(START_TIME); // Es >= 8:00
        boolean isBeforeEndTime = !time.isAfter(END_TIME);     // Es <= 15:00
        
        return isAfterStartTime && isBeforeEndTime;
    }
}
