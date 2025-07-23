package co.za.kasi.model.systemDTO;

import android.graphics.drawable.Drawable;

import co.za.kasi.model.DataObject;

public class ViewPagerDTO extends DataObject {

    String title;
    String context;
    Drawable illustration;

    public ViewPagerDTO(String title, String context, Drawable illustration) {
        this.title = title;
        this.context = context;
        this.illustration = illustration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Drawable getIllustration() {
        return illustration;
    }

    public void setIllustration(Drawable illustration) {
        this.illustration = illustration;
    }
}
