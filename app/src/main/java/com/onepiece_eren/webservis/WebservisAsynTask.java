package com.onepiece_eren.webservis;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Teyfik on 25.10.2016.
 */
public class WebservisAsynTask extends AsyncTask <String ,String , List<String>> {

    private Context context;
    private ListView liste;
    private ProgressDialog progressDialog;

    public WebservisAsynTask(Context context){
        this.context = context;

        liste = (ListView) ((AppCompatActivity)context).findViewById(R.id.listView);
    }

    @Override
    protected void onPreExecute() {
        //UI Thread  içerisinde yürütülür . (Ana iş akışı içerisinde yürütülür)
        //ilk calışacak metoddur.
        //Task calışmadan önce yapılacak hazırlıklar burada yapıllır
        progressDialog = ProgressDialog.show(context,"Lütfen bekleyiniz... ", "İşlem yürütülüyor... ", true);
        // "başlık" , "acıklama"
    }

    @Override
    protected List<String> doInBackground(String... params) {
        //yardımcı Thread üzerinde çalışır geri kalanlar ana thread üzeinder calışır.
        //Overide edilmesi zorunlu
        //1. parametre buraya geliyor dizi olarak geliyor
        //arkaplan işlemleri burayada yapılır.

        List<String> doviz_list = new ArrayList<>();
        HttpURLConnection baglanti=null;

        try {
            URL url = new URL(params[0]);

            baglanti = (HttpURLConnection) url.openConnection();
            int baglanti_durumu = baglanti.getResponseCode();

            if(baglanti_durumu==HttpURLConnection.HTTP_OK){

                BufferedInputStream stream = new BufferedInputStream(baglanti.getInputStream());

                publishProgress("Döviz kurları okunuyor... "); //ProgresDialog güncellemesi için bilgi gönderdik

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                Document document=documentBuilder.parse(stream);

                // Listedeki aramayı her Currency arasında sınırlandırıyor
                NodeList dovizNodeList = document.getElementsByTagName("Currency");

                for (int i=0 ; i<dovizNodeList.getLength(); i++){

                    Element element = (Element) dovizNodeList.item(i);

                    NodeList nodelistBirim = element.getElementsByTagName("Unit");
                    NodeList nodelistParaBirim = element.getElementsByTagName("Isim");
                    NodeList nodelistAlis = element.getElementsByTagName("ForexBuying");
                    NodeList nodelistSatis = element.getElementsByTagName("ForexSelling");

                    String birim = nodelistBirim.item(0).getFirstChild().getNodeValue();
                    String parabirimi = nodelistParaBirim.item(0).getFirstChild().getNodeValue();
                    String alis = nodelistAlis.item(0).getFirstChild().getNodeValue();
                    String satis = nodelistSatis.item(0).getFirstChild().getNodeValue();

                    doviz_list.add(birim +" " + parabirimi+"   Alış: " +alis+"  Satış:  "+satis );
                }

                publishProgress("Liste güncelleniyor.... "); //progressDialog için bilgi gönderiliyor

            }

        }catch (Exception e){

            Log.e("Xml parse hatasi",e.getMessage().toString());

        }finally {
            if(baglanti!=null){
                baglanti.disconnect();
            }
        }

        return doviz_list;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<String> result) {

        // 3. parametre ne olacak ?
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_expandable_list_item_1, );
        //liste.setAdapter(adapter);
        //progressDialog.cancel();
    }


    @Override
    protected void onCancelled(List<String> strings) {
        super.onCancelled(strings);
    }
}
