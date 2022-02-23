package com.ydbm.fragments;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.graphics.Bitmap;

import android.net.Uri;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;


import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.text.BaseColor;


import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;


import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

import com.ydbm.BuildConfig;
import com.ydbm.R;

import com.ydbm.activities.BookingListingActivity;
import com.ydbm.adapters.BookinListAdapter;
import com.ydbm.dialogs.DatePickerCustomDiaog;
import com.ydbm.models.BookingDetails;
import com.ydbm.models.BookingListWithType;
import com.ydbm.session.SessionManager;
import com.ydbm.utils.ConvertGMTtoIST;
import com.ydbm.utils.DateComparator;
import com.ydbm.utils.NetworkUtil;
import com.ydbm.utils.SQLiteHandler;

import org.apache.poi.ss.util.CellRangeAddress;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllBookingsFragment extends Fragment {
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

    public AllBookingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    public static AllBookingsFragment newInstance(BookingListingActivity.GetBookingList getBookingListListener) {
        Log.d("AJDGHADGDGH", "8");

        AllBookingsFragment fragment = new AllBookingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AJDGHADGDGH", "5");
        setHasOptionsMenu(true);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sqLiteHandler = new SQLiteHandler(getActivity());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        frameLayout = getActivity().findViewById(R.id.generatePDFFrame);
        linearLayoutenquiries = getActivity().findViewById(R.id.noBokingsLinear);
        isInternet = NetworkUtil.checkInternetConnection(getActivity());
        sessionManager = new SessionManager(getActivity());

        final String d_id = sessionManager.getUserDetailsOb().getD_id();
        if (!isInternet) {
            ArrayList<BookingDetails> all = sqLiteHandler.fetchAllBookings("all", d_id);
            bookingDetailsList.addAll(all);

            ArrayList<BookingDetails> bookingsTomorrow = sqLiteHandler.fetchAllBookings("tomorrow", d_id);
            Collections.reverse(bookingsTomorrow);
            bookingDetailsList.addAll(bookingsTomorrow);
            ArrayList<BookingDetails> bookingsToday = sqLiteHandler.fetchAllBookings("today", d_id);
            Collections.reverse(bookingsToday);
            bookingDetailsList.addAll(bookingsToday);
            ArrayList<BookingDetails> bookingsActive = sqLiteHandler.fetchAllBookings("active", d_id);
            Collections.reverse(bookingsActive);
            bookingDetailsList.addAll(bookingsActive);
            Collections.sort(bookingDetailsList, new DateComparator());
           // Collections.reverse(bookingDetailsList);
            if (bookinListAdapter != null) {
                bookinListAdapter.notifyDataSetChanged();
            } else {
                bookinListAdapter = new BookinListAdapter(bookingDetailsList, getActivity());
                recyclerView.setAdapter(bookinListAdapter);
            }
        }

    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
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

                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Name", sessionManager.getUserDetailsOb().getName() + "");
                    bundle1.putString("user_detail", sessionManager.getUserDetailsOb().getUsername()+" # "+ ConvertGMTtoIST.getCurrentDateTimeLastSeen() + " # "+newText + " in " + "All Bookings");
                    bundle1.putString("Dharamshala_Name", sessionManager.getDharamshalanm() + "");
                    mFirebaseAnalytics.logEvent("Search_Event", bundle1);

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

      /*  MenuItem itemPDF = menu.findItem(R.id.action_pdf);
        if (isFiltered) {
            itemPDF.setVisible(true);
        } else {
            itemPDF.setVisible(false);
        }*/
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_dates) {
            if (!isFiltered) {
                DatePickerCustomDiaog update = new DatePickerCustomDiaog(getActivity(), new DatePickerCustomDiaog.CalendarListener() {
                    @Override
                    public void onFirstDateSelected(Calendar startDate) {
                        // Toast.makeText(getActivity(), ""+startDate.getTime().toString(), Toast.LENGTH_SHORT).show();
                        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                        Log.d("AJKJKJKJ12343", startDate.getTime().toString());

                        for (BookingDetails bookingDetails : bookingDetailsListFiltered) {
                            String[] dtin = bookingDetails.getCheckinDt().split(" ");
                            String[] dtcheckout = bookingDetails.getCheckoutDt().split(" ");
                            List<String> datesBetwn = getDatesForSingleSelection(dtin[0], dtcheckout[0]);

                            Log.d("HAJHJHJHAJHA123", df1.format(startDate.getTime()) + "==" + "===" + dtin[0] + "==" + dtcheckout[0]);
                            for (int i = 0; i < datesBetwn.size(); i++) {
                                String dtbtwn = datesBetwn.get(i);
                                if (dtbtwn.equals(df1.format(startDate.getTime()))) {
                                    bookingDetailsListFilteredTranferDates.add(bookingDetails);
                                }
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
                getActivity().invalidateOptionsMenu();

            } else {
                bookingDetailsList.clear();
                bookingDetailsListFilteredBookings.clear();
                bookingDetailsList.addAll(bookingDetailsListFiltered);
                bookinListAdapter.notifyDataSetChanged();
                btnClearFilter.setVisibility(View.GONE);
                isFiltered = false;

            }
        } else if (id == R.id.action_transfer_dt) {
            if (!isFiltered) {
                Log.d("FLOWTEST1233", "0");
                DatePickerCustomDiaog update = new DatePickerCustomDiaog(getActivity(), new DatePickerCustomDiaog.CalendarListener() {
                    @Override
                    public void onFirstDateSelected(Calendar startDate) {
                        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                        Log.d("AJKJKJKJ12343", startDate.getTime().toString());

                        for (BookingDetails bookingDetails : bookingDetailsListFiltered) {
                            String dt = bookingDetails.getTranferDt();
                            Log.d("HAJHJHJHAJHA123", df1.format(startDate.getTime()) + "==" + "===" + dt);
                            if (dt.equals(df1.format(startDate.getTime()))) {
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
                        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                        List<String> datesBetwn = getDatesTranferDts(df1.format(startDate.getTime()).toString(), df1.format(endDate.getTime()).toString());
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
                        Log.d("FLOWTEST1233", "1");
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
            } else {
                Log.d("FLOWTEST1233", "2");
                bookingDetailsList.clear();
                bookingDetailsListFilteredTranferDates.clear();
                bookingDetailsList.addAll(bookingDetailsListFiltered);
                bookinListAdapter.notifyDataSetChanged();
                btnClearFilter.setVisibility(View.GONE);
                isFiltered = false;
            }
        } else if (id == R.id.action_pdf_submenu) {
            //Intent intent = new Intent(BookingListingActivity.this, ProfileScreenActivity.class);
            // startActivity(intent);

          /*  if (PDFType == 5) {
                if (isFiltered) {
                    checkPermissions2("pdf");
                } else {
                    Toast.makeText(getActivity(), "PDF Option can be enabled for filtered data only !!", Toast.LENGTH_SHORT).show();
                }

            } else {
                if (isFiltered)
                    checkPermissions2("pdf");
                else {
                    Toast.makeText(getActivity(), "PDF Option can be enabled for filtered data only !!", Toast.LENGTH_SHORT).show();
                }
            }*/ // remove filter
            //if (isFiltered) {
                checkPermissions2("pdf");
           /* } else {
                Toast.makeText(getActivity(), "PDF Option can be enabled for filtered data only !!", Toast.LENGTH_SHORT).show();
            }*/
        } else if (id == R.id.action_excel_submenu) {


            //if (isFiltered) {
                checkPermissions2("excel");
          /*  } else {
                Toast.makeText(getActivity(), "Excel Option can be enabled for filtered data only !!", Toast.LENGTH_SHORT).show();
            }*/


        } else if (id == R.id.action_bookedon_dt) {
            if (!isFiltered) {
                DatePickerCustomDiaog update = new DatePickerCustomDiaog(getActivity(), new DatePickerCustomDiaog.CalendarListener() {
                    @Override
                    public void onFirstDateSelected(Calendar startDate) {


                        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                        // Log.d("AJKJKJKJ12343", startDate.getTime().toString());

                        for (BookingDetails bookingDetails : bookingDetailsListFiltered) {
                            String dt = bookingDetails.getBookingBookedOn();
                            Log.d("HAJHJHJHAJHA123", df1.format(startDate.getTime()) + "==" + "===" + dt);
                            /*if (dt.equals(df1.format(startDate.getTime()))) {
                                bookingDetailsListFilteredBookedOnDates.add(bookingDetails);
                            }*/
                            String arr[] = dt.split("\\s+");

                            if (arr[0] != null) {
                                Log.d("HAJHJHJHAJHA1235", df1.format(startDate.getTime()) + "==" + "===" + arr[0]);
                                if (arr[0].trim().equals(df1.format(startDate.getTime()).trim())) {
                                    bookingDetailsListFilteredTranferDates.add(bookingDetails);
                                }
                            }
                        }

                        bookingDetailsList.clear();
                        bookingDetailsList.addAll(bookingDetailsListFilteredTranferDates);
                        bookinListAdapter.notifyDataSetChanged();
                        btnClearFilter.setVisibility(View.VISIBLE);
                        isFiltered = true;
                        PDFType = 5;
                    }

                    @Override
                    public void onDateRangeSelected(Calendar startDate, Calendar endDate) {
                        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                        List<String> datesBetwn = getDatesBookedOnDts(df1.format(startDate.getTime()).toString(), df1.format(endDate.getTime()).toString());
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
                        bookingDetailsListFilteredBookedOnDates.clear();
                        bookingDetailsList.addAll(bookingDetailsListFiltered);
                        bookinListAdapter.notifyDataSetChanged();
                        btnClearFilter.setVisibility(View.GONE);
                        isFiltered = false;
                    }
                });
                update.setCancelable(false);
                update.setCanceledOnTouchOutside(true);
                update.show();
            } else {
                bookingDetailsList.clear();
                bookingDetailsListFilteredTranferDates.clear();
                bookingDetailsListFilteredBookedOnDates.clear();
                bookingDetailsList.addAll(bookingDetailsListFiltered);
                bookinListAdapter.notifyDataSetChanged();
                btnClearFilter.setVisibility(View.GONE);
                isFiltered = false;
            }

        }
        return super.onOptionsItemSelected(item);

    }

    private boolean checkPermissions2(String type) {
        int result;
        Log.d("AJKJKJKJAK", "2");
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ActivityCompat.checkSelfPermission(getActivity(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            Log.d("AJKJKJKJAK", "1");
            requestPermissions(permissions, 100);
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
   /* Workbook workbook=new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bookings");
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[]{"Booking Id", "Yatri Name", "check in", "check out"});
        Row row = sheet.createRow(0);
        /*Cell cell = row.createCell(cellnum++);
        if (obj instanceof String) {
            cell.setCellValue((String) obj);
        } else if (obj instanceof Integer) {
            cell.setCellValue((Integer) obj);
        }*/
      /*  for (int i = 0; i < bookingDetailsList.size(); i++) {
            Object obj = new Object[]{bookingDetailsList.get(i).getBookingId(), bookingDetailsList.get(i).getYatriNm(), bookingDetailsList.get(i).getCheckinDt(), bookingDetailsList.get(i).getCheckoutDt()};
            // data.put("3", new Object[]{2, "Lokesh", "Gupta"});
            //data.put("4", new Object[]{3, "John", "Adwards"});
            //data.put("5", new Object[]{4, "Brian", "Schultz"});
            // Row row = sheet.createRow(rownum++);
            int rownum = i;
            int cellnum = 0;
            Row row = sheet.createRow(rownum++);
            Cell cell = row.createCell(cellnum++);
            if (obj instanceof String) {
                cell.setCellValue((String) obj);
            } else if (obj instanceof Integer) {
                cell.setCellValue((Integer) obj);
            }
        }
        try {
            String id = String.valueOf(System.currentTimeMillis());
            String DEST = "/bookinglist_excel" + "_" + id + ".xlsx";

            String root = Environment.getExternalStorageDirectory().toString() + "/ydbookingmanager";
            // final File dir1 = new File("");
            final File dir = new File(root);
            if (!dir.exists())
                dir.mkdirs();
            try {
                FileOutputStream out = new FileOutputStream(new File(dir.getPath() + DEST));
                workbook.write(out);
                out.close();
                Log.d("AJKKJAKJkjk", "23");
                System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");
                PrintDocument(dir.getPath() + DEST);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            //Write the workbook in file system

        } catch (Exception e) {
            e.printStackTrace();
        }
*/

      /*  for(int  i=0; i<bookingDetailsList.size(); i++){

            Row row = sheet.createRow(i);

            row.createCell(0).setCellValue(bookingDetailsList.get(i).getBookingId());
            row.createCell(1).setCellValue(bookingDetailsList.get(i).getYatriNm());
            row.createCell(2).setCellValue(bookingDetailsList.get(i).getBookingRoomsType());

        }
*/

        //Map<String, Object[]> data = new TreeMap<String, Object[]>();
        //  data.put("1", new Object[]{"Booking Id", "Yatri Name", "check in", "check out"});

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("bookings"); //Creating a sheet
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
            }else if (i == 12) {
                fieldNm = "Extra_mattress_status";
            }else if (i == 13) {
                fieldNm = "Extra_mattress_convenience";
            }else if (i == 14) {
                fieldNm = "Extra_mattress_label";
            }else if (i == 15) {
                fieldNm = "Extra_mattress_price";
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
            row1.createCell(12).setCellValue(bookingDetailsList.get(i).getExtra_mattress_status());
            row1.createCell(13).setCellValue(bookingDetailsList.get(i).getExtra_mattress_convenience());
            row1.createCell(14).setCellValue(bookingDetailsList.get(i).getExtra_mattress_label());
            row1.createCell(15).setCellValue(bookingDetailsList.get(i).getExtra_mattress_price());

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

        String foldername="ydbookingmanager";
        String filename="_bookings"+System.currentTimeMillis()+".xlsx";
        String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ File.separator+ foldername +File.separator+filename;
        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator+foldername);
        if (!file.exists())
        {
            file.mkdir();
        }
        FileOutputStream outputStream=null;
        try {
            outputStream=new FileOutputStream(path);
            workbook.write(outputStream);
            Snackbar snackbar = Snackbar.make(frameLayout, "Excel File Exported SuccessFully..!", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("View", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(path));
                    intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
//            Toast.makeText(getContext(), "Excel File Exported SuccessFully..\n"+path, Toast.LENGTH_SHORT).show();
                }
            });
            snackbar.show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Fail To Export...", Toast.LENGTH_SHORT).show();
            try {
                outputStream.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void printPDF(View view) {
        /*PrintManager printManager = (PrintManager) getActivity().getSystemService(PRINT_SERVICE);
        printManager.print("print_any_view_job_name", new ViewPrintAdapter(getActivity(),
                getActivity().findViewById(R.id.generatePDFFrame)), null);
*/

        try {
            createPdf(getActivity().findViewById(R.id.generatePDFFrame));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Fail To Download....!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                FrameLayout frameLayout = getActivity().findViewById(R.id.generatePDFFrame);
                printPDF(frameLayout);
                // do something
            } else {
                Log.d("AJKJKJKJAK", "3");
                Toast.makeText(getActivity(), "Please provide permission to access storage !", Toast.LENGTH_SHORT).show();
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Please provide permission to access storage!", Snackbar.LENGTH_LONG);

                // checkPermissions2();
            }
            // return;
        }
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.listBookings);
        btnClearFilter = view.findViewById(R.id.btnClearFilter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

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

    }

    @Override
    public void onStart() {
        Log.d("AJDGHADGDGH", "1");
        try {
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

    }


    @Subscribe
    public void onEvent(BookingListWithType list) {
        Log.d("onEvent", "() called with: list = [" + list + "]");


        if (bookingDetailsList.size() > 0) {
            Log.d("KADJKJAD", "1");
            bookingDetailsList.clear();
        }
        Log.d("JDKAKJJDKJ", list.getBookingDate() + "==");
        if (list.getBookingDate() != null) {
            bookingDetailsList.addAll(list.getModels());
            bookingDetailsListFiltered.addAll(list.getModels());

            //  Collections.reverse(bookingDetailsList);
            if (bookingDetailsList.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                linearLayoutenquiries.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                linearLayoutenquiries.setVisibility(View.VISIBLE);
            }
            if (bookinListAdapter != null) {
                bookinListAdapter.notifyDataSetChanged();
            } else {
                bookinListAdapter = new BookinListAdapter(bookingDetailsList, getActivity());
                recyclerView.setAdapter(bookinListAdapter);
            }
            String dt1 = list.getBookingDate();
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
            bookinListAdapter.notifyDataSetChanged();
            btnClearFilter.setVisibility(View.VISIBLE);
            if (bookingDetailsList.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                linearLayoutenquiries.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                linearLayoutenquiries.setVisibility(View.VISIBLE);
            }

        } else {
            bookingDetailsList.addAll(list.getModels());
            bookingDetailsListFiltered.addAll(list.getModels());

            //  Collections.reverse(bookingDetailsList);
            if (bookingDetailsList.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                linearLayoutenquiries.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                linearLayoutenquiries.setVisibility(View.VISIBLE);
            }
            if (bookinListAdapter != null) {
                bookinListAdapter.notifyDataSetChanged();
            } else {
                bookinListAdapter = new BookinListAdapter(bookingDetailsList, getActivity());
                recyclerView.setAdapter(bookinListAdapter);
            }
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

    public void createPdf(View view) throws FileNotFoundException {

//        String path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//        File file=new File(path,"Bookinglist"+System.currentTimeMillis()+".pdf");

//        String id = String.valueOf(System.currentTimeMillis());
//        String DEST = "/bookinglist" + "_" + id + ".pdf";
//        String root = Environment.getExternalStorageDirectory().toString() + "/ydbookingmanager";
//        // final File dir1 = new File("");
//        final File dir = new File(root);
//        if (!dir.exists())
//            dir.mkdirs();
//        try {
//            PrintDocument(dir.getPath() + DEST);
//        } catch (java.io.IOException e) {
//            e.printStackTrace();
//        }

        String foldername="ydbookingmanager";
        String filename="_Bookinglist"+System.currentTimeMillis()+".pdf";
        String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ File.separator+ foldername +File.separator+filename;
        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator+foldername);
        if (!file.exists())
        {
            file.mkdir();
        }
        OutputStream outputStream =null;
        try {
            outputStream =new FileOutputStream(path);
            PdfWriter writer =  new PdfWriter(path);
            PdfDocument pdfDocument=new PdfDocument(writer);
            Document document=new Document(pdfDocument);
            pdfDocument.setDefaultPageSize(PageSize.A4);
            document.setMargins(0,0,0,0);
            Drawable d=getDrawable(getContext(),R.drawable.logo);
            Bitmap bitmap=((BitmapDrawable)d).getBitmap();
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] bitmapdata=stream.toByteArray();
            ImageData imageData= ImageDataFactory.create(bitmapdata);
            Image image=new Image(imageData);
            image.setMargins(20f,200f,20,200f);
            com.itextpdf.layout.element.Paragraph paragraph=new com.itextpdf.layout.element.Paragraph("Yartadham").setBold().setFontSize(18).setTextAlignment(TextAlignment.LEFT);
            com.itextpdf.layout.element.Paragraph paragraph2=new com.itextpdf.layout.element.Paragraph("Report Generated By:"+sessionManager.getUserDetailsOb().getName()+ ", " + new Date()).setFontSize(10).setTextAlignment(TextAlignment.LEFT);
            com.itextpdf.layout.element.Paragraph paragraph3=new com.itextpdf.layout.element.Paragraph("Booking detail").setBold().setFontSize(18).setTextAlignment(TextAlignment.CENTER);
            paragraph3.setMarginTop(30f);
            float col[]={50f,50f,50f,50f,50f,50f,50f,50f,50f,50f,50f,50f};
            Table table=new Table(col);
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            table.addCell("S No");
            table.addCell("Check in/Check out");
            table.addCell("Nights");
            table.addCell("Roomtype");
            table.addCell("Rooms");
            table.addCell("Name");
            table.addCell("Guest");
            table.addCell("Booking Total");
            table.addCell("Tranfer Amount");
            table.addCell("Tranfer Date");
            table.addCell("Booking Id");
            table.addCell("Neft No.");

            double totalBooking = 0, totalTranfer = 0;

            for (int i=0;i<bookingDetailsList.size();i++)
            {
                table.addCell("" + i);
                table.addCell(bookingDetailsList.get(i).getCheckinDt() + "\n" +bookingDetailsList.get(i).getCheckoutDt());
                table.addCell(bookingDetailsList.get(i).getNights());
                table.addCell(bookingDetailsList.get(i).getRoomTyp());
                table.addCell(bookingDetailsList.get(i).getNoOfRooms());
                table.addCell(bookingDetailsList.get(i).getYatriNm());
                table.addCell(bookingDetailsList.get(i).getPerson());
                if (bookingDetailsList.get(i).getBookingTotal() != null && !bookingDetailsList.get(i).getBookingTotal().equals("null")) {
                    totalBooking = totalBooking + Double.parseDouble(bookingDetailsList.get(i).getBookingTotal());
                    table.addCell(bookingDetailsList.get(i).getBookingTotal());
                } else {
                    table.addCell("NA");
                }
                if (bookingDetailsList.get(i).getTranferAmt() != null && !bookingDetailsList.get(i).getTranferAmt().equals("null")) {
                    table.addCell(bookingDetailsList.get(i).getTranferAmt());
                    totalTranfer = totalTranfer + Double.parseDouble(bookingDetailsList.get(i).getTranferAmt());
                } else {
                    table.addCell("NA");
                }

                table.addCell(bookingDetailsList.get(i).getTranferDt());
                table.addCell(bookingDetailsList.get(i).getBookingId());
                table.addCell(bookingDetailsList.get(i).getNeftNo()).setFontSize(11f);
            }
            table.addCell(new com.itextpdf.layout.element.Cell(1,7).add(new com.itextpdf.layout.element.Paragraph("Total :  ")));
            table.addCell("" + totalBooking);
            table.addCell("" + totalTranfer);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            document.add(image);
            document.add(paragraph);
            document.add(paragraph2);
            document.add(paragraph3);
            document.add(table);
            document.close();
            Snackbar snackbar = Snackbar.make(frameLayout, "PDF file generated successfully.!", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("View", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(path));
                    intent.setDataAndType(uri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
//            Toast.makeText(getActivity(), "SuccessFully Pdf Downloaded...\n"+path, Toast.LENGTH_SHORT).show();
                }
            });
            snackbar.show();
        }catch (FileNotFoundException e) {
            Toast.makeText(getContext(), "Fail To download...", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private List<String> getDatesTranferDts(String dateString1, String dateString2) {
        Log.d("AKDHKHHHDJASHJ", dateString1 + "==>>" + dateString2);
        bookingDetailsListFilteredTranferDates.clear();
        ArrayList<Date> dates = new ArrayList<Date>();
        ArrayList<String> datesFormatetd = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");


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
            Log.d("JADJHJDHAJHAJ122", df1.format(cal1.getTime()) + "===>" + df1.format(cal2.getTime()));

            // dates.add(cal1.getTime());
            if (!dateString2.trim().equals(df1.format(cal1.getTime()).trim())) {
                datesFormatetd.add(df1.format(cal1.getTime()));

                Log.d("HAJHJHJHAJHA", df1.format(cal1.getTime()) + "==");
                for (BookingDetails bookingDetails : bookingDetailsListFiltered) {
                    String dt = bookingDetails.getTranferDt();
                    Log.d("HAJHJHJHAJHA123", df1.format(cal1.getTime()) + "==" + "===" + dt);
                    if (dt.equals(df1.format(cal1.getTime()))) {
                        bookingDetailsListFilteredTranferDates.add(bookingDetails);
                    } else {

                    }
                }
            }
            if (dateString2.trim().equals(df1.format(cal1.getTime()).trim())) {
                datesFormatetd.add(df1.format(cal1.getTime()));
                Log.d("HAJHJHJHAJHA", df1.format(cal1.getTime()) + "==");
                for (BookingDetails bookingDetails : bookingDetailsListFiltered) {
                    String dt = bookingDetails.getTranferDt();
                    Log.d("HAJHJHJHAJHA123", df1.format(cal1.getTime()) + "==" + "===" + dt);
                    if (dt.equals(df1.format(cal1.getTime()))) {
                        bookingDetailsListFilteredTranferDates.add(bookingDetails);
                    } else {

                    }
                }
            }
            cal1.add(Calendar.DATE, 1);
        }

        bookingDetailsList.clear();
        bookingDetailsList.addAll(bookingDetailsListFilteredTranferDates);
        bookinListAdapter.notifyDataSetChanged();
        btnClearFilter.setVisibility(View.VISIBLE);
        isFiltered = true;
        return datesFormatetd;
    }

    private List<String> getDatesBookedOnDts(String dateString1, String dateString2) {
        bookingDetailsListFilteredTranferDates.clear();
        ArrayList<Date> dates = new ArrayList<Date>();
        ArrayList<String> datesFormatetd = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");


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
                    String dt = bookingDetails.getBookingBookedOn();

                    String arr[] = dt.trim().split("\\s+");

                    if (arr[0] != null) {
                        Log.d("HAJHJHJHAJHA123", df1.format(cal1.getTime()) + "==" + "===" + arr[0]);
                        if (arr[0].equals(df1.format(cal1.getTime()))) {
                            bookingDetailsListFilteredTranferDates.add(bookingDetails);
                        }
                    }
                }
            }

            cal1.add(Calendar.DATE, 1);

        }
        bookingDetailsList.clear();
        bookingDetailsList.addAll(bookingDetailsListFilteredTranferDates);
        bookinListAdapter.notifyDataSetChanged();
        btnClearFilter.setVisibility(View.VISIBLE);
        isFiltered = true;
        return datesFormatetd;
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
}







     /*   searchView.setQueryHint("Search Here");

        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bookinListAdapter.getFilter().filter(newText);
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("SHDJHJHJSH", "closed");
                return false;
            }
        });*/
//    private void setHeaderCellStyle(XSSFSheet sheet, Cell cell) {
//        CellStyle s = null;
//
//        s = sheet.getWorkbook().createCellStyle();
//        cell.setCellStyle(s);
//        s.setBorderBottom(CellStyle.BORDER_THIN);
//        s.setBorderLeft(CellStyle.BORDER_THIN);
//        s.setBorderRight(CellStyle.BORDER_THIN);
//        s.setBorderTop(CellStyle.BORDER_THIN);
//        s.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        s.setAlignment(CellStyle.ALIGN_CENTER);
//
//    }
//    public void PrintDocument(String dest) throws IOException, java.io.IOException {
//        try {
//
////            Document document = new Document();
//            Document document = new Document(PageSize.LETTER, 10f, 10f, 10f, 0f);//PENGUIN_SMALL_PAPERBACK used to set the paper size
//            PdfWriter.getInstance(document, new FileOutputStream(dest));
//            document.open();
//            addMetaData(document);
//            addTitlePage(document);
//            if (PDFType == 5) {
//                Log.d("SLJDKJDSJKSJD", "1");
//                addContentToPDFTable(document);
//            } else {
//                Log.d("SLJDKJDSJKSJD", "2");
//
//                addContentToPDFTable(document);
//            }
//            document.close();
//            File file = new File(dest);
//
//            Bundle bundle1 = new Bundle();
//            bundle1.putString("user_detail", "By " + sessionManager.getUserDetailsOb().getUsername() + " at " + ConvertGMTtoIST.getCurrentDateTimeLastSeen());
//            mFirebaseAnalytics.logEvent("PDF_Generation", bundle1);
//
//            Snackbar snackbar = Snackbar.make(frameLayout, "PDF file generated successfully.!", Snackbar.LENGTH_INDEFINITE);
//            snackbar.setAction("View", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", file);
//                    intent.setDataAndType(uri, "application/pdf");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    startActivity(intent);
//
//                }
//            });
//            snackbar.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void addMetaData(Document document) {
//        document.addTitle("Booking List");
//        document.addSubject("Booking Details");
//        document.addKeywords("Java, PDF, Booking");
//        document.addAuthor("Maitri");
//        document.addCreator("Maitri");
//    }

//    private void addTitlePage(Document document)
//            throws DocumentException {
//
//        Paragraph preface = new Paragraph();
//        // We add one empty line
//        addEmptyLine(preface, 1);
//        // Lets write a big header
//
//        try {
//            // get input stream
//            InputStream ims = getActivity().getAssets().open("app_icon.png");
//            Bitmap bmp = BitmapFactory.decodeStream(ims);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            Image image = Image.getInstance(stream.toByteArray());
//
//            if (PDFType == 5) {
//                image.scaleAbsolute(60, 60);
//                image.setAlignment(Element.ALIGN_MIDDLE);
//            } else {
//                //image.setAlignment(Element.ALIGN_CENTER);
//                image.scaleAbsolute(60, 60);
//                image.setAlignment(Element.ALIGN_MIDDLE);
//            }
//            preface.add(image);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//
//        }
//        addEmptyLine(preface, 2);
//        // if (PDFType != 5) {
//        Paragraph paragraph = new Paragraph("Yatradham", catFont);
//        paragraph.setAlignment(Element.ALIGN_MIDDLE);
//        preface.add(paragraph);
//        // image.setAlignment(Element.ALIGN_MIDDLE );
//        //}
//        addEmptyLine(preface, 1);
//        // Will create: Report generated by: _name, _date
//        preface.add(new Paragraph(
//                "Report generated by: " + sessionManager.getUserDetailsOb().getName() + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//                smallBold));
//        addEmptyLine(preface, 3);
//     /*   preface.add(new Paragraph(
//                "This document describes something which is very important ",
//                smallBold));
//
//        addEmptyLine(preface, 8);
//*/
//       /* preface.add(new Paragraph(
//                "This document is a preliminary version and not subject to your license agreement or any other agreement with vogella.com ;-).",
//                redFont));
//*/
//
//        document.add(preface);
//        //        // Start a new page
////        if (PDFType != 5) {
////            //   document.newPage();
////        }
//    }

//    private void addContent(Document document) throws DocumentException {
//
//        Paragraph paragraph = new Paragraph("Booking List", catFont);
//
//
//        // Second parameter is the number of the chapter\
//        for (int i = 0; i < bookingDetailsList.size(); i++) {
//            BookingDetails bookingDetails = bookingDetailsList.get(i);
//
//            Paragraph subPara1 = new Paragraph("(" + i + ")", redFont);
//            subPara1.setAlignment(Element.ALIGN_CENTER);
//            document.add(subPara1);
//
//            Paragraph subPara = new Paragraph("Yatri Details", subFont);
//            subPara.setAlignment(Element.ALIGN_CENTER);
//            document.add(subPara);
//
//            Paragraph paragraph6 = new Paragraph();
//            addEmptyLine(paragraph6, 1);
//            document.add(paragraph6);
//
//            PdfPTable pdfPTable1 = new PdfPTable(2);
//            pdfPTable1.setWidthPercentage(100);
//
//            pdfPTable1.addCell(getPDFCell("Yatri Name"));
//            pdfPTable1.addCell(getPDFCell(bookingDetails.getYatriNm()));
//
//            pdfPTable1.addCell(getPDFCell("Address"));
//            pdfPTable1.addCell(getPDFCell(bookingDetails.getYatriAddress()));
//
//            pdfPTable1.addCell(getPDFCell(("Mobile Number")));
//            pdfPTable1.addCell(getPDFCell(bookingDetails.getYatriMobile()));
//            document.add(pdfPTable1);
//
//            Paragraph paragraph5 = new Paragraph();
//            addEmptyLine(paragraph5, 1);
//            document.add(paragraph5);
//
//            subPara = new Paragraph("Booking Details", subFont);
//            subPara.setAlignment(Element.ALIGN_CENTER);
//            document.add(subPara);
//            Paragraph parag8 = new Paragraph();
//            addEmptyLine(parag8, 1);
//            document.add(parag8);
//            PdfPTable pdfPTableBooking = new PdfPTable(2);
//            pdfPTableBooking.setWidthPercentage(100);
//            pdfPTableBooking.addCell(getPDFCell("Booking Id"));
//            pdfPTableBooking.addCell(getPDFCell(bookingDetails.getBookingId()));
//
//            pdfPTableBooking.addCell(getPDFCell("Room Type"));
//            pdfPTableBooking.addCell(getPDFCell(bookingDetails.getRoomTyp()));
//
//            pdfPTableBooking.addCell(getPDFCell("Contribution"));
//            pdfPTableBooking.addCell(getPDFCell(bookingDetails.getContribution()));
//
//            pdfPTableBooking.addCell(getPDFCell("Checkin"));
//            pdfPTableBooking.addCell(getPDFCell(bookingDetails.getCheckinDt()));
//
//            pdfPTableBooking.addCell(getPDFCell("Checkout"));
//            pdfPTableBooking.addCell(getPDFCell(bookingDetails.getCheckoutDt()));
//            document.add(pdfPTableBooking);
//
//            Paragraph paragraph1 = new Paragraph();
//            addEmptyLine(paragraph1, 1);
//            document.add(paragraph1);
//
//            PdfPTable pdfPTable = new PdfPTable(3);
//
//            PdfPCell c2 = new PdfPCell(new Phrase("Nights"));
//            //c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//            pdfPTable.addCell(c2);
//
//
//            c2 = new PdfPCell(new Phrase("Room"));
//            pdfPTable.addCell(c2);
//
//            c2 = new PdfPCell(new Phrase("Person"));
//            pdfPTable.addCell(c2);
//            pdfPTable.setHeaderRows(1);
//            pdfPTable.setHorizontalAlignment(Element.ALIGN_LEFT);
//
//            pdfPTable.addCell(bookingDetails.getNights());
//            pdfPTable.addCell(bookingDetails.getNoOfRooms());
//            pdfPTable.addCell(bookingDetails.getPerson());
//            document.add(pdfPTable);
//            Paragraph parag = new Paragraph();
//            addEmptyLine(parag, 1);
//            document.add(parag);
//
//            PdfPTable pdfPTableBookingTot = new PdfPTable(2);
//            pdfPTableBookingTot.setWidthPercentage(100);
//            pdfPTableBookingTot.addCell(getPDFCell("Booking Total"));
//            pdfPTableBookingTot.addCell(getPDFCell(bookingDetails.getBookingTotal()));
//            document.add(pdfPTableBookingTot);
//            Paragraph parag1 = new Paragraph();
//            addEmptyLine(parag1, 1);
//            document.add(parag1);
//            subPara = new Paragraph("Payment Details", subFont);
//            subPara.setAlignment(Element.ALIGN_CENTER);
//            document.add(subPara);
//            Paragraph parag9 = new Paragraph();
//            addEmptyLine(parag9, 1);
//            document.add(parag9);
//            PdfPTable pdfPTablePayment = new PdfPTable(2);
//            pdfPTablePayment.setWidthPercentage(100);
//            pdfPTablePayment.addCell(getPDFCell("Neft No."));
//            pdfPTablePayment.addCell(getPDFCell(bookingDetails.getNeftNo()));
//            pdfPTablePayment.addCell(getPDFCell("Tranfer Amount"));
//            pdfPTablePayment.addCell(getPDFCell(bookingDetails.getTranferAmt()));
//            pdfPTablePayment.addCell(getPDFCell("Tranfer Date"));
//            pdfPTablePayment.addCell(getPDFCell(bookingDetails.getTranferDt()));
//
//            document.add(pdfPTablePayment);
//
//            document.newPage();
//
//            // now add all this to the document
//            //   document.add(catPart);
//        }
//
//    }
//    public PdfPCell getPDFCell(String data) {
//        PdfPCell c1 = new PdfPCell(new Phrase(data));
//        c1.setBorderColor(BaseColor.WHITE);
//        return c1;
//    }
//
//    public PdfPCell getPDFCellBookedOn(String data) {
//        PdfPCell c1 = new PdfPCell(new Phrase(data));
//        c1.setBorderColor(BaseColor.GRAY);
//        c1.setPaddingTop(8);
//        c1.setPaddingBottom(8);
//        return c1;
//    }
//
//    public PdfPCell getPDFCellBookedOnTotal(String data) {
//
//
//        PdfPCell c1 = new PdfPCell(new Phrase(data));
//        c1.setBorderColor(BaseColor.GRAY);
//        c1.setPaddingTop(8);
//        c1.setPaddingBottom(8);
//        return c1;
//    }
//
//    public PdfPCell getPDFCellBookedOnTotalWithSpan(String data, int span, int align, int type) {
//        Font font = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
//        Phrase phrase = new Phrase(data);
//        phrase.setFont(font);
//
//        PdfPCell c1 = new PdfPCell(phrase);
//        c1.setBorderColor(BaseColor.BLACK);
//        c1.setHorizontalAlignment(align);
//        c1.setColspan(span);
//        c1.setPaddingTop(8);
//        c1.setPaddingBottom(8);
//        if (type == 1) {
//            c1.setPaddingRight(8);
//        }
//        return c1;
//    }
//    private void addContentToPDFTable(Document document) throws DocumentException {
//
//        //Paragraph paragraph = new Paragraph("Booking List", catFont);
//
//
//        Paragraph subPara = new Paragraph("", subFont);
//        subPara.setAlignment(Element.ALIGN_CENTER);
//        document.add(subPara);
//
//        Paragraph paragraph6 = new Paragraph();
//        addEmptyLine(paragraph6, 1);
//        document.add(paragraph6);
//
//        //  PdfPTable pdfPTable1 = new PdfPTable(2);
//        //pdfPTable1.setWidthPercentage(100);
//
//        subPara = new Paragraph("Booking Details", subFont);
//        subPara.setAlignment(Element.ALIGN_CENTER);
//        document.add(subPara);
//        Paragraph parag8 = new Paragraph();
//        addEmptyLine(parag8, 1);
//        document.add(parag8);
//
//        PdfPTable pdfPTableBooking = new PdfPTable(12);
//        pdfPTableBooking.setWidthPercentage(100);
//        pdfPTableBooking.setWidths(new int[]{1, 3, 1, 2, 1, 2, 1, 2, 2, 2, 2, 3});
//        pdfPTableBooking.addCell(getPDFCellBookedOn("S No"));
//        pdfPTableBooking.addCell(getPDFCellBookedOn("Check in/Check out"));
//        pdfPTableBooking.addCell(getPDFCellBookedOn("Nights"));
//        pdfPTableBooking.addCell(getPDFCellBookedOn("Roomtype"));
//        pdfPTableBooking.addCell(getPDFCellBookedOn("Rooms"));
//        pdfPTableBooking.addCell(getPDFCellBookedOn("Name"));
//        pdfPTableBooking.addCell(getPDFCellBookedOn("Guest"));
//        pdfPTableBooking.addCell(getPDFCellBookedOn("Booking Total"));
//        pdfPTableBooking.addCell(getPDFCellBookedOn("Neft No."));
//        pdfPTableBooking.addCell(getPDFCellBookedOn("Tranfer Amount"));
//
//        pdfPTableBooking.addCell(getPDFCellBookedOn("Tranfer Date"));
//        pdfPTableBooking.addCell(getPDFCellBookedOn("Booking Id"));
//
//
//        double totalBooking = 0, totalTranfer = 0;
//        // Second parameter is the number of the chapter\
//        for (int i = 0; i < bookingDetailsList.size(); i++) {
//            BookingDetails bookingDetails = bookingDetailsList.get(i);
//
//           /* Paragraph subPara1 = new Paragraph("(" + i + ")", redFont);
//            subPara1.setAlignment(Element.ALIGN_CENTER);
//            document.add(subPara1);
//            */
//
//            pdfPTableBooking.addCell(getPDFCellBookedOn("" + i));
//            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getCheckinDt() + "\n" + bookingDetails.getCheckoutDt()));
//            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getNights()));
//            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getRoomTyp()));
//            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getNoOfRooms()));
//            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getYatriNm()));
//            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getPerson()));
//
//
//            if (bookingDetails.getBookingTotal() != null && !bookingDetails.getBookingTotal().equals("null")) {
//                totalBooking = totalBooking + Double.parseDouble(bookingDetails.getBookingTotal());
//                pdfPTableBooking.addCell(getPDFCellBookedOnTotal(bookingDetails.getBookingTotal()));
//            } else {
//                pdfPTableBooking.addCell(getPDFCellBookedOn("NA"));
//            }
//            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getNeftNo()));
//
//            if (bookingDetails.getTranferAmt() != null && !bookingDetails.getTranferAmt().equals("null")) {
//                pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getTranferAmt()));
//                totalTranfer = totalTranfer + Double.parseDouble(bookingDetails.getTranferAmt());
//            } else {
//                pdfPTableBooking.addCell(getPDFCellBookedOn("NA"));
//            }
//
//            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getTranferDt()));
//            pdfPTableBooking.addCell(getPDFCellBookedOn(bookingDetails.getBookingId()));
//
//
//        }
//        Log.d("SDJJDKJSDKJDK123", "45");
//
//        pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("Total :  ", 7, Element.ALIGN_RIGHT, 1));
//        pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("" + totalBooking, 0, Element.ALIGN_LEFT, 2));
//        //  pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("", 0, Element.ALIGN_LEFT, 3));
//
//        pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("", 0, Element.ALIGN_LEFT, 3));
//        pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("" + totalTranfer, 0, Element.ALIGN_LEFT, 3));
//        pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("", 0, Element.ALIGN_LEFT, 3));
//        pdfPTableBooking.addCell(getPDFCellBookedOnTotalWithSpan("", 0, Element.ALIGN_LEFT, 3));
//
//      /*  c1.setRowspan(5);
//        c1.setBorderColor(BaseColor.GRAY);
//        pdfPTableBooking.addCell(c1);
//*/
//        //  PdfPCell c2 = new PdfPCell(new Phrase("Booking Total : "+totalBooking));
//
//        // c2.setBorderColor(BaseColor.GRAY);
//        //   pdfPTableBooking.addCell(c2);
//
//        document.add(pdfPTableBooking);
//        Paragraph parag1 = new Paragraph();
//        addEmptyLine(parag1, 1);
//        document.add(parag1);
//        //subPara = new Paragraph("Booking Total : "+total, subFont);
//        // subPara.setAlignment(Element.ALIGN_RIGHT);
//        // document.add(subPara);
//    }



