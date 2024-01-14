package city.org.rs;

import java.util.Objects;

public class BloodTest {
    private int test_id;
    private String patient_name;
    private double blood_glucose_level;
    private double carb_intake;
    private double medication_dose;
    private String created_at;

    // Constructors
    public BloodTest(int test_id) {
        this.test_id = test_id;
    }

    public BloodTest() {
    }

    public BloodTest(int test_id, String patient_name, double blood_glucose_level, double carb_intake, double medication_dose) {
        this.test_id = test_id;
        this.patient_name = patient_name;
        this.blood_glucose_level = blood_glucose_level;
        this.carb_intake = carb_intake;
        this.medication_dose = medication_dose;
    }

    // Getters
    public int getTest_id() {
        return test_id;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public double getBlood_glucose_level() {
        return blood_glucose_level;
    }

    public double getCarb_intake() {
        return carb_intake;
    }

    public double getMedication_dose() {
        return medication_dose;
    }

    public String getCreated_at() {
        return created_at;
    }

    // Setters
    public void setTest_id(int test_id) {
        this.test_id = test_id;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public void setBlood_glucose_level(double blood_glucose_level) {
        this.blood_glucose_level = blood_glucose_level;
    }

    public void setCarb_intake(double carb_intake) {
        this.carb_intake = carb_intake;
    }

    public void setMedication_dose(double medication_dose) {
        this.medication_dose = medication_dose;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    // Override equals and hashCode methods
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        BloodTest bloodTest = (BloodTest) obj;
        return test_id == bloodTest.test_id &&
                Double.compare(bloodTest.blood_glucose_level, blood_glucose_level) == 0 &&
                Double.compare(bloodTest.carb_intake, carb_intake) == 0 &&
                Double.compare(bloodTest.medication_dose, medication_dose) == 0 &&
                Objects.equals(patient_name, bloodTest.patient_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(test_id, patient_name, blood_glucose_level, carb_intake, medication_dose);
    }
}
