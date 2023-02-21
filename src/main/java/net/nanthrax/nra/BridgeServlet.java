package net.nanthrax.nra;

import javax.net.ssl.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

public class BridgeServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String uri = request.getRequestURI();
        String id = uri.substring("/nra/".length());
        StringBuilder builder = new StringBuilder();

        try {
            KeyStore clientStore = KeyStore.getInstance("JKS");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientStore, "rna".toCharArray());
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream("opt/karaf/etc/keystores/RNAclient-keystore.jks"), "rnarna".toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            URL url = new URL("https://sir.qualification.ines-cds.interieur.rie.gouv.fr:444");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Token", "5bab301f-c1fb-4dea-880c-b2896c7e56de5bab301f-c1fb-4dea-880c-b2896c7e56de");
            connection.setRequestProperty("SOAPAction", "urn:rnaService#getDossier");
            connection.setRequestProperty("Content-Type", "application/xml");
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
                writer.write("<soapenv:Envelope\n" +
                        "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                        "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                        "    xmlns:urn=\"urn:rnaService\">\n" +
                        "    <soapenv:Header/>\n" +
                        "    <soapenv:Body>\n" +
                        "       <urn:getDossier soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                        "          <numeroRna xsi:type=\"urn:typeNumeroRNA\"\n" +
                        "             xmlns:urn=\"urn:rnaWebService\">\n" + id + "\n" +
                        "          </numeroRna>\n" +
                        "         <application xsi:type=\"xsd:string\">dgme_vca</application>\n" +
                        "       </urn:getDossier>\n" +
                        "    </soapenv:Body>\n" +
                        " </soapenv:Envelope>");
            }
            connection.connect();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
            }
        } catch (Exception e) {
            throw new IOException("Can't request remote URL", e);
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()))) {
            writer.write(builder.toString());
        }
    }

}
