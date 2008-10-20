
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class ThreadPool
{
  private static class ThreadPoolWorker extends Thread
  {
    private ThreadPool pool    = null;
    private boolean    running = true;
    
    public ThreadPoolWorker(ThreadPool pool)
    {
      this.pool = pool;
    }
    
    @Override
    public void run()
    {
      while(this.running)
      {
        try
        {  
          Runnable run = pool.getNextTask();
          run.run();
        }
        catch(Exception ex)
        {
          ex.printStackTrace();
        }
      }
    }
  }
  
  private BlockingQueue<Runnable> queue;
  private Thread[]                workers;
  
  public ThreadPool(int numWorkers)
  {
    this.workers = new Thread[numWorkers];
    
    // Creates a queue that contains five times more elements than workers
    this.queue = new ArrayBlockingQueue<Runnable>(numWorkers * 5);
    
    for(int n = 0; n < numWorkers; n++)
    {
      this.workers[n] = new ThreadPoolWorker(this);
      this.workers[n].start();
    }
  }
  
  public void addTask(Runnable run)
    throws InterruptedException
  {
    this.queue.put(run);
  }
  
  private Runnable getNextTask()
    throws InterruptedException
  {
    return this.queue.take();
  }
}
