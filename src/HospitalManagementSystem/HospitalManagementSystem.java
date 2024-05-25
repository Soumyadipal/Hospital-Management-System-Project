package HospitalManagementSystem;

import com.mysql.cj.xdevapi.Collection;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/Hospital";
    private static final String username = "root";
    private static final String password = "Spfawis#8";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner sc = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,sc);
            Doctor doctor = new Doctor(connection);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter Your Choice : ");
                int Choice = sc.nextInt();
                switch (Choice) {
                    case 1 :
                        // Add Patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2 :
                        // View Patient
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3 :
                        // View Doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4 :
                        // Book Appointment
                        bookAppointment(patient,doctor,connection,sc);
                        System.out.println();
                        break;
                    case 5 :
                        // Exit
                        return;
                    default :
                        System.out.println("Enter Valid Choice");
                        break;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner sc){
        System.out.print("Enter Patient Id : ");
        int patientId = sc.nextInt();
        System.out.print("Enter Doctor Id : ");
        int doctorId = sc.nextInt();
        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String appointmentDate = sc.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkDoctorAvailability(doctorId,appointmentDate,connection)){
                String appointmentQuery = "INSERT INTO Appointments(patient_id,doctor_id,appointment_date) VALUES(?,?,?)";
                try {
                    PreparedStatement ps = connection.prepareStatement(appointmentQuery);
                    ps.setInt(1,patientId);
                    ps.setInt(2,doctorId);
                    ps.setString(3,appointmentDate);
                    int rowsAffected = ps.executeUpdate();
                    if(rowsAffected != 0){
                        System.out.println("Appointment Booked");
                    } else {
                        System.out.println("Failed to Book Appointment");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor not available on this date!!");
            }
        } else {
            System.out.println("Either Doctor or Patient doesn't exist!!");
        }
    }
    public static boolean checkDoctorAvailability(int doctorId,String appointmentDate,Connection connection){
        String query = "SELECT COUNT(*) FROM Appointments WHERE doctor_id = ? AND appointment_date = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1,doctorId);
            ps.setString(2,appointmentDate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                int count = rs.getInt(1);
                if(count == 0) return true;
                else return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
