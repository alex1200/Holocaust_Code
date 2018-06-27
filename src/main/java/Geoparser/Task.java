package Geoparser;// Java program to illustrate
// ThreadPool
import Commons.DB.DBConnection;
import InterviewParser.VHAInterviewParser;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Task class to be executed (Step 1)
class TaskThread implements Runnable
{
    private String name;
    private File file;

    public TaskThread(String s, File f)
    {
        name = s;
        file = f;
    }

    // Prints task name and sleeps for 1s
    // This Whole process is repeated 5 times
    public void run()
    {
        try
        {
            VHAInterviewParser parser = new VHAInterviewParser(file);
            parser.run();
        }

        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
public class Task
{
    // Maximum number of threads in thread pool
    static final int MAX_T = 10;

    public static void main(String[] args)
    {
        File dir = new File("VHA English transcripts");
        int skip = 0;
//        DBConnection.getInstance().createConnection("root", "", "127.0.0.1", 3306, "deepmap");
//        DBConnection.getInstance().openConnection();
//        Connection conn = DBConnection.getInstance().getConn();

//        PreparedStatement stmt = null;
//        try {
//            stmt = conn.prepareStatement("DELETE FROM deepmap.text_word_location WHERE latitude=0.0 AND longitude=0.0 AND confidence = 0");
//            stmt.execute();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }




        ExecutorService pool = Executors.newFixedThreadPool(MAX_T);
        for(File file: dir.listFiles()) {
            skip++;
            Runnable r1 = new TaskThread("task "+skip, file);
            pool.execute(r1);
        }
        pool.shutdown();
    }
}