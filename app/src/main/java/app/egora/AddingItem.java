package app.egora;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class AddingItem extends AppCompatActivity {



        //Deklaration
        private static final int pic_id = 123;
        ImageView click_image_id ;



        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_adding_item);

            //Zuweisung des ImageViews
            click_image_id = (ImageView)findViewById(R.id.item_imageView);
            click_image_id.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v)
                {

                    //Starten der Handykamera
                    Intent camera_intent
                            = new Intent(MediaStore
                            .ACTION_IMAGE_CAPTURE);


                    //Starten der Activität mit Rückgabe der BildID
                    startActivityForResult(camera_intent, pic_id);
                }
            });
        }

        // Übergabe des Bildes
        protected void onActivityResult(int requestCode,
                                        int resultCode,
                                        Intent data)
        {
            if (requestCode == pic_id) {

                Bitmap photo = (Bitmap)data.getExtras()
                        .get("data");
                click_image_id.setImageBitmap(photo);
            }
        }

}

