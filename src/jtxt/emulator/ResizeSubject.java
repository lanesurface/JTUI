package jtxt.emulator;

public interface ResizeSubject {
    /**
     * Adds the {@code ResizeSubscriber} as a subscriber of this subject; when
     * a resize is triggered, the given subscriber will be notified through an
     * invocation of it's {@link ResizeSubscriber#resize(int, int)} method.
     * 
     * @param subscriber The object which wishes to be notified whenever this
     *                   subject's dimensions change.
     */
    void subscribe(ResizeSubscriber subscriber);
    
    /**
     * Stops the given subscriber from receiving notifications about this
     * subject being resized.
     * 
     * @param subscriber The subscriber which should stop receiving
     *                   notifications.
     */
    void remove(ResizeSubscriber subscriber);
    
    /**
     * A subscriber should call this method whenever the number of lines or the
     * size of a line is changed by a client. This will ensure that all objects
     * which rely on these dimensions are aware whenever they are changed.
     */
    void resized();
}
