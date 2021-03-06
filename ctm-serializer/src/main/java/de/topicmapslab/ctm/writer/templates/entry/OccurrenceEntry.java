/*
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Sven Krosse
 * @email krosse@informatik.uni-leipzig.de
 *
 */
package de.topicmapslab.ctm.writer.templates.entry;

import static de.topicmapslab.ctm.writer.utility.CTMTokens.WHITESPACE;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;

import de.topicmapslab.ctm.writer.core.CTMTopicMapWriter;
import de.topicmapslab.ctm.writer.core.serializer.OccurrenceSerializer;
import de.topicmapslab.ctm.writer.exception.SerializerException;
import de.topicmapslab.ctm.writer.templates.entry.base.ScopedEntry;
import de.topicmapslab.ctm.writer.templates.entry.param.IEntryParam;
import de.topicmapslab.ctm.writer.templates.entry.param.ParamFactory;
import de.topicmapslab.ctm.writer.templates.entry.param.TopicTypeParam;
import de.topicmapslab.ctm.writer.templates.entry.param.VariableParam;
import de.topicmapslab.ctm.writer.templates.entry.param.WildcardParam;
import de.topicmapslab.ctm.writer.utility.ICTMWriter;

/**
 * Class representing a template-entry definition of an occurrence-entry.
 * 
 * @author Sven Krosse
 * @email krosse@informatik.uni-leipzig.de
 * 
 */
public class OccurrenceEntry extends ScopedEntry {

	/**
	 * the parent topic map writer
	 */
	private final CTMTopicMapWriter writer;
	/**
	 * the occurrence type
	 */
	private final IEntryParam type;
	/**
	 * the data-type defined as {@link Topic} or {@link String}
	 */
	private final Object datatypeAsTopicOrString;

	/**
	 * constructor
	 * 
	 * @param writer
	 *            the parent topic map writer
	 * @param value
	 *            the value definition of the template-entry
	 * @param type
	 *            the type of the occurrence
	 */
	protected OccurrenceEntry(CTMTopicMapWriter writer, IEntryParam value,
			final IEntryParam type) {
		this(writer, value, type, null);
	}

	/**
	 * constructor
	 * 
	 * @param writer
	 *            the parent topic map writer
	 * @param value
	 *            the value definition of the template-entry
	 * @param type
	 *            the type of the occurrence
	 * @param datatypeAsTopicOrString
	 *            the data-type of the occurrence, given as a {@link Topic} or a
	 *            {@link String}
	 */
	protected OccurrenceEntry(CTMTopicMapWriter writer, IEntryParam value,
			final IEntryParam type, final Object datatypeAsTopicOrString) {
		super(value);
		this.writer = writer;
		this.datatypeAsTopicOrString = datatypeAsTopicOrString;
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	public void serialize(ICTMWriter buffer) throws SerializerException, IOException {

		String identifier = null;
		if (type instanceof TopicTypeParam) {
			identifier = writer.getCtmIdentity().getMainIdentifier(
					writer.getProperties(), ((TopicTypeParam) type).getTopic())
					.toString();
		} else if (type instanceof WildcardParam) {
			identifier = type.getCTMRepresentation();
		} else if (type instanceof VariableParam) {
			identifier = type.getCTMRepresentation();
		}

		OccurrenceSerializer.serialize(writer, getParameter()
				.getCTMRepresentation(), datatypeAsTopicOrString, identifier,
				buffer);
		if (getScopeEntry() != null) {
			buffer.append(WHITESPACE);
			getScopeEntry().serialize(buffer);
		}
		if (getReifierEntry() != null) {
			buffer.append(WHITESPACE);
			getReifierEntry().serialize(buffer);
		}
		buffer.appendTailLine();
	}

	// /**
	// * {@inheritDoc}
	// */
	// public boolean isAdaptiveFor(Topic topic) {
	// Set<Occurrence> occurrences = null;
	// if (type instanceof TopicTypeParam) {
	// occurrences = topic.getOccurrences(((TopicTypeParam) type)
	// .getTopic());
	// } else {
	// occurrences = topic.getOccurrences();
	// }
	// boolean result = false;
	//
	// if (datatypeAsTopicOrString == null) {
	// result = !occurrences.isEmpty();
	// } else {
	// for (Occurrence occurrence : occurrences) {
	// final String reference = writer.getCtmIdentity()
	// .getPrefixedIdentity(occurrence.getDatatype());
	// if (datatypeAsTopicOrString instanceof Topic
	// && datatypeAsTopicOrString.equals(occurrence
	// .getDatatype())) {
	// result = true;
	// } else if (datatypeAsTopicOrString.toString().equalsIgnoreCase(
	// reference)) {
	// result = true;
	// }
	//
	// if (result && getScopeEntry() != null) {
	// result = occurrence.getScope().containsAll(
	// Arrays.asList(getScopeEntry().getThemes()));
	// }
	//
	// if (result && getReifierEntry() != null) {
	// result = getReifierEntry().getReifier().equals(
	// occurrence.getReifier());
	// }
	//
	// if (result) {
	// break;
	// }
	// }
	// }
	//
	// return result;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public List<String> extractArguments(Topic topic,
	// Set<Object> affectedConstructs) throws SerializerException {
	// if (!isAdaptiveFor(topic)) {
	// throw new SerializerException(
	// "template entry is not adaptive for given topic.");
	// }
	//
	// List<String> arguments = new LinkedList<String>();
	// /*
	// * if value is a variable
	// */
	// if (getParameter() instanceof VariableParam) {
	// /*
	// * try to extract occurrence entity
	// */
	// Occurrence occurrence = tryToExtractOccurrenceEntity(topic);
	//
	// /*
	// * extract type
	// */
	// if (type instanceof VariableParam) {
	// arguments.add(writer.getCtmIdentity().getMainIdentifier(
	// writer.getProperties(), occurrence.getType())
	// .toString());
	// }
	//
	// /*
	// * extract values and transform by type
	// */
	// arguments.add(DatatypeAwareSerializer
	// .toArgument(writer, occurrence));
	//
	// /*
	// * add affected construct
	// */
	// affectedConstructs.add(occurrence);
	// }
	// /*
	// * if value is a constant
	// */
	// else {
	//
	// /*
	// * extract values and transform by type
	// */
	// arguments.add(DatatypeAwareSerializer.toArgument(getParameter()
	// .getCTMRepresentation()));
	//
	// /*
	// * try to extract occurrence entity
	// */
	// Occurrence occurrence = tryToExtractOccurrenceEntity(topic);
	//
	// /*
	// * extract type
	// */
	// if (type instanceof VariableParam) {
	// arguments.add(writer.getCtmIdentity().getMainIdentifier(
	// writer.getProperties(), occurrence.getType())
	// .toString());
	// }
	//
	// /*
	// * extract values and transform by type
	// */
	// arguments.add(DatatypeAwareSerializer
	// .toArgument(writer, occurrence));
	//
	// /*
	// * add affected construct
	// */
	// affectedConstructs.add(occurrence);
	// }
	// return arguments;
	// }
	//
	// /**
	// * Internal method to extract the occurrence from topic matching this
	// * instance of template-entry. Method checks the type information, the
	// * data-type, the scope and the reifier entry.
	// *
	// * @param topic
	// * the topic to extract the occurrence
	// * @return the extracted occurrence if exists and never <code>null</code>
	// *
	// * @throws SerializerException
	// * thrown if occurrence can not be extracted
	// */
	// private Occurrence tryToExtractOccurrenceEntity(final Topic topic)
	// throws SerializerException {
	// Set<Occurrence> occurrences = null;
	// if (type instanceof TopicTypeParam) {
	// occurrences = topic.getOccurrences(((TopicTypeParam) type)
	// .getTopic());
	// } else {
	// occurrences = topic.getOccurrences();
	// }
	//
	// for (Occurrence occurrence : occurrences) {
	// boolean isAdaptive = true;
	// final String reference = writer.getCtmIdentity()
	// .getPrefixedIdentity(occurrence.getDatatype());
	// if (datatypeAsTopicOrString instanceof Topic
	// && datatypeAsTopicOrString.equals(occurrence.getDatatype())) {
	// isAdaptive &= true;
	// } else if (datatypeAsTopicOrString.toString().equalsIgnoreCase(
	// reference)) {
	// isAdaptive &= true;
	// }
	// if (getScopeEntry() != null) {
	// isAdaptive &= getScopeEntry().isAdaptiveFor(occurrence);
	// }
	// if (getReifierEntry() != null) {
	// isAdaptive &= getReifierEntry().isAdaptiveFor(occurrence);
	// }
	// if (isAdaptive) {
	// return occurrence;
	// }
	// }
	// throw new SerializerException(
	// "template entry is not adaptive for given topic.");
	// }

	/**
	 * Static method to create a occurrence-entry by given occurrence construct
	 * of a topic. All information needed to define the entry are extracted from
	 * the given construct.
	 * 
	 * @param writer
	 *            the parent topic map writer
	 * @param occurrence
	 *            the occurrence construct
	 * @return the generate occurrence-entry
	 * @throws SerializerException
	 *             thrown if generation failed
	 */
	public static OccurrenceEntry buildFromConstruct(
			final CTMTopicMapWriter writer, final Occurrence occurrence)
			throws SerializerException {
		/*
		 * parameter factory
		 */
		ParamFactory factory = new ParamFactory();
		/*
		 * the type
		 */
		Topic type = occurrence.getType();
		/*
		 * generate variable name
		 */
		String variable = "occ";
		/*
		 * set CTM identity of data-type
		 */
		String datatype = writer.getCtmIdentity().getPrefixedIdentity(
				occurrence.getDatatype());
		/*
		 * generate scope entry if necessary
		 */
		ScopeEntry scopeEntry = null;
		if (!occurrence.getScope().isEmpty()) {
			List<IEntryParam> params = new LinkedList<IEntryParam>();
			for (Topic theme : occurrence.getScope()) {
				params.add(factory.newTopicTypeParam(theme));
			}
			scopeEntry = writer.getFactory().getEntryFactory().newScopeEntry(
					params.toArray(new IEntryParam[0]));
		}

		/*
		 * generate reifier entry if necessary
		 */
		ReifierEntry reifierEntry = null;
		if (occurrence.getReifier() != null) {
			reifierEntry = writer.getFactory().getEntryFactory()
					.newReifierEntry(
							factory.newTopicTypeParam(occurrence.getReifier()));
		}

		/*
		 * create new occurrence-entry
		 */
		OccurrenceEntry entry = new OccurrenceEntry(writer, factory
				.newVariableParam(variable), factory.newTopicTypeParam(type),
				datatype);
		entry.setReifierEntry(reifierEntry);
		entry.setScopeEntry(scopeEntry);
		return entry;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OccurrenceEntry) {
			/*
			 * value or variable name must be equal
			 */
			boolean result = super.equals(obj)
					&& type.equals(((OccurrenceEntry) obj).type);
			/*
			 * data-type must be equal
			 */
			if (datatypeAsTopicOrString != null) {
				result &= datatypeAsTopicOrString
						.equals(((OccurrenceEntry) obj).datatypeAsTopicOrString);
			} else {
				result &= ((OccurrenceEntry) obj).datatypeAsTopicOrString == null;
			}
			/*
			 * reifier must be equal
			 */
			if (getReifierEntry() != null) {
				result &= getReifierEntry().equals(
						((OccurrenceEntry) obj).getReifierEntry());
			} else {
				result &= ((OccurrenceEntry) obj).getReifierEntry() == null;
			}
			/*
			 * scope must be equal
			 */
			if (getScopeEntry() != null) {
				result &= getScopeEntry().equals(
						((OccurrenceEntry) obj).getScopeEntry());
			} else {
				result &= ((OccurrenceEntry) obj).getScopeEntry() == null;
			}
			return result;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getVariables() {
		List<String> variables = new LinkedList<String>();
		if (type instanceof VariableParam) {
			variables.add(type.getCTMRepresentation());
		}
		variables.addAll(super.getVariables());
		return variables;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
