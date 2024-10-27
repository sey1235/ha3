package com.github.fhdo7100003.ha;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

abstract class Actor {
  protected abstract void handleMsg(Ref me, Object msg) throws UnhandledMessageException;

  protected boolean handlesShutdown() {
    return false;
  }

  static final class Ref {
    private final BlockingQueue<Object> mbox;
    private final Thread thread;

    private Ref(BlockingQueue<Object> mbox, Thread thread) {
      this.mbox = mbox;
      this.thread = thread;
    }

    private void assertAlive() {
      if (!thread.isAlive()) {
        throw new RuntimeException("Actor ded");
      }
    }

    public void tell(final Object msg) {
      assertAlive();
      try {
        mbox.put(msg);
      } catch (InterruptedException e) {
      }
    }

    public <Resp, Msg extends ReplyMessage<Resp>> Resp ask(final Msg msg) {
      assertAlive();
      try {
        mbox.put(msg);
        return msg.fut.join();
      } catch (InterruptedException e) {
      }

      return null;
    }

    public void shutdown() {
      tell(new Shutdown());
    }

    public void kill() {
      thread.interrupt();
    }
  }

  static Ref spawn(final Actor a, final int mboxCap) {
    final var mbox = new ArrayBlockingQueue<>(mboxCap);
    final var futureMe = new CompletableFuture<Ref>();
    final var thread = Thread.ofVirtual().start(() -> {
      final var me = futureMe.join();

      var active = true;
      while (active) {
        try {
          final var msg = mbox.take();
          if (msg instanceof Shutdown) {
            active = false;
            if (!a.handlesShutdown()) {
              break;
            }
          }
          a.handleMsg(me, msg);
        } catch (InterruptedException e) {
          active = false;
        } catch (UnhandledMessageException e) {
          // just log it idk
          System.out.println(e);
        }
      }
    });

    final var ret = new Ref(mbox, thread);
    futureMe.complete(ret);

    return ret;

  }

  class UnhandledMessageException extends Exception {
    public UnhandledMessageException(Class<?> c) {
      super(String.format("Can't handle message of type %s", c.toString()));
    }
  }

  class ShutdownError extends RuntimeException {
    public ShutdownError(String msg) {
      super(msg);
    }

    public ShutdownError(String msg, Throwable e) {
      super(msg, e);
    }
  }
}

record Shutdown() {
}

abstract class ReplyMessage<Resp> {
  CompletableFuture<Resp> fut = new CompletableFuture<>();

  void reply(final Resp o) {
    fut.complete(o);
  }
}
