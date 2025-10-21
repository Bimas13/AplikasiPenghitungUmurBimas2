/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Lenovo
 */

import java.time.LocalDate;
import java.time.Period;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import java.util.function.Supplier;
import javax.swing.JTextArea;
import org.json.JSONArray;
import org.json.JSONObject;

public class PenghitungUmurHelper {
    
    
    
    // ==========================
    // 🔹 Mendapatkan peristiwa penting dan menerjemahkannya
    // ==========================
    public void getPeristiwaBarisPerBaris(LocalDate tanggal, JTextArea txtAreaPeristiwa, Supplier<Boolean> shouldStop) {
        try {
            if (shouldStop.get()) {
                return;
            }

            String urlString = "https://byabbe.se/on-this-day/" +
                               tanggal.getMonthValue() + "/" +
                               tanggal.getDayOfMonth() + "/events.json";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new Exception("HTTP response code: " + responseCode +
                                    ". Silakan coba lagi nanti atau cek koneksi internet.");
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                if (shouldStop.get()) {
                    in.close();
                    conn.disconnect();
                    javax.swing.SwingUtilities.invokeLater(() ->
                        txtAreaPeristiwa.setText("Pengambilan data dibatalkan.\n")
                    );
                    return;
                }
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            JSONObject json = new JSONObject(content.toString());
            JSONArray events = json.getJSONArray("events");

            if (events.length() == 0) {
                javax.swing.SwingUtilities.invokeLater(() ->
                    txtAreaPeristiwa.setText("Tidak ada peristiwa penting yang ditemukan pada tanggal ini.")
                );
                return;
            }

            for (int i = 0; i < events.length(); i++) {
                if (shouldStop.get()) {
                    javax.swing.SwingUtilities.invokeLater(() ->
                        txtAreaPeristiwa.setText("Pengambilan data dibatalkan.\n")
                    );
                    return;
                }

                JSONObject event = events.getJSONObject(i);
                String year = event.getString("year");
                String description = event.getString("description");

                // 🔹 Terjemahkan ke bahasa Indonesia
                String translated = translateToIndonesian(description);

                String peristiwa = year + ": " + translated;

                javax.swing.SwingUtilities.invokeLater(() ->
                    txtAreaPeristiwa.append(peristiwa + "\n")
                );
            }

        } catch (Exception e) {
            javax.swing.SwingUtilities.invokeLater(() ->
                txtAreaPeristiwa.setText("Gagal mendapatkan data peristiwa: " + e.getMessage())
            );
        }
    }

    // ==========================
    // 🔹 Menghitung umur secara detail (tahun, bulan, hari)
    // ==========================
    public String hitungUmurDetail(LocalDate lahir, LocalDate sekarang) {
        Period period = Period.between(lahir, sekarang);
        return period.getYears() + " tahun, " + period.getMonths() + " bulan, " + period.getDays() + " hari";
    }

    // ==========================
    // 🔹 Menghitung hari ulang tahun berikutnya
    // ==========================
    public LocalDate hariUlangTahunBerikutnya(LocalDate lahir, LocalDate sekarang) {
        LocalDate ulangTahunBerikutnya = lahir.withYear(sekarang.getYear());
        if (!ulangTahunBerikutnya.isAfter(sekarang)) {
            ulangTahunBerikutnya = ulangTahunBerikutnya.plusYears(1);
        }
        return ulangTahunBerikutnya;
    }

    // ==========================
    // 🔹 Menerjemahkan teks hari ke Bahasa Indonesia
    // ==========================
    public String getDayOfWeekInIndonesian(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY:
                return "Senin";
            case TUESDAY:
                return "Selasa";
            case WEDNESDAY:
                return "Rabu";
            case THURSDAY:
                return "Kamis";
            case FRIDAY:
                return "Jumat";
            case SATURDAY:
                return "Sabtu";
            case SUNDAY:
                return "Minggu";
            default:
                return "";
        }
    }

    // ==========================
    // 🔹 Fungsi terjemahan otomatis (pakai endpoint Google Translate publik)
    // ==========================
    private String translateToIndonesian(String text) {
        try {
            // Gunakan endpoint publik Google Translate
            String urlString = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=id&dt=t&q=" +
                                URLEncoder.encode(text, "UTF-8");

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            conn.disconnect();

            // Format respons: [[[["Terjemahan","Teks asli",null,null,...]]],null,"en"]
            // Jadi kita ambil teks setelah [[" dan sebelum "," (untuk hasil pertama)
            String result = response.toString();
            result = result.substring(4, result.indexOf("\",\""));
            return result;

        } catch (Exception e) {
            return text + " (Gagal diterjemahkan)";
        }
    }

    String hitungUmurDetail(LocalDate lahir, LocalDate sekarang) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}


