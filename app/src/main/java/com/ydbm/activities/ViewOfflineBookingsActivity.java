package com.ydbm.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import com.ydbm.R;
import com.ydbm.adapters.BookinListAdapter;
import com.ydbm.dialogs.DatePickerCustomDiaog;
import com.ydbm.models.BookingDetails;
import com.ydbm.session.SessionManager;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.SQLiteHandler;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewOfflineBookingsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    BookinListAdapter bookinListAdapter;
    LinearLayout linearLayoutenquiries;

    SessionManager sessionManager;
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD); // Set of font family alrady present with itextPdf library.
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.BLUE);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);


    boolean isInternet = false;
    TextView btnClearFilter;
    boolean isFiltered = false;
    int PDFType = 0;
    int from = 8;
    String bookingDate = null;
    private static final int MILS_IN_INCH = 1000;
    String[] permissions = new String[]{

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,


    };

    FrameLayout frameLayout;
    SQLiteHandler sqLiteHandler;
    DatePickerCustomDiaog.CalendarListener calendarListener;
    private ArrayList<BookingDetails> bookingDetailsList = new ArrayList<>();
    private ArrayList<BookingDetails> bookingDetailsListFiltered = new ArrayList<>();

    private ArrayList<BookingDetails> bookingDetailsListFilteredBookings = new ArrayList<>();
    private ArrayList<BookingDetails> bookingDetailsListFilteredTranferDates = new ArrayList<>();
    private ArrayList<BookingDetails> bookingDetailsListFilteredBookedOnDates = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;
    private FirebaseAnalytics mFirebaseAnalytics;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offline_bookings);
        sessionManager = new SessionManager(ViewOfflineBookingsActivity.this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            from = bundle.getInt("from");
            if (from == 16) {
                bookingDate = bundle.getString("key");
                Log.d("TEWYTYET1232", bookingDate + "");
            }
        }

        initToolbar();
        initViews();
        fetchBookingListAPICall(sessionManager.getDharamshalaId());
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //if (from == 7 && sessionManager.getRole().equals("1")) {
        getMenuInflater().inflate(R.menu.offline_menu, menu);
        //    MenuItem itemUsrLst = menu.findItem(R.id.usersList);
        /*if (sessionManager.getRole().equals("1")) {
            itemUsrLst.setVisible(true);
        } else {
            itemUsrLst.setVisible(false);
        }*/
        //   }

        MenuItem item = menu.findItem(R.id.action_search);

        item.setVisible(true);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search Here");
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (bookinListAdapter != null) {

                    bookinListAdapter.getFilter().filter(newText);
                    if (newText.length() > 0) {
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Name", sessionManager.getUserDetailsOb().getName() + "");
                        bundle1.putString("user_detail", sessionManager.getUserDetailsOb().getUsername() + " # " + ConvertGMTtoIST.getCurrentDateTimeLastSeen() + " # " + newText + " in " + "offline Bookings");
                        bundle1.putString("Dharamshala_Name", sessionManager.getDharamshalanm() + "");
                        mFirebaseAnalytics.logEvent("Search_Event", bundle1);
                    }
                }
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("SHDJHJHJSH", "closed");
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_dates) {
            if (!isFiltered) {
                DatePickerCustomDiaog update = new DatePickerCustomDiaog(ViewOfflineBookingsActivity.this, new DatePickerCustomDiaog.CalendarListener() {
                    @Override
                    public void onFirstDateSelected(Calendar startDate) {
                        // Toast.makeText(getActivity(), ""+startDate.getTime().toString(), Toast.LENGTH_SHORT).show();
                        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                        Log.d("AJKJKJKJ12343", startDate.getTime().toString());

                        for (BookingDetails bookingDetails : bookingDetailsListFiltered) {
                            String[] dt = bookingDetails.getCheckinDt().split(" ");
                            Log.d("HAJHJHJHAJHA123", df1.format(startDate.getTime()) + "==" + "===" + dt[0]);
                            if (dt[0].equals(df1.format(startDate.getTime()))) {
                                bookingDetailsListFilteredTranferDates.add(bookingDetails);
                            }
                        }

                        bookingDetailsList.clear();
                        bookingDetailsList.addAll(bookingDetailsListFilteredTranferDates);
                        bookinListAdapter.notifyDataSetChanged();
                        btnClearFilter.setVisibility(View.VISIBLE);
                        isFiltered = true;
                    }

                    @Override
                    public void onDateRangeSelected(Calendar startDate, Calendar endDate) {
                        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                        // String checkIn = bookingDetails.getCheckinDt().split(" ")[0];
                        // String checkout = bookingDetails.getCheckoutDt().split(" ")[0];
                        Log.d("AJKJKJKJ12343", startDate.getTime().toString() + "===" + endDate.getTime().toString());
                        List<String> datesBetwn = getDates(df1.format(startDate.getTime()).toString(), df1.format(endDate.getTime()).toString());
                        Log.d("DKKDJKJDKJ", datesBetwn.size() + "==");
                  /*  for (BookingDetails bookingDetails : bookingDetailsList) {
                        if (datesBetwn.contains(bookingDetails.getCheckinDt())) {

                        }
                        else {
                            bookingDetailsList.remove(bookingDetails);
                            bookinListAdapter.notifyDataSetChanged();
                        }
                    }*/
                    }

                    @Override
                    public void onResetBtnClicked() {
                        bookingDetailsList.clear();
                        bookingDetailsListFilteredTranferDates.clear();
                        bookingDetailsList.addAll(bookingDetailsListFiltered);
                        bookinListAdapter.notifyDataSetChanged();
                        btnClearFilter.setVisibility(View.GONE);
                        isFiltered = false;
                    }
                });
                update.setCancelable(false);
                update.setCanceledOnTouchOutside(true);
                update.show();
                invalidateOptionsMenu();

            } else {
                bookingDetailsList.clear();
                bookingDetailsListFilteredBookings.clear();
                bookingDetailsList.addAll(bookingDetailsListFiltered);
                bookinListAdapter.notifyDataSetChanged();
                btnClearFilter.setVisibility(View.GONE);
                isFiltered = false;

            }
        } else if (id == R.id.action_pdf_submenu) {
            //Intent intent = new Intent(BookingListingActivity.this, ProfileScreenActivity.class);
            // startActivity(intent);

            Toast.makeText(getApplicationContext(), "pdf", Toast.LENGTH_SHORT).show();

            if (PDFType == 5) {
                if (isFiltered) {
                    checkPermissions2("pdf");
                } else {
                    Toast.makeText(ViewOfflineBookingsActivity.this, "PDF Option can be enabled for filtered data only !!", Toast.LENGTH_SHORT).show();
                }

            } else {
                if (isFiltered)
                    checkPermissions2("pdf");
                else {
                    Toast.makeText(ViewOfflineBookingsActivity.this, "PDF Option can be enabled for filtered data only !!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (id == R.id.action_excel_submenu) {
            //Intent intent = new Intent(BookingListingActivity.this, ProfileScreenActivity.class);
            // startActivity(intent);

            //if (PDFType == 5) {
            if (isFiltered) {
                checkPermissions2("excel");
            } else {
                Toast.makeText(ViewOfflineBookingsActivity.this, "Excel Option can be enabled for filtered data only !!", Toast.LENGTH_SHORT).show();
            }


        } else if (id == R.id.profilescreen) {
            Intent intent = new Intent(ViewOfflineBookingsActivity.this, ProfileScreenActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.listBookings);
        btnClearFilter = findViewById(R.id.btnClearFilter);
        linearLayoutenquiries = findViewById(R.id.noBokingsLinear);
        frameLayout = findViewById(R.id.generatePDFFrame);
        linearLayoutManager = new LinearLayoutManager(ViewOfflineBookingsActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        sqLiteHandler = new SQLiteHandler(ViewOfflineBookingsActivity.this);
        sessionManager = new SessionManager(ViewOfflineBookingsActivity.this);
        String dId = sessionManager.getDharamshalaId();
        btnClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookingDetailsList.clear();
                bookingDetailsListFilteredBookings.clear();
                bookingDetailsList.addAll(bookingDetailsListFiltered);
                bookinListAdapter.notifyDataSetChanged();
                btnClearFilter.setVisibility(View.GONE);
                isFiltered = false;
                if (bookingDetailsList.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    linearLayoutenquiries.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    linearLayoutenquiries.setVisibility(View.VISIBLE);
                }
            }
        });
        // bookingDetailsList = sqLiteHandler.fetchAllOfflineBookings("offline", dId);

        if (from == 16) {

            //  Collections.reverse(bookingDetailsList);

            String dt1 = bookingDate;
            //remove comment
            DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
            Log.d("AJKJKJKJ12343", dt1);
            //  List<String> datesBetwn = getDates(df1.format(startDate.getTime()).toString(), df1.format(endDate.getTime()).toString());

            for (BookingDetails bookingDetails : bookingDetailsListFiltered) {
                String[] dtin = bookingDetails.getCheckinDt().split(" ");
                String[] dtcheckout = bookingDetails.getCheckoutDt().split(" ");
                List<String> datesBetwn = getDatesForSingleSelection(dtin[0], dtcheckout[0]);

                Log.d("HAJHJHJHAJHA123", dt1 + "==" + "===" + dtin[0] + "==" + dtcheckout[0]);
                for (int i = 0; i < datesBetwn.size(); i++) {
                    String dtbtwn = datesBetwn.get(i);
                    if (dtbtwn.equals(dt1)) {
                        bookingDetailsListFilteredTranferDates.add(bookingDetails);
                    }
                }

            }

            bookingDetailsList.clear();
            bookingDetailsList.addAll(bookingDetailsListFilteredTranferDates);
            // bookinListAdapter.notifyDataSetChanged();
            btnClearFilter.setVisibility(View.VISIBLE);
            if (bookingDetailsList.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                linearLayoutenquiries.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                linearLayoutenquiries.setVisibility(View.VISIBLE);
            }
            bookinListAdapter = new BookinListAdapter(bookingDetailsList, ViewOfflineBookingsActivity.this, 9);
            recyclerView.setAdapter(bookinListAdapter);
        }

    }

    private boolean checkPermissions2(String type) {
        int result;
        Log.d("AJKJKJKJAK", "2");
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ActivityCompat.checkSelfPermission(ViewOfflineBookingsActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            Log.d("AJKJKJKJAK", "1");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 100);
            }
            // ContextCompat.checkSelfPermission(getActivity(), listPermissionsNeeded.get(0));
            // ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        } else {
            if (type.equals("pdf")) {
                Log.d("DHSUSUDUYUS", "1");

                printPDF(frameLayout);
            } else {
                Log.d("DHSUSUDUYUS", "2");
                createExcelFile();
            }
        }
        return true;
    }

    private void createExcelFile() {


        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("bookings"); //Creating a sheet

      /*  for(int  i=0; i<bookingDetailsList.size(); i++){

            Row row = sheet.createRow(i);

            row.createCell(0).setCellValue(bookingDetailsList.get(i).getBookingId());
            row.createCell(1).setCellValue(bookingDetailsList.get(i).getYatriNm());
            row.createCell(2).setCellValue(bookingDetailsList.get(i).getBookingRoomsType());

        }
*/

        //Map<String, Object[]> data = new TreeMap<String, Object[]>();
        //  data.put("1", new Object[]{"Booking Id", "Yatri Name", "check in", "check out"});
        XSSFFont font = workbook.createFont();

        font.setFontName("Arial");

        font.setBold(true);
        font.setItalic(false);


        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);


        XSSFCellStyle cellStyle2 = workbook.createCellStyle();
        cellStyle2.setFont(font);
        cellStyle2.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);


        String fieldNm = "";
        CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 11); // row 1 col B and C

        sheet.addMergedRegion(mergedRegion);
        Row row2 = sheet.createRow(0);
        Cell cellMain = row2.createCell(0);

        cellMain.setCellStyle(cellStyle2);

        cellMain.setCellValue(sessionManager.getDharamshalanm() + "");
        //  CellUtil.setCellStyleProperty(cellMain,workbook, CellUtil.ALIGNMENT, HorizontalAlignment.CENTER);
        // CellUtil.setAlignment(cellMain, workbook, CellStyle.);

        Row row = sheet.createRow(1);


        for (int i = 0; i < 12; i++) {
            if (i == 0) {
                fieldNm = "S No";
            } else if (i == 1) {
                fieldNm = "Check in/Check out";
            } else if (i == 2) {
                fieldNm = "Nights";
            } else if (i == 3) {
                fieldNm = "Room Type";
            } else if (i == 4) {
                fieldNm = "Rooms";
            } else if (i == 5) {
                fieldNm = "Name";
            } else if (i == 6) {
                fieldNm = "Guest";
            } else if (i == 7) {
                fieldNm = "Booking Total";
            } else if (i == 8) {
                fieldNm = "NEFT No";
            } else if (i == 9) {
                fieldNm = "NEFT Amount";
            } else if (i == 10) {
                fieldNm = "NEFT Date";
            } else if (i == 11) {
                fieldNm = "booking Id";
            }
            Cell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(fieldNm);


        }


        int rownum = 2;
        int serialNo = 1;
        double totalBooking = 0, totalTranfer = 0;
        for (int i = 0; i < bookingDetailsList.size(); i++) {


            Log.d("ASKKHKAHKH", rownum + "");

            Row row1 = sheet.createRow(rownum);

            // Cell cell = row1.createCell(cellnum++);
            row1.createCell(0).setCellValue("" + serialNo);
            row1.createCell(1).setCellValue(bookingDetailsList.get(i).getCheckinDt() + "/" + bookingDetailsList.get(i).getCheckoutDt());
            row1.createCell(2).setCellValue(bookingDetailsList.get(i).getNights());
            row1.createCell(3).setCellValue(bookingDetailsList.get(i).getRoomTyp());
            row1.createCell(4).setCellValue(bookingDetailsList.get(i).getNoOfRooms());
            row1.createCell(5).setCellValue(bookingDetailsList.get(i).getYatriNm());
            row1.createCell(6).setCellValue(bookingDetailsList.get(i).getPerson());
            if (bookingDetailsList.get(i).getBookingTotal() != null && !bookingDetailsList.get(i).getBookingTotal().equals("null")) {
                row1.createCell(7).setCellValue(bookingDetailsList.get(i).getBookingTotal());
                totalBooking = totalBooking + Double.parseDouble(bookingDetailsList.get(i).getBookingTotal());
            } else {
                row1.createCell(7).setCellValue("NA");
            }

            row1.createCell(8).setCellValue(bookingDetailsList.get(i).getNeftNo());
            if (bookingDetailsList.get(i).getTranferAmt() != null && !bookingDetailsList.get(i).getTranferAmt().equals("null")) {
                row1.createCell(9).setCellValue(bookingDetailsList.get(i).getTranferAmt());
                totalTranfer = totalTranfer + Double.parseDouble(bookingDetailsList.get(i).getTranferAmt());
            } else {
                row1.createCell(9).setCellValue("NA");
            }


            row1.createCell(10).setCellValue(bookingDetailsList.get(i).getTranferDt());
            row1.createCell(11).setCellValue(bookingDetailsList.get(i).getBookingId());

            rownum++;
            serialNo++;

        }

        CellRangeAddress mergedRegionLast = new CellRangeAddress(rownum, rownum, 0, 6); // row 1 col B and C
        sheet.addMergedRegion(mergedRegionLast);
        sheet.addMergedRegion(mergedRegion);
        Row rowlast = sheet.createRow(rownum);

        XSSFCellStyle cellStyle3 = workbook.createCellStyle();
        cellStyle3.setFont(font);
        cellStyle3.setAlignment(CellStyle.ALIGN_RIGHT);
        cellStyle3.setVerticalAlignment(CellStyle.ALIGN_RIGHT);

        Cell cellMainLast = rowlast.createCell(0);
        cellMainLast.setCellStyle(cellStyle3);
        cellMainLast.setCellValue("Total" + " : ");

        Cell cellMainLastBookingTotal = rowlast.createCell(7);
        cellMainLastBookingTotal.setCellStyle(cellStyle);
        cellMainLastBookingTotal.setCellValue("" + totalBooking);


        Cell cellMainLasTfTotal = rowlast.createCell(9);
        cellMainLasTfTotal.setCellStyle(cellStyle);
        cellMainLasTfTotal.setCellValue("" + totalTranfer);


        String id = String.valueOf(System.currentTimeMillis());
        String nm = id + "_bookings.xlsx";
        String fileName = nm; //Name of the file

        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        File folder = new File(extStorageDirectory, "ydbookingmanager");// Name of the folder you want to keep your file in the local storage.
        folder.mkdir(); //creating the folder
        File file = new File(folder, fileName);
        try {
            file.createNewFile(); // creating the file inside the folder
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            FileOutputStream fileOut = new FileOutputStream(file); //Opening the file
            workbook.write(fileOut); //Writing all your row column inside the file
            fileOut.close(); //closing the file and done
            Bundle bundle1 = new Bundle();
            bundle1.putString("user_detail", "By " + sessionManager.getUserDetailsOb().getUsername() + " at " + ConvertGMTtoIST.getCurrentDateTimeLastSeen());
            mFirebaseAnalytics.logEvent("PDF_Generation", bundle1);


            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Excel file generated successfully.!", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("View", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(ViewOfflineBookingsActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);

                }
            });
            snackbar.show();
            //Toast.makeText(getActivity(), "Excel file generated successfully !!", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void printPDF(View view) {
        /*PrintManager printManager = (PrintManager) getActivity().getSystemService(PRINT_SERVICE);
        printManager.print("print_any_view_job_name", new ViewPrintAdapter(getActivity(),
                getActivity().findViewById(R.id.generatePDFFrame)), null);
*/
         createPdf(findViewById(R.id.generatePDFFrame));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                FrameLayout frameLayout = findViewById(R.id.generatePDFFrame);
                printPDF(frameLayout);
                // do something
            } else {
                Log.d("AJKJKJKJAK", "3");
                //  Toast.makeText(ViewOfflineBookingsActivity.this, "Please provide permission to access storage !", Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(android.R.id.content), "Please provide permission to access storage!", Snackbar.LENGTH_LONG);

                // checkPermissions2();
            }
            // return;
        }
    }

    private List<String> getDates(String dateString1, String dateString2) {
        bookingDetailsListFilteredBookings.clear();
        ArrayList<Date> dates = new ArrayList<Date>();
        ArrayList<String> datesFormatetd = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");


        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Log.d("HAJHJHJHAJHA", dateString1 + "===" + dateString2);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        dates.add(cal1.getTime());
        while (!cal1.after(cal2)) {
            // dates.add(cal1.getTime());
            if (!dateString2.equals(df1.format(cal1.getTime()))) {
                datesFormatetd.add(df1.format(cal1.getTime()));

                Log.d("HAJHJHJHAJHA", df1.format(cal1.getTime()) + "==");
                for (BookingDetails bookingDetails : bookingDetailsListFiltered) {
                    String[] dt = bookingDetails.getCheckinDt().split(" ");
                    Log.d("HAJHJHJHAJHA123", df1.format(cal1.getTime()) + "==" + "===" + dt[0]);
                    if (dt[0].equals(df1.format(cal1.getTime()))) {
                        bookingDetailsListFilteredBookings.add(bookingDetails);
                    } else {


                    }
                }


            }
            cal1.add(Calendar.DATE, 1);

        }
        bookingDetailsList.clear();
        bookingDetailsList.addAll(bookingDetailsListFilteredBookings);
        bookinListAdapter.notifyDataSetChanged();
        btnClearFilter.setVisibility(View.VISIBLE);
        isFiltered = true;
        return datesFormatetd;
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public void PrintDocument(String dest) throws IOException, java.io.IOException {
        try {

//            Document document = new Document();
            Document document = new Document(PageSize.LETTER, 10f, 10f, 10f, 0f);//PENGUIN_SMALL_PAPERBACK used to set the paper size
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            document.open();
            addMetaData(document);
            addTitlePage(document);
            if (PDFType == 5) {
                Log.d("SLJDKJDSJKSJD", "1");
                addContentToPDFTable(document);
            } else {
                Log.d("SLJDKJDSJKSJD", "2");

                addContentToPDFTable(document);
            }
            document.close();
            File file = new File(dest);
            Bundle bundle1 = new Bundle();
            bundle1.putString("user_detail", "By " + sessionManager.getUserDetailsOb().getUsername() + " at " + ConvertGMTtoIST.getCurrentDateTimeLastSeen());
            mFirebaseAnalytics.logEvent("PDF_Generation", bundle1);

            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "PDF file generated successfully.!", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("View", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(ViewOfflineBookingsActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);

                }
            });
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTitlePage(Document document)
            throws DocumentException {

        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header

        try {
            // get input stream
            InputStream ims = getAssets().open("app_icon.png");
            Bitmap bmp = BitmapFactory.decodeStream(ims);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());

            if (PDFType == 5) {
                image.scaleAbsolute(60, 60);
                image.setAlignment(Element.ALIGN_MIDDLE);
            } else {
                //image.setAlignment(Element.ALIGN_CENTER);
                image.scaleAbsolute(60, 60);
                image.setAlignment(Element.ALIGN_MIDDLE);
            }
            preface.add(image);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        addEmptyLine(preface, 2);
        // if (PDFType != 5) {
        Paragraph paragraph = new Paragraph("Yatradham", catFont);
        paragraph.setAlignment(Element.ALIGN_MIDDLE);
        preface.add(paragraph);
        // image.setAlignment(Element.ALIGN_MIDDLE );
        //}
        addEmptyLine(preface, 1);
        // Will create: Report generated by: _name, _date
        preface.add(new Paragraph(
                "Report generated by: " + sessionManager.getUserDetailsOb().getName() + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                smallBold));
        addEmptyLine(preface, 3);
     /*   preface.add(new Paragraph(
                "This document describes something which is very important ",
                smallBold));

        addEmptyLine(preface, 8);
*/
       /* preface.add(new Paragraph(
                "This document is a preliminary version and not subject to your license agreement or any other agreement with vogella.com ;-).",
                redFont));
*/
        document.add(preface);
        // Start a new page
        if (PDFType != 5) {
            //   document.newPage();
        }
    }

    private static void addMetaData(Document document) {
        document.addTitle("Booking List");
        document.addSubject("Booking Details");
        document.addKeywords("Java, PDF, Booking");
        document.addAuthor("Maitri");
        document.addCreator("Maitri");
    }

    public void createPdf(View view) {
        String id = String.valueOf(System.currentTimeMillis());
        String DEST = "/bookinglist" + "_" + id + ".pdf";

        String root = Environment.getExternalStorageDirectory().toString() + "/ydbookingmanager";
        // final File dir1 = new File("");
        final File dir = new File(root);
        if (!dir.exists())
            dir.mkdirs();
        try {
            PrintDocument(dir.getPath() + DEST);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public PdfPCell getPDFCell(String data) {
        PdfPCell c1 = new PdfPCell(new Phrase(data));
        c1.setBorderColor(BaseColor.WHITE);
        return c1;
    }

    private void addContentToPDFTable(Document document) throws DocumentException {

        //Paragraph paragraph = new Paragraph("Booking List", catFont);


        Paragraph subPara = new Paragraph("", subFont);
        subPara.setAlignment(Element.ALIGN_CENTER);
        document.add(subPara);

        Paragraph paragraph6 = new Paragraph();
        addEmptyLine(paragraph6, 1);
        document.add(paragraph6);

        //  PdfPTable pdfPTable1 = new PdfPTable(2);
        //pdfPTable1.setWidthPercentage(100);

        subPara = new Paragraph("Booking Details", subFont);
        subPara.setAlignment(Element.ALIGN_CENTER);
        document.add(subPara);
        Paragraph parag8 = new Paragraph();
        addEmptyLine(parag8, 1);
        document.add(parag8);

        PdfPTable pdfPTableBooking = new PdfPTable(12);
        pdfPTableBooking.setWidthPercentage(100);
        pdfPTableBooking.setWidths(new int[]{1, 3, 1, 2, 1, 2, 1, 2, 2, 2, 2, 3});
        pdfPTableBooking.addCell(getPDFCellBookedOn("S No"));
        pdfPTableBooking.addCell(getPDFCellBookedOn("Check in/Check out"));
        pdfPTableBooking.addCell(getPDFCellBookedOn("Nights"));
        pdfPTableBooking.addCell(getPDFCellBookedOn("Roomtype"));
        pdfPTableBooking.addCell(getPDFCellBookedOn("Rooms"));
        pdfPTableBooking.addCell(getPDFCellBookedOn("Name"));
        pdfPTableBooking.addCell(getPDFCellBookedOn("Guest"));
        pdfPTableBooking.addCell(getPDFCellBookedOn("Booking Total"));
        pdfPTableBooking.addCell(getPDFCellBookedOn("Neft No."));
        pdfPTableBooking.addCell(getPDFCellBookedOn("Tranfer Amount"));

        pdfPTableBooking.addCell(getPDFCellBookedOn("Tranfer Date"));
        pdfPTableBooking.addCell(getPDFCellBookedOn("Booking Id"));


        double totalBooking = 0, totalTranfer = 0;
        // Second parameter is the number of the chapter\
        for (int i = 0; i < bookingDetailsList.size(); i++) {
            BookingDetails bookingDetails = bookingDetailsList.get(i);

           /* Paragraph subPara1 = new Paragraph("(" + i + ")", redFont);
            subPara1.setAlignment(Element.ALIGN_CENTER);
            document.add(subPara1);
            */
            pdfPTableBooking.addCell(getPDFCellBookedOn("" + i));
            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getCheckinDt() + "\n" + bookingDetails.getCheckoutDt()));
            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getNights()));
            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getRoomTyp()));
            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getNoOfRooms()));
            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getYatriNm()));
            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getPerson()));


            if (bookingDetails.getBookingTotal() != null && !bookingDetails.getBookingTotal().equals("null")) {
                totalBooking = totalBooking + Double.parseDouble(bookingDetails.getBookingTotal());
                pdfPTableBooking.addCell(getPDFCellBookedOnTotal(bookingDetails.getBookingTotal()));
            } else {
                pdfPTableBooking.addCell(getPDFCellBookedOn("NA"));
            }
            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getNeftNo()));

            if (bookingDetails.getTranferAmt() != null && !bookingDetails.getTranferAmt().equals("null")) {
                pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getTranferAmt()));
                totalTranfer = totalTranfer + Double.parseDouble(bookingDetails.getTranferAmt());
            } else {
                pdfPTableBooking.addCell(getPDFCellBookedOn("NA"));
            }

            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getTranferDt()));
            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getBookingId()));


        }
        Log.d("SDJJDKJSDKJDK123", "45");

        pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("Total :  ", 7, Element.ALIGN_RIGHT, 1));
        pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("" + totalBooking, 0, Element.ALIGN_LEFT, 2));
        //  pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("", 0, Element.ALIGN_LEFT, 3));

        pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("", 0, Element.ALIGN_LEFT, 3));
        pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("" + totalTranfer, 0, Element.ALIGN_LEFT, 3));
        pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("", 0, Element.ALIGN_LEFT, 3));
        pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("", 0, Element.ALIGN_LEFT, 3));

      /*  c1.setRowspan(5);
        c1.setBorderColor(BaseColor.GRAY);
        pdfPTableBooking.addCell(c1);
*/
        //  PdfPCell c2 = new PdfPCell(new Phrase("Booking Total : "+totalBooking));

        // c2.setBorderColor(BaseColor.GRAY);
        //   pdfPTableBooking.addCell(c2);

        document.add(pdfPTableBooking);
        Paragraph parag1 = new Paragraph();
        addEmptyLine(parag1, 1);
        document.add(parag1);
        //subPara = new Paragraph("Booking Total : "+total, subFont);
        // subPara.setAlignment(Element.ALIGN_RIGHT);
        // document.add(subPara);
    }

    public PdfPCell getPDFCellBookedOn(String data) {
        PdfPCell c1 = new PdfPCell(new Phrase(data));
        c1.setBorderColor(BaseColor.GRAY);
        c1.setPaddingTop(8);
        c1.setPaddingBottom(8);
        return c1;
    }

    public PdfPCell getPDFCellBookedOnTotal(String data) {


        PdfPCell c1 = new PdfPCell(new Phrase(data));
        c1.setBorderColor(BaseColor.GRAY);
        c1.setPaddingTop(8);
        c1.setPaddingBottom(8);
        return c1;
    }

    public PdfPCell getPDFCellBookedOnTotalWithSpan(String data, int span, int align, int type) {
        Font font = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Phrase phrase = new Phrase(data);
        phrase.setFont(font);

        PdfPCell c1 = new PdfPCell(phrase);
        c1.setBorderColor(BaseColor.BLACK);
        c1.setHorizontalAlignment(align);
        c1.setColspan(span);
        c1.setPaddingTop(8);
        c1.setPaddingBottom(8);
        if (type == 1) {
            c1.setPaddingRight(8);
        }
        return c1;
    }

    private List<String> getDatesForSingleSelection(String dateString1, String dateString2) {
        bookingDetailsListFilteredBookings.clear();
        ArrayList<Date> dates = new ArrayList<Date>();
        ArrayList<String> datesFormatetd = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");


        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Log.d("HAJHJHJHAJHA", dateString1 + "===" + dateString2);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        dates.add(cal1.getTime());
        while (!cal1.after(cal2)) {
            // dates.add(cal1.getTime());
            if (!dateString2.equals(df1.format(cal1.getTime()))) {
                datesFormatetd.add(df1.format(cal1.getTime()));

                Log.d("HAJHJHJHAJHA", df1.format(cal1.getTime()) + "==");


            }
            cal1.add(Calendar.DATE, 1);

        }

        return datesFormatetd;
    }

    private void fetchBookingListAPICall(String dharamshala_id) {
        showProgressDialog("Fetching Booking Details.. Please Wait!");
        Log.d("AHJHJHAJH", dharamshala_id + "");
        RequestQueue queue = Volley.newRequestQueue(ViewOfflineBookingsActivity.this);
        //Log.d("CHECKRESPONS", "https://yatradham.org/demosite/restapi.php?apiname=addgroup&number=" + finalMembers + "&group_id=" + mGroupId + "&is_mobile=APP");
        //    Log.d("TEGSDHGHDSHD", AppConstants.BASE_API_URL + "fetchbookinglist" + "&dsid=" + dharamshala_id + "&is_mobile=APP");
        String url = "http://192.168.0.105/MANAGER_API/INSERTDISPLAYDATA/action/display-manager-data.php?dharmshala_id=" + dharamshala_id;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();
                Log.d("GAAGHAGHHAG1122", response + "==");
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Log.d("CHECKRESPONSE", response.toString());
                    if (jsonObject.has("success")) {
                        if (jsonObject.getBoolean("success")) {
                            JSONArray jsonArrayInfo = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArrayInfo.length(); i++) {
                                Log.d("HADJHJHJDHAJD44", i + "==");
                                BookingDetails bookingDetails = new BookingDetails();
                                JSONObject object = jsonArrayInfo.getJSONObject(i);
                                bookingDetails.setBookingId(object.getString("id"));
                                bookingDetails.setDs_id(object.getString("dharmshala_id"));
                                bookingDetails.setDs_name(object.getString("dharmshala_name"));
                                bookingDetails.setYatriNm(object.getString("yatri_name"));
                                bookingDetails.setYatriAddress(object.getString("yatri_address"));
                                bookingDetails.setYatriMobile(object.getString("yatri_contactnumber"));
                                bookingDetails.setRoomTyp(object.getString("room_type"));
                                //contact.setMessageType("text");
                                bookingDetails.setCheckinDt(object.getString("checkindate"));

                                bookingDetails.setCheckoutDt(object.getString("checkoutdate"));
                                bookingDetails.setNoOfRooms(object.getString("rooms"));
                                bookingDetails.setPerson(object.getString("guests"));
                                // bookingDetails.setContribution(object.getString(""));
                                bookingDetails.setBookingTotal(object.getString("total"));
                                //contact.setMessageType("text");
                                // bookingDetails.setNeftNo(cursor.getString(13));
                                // bookingDetails.setTranferDt(cursor.getString(14));
                                //   bookingDetails.setTranferAmt(cursor.getString(15));
                                //bookingDetails.setBookingStatus(cursor.getString(16));
                                //contact.setMessageType("text");
                                //  Log.d("ASDJHJHJJHAJH", cursor.getString(18) + "===");
                                bookingDetails.setExpectedCheckinTime(object.getString("expected_datetime"));
                                bookingDetails.setRoom_id(object.getString("room_type_id"));
                                bookingDetails.setNights(object.getString("nights"));
                                bookingDetails.setBookingBookedOn(object.getString("created_at"));
                                bookingDetailsList.add(bookingDetails);
                                Log.d("HADJHJHJDHAJD", i + "==");
                                //    if (cursor.getString(20) != null)
                                //bookingDetails.setBookingBookedOn(cursor.getString(20));

                            }
                            bookingDetailsListFiltered.addAll(bookingDetailsList);
                            Log.d("HADJHJHJDHAJD", bookingDetailsList.size() + "==");

                            if (bookingDetailsList.size() > 0) {
                                recyclerView.setVisibility(View.VISIBLE);
                                linearLayoutenquiries.setVisibility(View.GONE);
                            } else {
                                recyclerView.setVisibility(View.GONE);
                                linearLayoutenquiries.setVisibility(View.VISIBLE);
                            }
                            bookinListAdapter = new BookinListAdapter(bookingDetailsList, ViewOfflineBookingsActivity.this);
                            recyclerView.setAdapter(bookinListAdapter);
                        } else {
                            if (jsonObject.has("data")) {
                                Toast.makeText(ViewOfflineBookingsActivity.this, jsonObject.getString("data") + "", Toast.LENGTH_SHORT).show();
                            }

                            if (bookingDetailsList.size() > 0) {
                                recyclerView.setVisibility(View.VISIBLE);
                                linearLayoutenquiries.setVisibility(View.GONE);
                            } else {
                                recyclerView.setVisibility(View.GONE);
                                linearLayoutenquiries.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } //  Toast.makeText(CreateUserActivity.this,response,Toast.LENGTH_LONG).show();
                //JSONObject jsonObject = null;


            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Log.d("ERROR132", error.toString());
                Toast.makeText(ViewOfflineBookingsActivity.this, "Unable to handle server response!", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> param = new HashMap<String, String>();

                return param;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        sr.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(sr);
    }

    public void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(ViewOfflineBookingsActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(msg);
        }
        if (mProgressDialog != null && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (!isFinishing() && mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();

        }
    }
}
