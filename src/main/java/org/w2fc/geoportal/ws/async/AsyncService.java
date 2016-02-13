package org.w2fc.geoportal.ws.async;

import org.w2fc.geoportal.config.ThreadTokens;

import java.util.UUID;

public class AsyncService {

    public String asyncExecute(Runnable runnable){
        String pid = UUID.randomUUID().toString();

        Thread thread = new Thread(new AsyncTask(runnable, pid));
        ThreadTokens.INSTANCE.copy(thread.getId());
        thread.start();

        return pid;
    }

    private static class AsyncTask implements Runnable
    {
        Runnable runnable;
        String pid;

        AsyncTask(Runnable runnable, String pid) {
            this.runnable = runnable;
            this.pid = pid;
        }

        @Override
        public void run() {
            try {
                SOAPProcessStatus.INSTANCE.put(pid, "in progress");

                runnable.run();

                SOAPProcessStatus.INSTANCE.put(pid, "success");
            } catch (Exception e) {
                SOAPProcessStatus.INSTANCE.put(pid, "error");

                throw new RuntimeException(e);
            }
        }
    }
}
