package com.tdh7.documentscanner.ui.fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.chrisbanes.photoview.PhotoView;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.ldt.navigation.NavigationFragment;
import com.ldt.navigation.PresentStyle;
import com.tdh7.documentscanner.App;
import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.model.BitmapDocument;
import com.tdh7.documentscanner.model.RawBitmapDocument;
import com.tdh7.documentscanner.ui.MainActivity;
import com.tdh7.documentscanner.ui.dialog.LoadingScreenDialog;
import com.tdh7.documentscanner.util.ScanConstants;
import com.tdh7.documentscanner.util.ScanUtils;
import com.tdh7.documentscanner.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class EditorFragment extends BaseFragment {

    private View view;
    private View mStatusBar;
    private PhotoView scannedImageView;
    private View doneButton;
    private Bitmap original;
    private Button originalButton;
    private Button MagicColorButton;
    private Button grayModeButton;
    private Button bwButton;
    private Bitmap transformed;

    private Button rotateLeftButton;
    private Button rotateRightButton;
    private Button rotate360Button;
    private Object mArg;
    public static EditorFragment newInstance(RawBitmapDocument raw) {
        EditorFragment fragment = new EditorFragment();
        fragment.mArg = raw;
        return fragment;
    }

    public static EditorFragment newInstance(BitmapDocument bitmapDocument) {
        EditorFragment fragment = new EditorFragment();
        fragment.mArg = bitmapDocument;
        return fragment;
    }

    public EditorFragment() {
    }

    @Override
    public int contentLayout() {
        return R.layout.editor_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        getBitmapDocumentFromParameter();
    }

    private void getBitmapDocumentFromParameter() {
        if(mArg instanceof RawBitmapDocument) {
            // convert raw document to document
            setScannedDocument((RawBitmapDocument) mArg);
        } else if(mArg instanceof BitmapDocument) {
            // just display the bitmap
          setScannedImage(((BitmapDocument) mArg).mDocumentBitmap);
        }
        mArg = null;
    }
    LoadingScreenDialog mLoadingDialog = null;

    private void setScannedDocument(RawBitmapDocument document) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                view.post(()-> showLoading());

                    BitmapDocument bitmapDocument;
                    try {
                        bitmapDocument = ScanUtils.buildBitmapDocument(document);
                        view.post(() -> setScannedDocument(bitmapDocument));
                    } catch (Exception e) {
                        view.postDelayed(() -> {
                            Toasty.error(App.getInstance(),"Error when trying to crop the document").show();
                            dismiss();
                        },350);

                    }
                hideLoading();

            }
        });
    }

    private void hideLoading() {
        view.post(()-> {

            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        });
    }

    private void showLoading() {

        mLoadingDialog = LoadingScreenDialog.newInstance(getContext());
        mLoadingDialog.show(getChildFragmentManager(),"LoadingScreenDialog");
    }

    private void setScannedDocument(BitmapDocument document) {
        setScannedImage(((BitmapDocument) document).mDocumentBitmap);
    }

    private void init() {
        mStatusBar = view.findViewById(R.id.status_bar);
        scannedImageView = view.findViewById(R.id.scannedImage);
        scannedImageView.setMinimumScale(0.85f);
        originalButton = (Button) view.findViewById(R.id.original);
        originalButton.setOnClickListener(new OriginalButtonClickListener());
        MagicColorButton = (Button) view.findViewById(R.id.magicColor);
        MagicColorButton.setOnClickListener(new MagicColorButtonClickListener());
        grayModeButton = (Button) view.findViewById(R.id.grayMode);
        grayModeButton.setOnClickListener(new GrayButtonClickListener());
        bwButton = (Button) view.findViewById(R.id.BWMode);
        bwButton.setOnClickListener(new BWButtonClickListener());

        doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new DoneButtonClickListener());

        View cancelButton = view.findViewById(R.id.cancelButton2);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                original.recycle();
                dismiss();
            }
        });

        rotateLeftButton = (Button) view.findViewById(R.id.rotateLeftButton);
        rotateLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               executeRotate(-90);
            }
        });

        rotateRightButton = (Button) view.findViewById(R.id.rotateRightButton);
        rotateRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              executeRotate(90);
            }
        });

        rotate360Button = (Button) view.findViewById(R.id.rotate360Button);
        rotate360Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            executeRotate(180);
            }
        });
    }

    private void executeRotate(float angle) {
        if(transformed==null) transformed = original;
        rotateImageTo(angle);
        if(true) return;
        showProgressDialog("rotating...");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if(transformed==null)
                    transformed = Utils.rotateBitmap(original,angle);
                    else transformed = Utils.rotateBitmap(transformed,angle);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setImage(transformed, true);
                            dismissDialog();
                        }
                    });
                } catch (final OutOfMemoryError e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            transformed = original;
                            mCurrentRotate = angle;
                            setImage(transformed, true);
                            e.printStackTrace();
                            dismissDialog();
                        }
                    });
                }

            }
        });
    }
    private float mCurrentRotate = 0;

    private Bitmap getBitmap() {
        Uri uri = getUri();
        try {
            original = Utils.getBitmap(getActivity(), uri);
            getActivity().getContentResolver().delete(uri, null, null);
            return original;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getUri() {
        Uri uri = getArguments().getParcelable(ScanConstants.SCANNED_RESULT);
        return uri;
    }

    public void setScannedImage(Bitmap scannedImage) {
       setImage(scannedImage, true);
        transformed =  original = scannedImage;
    }

    private ValueAnimator mValueAnimator;
    private  void rotateImageTo(float angle) {
        if(mValueAnimator!=null&&mValueAnimator.isRunning()) return;
        if(angle!=mCurrentRotate) {
            mValueAnimator = ValueAnimator.ofFloat(mCurrentRotate,angle);
            mValueAnimator.setDuration(350);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    rotateImage((Float) animation.getAnimatedValue());
                }
            });
            mValueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mValueAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mValueAnimator.start();
        }
    }
    private void rotateImage(float angle) {
        mCurrentRotate = angle;
        scannedImageView.setScale(0.85f,false);
        scannedImageView.setRotationTo(mCurrentRotate);
    }

    private void setImage(Bitmap bitmap, boolean animate) {
        scannedImageView.setImageBitmap(bitmap);
        scannedImageView.setScale(0.85f,animate);
        scannedImageView.setRotationTo(mCurrentRotate);
    }

    String foname;

    private class DoneButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            foname = new SimpleDateFormat("dd MM yyyy (HH:mm:ss).pdf").format(new Date());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Nhập tên file:");
            final EditText iname = new EditText(getActivity());
            iname.setText(foname);
            iname.setSelectAllOnFocus(true);
            float oneDp = getResources().getDimension(R.dimen.dp_unit);
            int _8dp = (int) (oneDp*16);
            iname.setPadding(_8dp,_8dp,_8dp,_8dp);
            builder.setView(iname);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    foname = iname.getText().toString();

                    showProgressDialog(getResources().getString(R.string.loading));
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Intent data = new Intent();
                                Bitmap bitmap = transformed;
                                if (bitmap == null) {
                                    bitmap = original;
                                }
                                /*Uri uri = Utils.getUri(getActivity(), bitmap);
                                data.putExtra(ScanConstants.SCANNED_RESULT, uri);
                                getActivity().setResult(Activity.RESULT_OK, data);*/

                                //save pdf file
                                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                Image image = Image.getInstance(bytes.toByteArray());
                                Document document = new Document(new Rectangle(bitmap.getWidth(), bitmap.getHeight()), 0, 0, 0, 0);
                                File fo = new File(ScanConstants.PDF_PATH, foname + ".pdf");
                                if (!fo.getParentFile().exists()) {
                                    fo.getParentFile().mkdirs();
                                }
                                PdfWriter.getInstance(document, new FileOutputStream(fo));
                                document.open();

                                document.add(image);
                                document.close();
                                //end save

                                //open file with another program
                      /*          Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(fo), "application/pdf");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);*/
                                //end open

                                data.putExtra(ScanConstants.SCANNED_RESULT, fo.getName());

                                original.recycle();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toasty.success(getContext(),"Save successfully!").show();
                                        dismissDialog();
                                        if(getActivity() instanceof MainActivity)
                                            ((MainActivity)getActivity()).reloadSavedList();
                                        dismiss();
                                    }
                                });
                            } catch (Exception e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toasty.error(App.getInstance(),"Sorry, something went wrong!").show();
                                        dismissDialog();
                                        getActivity().finish();
                                    }
                                });
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });
            builder.show();
        }
    }

    @Override
    public boolean isWhiteTheme(boolean current) {
        return true;
    }

    private class BWButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            showProgressDialog(getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        transformed = ScanUtils.getBWBitmap(original);
                    } catch (final OutOfMemoryError e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                transformed = original;
                                setImage(original, false);
                                e.printStackTrace();
                                dismissDialog();
                                onClick(v);
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setImage(transformed, false);
                            dismissDialog();
                        }
                    });
                }
            });
        }
    }

    private class MagicColorButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            showProgressDialog(getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        transformed = ScanUtils.getMagicColorBitmap(original);
                    } catch (final OutOfMemoryError e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                transformed = original;
                                setImage(transformed, false);
                                e.printStackTrace();
                                dismissDialog();
                                onClick(v);
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setImage(transformed, false);
                            dismissDialog();
                        }
                    });
                }
            });
        }
    }

    private class OriginalButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                showProgressDialog(getResources().getString(R.string.applying_filter));
                transformed = original;
                setImage(transformed, false);
                dismissDialog();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                dismissDialog();
            }
        }
    }

    private class GrayButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            showProgressDialog(getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        transformed = ScanUtils.getGrayBitmap(original);
                    } catch (final OutOfMemoryError e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                transformed = original;
                                setImage(transformed, false);
                                e.printStackTrace();
                                dismissDialog();
                                onClick(v);
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setImage(transformed, false);
                            dismissDialog();
                        }
                    });
                }
            });
        }
    }

    protected synchronized void showProgressDialog(String message) {
        showLoading();
    }

    protected synchronized void dismissDialog() {
        hideLoading();
    }

    @Override
    public void onSetStatusBarMargin(int value) {
        mStatusBar.getLayoutParams().height = value;
    }

    @Override
    public int defaultTransition() {
        return PresentStyle.SLIDE_LEFT;
    }
}