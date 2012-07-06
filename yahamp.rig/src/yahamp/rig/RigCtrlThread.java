/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import yahamp.rig.internal.Preferences;

/** Thread to periodically poll rig.
 *  @see RigListener
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RigCtrlThread extends Thread
{
    private static final int POLL_DELAY = 1000;

    /** Reference count for <code>instance</code>.
     *  Lock on <code>this</code>.
     */
    private static int references = 0;

    /** The shared, ref counted instance. */
    private static RigCtrlThread instance = null;

    /** Thread stops when <code>run==false</code>. */
    private boolean run = true;

    /** The rig.
     *  <p>
     *  When (re-)connecting, a new rig instance is created.
     *  To avoid thread issues, lock on <code>this</code> when accessing rig.
     */
    private RigCtrl rig;

    /** Registered listeners */
    final protected CopyOnWriteArrayList<RigListener> listeners =
        new CopyOnWriteArrayList<RigListener>();

    /** When set, the rig thread will try to program the rig to this freq. */
    private final AtomicReference<Double> set_freq = new AtomicReference<>();

    private RigModel model = null;

    /** @return The reference-counted instance
     *  @see #release()
     */
    public static synchronized RigCtrlThread getInstance()
    {
        if (instance == null)
        {
            instance = new RigCtrlThread();
            instance.start();
        }
        ++references;
        return instance;
    }

    /** Check for unrelease references */
    @Override
    protected void finalize() throws Throwable
    {
        if (references > 0)
            System.out.println("RigCtrlThread has " + references + " unreleased references");
        super.finalize();
    }

    /** Release the thread instance.
     *  <p>
     *  Thread ends when the last reference is released.
     */
    public synchronized void release()
    {
        --references;
        if (references > 0)
            return;
        run = false;
        synchronized (this)
        {
            notifyAll();
        }
        try
        {
            join();
        }
        catch (final Exception ex)
        {
            ex.printStackTrace();
        }
        instance = null;
    }

    /** @see #getInstance() */
    private RigCtrlThread()
    {
        super("RigCtrl");
    }

    /** Add a listener */
    final public void addListener(final RigListener listener)
    {
        listeners.add(listener);
    }

    /** Remove a listener */
    final public void removeListener(final RigListener listener)
    {
        listeners.remove(listener);
    }

    /** Select a (new) rig
     *
     *  <p>Rig is auto-detected on startup, but can be changed
     *  via this call.
     *
     *  @param model Model or <code>null</code> for 'no rig'
     *  @throws Exception
     */
    public synchronized void selectRig(final RigModel model) throws Exception
    {
        this.model = model;
        notifyAll();
    }

    /** 'main loop' of rig control thread */
    @Override
    public void run()
    {
        final String port = Preferences.getPort();
        if (port.length() < 1)
            return;
        final int rate = Preferences.getRate();

        // Auto-detect rig (but can be changed via selectRig)
        try
        {
        	model = RigCtrl.detectRig(port, rate);
        }
        catch (final Exception ex)
        {
            for (final RigListener listener : listeners)
                listener.rigError(ex);
        }

        while (run)
        {
            final RigModel connected = connect(port, rate);
            if (connected == null)
            {
                synchronized (this)
                {
                    try
                    {
                        wait(30000);
                    }
                    catch (final InterruptedException ex) {}
                    continue;
                }
            }
            handleRig(connected);
        }
    }

    /** Connect to rig
     *  @param port
     *  @param rate
     *  @return Model to which we connected or <code>null</code> on error
     */
    private synchronized RigModel connect(final String port, final int rate)
    {
        if (model == null)
            return null;
        try
        {
            rig = RigCtrl.getRig(model, port, rate);
        }
        catch (final Exception ex)
        {
            for (final RigListener listener : listeners)
                listener.rigError(ex);
            return null;
        }
        return model;
    }

    /** Poll rig.
     *  @param connected Model to which we connected
     *  Return when model changes, on error,
     *  or when <code>run</code> is cleared.
     */
    private void handleRig(final RigModel connected)
    {
        RigInfo info = null;
        try
        {
        	// Run until stopped or model is changed,
        	// which requires a re-connect
            while (run  &&  model == connected)
            {
                // After writing, better allow rig some time
                // because otherwise would get read timeouts
                // while rig handles the write
                if (! anythingToWrite())
                {
                    final RigInfo new_info = rig.poll();
                    if (new_info != null  &&
                        !new_info.equals(info))
                    {
                        info = new_info;
                        fireNewRigInfo(info);
                    }
                }
                Thread.sleep(POLL_DELAY);
            }
        }
        catch (final Exception ex)
        {   // Communication error: Show info
            for (final RigListener listener : listeners)
                listener.rigError(ex);
        }
        rig.close();
    }

    /** Check for pending write activity.
     *  For example, set rig freq. if a new freq. was requested.
     *  @return <code>true</code> if something was written
     */
    private boolean anythingToWrite() throws Exception
    {
        // Check if new freq was requested
        final Double to_set = set_freq.getAndSet(null);
        if (to_set == null)
            return false;
        rig.setFreq(to_set);
        return true;
    }

    /** Send rig info to listeners */
    private void fireNewRigInfo(final RigInfo info)
    {
        for (final RigListener listener : listeners)
            listener.newRigInfo(info);
    }

    /** Set freq.
     *  @param new_freq MHz
     */
    public void setFreq(final double new_freq)
    {
        set_freq.set(new_freq);
    }
}
