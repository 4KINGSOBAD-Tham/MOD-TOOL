package com.thamsuwannarat.modtool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int PICK_FOLDER = 10;
    private static final int PICK_IMG = 11;
    private static final int PICK_PNG = 12;
    private Uri folder;
    private TextView folderLabel, info;
    private final int bg = Color.rgb(9, 13, 27), panel = Color.rgb(22, 30, 51);
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(bg);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(22, 32, 22, 28);
        scroll.addView(root);
        TextView logo = text("⚙", 42, Color.rgb(70, 192, 255), true);
        logo.setGravity(Gravity.CENTER); root.addView(logo);
        TextView title = text("MOD TOOL", 34, Color.WHITE, true);
        title.setGravity(Gravity.CENTER); root.addView(title);
        TextView author = text("Tham Suwannarat", 17, Color.rgb(190, 170, 255), false);
        author.setGravity(Gravity.CENTER); root.addView(author);
        spacer(root, 30);
        folderLabel = text("Game folder: Not selected", 14, Color.LTGRAY, false);
        root.addView(folderLabel);
        addButton(root, "SELECT GAME FOLDER", Color.rgb(34, 85, 157), () -> chooseFolder());
        spacer(root, 20);
        card(root, "IMG EDITOR", "Import DFF  •  Replace  •  Rebuild IMG", "OPEN IMG", Color.rgb(0, 130, 230), () -> chooseFile(PICK_IMG, "*/*"));
        card(root, "TXD EDITOR", "Import PNG  •  Replace Texture  •  Save Database", "IMPORT PNG", Color.rgb(124, 52, 221), () -> chooseFile(PICK_PNG, "image/png"));
        info = text("Prototype v0.1 — Files are not modified.", 13, Color.LTGRAY, false);
        info.setPadding(8, 16, 8, 16); root.addView(info);
        addButton(root, "BACKUP", Color.rgb(52, 65, 91), () -> message("Backup", "Backup support is planned. No game files have been changed."));
        addButton(root, "SAVE & INSTALL", Color.rgb(0, 110, 225), () -> message("Not available yet", "IMG and TXD writers require format verification. This prototype never modifies game files."));
        setContentView(scroll);
    }
    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }
    private void spacer(LinearLayout parent, int h) {
        View v = new View(this); parent.addView(v, new LinearLayout.LayoutParams(1, h));
    }
    private void addButton(LinearLayout parent, String title, int color, Runnable action) {
        Button b = new Button(this); b.setText(title); b.setTextColor(Color.WHITE); b.setAllCaps(false);
        GradientDrawable shape = new GradientDrawable(); shape.setColor(color); shape.setCornerRadius(20);
        b.setBackground(shape);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, 58); lp.topMargin = 12;
        parent.addView(b, lp); b.setOnClickListener(v -> action.run());
    }
    private void card(LinearLayout parent, String title, String description, String action, int accent, Runnable callback) {
        LinearLayout panelView = new LinearLayout(this); panelView.setOrientation(LinearLayout.VERTICAL);
        panelView.setPadding(18, 18, 18, 18);
        GradientDrawable shape = new GradientDrawable(); shape.setColor(panel); shape.setCornerRadius(22);
        shape.setStroke(2, accent); panelView.setBackground(shape);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2); lp.bottomMargin = 18;
        parent.addView(panelView, lp);
        panelView.addView(text(title, 24, Color.WHITE, true));
        panelView.addView(text(description, 13, Color.LTGRAY, false));
        addButton(panelView, action, accent, callback);
    }
    private void chooseFolder() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(i, PICK_FOLDER);
    }
    private void chooseFile(int code, String type) {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType(type); i.addCategory(Intent.CATEGORY_OPENABLE);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(i, code);
    }
    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (result != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        if (request == PICK_FOLDER) {
            folder = uri;
            try {
                int flags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uri, flags);
            } catch (SecurityException ignored) { }
            folderLabel.setText("Game folder: " + uri.getLastPathSegment());
            info.setText("Folder selected. No game files modified.");
        } else if (request == PICK_IMG || request == PICK_PNG) {
            info.setText("Selected: " + uri.getLastPathSegment() + "\nImport/export engine not yet implemented.");
        }
    }
    private void message(String title, String body) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(body).setPositiveButton("OK", null).show();
    }
}
