package jtxt.emulator.tui;

import jtxt.GlyphBuffer;

/**
 * Adds dynamic runtime properties to a {@code Component}, thus allowing components
 * to add properties like borders, outlines, and more generic aspects without having
 * to explicitly inherit them from another component.
 *
 * @see Border
 */
public abstract class Decorator extends Component {
  /**
   * The {@code Component} which this decorator is applied to.
   */
  protected Component component;

  protected Decorator(Component component) {
    this.component = component;
  }

  @Override
  public void draw(GlyphBuffer buffer) {
    component.draw(buffer);
  }

  /**
   * Returns the layout parameters that this {@code Decorator}s component was
   * initialized with.
   */
  @Override
  public Object getLayoutParameters() {
    return component.getLayoutParameters();
  }
}
