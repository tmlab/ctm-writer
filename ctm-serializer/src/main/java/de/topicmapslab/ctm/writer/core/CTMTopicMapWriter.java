/*
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Sven Krosse
 * @email krosse@informatik.uni-leipzig.de
 *
 */
package de.topicmapslab.ctm.writer.core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.tmapi.core.Construct;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;
import org.tmapix.io.TopicMapWriter;

import de.topicmapslab.ctm.writer.core.serializer.TopicMapSerializer;
import de.topicmapslab.ctm.writer.exception.SerializerException;
import de.topicmapslab.ctm.writer.properties.CTMTopicMapWriterProperties;
import de.topicmapslab.ctm.writer.templates.Template;
import de.topicmapslab.ctm.writer.templates.TemplateFactory;
import de.topicmapslab.ctm.writer.utility.CTMIdentity;
import de.topicmapslab.ctm.writer.utility.CTMStreamWriter;
import de.topicmapslab.ctm.writer.utility.ICTMWriter;

/**
 * Implementation of {@link TopicMapWriter} interface to provide a CTM topic map writer.
 * 
 * @author Sven Krosse
 * @email krosse@informatik.uni-leipzig.de
 * 
 */
public class CTMTopicMapWriter implements TopicMapWriter {

	/**
	 * the output stream
	 */
	private final OutputStream stream;
	/**
	 * the base-URI
	 */
	private final String baseURI;
	/**
	 * the internal instance of a topic map writer
	 */
	private final TopicMapSerializer serializer;

	/**
	 * the property instance
	 */
	private final CTMTopicMapWriterProperties properties;

	/**
	 * the prefix handler
	 */
	private final PrefixHandler prefixHandler;

	/**
	 * the identity class
	 */
	private final CTMIdentity ctmIdentity;

	/**
	 * the template factory
	 */
	private final TemplateFactory factory;

	/**
	 * the list of includes
	 */
	private List<String> includes;

	/**
	 * The map containging iris to topic maps as keys and the notation as value. The notation may only be CTM or XTM
	 */
	private Map<String, String> mergeMaps;

	// the serializer
	// private AKindOfSerializer aKindOfSerializer = null;
	// private AssociationSerializer associationSerializer = null;
	// private DatatypeAwareSerializer datatypeAwareSerializer = null;
	// private IncludeSerializer includeSerializer = null;
	// private IsInstanceOfSerializer isInstanceOfSerializer = null;
	// private MergeMapSerializer mergeMapSerializer = null;
	// private NameSerializer nameSerializer = null;
	// private OccurrenceSerializer occurrenceSerializer = null;
	// private PrefixesSerializer prefixesSerializer = null;
	// private ReifiableSerializer reifiableSerializer = null;
	// private RoleSerializer roleSerializer = null;
	// private ScopedSerializer scopedSerializer = null;
	// private TopicMapSerializer topicMapSerializer = null;
	// private TopicSerializer topicSerializer = null;
	// private VariantSerializer variantSerializer = null;
	/**
	 * constructor
	 * 
	 * @param outputStream
	 *            the {@link OutputStream} as target for serialized topic map
	 * @param baseURI
	 *            the base URI used to create a default identify for topics without item-identifier, subject-identifier
	 *            and subject-locator
	 */
	public CTMTopicMapWriter(final OutputStream outputStream, final String baseURI) {
		this(outputStream, baseURI, null);
	}

	/**
	 * constructor
	 * 
	 * @param outputStream
	 *            the {@link OutputStream} as target for serialized topic map
	 * @param baseURI
	 *            the base URI used to create a default identify for topics without item-identifier, subject-identifier
	 *            and subject-locator
	 * @param propertyLine
	 *            a argument line, containing internal system properties to set. See also: (
	 *            {@link CTMTopicMapWriterProperties#parse(String)}
	 */
	public CTMTopicMapWriter(final OutputStream outputStream, final String baseURI, final String propertyLine) {
		this.stream = outputStream;
		this.baseURI = baseURI;
		this.properties = new CTMTopicMapWriterProperties();
		this.prefixHandler = new PrefixHandler();
		this.ctmIdentity = new CTMIdentity(prefixHandler);
		this.serializer = new TopicMapSerializer(this, prefixHandler);
		if (propertyLine != null) {
			this.properties.parse(propertyLine);
		}

		factory = new TemplateFactory(this);
	}

	/**
	 * Adding a new {@link Template} to the internal {@link TopicMapSerializer}. Templates are used to extract knowledge
	 * about ontology from topic items.
	 * 
	 * @param template
	 *            the template to add
	 */
	public void addTemplate(Template template) {
		this.serializer.addTemplate(template);
	}

	/**
	 * Adds a construct to the ignore set. Every construct in this set will not be serialized. This might be useful, if
	 * the topic map contains topics specified in external ctm files.
	 * 
	 * @param the
	 *            construct to ignore
	 */
	public void addIgnoredConstruct(Construct construct) {
		this.serializer.addIgnoredConstruct(construct);
	}

	/**
	 * Serialize the given topic map to CTM and write it into the given {@link OutputStream}.
	 * 
	 * @param topicMap
	 *            the topic map to serialize
	 * @throws Exception
	 *             thrown if serialization failed.
	 */
	public void write(TopicMap topicMap) throws IOException {

		 ICTMWriter writer = new CTMStreamWriter(stream);
		
		// open index if not opened
		TypeInstanceIndex idx = topicMap.getIndex(TypeInstanceIndex.class);
		if (!idx.isOpen()) {
			idx.open();
		}
		try {
			serializer.serialize(topicMap, writer);
		} catch (SerializerException e) {
			throw new IOException("Serialization failed, because of " + e.getLocalizedMessage());
		}
		stream.flush();
	}

	/**
	 * Returns the internal baseURI used to create a default identify for topics without item-identifier,
	 * subject-identifier and subject-locator.
	 * 
	 * @return the baseURI
	 */
	public String getBaseURI() {
		return baseURI;
	}

	/**
	 * Searches for the property with the specified key in this property list. If the key is not found in this property
	 * list, the default property list, and its defaults, recursively, are then checked. The method returns null if the
	 * property is not found.
	 * 
	 * @param key
	 *            the property key
	 * @return the value in this property list with the specified key value.
	 */
	public String getProperty(final String key) {
		return properties.getProperty(key);
	}

	/**
	 * Checks the plausibility of values before setting it.
	 * 
	 * @param key
	 *            the property key
	 * @param value
	 *            the property value
	 */
	public void setProperty(final String key, final String value) {
		this.properties.setProperty(key, value);
	}

	/**
	 * Get the IRI for the given prefix.
	 * 
	 * @param prefix
	 *            the prefix identifier
	 * @return the IRI or <code>null</code> if no prefix is registered for given name-space
	 */
	public String getPrefix(final String prefix) {
		return prefixHandler.getPrefix(prefix);
	}

	/**
	 * Register a new prefix definition.
	 * 
	 * A prefix definition in CTM is: %prefix key IRI
	 * 
	 * @param prefix
	 *            the prefix
	 * @param iri
	 *            the IRI
	 */
	public void setPrefix(final String prefix, final String iri) {
		this.prefixHandler.setPrefix(prefix, iri);
	}

	/**
	 * Serialize the given topic map to CTM and write it into the given {@link OutputStream}.
	 * 
	 * @param constructs
	 *            a collection containing all constructs to serialize
	 * @throws Exception
	 *             thrown if serialization failed.
	 */
	public void write(Construct... constructs) throws IOException {
		Collection<Construct> constructs_ = new HashSet<Construct>();
		for (Construct c : constructs) {
			constructs_.add(c);
		}
		write(constructs_);
	}

	/**
	 * Serialize the given topic map to CTM and write it into the given {@link OutputStream}.
	 * 
	 * @param constructs
	 *            a collection containing all constructs to serialize
	 * @throws Exception
	 *             thrown if serialization failed.
	 */
	public void write(Collection<Construct> constructs) throws IOException {
		ICTMWriter writer = new CTMStreamWriter(stream);
		try {
			serializer.serialize(constructs, writer);
		} catch (SerializerException e) {
			throw new IOException("Serialization failed, because of " + e.getLocalizedMessage());
		}
		stream.flush();
	}

	/**
	 * Returns the internal instance of the template factory
	 * 
	 * @return the template factory
	 */
	public TemplateFactory getFactory() {
		return factory;
	}

	/**
	 * Return the identity utility class instance
	 * 
	 * @return the utility class
	 */
	public CTMIdentity getCtmIdentity() {
		return ctmIdentity;
	}

	/**
	 * Returns the properties handler reference.
	 * 
	 * @return the properties handler
	 */
	public CTMTopicMapWriterProperties getProperties() {
		return properties;
	}

	/**
	 * Adds an uri to the include list
	 * 
	 * @param uri
	 *            the uri for an include directive
	 */
	public void addInclude(String uri) {
		if (includes == null)
			includes = new ArrayList<String>();
		includes.add(uri);
	}

	/**
	 * Adds an uri to the include list
	 * 
	 * @param uri
	 *            the uri for an include directive
	 */
	public void removeInclude(String uri) {
		if (includes != null)
			includes.remove(uri);
	}

	/**
	 * Returns the list of IRIs for include directives
	 * 
	 * @return
	 */
	public List<String> getIncludes() {
		if (includes == null)
			return Collections.emptyList();

		return includes;
	}

	/**
	 * Adds XTM map to merge
	 * @param iri the url for the xtm
	 */
	public void addMergeXTMMap(String iri) {
		if (mergeMaps == null)
			mergeMaps = new HashMap<String, String>();

		mergeMaps.put(iri, "XTM");
	}
	/**
	 * Adds CTM map to merge
	 * @param iri the url for the ctm
	 */
	public void addMergeCTMMap(String iri) {
		if (mergeMaps == null)
			mergeMaps = new HashMap<String, String>();

		mergeMaps.put(iri, "CTM");
	}

	/**
	 * Removes the map to merge with the given url
	 * @param iri
	 */
	public void removeMergeMap(String iri) {
		if (mergeMaps != null)
			mergeMaps.remove(iri);
	}

	/**
	 * Returns the map of maps to merge
	 * @return a map with the maps (Map<IRI, FORMAT>) 
	 */
	public Map<String, String> getMergeMaps() {
		if (mergeMaps == null)
			mergeMaps = new HashMap<String, String>();
		return mergeMaps;
	}
}
