package ru.mellman.conv3rter.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.IllegalBlockingModeException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NetworkUtils {
    public static boolean hasConnection(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return false;
        } else {
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(network);
            return actNw != null
                    && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        }
    }
    public static boolean pingInternetConnection() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> isOnlineTask = executorService.submit(new isInternetConnection());
        while (true){
            try {
                if (isOnlineTask.isDone()){
                    executorService.shutdownNow();
                    return isOnlineTask.get(2, TimeUnit.SECONDS);
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private static class isInternetConnection implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception {
            try {
                Socket socket = new Socket();
                SocketAddress address = new InetSocketAddress("8.8.8.8", 53);
                socket.connect(address, 1000);
                socket.close();
                return true;
            } catch (IOException| IllegalBlockingModeException e) {
                return false;
            }
        }
    }
}
