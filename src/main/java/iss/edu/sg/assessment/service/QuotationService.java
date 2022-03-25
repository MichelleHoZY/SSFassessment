package iss.edu.sg.assessment.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import iss.edu.sg.assessment.model.Quotation;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class QuotationService {
    
    public Optional<Quotation> getQuotations(List<String> items) throws IOException {

        JsonArrayBuilder arrList = Json.createArrayBuilder();
        for (String item : items) {
            arrList.add(item);
        }
        JsonArray arrayList = arrList.build();
        System.out.println(arrayList);

        RequestEntity<String> req = RequestEntity
            .post("https://quotation.chuklee.com/quotation")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(arrayList.toString());
            
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);

        try (InputStream is = new ByteArrayInputStream(resp.getBody().getBytes())) {
            JsonReader reader = Json.createReader(is);
            JsonObject jObject = reader.readObject();

            Quotation quotation = new Quotation();
            quotation.setQuoteId(jObject.getString("quoteId"));

            System.out.println(">>> SET QUOTEID: " + quotation.getQuoteId());


            JsonArray fruitOrder = jObject.getJsonArray("quotations");

            System.out.println(">>> FRUIT ORDER: " + fruitOrder);

            for (int i=0; i < fruitOrder.size(); i++) {
                JsonObject itemOrder = fruitOrder.getJsonObject(i);
                String itemFruit = itemOrder.getString("item");
                Double fruitPrice = itemOrder.getJsonNumber("unitPrice").doubleValue();
                Float price = fruitPrice.floatValue();
                quotation.addQuotation(itemFruit, price);
            }
            
            return Optional.of(quotation);

        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
