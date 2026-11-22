package com.example.pipabnb.data;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.pipabnb.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class FotoUtils {
    private FotoUtils() {
    }

    public static String convertirABase64(ContentResolver resolver, Uri uri) throws IOException {
        Bitmap original;
        try (InputStream input = resolver.openInputStream(uri)) {
            original = BitmapFactory.decodeStream(input);
        }
        if (original == null) throw new IOException("No se pudo leer la imagen");

        int ancho = original.getWidth();
        int alto = original.getHeight();
        float escala = Math.min(1f, 800f / Math.max(ancho, alto));
        Bitmap reducida = escala < 1f
                ? Bitmap.createScaledBitmap(original, Math.round(ancho * escala),
                Math.round(alto * escala), true)
                : original;

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        reducida.compress(Bitmap.CompressFormat.JPEG, 65, salida);
        String base64 = Base64.encodeToString(salida.toByteArray(), Base64.NO_WRAP);

        if (reducida != original) reducida.recycle();
        original.recycle();
        return base64;
    }

    public static void mostrar(ImageView imageView, String foto) {
        if (foto == null || foto.trim().isEmpty()) {
            imageView.setImageResource(R.drawable.ic_launcher_background);
            return;
        }

        Object origen = foto;
        if (!foto.startsWith("http://") && !foto.startsWith("https://")) {
            try {
                origen = Base64.decode(foto, Base64.DEFAULT);
            } catch (IllegalArgumentException error) {
                imageView.setImageResource(R.drawable.ic_launcher_background);
                return;
            }
        }

        imageView.setVisibility(View.VISIBLE);
        Glide.with(imageView)
                .load(origen)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
    }
}
