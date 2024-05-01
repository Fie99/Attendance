package com.example.attfirebase.Records;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class PdfGenerator  {
    private static final int PDF_PAGE_WIDTH = 595; // 8.26 Inch
    private static final int PDF_PAGE_HEIGHT = 842; // 11.69 Inch

    private final Context context;
    private final FirebaseFirestore db;
    private final String studentID;
    private final String unitID;

    public PdfGenerator(Context context, FirebaseFirestore db, String studentID, String unitID) {
        this.context = context;
        this.db = db;
        this.studentID = studentID;
        this.unitID = unitID;
    }


    public void generatePdf() {
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PDF_PAGE_WIDTH, PDF_PAGE_HEIGHT, 1).create();

        // Fetch attendance data from Firestore
        fetchAttendanceData(doc, pageInfo);
    }

    private void fetchAttendanceData(PdfDocument doc, PdfDocument.PageInfo pageInfo) {
        db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("AttendanceRecord")
                .document(studentID)
                .collection("Records")
                .whereEqualTo("unit", unitID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        // Process the attendance data and generate the PDF
                        generatePdfFromData(doc, pageInfo, documents);
                    } else {
                        // Handle error
                    }
                });
    }

    private void generatePdfFromData(PdfDocument doc, PdfDocument.PageInfo pageInfo, List<DocumentSnapshot> documents) {
        PdfDocument.Page page = doc.startPage(pageInfo);

        // Create a View to render the attendance data
        View attendanceView = createAttendanceView(documents);

        // Render the View to a Bitmap
        Bitmap bitmap = loadBitmapFromView(attendanceView);

        // Draw the Bitmap on the PDF page
        if (bitmap != null) {
            page.getCanvas().drawBitmap(bitmap, 0f, 0f, null);
        }

        doc.finishPage(page);

        // Save the PDF document to external storage
        String fileName = "attendance_report.pdf";
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            doc.writeTo(outputStream);
            outputStream.close();
            doc.close();

            // Open the PDF file with a PDF viewer app
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(context, "com.example.attfirebase.provider", file);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private View createAttendanceView(List<DocumentSnapshot> documents) {
        // Create a layout to hold the attendance data
        LinearLayout attendanceLayout = new LinearLayout(context);
        attendanceLayout.setOrientation(LinearLayout.VERTICAL);

        // Iterate over the documents and add attendance data to the layout
        for (DocumentSnapshot document : documents) {
            // Get the attendance data from the document
            String date = document.getString("date");
            Timestamp timestamp = document.getTimestamp("time");
            boolean attended = document.getBoolean("attended");

            // Create a TextView to display the attendance data
            TextView attendanceTextView = new TextView(context);
            attendanceTextView.setText(formatAttendanceData(date, timestamp, attended));

            // Add the TextView to the layout
            attendanceLayout.addView(attendanceTextView);
        }

        return attendanceLayout;
    }

    private String formatAttendanceData(String date, Timestamp timestamp, boolean attended) {
        // Format the attendance data as desired
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        String formattedDate = dateFormat.format(timestamp.toDate());
        String formattedTime = timeFormat.format(timestamp.toDate());
        String attendanceStatus = attended ? "Present" : "Absent";

        return date + " | " + formattedDate + " " + formattedTime + " | " + attendanceStatus;
    }

    private Bitmap loadBitmapFromView(View view) {
        if (view.getMeasuredWidth() <= 0 || view.getMeasuredHeight() <= 0) {
            // Handle the case where the View has invalid dimensions
            // You can log an error message or return a default Bitmap
            return null;
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }



}
