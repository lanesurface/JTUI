package jtxt.emulator;

public interface ResizeSubscriber {
  /**
   * This method is called for each subscriber whenever the subject that dictates
   * when these events should be propagated determines that it is necessary to update
   * the dimensions of the interface.
   *
   * @param lines The new number of lines within the text interface.
   * @param lineSize The number of characters on each of these lines.
   */
  void resize(
    int lines,
    int lineSize);
}
