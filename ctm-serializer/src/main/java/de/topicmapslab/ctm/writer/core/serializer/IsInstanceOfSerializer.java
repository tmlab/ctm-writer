/*
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Sven Krosse
 * @email krosse@informatik.uni-leipzig.de
 *
 */
package de.topicmapslab.ctm.writer.core.serializer;

import static de.topicmapslab.ctm.writer.utility.CTMTokens.ISA;
import static de.topicmapslab.ctm.writer.utility.CTMTokens.TABULATOR;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.ctm.writer.core.CTMTopicMapWriter;
import de.topicmapslab.ctm.writer.exception.SerializerException;
import de.topicmapslab.ctm.writer.utility.ICTMWriter;
import de.topicmapslab.identifier.TmdmSubjectIdentifier;

/**
 * Class to realize the serialization of the following CTM grammar rule. <br />
 * <br />
 * <code>instance-of ::= 'isa' topic-ref</code><br />
 * <br />
 * The serialized CTM string represents the type-instance-relation of a topic
 * within the topic block.
 * 
 * @author Sven Krosse
 * @email krosse@informatik.uni-leipzig.de
 * 
 */
public class IsInstanceOfSerializer implements ISerializer<Topic> {

	/**
	 * Method to convert the given construct to its specific CTM string. The
	 * result should be written to the given output buffer.
	 * 
	 * @param writer
	 *            the CTM writer
	 * @param affectedConstructs
	 *            the constructs affected by upper templates
	 * @param instance
	 *            the instance to serialize
	 * @param buffer
	 *            the output buffer
	 *            @param newLine indicates if the writer has to add a new line before firsr isa statement
	 * @return <code>true</code> if new content was written into buffer,
	 *         <code>false</code> otherwise
	 * @throws SerializerException
	 *             Thrown if serialization failed.
	 */
	public static boolean serialize(CTMTopicMapWriter writer,
			Set<Object> affectedConstructs, Topic instance, ICTMWriter buffer, boolean newLine)
			throws SerializerException, IOException {
		/*
		 * iterate over all types given known about getTypes
		 */
		boolean addTail = false;
		Set<Topic> types = instance.getTypes();
		for (Topic type : types) {
			/*
			 * add to buffer
			 */
			if (!affectedConstructs.contains(type)) {
				/*
				 * adding a new line after main identity
				 */
				if ( newLine ){
					buffer.appendLine();
					newLine = false;
				}
				if ( addTail ){
					buffer.appendTailLine();
					addTail = false;
				}
				// write the tabs before the rest to omit whitespace between tabs and "isa"
				buffer.append(TABULATOR);
				buffer.append(true, ISA, writer
						.getCtmIdentity().getMainIdentifier(
								writer.getProperties(), type).toString());
				addTail = true;
			}
		}

		// storing type already written
		Set<Topic> writtenTypes = new HashSet<Topic>(types);

		/*
		 * check additional types by extracting the TMDM association type
		 */
		TopicMap topicMap = instance.getTopicMap();

		/*
		 * get TMDM type-instance-association type
		 */
		Topic instanceOf = topicMap
				.getTopicBySubjectIdentifier(topicMap
						.createLocator(TmdmSubjectIdentifier.TMDM_TYPE_INSTANCE_ASSOCIATION_TYPE));

		if (instanceOf != null) {
			/*
			 * get TMDM instance-role type
			 */
			Topic instanceRole = topicMap
					.getTopicBySubjectIdentifier(topicMap
							.createLocator(TmdmSubjectIdentifier.TMDM_INSTANCE_ROLE_TYPE));
			/*
			 * get TMDM type-role type
			 */
			Topic typeRole = topicMap.getTopicBySubjectIdentifier(topicMap
					.createLocator(TmdmSubjectIdentifier.TMDM_TYPE_ROLE_TYPE));

			/*
			 * check if exists
			 */
			if (instanceRole == null || typeRole == null) {
				throw new SerializerException(
						new ModelConstraintException(instanceOf,
								"Invalid association item of type 'is-instance-of' - unexprected role types."));
			}

			/*
			 * iterate over association items
			 */
			for (Role role : instance.getRolesPlayed(instanceRole)) {
				Set<Role> typePlayers = role.getParent().getRoles(typeRole);
				if (typePlayers.size() != 1) {
					throw new SerializerException(
							new ModelConstraintException(
									role.getParent(),
									"Invalid association item of type 'is-instance-of' - expected number of players of role-type 'type' is 1, but was"
											+ typePlayers.size()));
				}
				/*
				 * add to buffer
				 */
				Topic newType = typePlayers.iterator().next().getPlayer();
				if (!writtenTypes.contains(newType)) {
					if ( addTail ){
						buffer.appendTailLine();
						addTail = false;
					}
					buffer
							.append(true, TABULATOR, ISA, writer
									.getCtmIdentity().getMainIdentifier(
											writer.getProperties(), newType)
									.toString());
					addTail = true;
				}
			}
		}
		return addTail;
	}

}
