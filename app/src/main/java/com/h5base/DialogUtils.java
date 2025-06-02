package com.h5base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.h5gamebase.R;
import com.h5base.ads.AdManager;

public class DialogUtils {
    public static void showErrorDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false); // Ngăn người dùng tắt dialog bằng cách nhấn ra ngoài
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void showNoRewardVideoDialog(Context context) {
        // Tạo dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_no_reward);
        dialog.setCancelable(false);

        // Xử lý nút OK
        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Cài đặt kích thước dialog (tùy chọn)
        dialog.getWindow().setLayout(
                (int)(context.getResources().getDisplayMetrics().widthPixels * 0.85),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Hiển thị dialog
        dialog.show();
    }

    public static void showDialogConfirm(WebViewActivity activity, String quitText, String cancelText) {
        activity.showLoadingOverlay();
        // Tạo dialog
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_confirm); // Đảm bảo tên layout khớp với file XML của bạn
        dialog.setCancelable(false);

        // Tìm các nút trong dialog
        Button btnQuit = dialog.findViewById(R.id.btn_quit);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        // Đặt text cho các nút
        btnQuit.setText(quitText);
        btnCancel.setText(cancelText);

        // Xử lý nút Quit
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // Thoát activity khi nhấn Quit
                if (!AdManager.Is30Min()){
                    AdManager.showIntersAd(activity);
                }
                activity.finish();
            }
        });

        // Xử lý nút Cancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.hideLoadingOverlay();
            }
        });

        // Cài đặt kích thước dialog (tùy chọn)
        dialog.getWindow().setLayout(
                (int)(activity.getResources().getDisplayMetrics().widthPixels * 0.85),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Hiển thị dialog
        dialog.show();
    }

    public static void showDialogConfirm(WebViewActivity activity, String title, String message, String quitText, String cancelText) {
        activity.showLoadingOverlay();
        // Tạo dialog
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_confirm); // Đảm bảo tên layout khớp với file XML của bạn
        dialog.setCancelable(false);

        // Tìm các TextView và Button trong dialog
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        Button btnQuit = dialog.findViewById(R.id.btn_quit);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        // Đặt nội dung cho title và message
        tvTitle.setText(title);
        tvMessage.setText(message);

        // Đặt text cho các nút
        btnQuit.setText(quitText);
        btnCancel.setText(cancelText);

        // Xử lý nút Quit
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // Thoát activity khi nhấn Quit
                if (AdManager.Is30Min()){
                    AdManager.showIntersAd(activity);
                }
                activity.finish();
            }
        });

        // Xử lý nút Cancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.hideLoadingOverlay();
            }
        });

        // Cài đặt kích thước dialog (tùy chọn)
        dialog.getWindow().setLayout(
                (int)(activity.getResources().getDisplayMetrics().widthPixels * 0.85),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Hiển thị dialog
        dialog.show();
    }

    // Hàm mới cho dialog với 1 nút OK
    public static void showSimpleDialog(Activity activity, String title, String message, String okText) {
        if (!WebViewActivity.shouldShowDialog) return;

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_no_reward); // Sử dụng layout mới
        dialog.setCancelable(false);

        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        Button btnOk = dialog.findViewById(R.id.btn_ok);

        tvTitle.setText(title);
        tvMessage.setText(message);
        btnOk.setText(okText);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.hasShownNoInternetDialog = false;
                dialog.dismiss();
//                activity.finish(); // Thoát activity khi nhấn OK
            }
        });

        dialog.getWindow().setLayout(
                (int)(activity.getResources().getDisplayMetrics().widthPixels * 0.85),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );

        dialog.show();
    }
}
