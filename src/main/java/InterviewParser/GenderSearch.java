package InterviewParser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Alexander on 9/26/2017.
 */
public class GenderSearch {
    public GenderSearch(){

    }

    public String run(String name){
        try {

            String myKey = "BAfxgwwbAMsjRzTqRg";

            URL url = new URL("https://gender-api.com/get?key=" + myKey + "&name=" + name);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();



            if (conn.getResponseCode() != 200) {

                throw new RuntimeException("Error: " + conn.getResponseCode());

            }



            InputStreamReader input = new InputStreamReader(conn.getInputStream());

            BufferedReader reader = new BufferedReader(input);



            Gson gson = new Gson();

            JsonObject json = gson.fromJson(reader, JsonObject.class);

            String gender = json.get("gender").getAsString();

            System.out.println("Gender: " + gender); // Gender: male

            conn.disconnect();

            return gender;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }
    }
}
