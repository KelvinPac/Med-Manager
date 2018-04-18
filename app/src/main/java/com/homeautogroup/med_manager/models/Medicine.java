package com.homeautogroup.med_manager.models;

/**
 * MedManager Created by kelvin on 10/04/18.
 * com.homeautogroup.med_manager.models
 * flyboypac@gmail.com
 * +254705419309
 */
public class Medicine extends MedicineExtra {

    private String medicineName;
    private String medicineDesc;
    private String startDate;
    private String endDate;
    private int intakeMonth;
    private int intakeYear;
    private int selectedIcon;

    public Medicine(String medicineName, String medicineDesc, String startDate, String endDate, int intakeMonth, int intakeYear, int selectedIcon) {
        this.medicineName = medicineName;
        this.medicineDesc = medicineDesc;
        this.startDate = startDate;
        this.endDate = endDate;
        this.intakeMonth = intakeMonth;
        this.intakeYear = intakeYear;
        this.selectedIcon = selectedIcon;
    }

    public Medicine() {
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineDesc() {
        return medicineDesc;
    }

    public void setMedicineDesc(String medicineDesc) {
        this.medicineDesc = medicineDesc;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getIntakeMonth() {
        return intakeMonth;
    }

    public void setIntakeMonth(int intakeMonth) {
        this.intakeMonth = intakeMonth;
    }

    public int getIntakeYear() {
        return intakeYear;
    }

    public void setIntakeYear(int intakeYear) {
        this.intakeYear = intakeYear;
    }

    public int getSelectedIcon() {
        return selectedIcon;
    }

    public void setSelectedIcon(int selectedIcon) {
        this.selectedIcon = selectedIcon;
    }
}
