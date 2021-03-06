package org.w2fc.geoportal.ws.async;

import org.w2fc.geoportal.config.ThreadPids;
import org.w2fc.geoportal.config.ThreadTokens;
import org.w2fc.geoportal.ws.error.ErrorDesc;
import org.w2fc.geoportal.ws.error.ErrorsReport;

import java.util.List;
import java.util.UUID;

public class AsyncService {

    public String asyncExecute(Task runnable){
        String pid = UUID.randomUUID().toString();

        Thread thread = new Thread(new AsyncTask(runnable, pid));
        ThreadTokens.INSTANCE.copy(thread.getId());
        ThreadPids.INSTANCE.put(thread.getId(), pid);
        thread.start();

        return pid;
}

    private static class AsyncTask implements Runnable
    {
        Task runnable;
        String pid;

        AsyncTask(Task runnable, String pid) {
            this.runnable = runnable;
            this.pid = pid;
        }

        @Override
        public void run() {
            try {
                SOAPProcessStatus.INSTANCE.put(pid, "in progress");

                runnable.run();

                if (!runnable.getErrors().isEmpty())
                    SOAPProcessStatus.INSTANCE.put(pid, new ErrorsReport(runnable.getErrors()) );
                else
                    SOAPProcessStatus.INSTANCE.put(pid, "success");

            } catch (Exception e) {
                SOAPProcessStatus.INSTANCE.put(pid, "error");
            }
        }

    }


}
