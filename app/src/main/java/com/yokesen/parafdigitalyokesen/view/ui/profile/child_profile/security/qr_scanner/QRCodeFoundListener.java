package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.qr_scanner;



public interface QRCodeFoundListener {
        void onQRCodeFound(String qrCode);
        void qrCodeNotFound();
}

