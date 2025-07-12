package vn.fpt.qcmb_mobile.ui.store;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import vn.fpt.qcmb_mobile.R;

public class PaymentWebViewActivity extends AppCompatActivity {
    
    private WebView webView;
    private long orderCode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);
        
        // Lấy payment URL từ intent
        String paymentUrl = getIntent().getStringExtra("payment_url");
        orderCode = getIntent().getLongExtra("order_code", -1);
        
        if (paymentUrl == null) {
            Toast.makeText(this, "❌ Lỗi: Không có URL thanh toán", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        setupWebView();
        webView.loadUrl(paymentUrl);
        
        // Setup back button
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            onBackPressed();
        });
    }
    
    private void setupWebView() {
        webView = findViewById(R.id.webView);
        
        // Enable JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                
                // Kiểm tra nếu là callback URL
                if (isCallbackUrl(url)) {
                    handlePaymentCallback(url);
                }
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                
                // Kiểm tra nếu là localhost hoặc lỗi kết nối
                if (url.contains("localhost") || url.contains("ERR_CONNECTION_REFUSED")) {
                    // Có thể là callback thành công, kiểm tra trạng thái payment
                    checkPaymentStatusAndFinish();
                }
            }
            
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                
                // Nếu lỗi là do localhost không kết nối được, có thể là callback thành công
                if (failingUrl.contains("localhost") && errorCode == -2) {
                    checkPaymentStatusAndFinish();
                }
            }
        });
    }
    
    private boolean isCallbackUrl(String url) {
        // Kiểm tra các pattern callback phổ biến
        return url.contains("callback") || 
               url.contains("return") || 
               url.contains("success") || 
               url.contains("cancel") || 
               url.contains("fail") ||
               url.contains("localhost");
    }
    
    private void handlePaymentCallback(String url) {
        Uri uri = Uri.parse(url);
        
        // Kiểm tra các parameter callback
        String status = uri.getQueryParameter("status");
        String code = uri.getQueryParameter("code");
        
        if (status != null) {
            handlePaymentResult(status);
        } else if (code != null) {
            handlePaymentResult(code);
        } else {
            // Không có parameter rõ ràng, check với server
            checkPaymentStatusAndFinish();
        }
    }
    
    private void handlePaymentResult(String result) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("order_code", orderCode);
        
        switch (result.toLowerCase()) {
            case "success":
            case "paid":
            case "00":
                resultIntent.putExtra("payment_status", "success");
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(this, "✅ Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                break;
                
            case "cancel":
            case "cancelled":
            case "01":
                resultIntent.putExtra("payment_status", "cancelled");
                setResult(RESULT_CANCELED, resultIntent);
                Toast.makeText(this, "❌ Thanh toán đã bị hủy", Toast.LENGTH_SHORT).show();
                break;
                
            case "fail":
            case "failed":
            case "02":
                resultIntent.putExtra("payment_status", "failed");
                setResult(RESULT_CANCELED, resultIntent);
                Toast.makeText(this, "❌ Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                break;
                
            default:
                checkPaymentStatusAndFinish();
                return;
        }
        
        finish();
    }
    
    private void checkPaymentStatusAndFinish() {
        // Trả về kết quả để StoreActivity tự check với server
        Intent resultIntent = new Intent();
        resultIntent.putExtra("order_code", orderCode);
        resultIntent.putExtra("payment_status", "check_required");
        setResult(RESULT_OK, resultIntent);
        
        Toast.makeText(this, "🔄 Đang kiểm tra trạng thái thanh toán...", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // User muốn thoát, coi như hủy thanh toán
            Intent resultIntent = new Intent();
            resultIntent.putExtra("order_code", orderCode);
            resultIntent.putExtra("payment_status", "cancelled");
            setResult(RESULT_CANCELED, resultIntent);
            super.onBackPressed();
        }
    }
} 