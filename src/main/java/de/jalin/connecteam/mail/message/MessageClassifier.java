package de.jalin.connecteam.mail.message;

import java.util.Iterator;

import de.jalin.connecteam.data.Subscription;
import de.jalin.connecteam.data.Topic;

public class MessageClassifier {

	private final Post mlPost;
	private final Topic topic;

	public MessageClassifier(final Topic topic, final Post msg) {
		this.topic = topic;
		this.mlPost = msg;
	}
	
	public boolean isRejected() {
		final String originalFrom = mlPost.getOriginalFrom();
		final Iterator<Subscription> subsIterator = topic.getSubscriptions().iterator();
		while (subsIterator.hasNext()) {
			final Subscription subscription = subsIterator.next();
			if (subscription.isActive()) {
				final String subscriberAddress = subscription.getSubscriber().getAddress();
				if (subscriberAddress.equals(originalFrom)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isAccepted() {
		final String originalFrom = mlPost.getOriginalFrom();
		final Iterator<Subscription> subsIterator = topic.getSubscriptions().iterator();
		while (subsIterator.hasNext()) {
			final Subscription subscription = subsIterator.next();
			if (subscription.isActive()) {
				final String subscriberAddress = subscription.getSubscriber().getAddress();
				if (subscriberAddress.equals(originalFrom)) {
					return true;
				}
			}
		}
		return false;
	}

}
