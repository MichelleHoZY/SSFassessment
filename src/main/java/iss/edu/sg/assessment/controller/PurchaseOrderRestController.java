package iss.edu.sg.assessment.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import iss.edu.sg.assessment.model.Quotation;
import iss.edu.sg.assessment.service.QuotationService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@RestController
@RequestMapping(path="/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class PurchaseOrderRestController {

    @Autowired
    private QuotationService qSvc;

    @PostMapping("/po")
    public ResponseEntity<String> requestHandler(@RequestBody String reqBody) throws IOException {

        System.out.println(reqBody);

        String name;
        String email;
        String address;
        Integer navID;
        Integer qty = 0;
        Float total = 0f;
        

        try(InputStream is = new ByteArrayInputStream(reqBody.getBytes())) {
            JsonReader reader = Json.createReader(is);
            JsonObject jObject = reader.readObject();
            name = jObject.getString("name");
            email = jObject.getString("email");
            address = jObject.getString("address");
            navID = jObject.getInt("navigationId");
            List<String> itemsList = new ArrayList<>();

            if (jObject.containsKey("lineItems")) {
                JsonArray orderArr = jObject.getJsonArray("lineItems");
                for (int i=0; i < orderArr.size(); i++) {
                    JsonObject itemOrder = orderArr.getJsonObject(i);
                    String itemFruit = itemOrder.getString("item");
                    qty = itemOrder.getInt("quantity");
                    itemsList.add(itemFruit);
                }
            }


            System.out.println(">>> Order details: " + name + ", " + email + ", " + address + ", " + itemsList + ", " + navID);
            Optional<Quotation> quotation = qSvc.getQuotations(itemsList);

            Quotation q = quotation.get();

            for (String i : itemsList) {
            total = total + (q.getQuotation(i) * qty); 
            }
            System.out.println(">>> TOTAL PRICE: " + total);

            JsonObject resultObj = Json.createObjectBuilder()
                .add("invoiceId", q.getQuoteId())
                .add("name", name)
                .add("total", total)
                .build();

            return ResponseEntity.ok().body(resultObj.toString());
        } catch (Exception ex) {
            JsonObject errResp = Json.createObjectBuilder()
                .add("error", "bad request")
                .build();

            return ResponseEntity.status(400).body(errResp.toString());
        }

    }
    
}
