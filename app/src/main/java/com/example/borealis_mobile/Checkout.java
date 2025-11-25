package com.example.borealis_mobile;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Checkout extends AppCompatActivity {

    private ListView listViewItems;
    private TextView tvTotalAmount;
    private Button btnPay;

    private Map<String, UserShopItemAdapter.CartItem> cart;
    private String customerId;

    private long totalCents = 0;

    APIKeys apiKeys = new APIKeys();
    PaymentSheet paymentSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        listViewItems  = findViewById(R.id.listViewCheckoutItems);
        tvTotalAmount  = findViewById(R.id.tvTotalAmount);
        btnPay         = findViewById(R.id.btnPay);

        PaymentConfiguration.init(this, apiKeys.getPublishableKey());
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        cart = (Map<String, UserShopItemAdapter.CartItem>)
                getIntent().getSerializableExtra("cartMap");

        customerId = getIntent().getStringExtra("customerId");

        ArrayList<String> lines = new ArrayList<>();
        totalCents = 0;

        for (UserShopItemAdapter.CartItem item : cart.values()) {

            lines.add(item.name + " x " + item.quantity + " ($" + item.price + ")");

            double itemTotal = item.price * item.quantity;
            totalCents += Math.round(itemTotal * 100);
        }

        listViewItems.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                lines
        ));

        tvTotalAmount.setText("$" + (totalCents / 100.0));

        btnPay.setOnClickListener(v -> getClientSecret());
    }
    private void getClientSecret() {

        String url = "https://api.stripe.com/v1/payment_intents";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String client_secret = obj.getString("client_secret");

                        paymentSheet.presentWithPaymentIntent(
                                client_secret,
                                new PaymentSheet.Configuration("Stripe"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + apiKeys.getSecretKey());
                return h;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("customer", customerId);
                p.put("amount", String.valueOf(totalCents));  // total real
                p.put("currency", "usd");
                p.put("automatic_payment_methods[enabled]", "true");
                return p;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void onPaymentResult(PaymentSheetResult result) {
        if (result instanceof PaymentSheetResult.Completed) {
            saveOrder();
        }
    }

    private void saveOrder() {

        String uid = FirebaseAuth.getInstance().getUid();
        String orderId = FirebaseDatabase.getInstance()
                .getReference("orders")
                .push()
                .getKey();

        Map<String,Object> itemsMap = new HashMap<>();

        for (UserShopItemAdapter.CartItem item : cart.values()) {

            Map<String,Object> m = new HashMap<>();
            m.put("name", item.name);
            m.put("quantity", item.quantity);
            m.put("price", item.price);  // precio unitario real

            itemsMap.put(item.productId, m);
        }

        Map<String,Object> order = new HashMap<>();
        order.put("orderId", orderId);
        order.put("userId", uid);
        order.put("timestamp", System.currentTimeMillis());
        order.put("totalAmountCents", totalCents); // total real
        order.put("items", itemsMap);

        FirebaseDatabase.getInstance()
                .getReference("orders")
                .child(orderId)
                .setValue(order);

        Toast.makeText(this, "Order Created Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
