/*
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Sven Krosse
 * @email krosse@informatik.uni-leipzig.de
 *
 */
package de.topicmapslab.ctm.writer.core.serializer;

import static de.topicmapslab.ctm.writer.utility.CTMTokens.COLON;
import static de.topicmapslab.ctm.writer.utility.CTMTokens.TABULATOR;
import static de.topicmapslab.ctm.writer.utility.CTMTokens.WHITESPACE;

import org.tmapi.core.Occurrence;

import de.topicmapslab.ctm.writer.core.CTMTopicMapWriter;
import de.topicmapslab.ctm.writer.exception.SerializerException;
import de.topicmapslab.ctm.writer.utility.CTMBuffer;

/**
 * Class to realize the serialization of the following CTM grammar rule. <br />
 * <br />
 * <code>occurrence ::= '-'  type ':' literal scope?  reifier?  </code><br />
 * <br />
 * The serialized CTM string represents the occurrence of a topic within the
 * topic block.
 * 
 * @author Sven Krosse
 * @email krosse@informatik.uni-leipzig.de
 * 
 */
public class OccurrenceSerializer implements ISerializer<Occurrence> {

	/**
	 * Method to convert the given construct to its specific CTM string. The
	 * result should be written to the given output buffer.
	 * 
	 * @param writer
	 *            the CTM writer
	 * @param occurrence
	 *            the occurrence to serialize
	 * @param buffer
	 *            the output buffer
	 * @return <code>true</code> if new content was written into buffer,
	 *         <code>false</code> otherwise
	 * @throws SerializerException
	 *             Thrown if serialization failed.
	 */
	public static boolean serialize(CTMTopicMapWriter writer,
			Occurrence occurrence, CTMBuffer buffer) throws SerializerException {

		/*
		 * begin occurrence-definition block
		 */
		buffer.append(true, TABULATOR,
				writer.getCtmIdentity().getMainIdentifier(
						writer.getProperties(), occurrence.getType())
						.toString(), COLON, WHITESPACE);

		/*
		 * add value and data-type
		 */
		DatatypeAwareSerializer.serialize(writer, occurrence, buffer);

		CTMBuffer ctmBuffer = null;
		/*
		 * add scope if exists
		 */
		ctmBuffer = new CTMBuffer();
		if (ScopedSerializer.serialize(writer, occurrence, ctmBuffer)) {
			buffer.append(WHITESPACE);
			buffer.append(ctmBuffer);
		}

		/*
		 * add reifier if exists
		 */
		ctmBuffer = new CTMBuffer();
		if (ReifiableSerializer.serialize(writer, occurrence, ctmBuffer)) {
			buffer.append(WHITESPACE);
			buffer.append(ctmBuffer);
		}

		buffer.appendTailLine();
		return true;
	}

	/**
	 * Static method to generate CTM occurrence-block by value, data-type and
	 * type.
	 * 
	 * @param writer
	 *            the parent topic map writer
	 * @param value
	 *            the value of the occurrence
	 * @param datatype
	 *            the data-type of the occurrence
	 * @param type
	 *            the string-representation of the type of the occurrence
	 * @param buffer
	 *            the buffer written to
	 * @return <code>true</code> if new content was written into buffer,
	 *         <code>false</code> otherwise.
	 * @throws SerializerException
	 *             Thrown if serialization failed.
	 */
	public static boolean serialize(CTMTopicMapWriter writer, String value,
			Object datatype, final String type, CTMBuffer buffer)
			throws SerializerException {

		buffer.append(true, TABULATOR, type, COLON, WHITESPACE);

		/*
		 * add value and data-type
		 */
		DatatypeAwareSerializer.serialize(writer, datatype, value, buffer);

		return true;
	}

}
