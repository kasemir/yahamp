/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model;

/** Information about one QSO: Time, callsign, ...
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class QSO extends Callsign
{
    private int number = -1;
    private UTC utc = new UTC();
    private String freq = "",
        rst_sent = "", rst_rcvd = "", mode = "", category = "", info = "", qsl = "";

    /** Initialize
     *  @param callsign  {@link Callsign}
     */
    public QSO(final Callsign callsign)
    {
        this(callsign.getCall());
    }

    /** Initialize
     *  @param callsign Call sign
     */
    public QSO(final String callsign)
    {
        super(callsign);
    }

    /** @return {@link Callsign} for call in this QSO
     *  @see QSO#getCall()
     */
    public Callsign getCallsign()
    {
        return new Callsign(call);
    }

    /** @return Number of QSO in list of currently inspected QSOs,
     *          for example selected by {@link Category}
     */
    public int getNumber()
    {
        return number;
    }

    /** @return Time of QSO */
    public UTC getUTC()
    {
        return utc;
    }

    /** @return Frequency (MHz) */
    public String getFreq()
    {
        return freq;
    }

    /** @return RST sent */
    public String getRstSent()
    {
        return rst_sent;
    }

    /** @return RST received */
    public String getRstRcvd()
    {
        return rst_rcvd;
    }

    /** @return Operating mode (CW, ...) */
    public String getMode()
    {
        return mode;
    }

    /** @return QSL sent, received, both? */
    public String getQsl()
    {
        return qsl;
    }

    /** @return {@link Category} */
    public String getCategory()
    {
        return category;
    }

    /** @return Arbitrary QSO info */
    public String getInfo()
    {
        return info;
    }

    /** @param number QSO sequence number in list of QSOs */
    public void setNumber(final int number)
    {
        this.number = number;
    }

    /** @param utc Time of QSO */
    public void setUTC(final UTC utc)
    {
        this.utc = utc;
    }

    /** @param utc Time of QSO */
    public void setFreq(final String freq)
    {
        this.freq = freq;
    }

    /** @param rst_sent RST sent */
    public void setRstSent(final String rst_sent)
    {
        this.rst_sent = rst_sent.trim().toUpperCase();
    }

    /** @param rst_rcvd RST received */
    public void setRstRcvd(final String rst_rcvd)
    {
        this.rst_rcvd = rst_rcvd.trim().toUpperCase();
    }

    /** @param mode Operating mode */
    public void setMode(final String mode)
    {
        this.mode = mode.trim().toUpperCase();
    }

    /** @param category Category */
    public void setCategory(final String category)
    {
        this.category = category;
    }

    /** @param info Arbitrary QSO info */
    public void setInfo(final String info)
    {
        this.info = info;
    }

    /** @param qsl QSO sent, received, both? */
    public void setQsl(final String qsl)
    {
        this.qsl = qsl;
    }

    /** QSOs are identified by their UTC time
     *  @return Hash code of the QSO's time
     */
    @Override
    public int hashCode()
    {
        return utc.hashCode();
    }

    /** QSOs are identified by their UTC time
     *  @param obj Other object
     *  @return <code>true</code> if other object is QSO with same time
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof QSO))
            return false;
        final QSO other = (QSO) obj;
        return other.utc.equals(utc);
    }

    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("QSO ");
        buf.append(utc);
        buf.append(": ");
        buf.append(call);
        buf.append(", freq=");
        buf.append(freq);
        buf.append(", mode=");
        buf.append(mode);
        buf.append(", rst_rcvd=");
        buf.append(rst_rcvd);
        buf.append(", rst_sent=");
        buf.append(rst_sent);
        buf.append(", qsl=");
        buf.append(qsl);
        buf.append(", info=");
        buf.append(info);
        return buf.toString();
    }
}
