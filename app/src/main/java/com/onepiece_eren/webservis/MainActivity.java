package com.onepiece_eren.webservis;

import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    ListView liste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        liste = (ListView) findViewById(R.id.listView);
        WebServisiIleListeDoldur();

        //WebservisAsynTask task = new WebservisAsynTask(this);
        //task.execute("ttp://www.tcmb.gov.tr/kurlar/today.xml");

    }

    private void WebServisiIleListeDoldur() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String doviz_url="http://www.tcmb.gov.tr/kurlar/today.xml";
        List<String> doviz_list = new ArrayList<>();
        HttpURLConnection baglanti=null;

        try {
            URL url = new URL(doviz_url);
            baglanti = (HttpURLConnection) url.openConnection();
            int baglanti_durumu = baglanti.getResponseCode();

            if(baglanti_durumu==HttpURLConnection.HTTP_OK){
                BufferedInputStream stream = new BufferedInputStream(baglanti.getInputStream());
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
            }

        }catch (Exception e){

            Log.e("XML hatasi",e.getMessage().toString());

        }finally {
            if(baglanti!=null){
                baglanti.disconnect();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1,doviz_list);
        liste.setAdapter(adapter);
    }
}
