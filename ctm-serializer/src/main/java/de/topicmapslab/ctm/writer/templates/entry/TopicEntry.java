package de.topicmapslab.ctm.writer.templates.entry;

import java.util.LinkedList;
import java.util.List;

import de.topicmapslab.ctm.writer.exception.SerializerException;
import de.topicmapslab.ctm.writer.templates.entry.base.EntryImpl;
import de.topicmapslab.ctm.writer.templates.entry.base.IEntry;
import de.topicmapslab.ctm.writer.templates.entry.param.IEntryParam;
import de.topicmapslab.ctm.writer.utility.CTMBuffer;
import de.topicmapslab.ctm.writer.utility.CTMTokens;

public class TopicEntry extends EntryImpl {

	private List<IEntry> entries = new LinkedList<IEntry>();

	public TopicEntry(IEntryParam param) {
		super(param);
	}

	// @Override
	// public List<String> extractArguments(Topic topic,
	// Set<Object> affectedConstructs) throws SerializerException {
	// List<String> values = new LinkedList<String>();
	// for (IEntry entry : entries) {
	// values.addAll(entry.extractArguments(null, topic,
	// affectedConstructs));
	// }
	// return values;
	// }

	// @Override
	// public boolean isAdaptiveFor(Construct construct) {
	// if (getParameter() instanceof WildcardParam) {
	// return true;
	// } else if (construct instanceof Topic) {
	// return isAdaptiveFor((Topic) construct);
	// }
	// return false;
	// }
	//
	// @Override
	// public boolean isAdaptiveFor(Topic topic) {
	// if (getParameter() instanceof WildcardParam) {
	// return true;
	// }
	// for (IEntry entry : entries) {
	// if (!entry.isAdaptiveFor(topic)) {
	// return false;
	// }
	// }
	// return true;
	// }

	public void serialize(CTMBuffer buffer) throws SerializerException {
		CTMBuffer b = new CTMBuffer();
		b.append(getParameter().getCTMRepresentation(), CTMTokens.WHITESPACE);
		for (IEntry entry : entries) {
			entry.serialize(b);
		}
		b.clearCTMTail();
		buffer.append(b);
	}

	public void add(IEntry entry) {
		entries.add(entry);
	}

	@Override
	public List<String> getVariables() {
		List<String> variables = new LinkedList<String>();
		for (IEntry entry : entries) {
			variables.addAll(entry.getVariables());
		}
		return variables;
	}
}
