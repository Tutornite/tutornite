package com.example.tutornite.activities;

import static com.example.tutornite.utils.Constants.app_date_format;
import static com.example.tutornite.utils.DateTimeFormatter.convertTimestampToFormat;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.example.tutornite.BuildConfig;
import com.example.tutornite.R;
import com.example.tutornite.utils.Constants;
import com.google.firebase.Timestamp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class CertificateActivity extends BaseActivity {

    ImageView back_img;
    Button btn_save_certificate;
    TextView txt_tutor_name, txt_current_date;
    RelativeLayout lin_certificate_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        initViews();
        setData();
    }

    private void setData() {
        String fullName = "";

        if (!TextUtils.isEmpty(Constants.currentUserModel.getFirstName())) {
            fullName += Constants.currentUserModel.getFirstName();
        }
        if (!TextUtils.isEmpty(Constants.currentUserModel.getLastName())) {
            fullName += " " + Constants.currentUserModel.getLastName();
        }

        if (!TextUtils.isEmpty(fullName)) {
            txt_tutor_name.setText(fullName);
        }

        Calendar calendar = Calendar.getInstance();
        Timestamp timestamp = new Timestamp(new Date(calendar.getTimeInMillis()));
        txt_current_date.setText(convertTimestampToFormat(app_date_format, timestamp));
    }

    private void initViews() {
        btn_save_certificate = findViewById(R.id.btn_save_certificate);
        txt_tutor_name = findViewById(R.id.txt_tutor_name);
        txt_current_date = findViewById(R.id.txt_current_date);
        back_img = findViewById(R.id.back_img);
        lin_certificate_lay = findViewById(R.id.lin_certificate_lay);
        back_img.setOnClickListener(view -> onBackPressed());
        btn_save_certificate.setOnClickListener(view -> saveCertificate());
    }

    private void saveCertificate() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(lin_certificate_lay.getWidth(), lin_certificate_lay.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        lin_certificate_lay.draw(canvas);
        document.finishPage(page);

        File pdfFile = new File(getCacheDir(), "TutorniteCertificateOfRecognition.pdf");
        try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
            document.writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri pdfUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share Certificate Of Recognition using:"));
    }
}