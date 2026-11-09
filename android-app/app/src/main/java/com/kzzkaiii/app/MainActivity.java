package com.kzzkaiii.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends Activity {
    private static final int SERVER_PORT = 8907;
    private static final String START_URL = "http://localhost:" + SERVER_PORT + "/";
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private WebView webView;
    private TextView statusView;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        configureWindow();
        showStatus("Preparando o KzzKaiii…");
        new Thread(() -> {
            try {
                File root = new File(getFilesDir(), "kzzkaiii");
                prepareSite(root);
                startPythonServer(root);
                waitForServer();
            } catch (Exception error) {
                showFatalError(error);
            }
        }, "kzzkaiii-bootstrap").start();
    }

    private void configureWindow() {
        Window window = getWindow();
        window.setStatusBarColor(Color.rgb(8, 10, 18));
        window.setNavigationBarColor(Color.rgb(8, 10, 18));
        window.getDecorView().setSystemUiVisibility(0);
    }

    private void showStatus(String message) {
        statusView = new TextView(this);
        statusView.setText(message);
        statusView.setTextColor(Color.rgb(243, 244, 255));
        statusView.setTextSize(16);
        statusView.setGravity(android.view.Gravity.CENTER);
        statusView.setBackgroundColor(Color.rgb(8, 10, 18));
        setContentView(statusView);
    }

    private void prepareSite(File root) throws IOException {
        File site = new File(root, "telechargement");
        new File(root, "cache").mkdirs();
        SharedPreferences prefs = getSharedPreferences("kzzkaiii", MODE_PRIVATE);
        long version = getInstalledVersion();
        if (prefs.getLong("site_version", -1) == version && new File(site, "index.html").isFile()) {
            return;
        }
        deleteTree(site);
        if (!copyAssetFolder("site", site)) {
            throw new IOException("Os arquivos do launcher não foram encontrados no APK.");
        }
        prefs.edit().putLong("site_version", version).apply();
        new File(root, "cache").mkdirs();
    }

    private long getInstalledVersion() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return Build.VERSION.SDK_INT >= 28 ? info.getLongVersionCode() : info.versionCode;
        } catch (Exception ignored) {
            return 1;
        }
    }

    private boolean copyAssetFolder(String assetPath, File destination) throws IOException {
        String[] children = getAssets().list(assetPath);
        if (children == null) return false;
        if (children.length == 0) {
            try (InputStream input = getAssets().open(assetPath)) {
                File parent = destination.getParentFile();
                if (parent != null) parent.mkdirs();
                try (FileOutputStream output = new FileOutputStream(destination)) {
                    byte[] buffer = new byte[32 * 1024];
                    int count;
                    while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
                }
                return true;
            } catch (FileNotFoundException directoryOrMissing) {
                return destination.mkdirs() || destination.isDirectory();
            }
        }
        destination.mkdirs();
        for (String child : children) {
            copyAssetFolder(assetPath + "/" + child, new File(destination, child));
        }
        return true;
    }

    private void deleteTree(File file) {
        if (file == null || !file.exists()) return;
        File[] children = file.listFiles();
        if (children != null) for (File child : children) deleteTree(child);
        file.delete();
    }

    private void startPythonServer(File root) {
        Thread serverThread = new Thread(() -> {
            try {
                if (!Python.isStarted()) Python.start(new AndroidPlatform(getApplicationContext()));
                PyObject bootstrap = Python.getInstance().getModule("android_bootstrap");
                bootstrap.callAttr("start_server", root.getAbsolutePath(), SERVER_PORT);
            } catch (Exception error) {
                showFatalError(error);
            }
        }, "kzzkaiii-local-server");
        serverThread.start();
    }

    private void waitForServer() {
        for (int attempt = 0; attempt < 100; attempt++) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("127.0.0.1", SERVER_PORT), 350);
                mainHandler.post(this::showLauncher);
                return;
            } catch (IOException ignored) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        showFatalError(new IOException("O servidor local demorou demais para iniciar."));
    }

    private void showLauncher() {
        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(8, 10, 18));
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(false);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage message) {
                android.util.Log.d("KzzKaiii-Web", message.message());
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                boolean playing = url.contains("/reborn/") || url.contains("/pogo/") || url.contains("/training/");
                setRequestedOrientation(playing
                    ? android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    : android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String host = uri.getHost();
                return host != null && !(host.equals("localhost") || host.equals("127.0.0.1"));
            }
        });
        setContentView(webView);
        webView.loadUrl(START_URL);
    }

    private void showFatalError(Exception error) {
        mainHandler.post(() -> {
            TextView message = new TextView(this);
            message.setText("Não foi possível iniciar o KzzKaiii.\n\n" +
                error.getClass().getSimpleName() + ": " + error.getMessage() +
                "\n\nConfira sua conexão e tente abrir o aplicativo novamente.");
            message.setTextColor(Color.rgb(243, 244, 255));
            message.setTextSize(14);
            message.setPadding(28, 28, 28, 28);
            message.setGravity(android.view.Gravity.CENTER);
            message.setBackgroundColor(Color.rgb(8, 10, 18));
            setContentView(message);
        });
    }

    @Override
    public void onBackPressed() {
        if (webView != null) {
            String url = webView.getUrl();
            if (url != null && (url.contains("/reborn/") || url.contains("/pogo/") || url.contains("/training/"))) {
                webView.evaluateJavascript("location.href='/'", null);
                return;
            }
            if (webView.canGoBack()) {
                webView.goBack();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
