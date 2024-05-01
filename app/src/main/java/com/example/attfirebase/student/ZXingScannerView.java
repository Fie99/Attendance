package com.example.attfirebase.student;

import com.google.zxing.Result;

public class ZXingScannerView {
    public ZXingScannerView(CodeScanner codeScanner) {
    }

    public void startCamera() {
    }

    public void stopCamera() {
    }

    public void setResultHandler(CodeScanner codeScanner) {
    }

    public interface ResultHandler {
        void handleResult(Result result);
    }
}
