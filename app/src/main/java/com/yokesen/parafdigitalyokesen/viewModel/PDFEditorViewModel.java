package com.yokesen.parafdigitalyokesen.viewModel;

import android.app.Application;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import ir.vasl.magicalpec.utils.Core.MagicalPdfCore;
import ir.vasl.magicalpec.utils.Exceptions.MagicalException;


public class PDFEditorViewModel extends AndroidViewModel {

    public enum PECCoreStatusEnum {IDLE, PROCESSING, FAILED, SUCCESS}

    private MutableLiveData<PECCoreStatusEnum> pecCoreStatus;

    public PDFEditorViewModel(@NonNull Application application) {
        super(application);
        this.pecCoreStatus = new MutableLiveData<>();
        this.pecCoreStatus.postValue(PECCoreStatusEnum.IDLE);
    }

    public MutableLiveData<PECCoreStatusEnum> getPecCoreStatus() {
        return pecCoreStatus;
    }

    public void addOCG(PointF pointF, String filePath, int currPage, String referenceHash, byte[] OCGCover) {
        addOCG(pointF, filePath, currPage, referenceHash, OCGCover, 0, 0);
    }

    public void addOCG(PointF pointF, Uri uri, int currPage, String referenceHash, byte[] OCGCover) {
        addOCG(pointF, uri, currPage, referenceHash, OCGCover, 0, 0);
    }

    public void addOCG(PointF pointF, String filePath, int currPage, String referenceHash, byte[] OCGCover, float OCGWidth, float OCGHeight) {

        PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.PROCESSING);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                try {
                    MagicalPdfCore.getInstance().addOCG(pointF, filePath, currPage, referenceHash, OCGCover, OCGWidth, OCGHeight);
                    PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.SUCCESS);
                } catch (MagicalException e) {
                    PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.FAILED);
                    e.printStackTrace();
                }
            }
        });
    }

    public void addOCG(PointF pointF, Uri uri, int currPage, String referenceHash, byte[] OCGCover, float OCGWidth, float OCGHeight) {

        PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.PROCESSING);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                try {
                    MagicalPdfCore.getInstance().addOCG(getApplication(), pointF, uri, currPage, referenceHash, OCGCover, OCGWidth, OCGHeight);
                    PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.SUCCESS);
                } catch (MagicalException e) {
                    PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.FAILED);
                    e.printStackTrace();
                }
            }
        });
    }

    public void removeOCG(String filePath, String referenceHash) {

        PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.PROCESSING);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                try {
                    MagicalPdfCore.getInstance().removeOCG(filePath, referenceHash);
                    PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.SUCCESS);
                } catch (MagicalException e) {
                    PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.FAILED);
                    e.printStackTrace();
                }
            }
        });
    }

    public void removeOCG(Uri uri, String referenceHash) {

        PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.PROCESSING);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                try {
                    MagicalPdfCore.getInstance().removeOCG(getApplication(), uri, referenceHash);
                    PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.SUCCESS);
                } catch (MagicalException e) {
                    PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.FAILED);
                    e.printStackTrace();
                }
            }
        });
    }

    public void removeAllOCGs() {
        // under construction
    }

    public void updateOCG(PointF pointF, String filePath, int currPage, String referenceHash, byte[] newOCGCover) {
        PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.PROCESSING);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                try {
                    MagicalPdfCore.getInstance().updateOCG(pointF, filePath, currPage, referenceHash, newOCGCover);
                    PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.SUCCESS);
                } catch (MagicalException e) {
                    PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.FAILED);
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateOCG(PointF pointF, Uri uri, int currPage, String referenceHash, byte[] newOCGCover) {
        PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.PROCESSING);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                try {
                    MagicalPdfCore.getInstance().updateOCG(getApplication(), pointF, uri, currPage, referenceHash, newOCGCover);
                    PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.SUCCESS);
                } catch (MagicalException e) {
                    PDFEditorViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.FAILED);
                    e.printStackTrace();
                }
            }
        });
    }

}
