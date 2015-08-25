package com.jadenine.circle.app;

import android.content.Context;

import com.jadenine.circle.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import timber.log.Timber;

/**
 * Created by linym on 8/25/15.
 */
class SSLSocketFactoryLoader {

    public static SSLSocketFactory getSocketFactory(Context context) {
        try {
            // loading CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream cert = context.getResources().openRawResource(R.raw.circle_public);
            Certificate ca;
            try {
                ca = cf.generateCertificate(cert);
            } finally { cert.close(); }

            // creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // creating a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (KeyStoreException e) {
            Timber.e(e, "SSL ERROR: KeyStoreException");
        } catch (IOException e) {
            Timber.e(e, "SSL ERROR: IOException");
        } catch (NoSuchAlgorithmException e) {
            Timber.e(e, "SSL ERROR: NoSuchAlgorithmException");
        } catch (CertificateException e) {
            Timber.e(e, "SSL ERROR: CertificateException");
        } catch (KeyManagementException e) {
            Timber.e(e, "SSL ERROR: KeyManagementException");
        }
        return null;
    }
}
