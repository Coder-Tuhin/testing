package view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.ventura.venturawealth.R;

import java.io.ByteArrayOutputStream;

import models.NotificationModel;
import utils.DateUtil;
import utils.GlobalClass;


public class WhatsAppImageShare {

    private float getPadding() {
        return 40;
    }
    private int getWidth() {
        return 720;
    }
    private int getHeight() {
        return 1080;
    }
    private int getGAP() {
        return 20;
    }
    private int getTextSize20() {
        return 35;
    }
    private int getTextSize25() {
        return 45;
    }
    private int getTextSize12() {
        return 25;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createAndShareImage(NotificationModel notification) {
        try {
            Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            canvas.drawColor(Color.rgb(0,26,51)); // Can add Custom Color

            canvas.save();
            float yP = getPadding()+getGAP();
            canvas.translate(getPadding(), yP);

            TextPaint textPaint = getTextPaint(getTextSize20(),Color.GRAY,true);
            String text = notification.getTitle();
            StaticLayout staticLayout = getStaticLayout(text,1,textPaint,0);
            staticLayout.draw(canvas);


            yP = 10+getGAP();//yP +staticLayout.getHeight()+getGAP();
            canvas.restore();
            canvas.save();
            canvas.translate((getWidth() - (getPadding()+86+150)), yP);

            Drawable d = GlobalClass.latestContext.getResources().getDrawable(R.mipmap.ventura_new, null);
            d.setBounds(0, 0, 86, 78);
            d.draw(canvas);
            canvas.restore();
            canvas.save();

            canvas.translate((getWidth() - (getPadding()+140)), yP + 20);

            textPaint = getTextPaint(getTextSize20(),Color.WHITE,true);
            text = "Ventura";
            staticLayout = getStaticLayout(text,1,textPaint,0);
            staticLayout.draw(canvas);
            canvas.restore();
            canvas.save();

            yP = getPadding() +staticLayout.getHeight()+3*getGAP();
            canvas.restore();
            canvas.save();
            canvas.translate(getPadding(), yP);

            textPaint = getTextPaint(getTextSize25(),Color.WHITE,true);
            text = notification.getMessage();
            staticLayout = getStaticLayout(text,10,textPaint,5);
            staticLayout.draw(canvas);

            yP = yP +staticLayout.getHeight()+getGAP();

            canvas.restore();
            canvas.save();
            canvas.translate(getPadding(), yP);

            textPaint = getTextPaint(getTextSize20(),Color.GRAY,true);
            text = DateUtil.DateForNotification(Long.parseLong(notification.getTime()));
            staticLayout = getStaticLayout(text,1,textPaint,0);
            staticLayout.draw(canvas);

            yP = yP +staticLayout.getHeight()+getGAP();
            canvas.restore();
            canvas.save();

            String shareText = "Check these news on Ventura Wealth.\n"+
                    "All the calls/opinions are subject to Disclosures and Disclaimer https://bit.ly/367NHL9.\n"+
                    "To download Ventura Wealth App Click http://bit.ly/1Lm6iSo.";

            shareWhatsApp(shareText,bm,GlobalClass.latestContext);
        }catch (Exception e){
            GlobalClass.onError("createAndShareImage",e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private StaticLayout getStaticLayout(String text, int maxLine, TextPaint textPaint, int lineSpace){
        Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
        float spacingMultiplier = 1;
        float spacingAddition = lineSpace;
        boolean includePadding = false;

        int text_width = (int) (getWidth()-(getPadding()*2));
        StaticLayout.Builder builder = StaticLayout.Builder.obtain(text, 0, text.length(), textPaint, text_width)
                .setAlignment(alignment)
                .setLineSpacing(spacingAddition, spacingMultiplier)
                .setEllipsize(TextUtils.TruncateAt.END)
                .setIncludePad(includePadding)
                .setMaxLines(maxLine);
        StaticLayout staticLayout = builder.build();
        return staticLayout;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private TextPaint getTextPaint(int size,int color, boolean isBold){
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(size);
        textPaint.setColor(color);
        textPaint.setElegantTextHeight(true);
        if(isBold) {
            textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        return textPaint;
    }

    private void shareWhatsApp(String text, Bitmap bitmap, Context context) {
        //PackageManager pm = context.getPackageManager();
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "IMG_"+DateUtil.getTimeDiffInSeconds(), null);
            Uri imageUri = Uri.parse(path);

            @SuppressWarnings("unused")
            //PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("image/*");
            //waIntent.setPackage("com.whatsapp");
            waIntent.putExtra(android.content.Intent.EXTRA_STREAM, imageUri);
            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Whatsapp not Installed", Toast.LENGTH_SHORT).show();
        }
    }
}
