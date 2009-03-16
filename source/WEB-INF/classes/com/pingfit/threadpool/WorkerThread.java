package com.pingfit.threadpool;

/**
 * User: Joe Reger Jr
 * Date: Oct 28, 2006
 * Time: 7:19:41 AM
 */
class WorkerThread extends Thread {
 /**
  * True if this thread is currently processing.
  */
 public boolean busy;
 /**
  * The thread pool that this object belongs to.
  */
 public ThreadPool owner;

 /**
  * The constructor.
  *
  * @param o the thread pool
  */
 WorkerThread(ThreadPool o)
 {
  owner = o;
 }

 /**
  * Scan for and execute tasks.
  */
 public void run()
 {
  Runnable target = null;

  do {
   target = owner.getAssignment();
   if (target!=null) {
    target.run();
    owner.done.workerEnd();
   }
  } while (target!=null);
 }
}

