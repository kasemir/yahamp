/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
// JAXB Magic
//
// QRZDatabase uses xmlns with no prefix:
// <QRZDatabase version="1.18" xmlns="http://xmldata.qrz.com">
//
// JAXB would by default use a prefix like "ns2":
// <ns2:QRZDatabase version="1.18" xmlns:ns2="http://xmldata.qrz.com">
//
// The XmlSchema sets the prefix to ""
@XmlSchema(namespace = "http://xmldata.qrz.com",
    xmlns = {
        @XmlNs(namespaceURI = "http://xmldata.qrz.com", prefix = "")
    },
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)

package yahamp.qrz.internal;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;

